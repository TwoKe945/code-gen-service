package cn.com.twoke.develop.codetemplate;

import cn.com.twoke.develop.codetemplate.bean.Constants;
import cn.com.twoke.develop.codetemplate.bean.TableInfo;
import cn.com.twoke.develop.codetemplate.config.datasource.DataSourceContextHolder;
import cn.com.twoke.develop.codetemplate.service.MetaDataService;
// import cn.com.twoke.develop.codetemplate.template.methods.EnhancedTemplateMethodModel;
import cn.com.twoke.develop.codetemplate.template.methods.TemplateMethodHandler;
import cn.com.twoke.develop.codetemplate.template.methods.impl.*;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Slf4j
@SpringBootTest
public class TemplateTest {
    @Autowired
    private freemarker.template.Configuration freeMarkerConfig;
    @Autowired
    private MetaDataService metaDataService;
    static final String TEMPLATES_NAME = "templates";
    static final String CONFIG_FILE_NAME = "config.properties";

    @Test
    public void test() throws ConfigurationException, IOException, TemplateException, URISyntaxException {
         String template = "java_pojo_01";
         String basePackage = "cn.com.twoke.easypan";
        Configuration config = loadConfig(template);
        DataSourceContextHolder.setDataSourceKey("default");
        TableInfo info =  metaDataService.queryTable("user_info");

        URL resourceUrl = Thread.currentThread().getContextClassLoader().getResource("templates/" + template);
        Path templateDir = Paths.get(resourceUrl.toURI());
        Files.list(templateDir).forEach(path -> {
            if (!path.getFileName().toString().equals(CONFIG_FILE_NAME)) {
                try {
                    String key = path.getFileName().toString().replace(".ftl", "");
                    Template tpl = freeMarkerConfig.getTemplate( template +"/" + path.getFileName());
                    Map<String, Object> dataModel = new HashMap<>();
                    dataModel.put("basePackage", basePackage);
                    String pkg = config.getString(key + ".package", "");
                    String pkgPath = StringUtils.isBlank(pkg) ? basePackage : basePackage + "." + pkg;
                    dataModel.put("package", pkgPath);
                    dataModel.put("table", info);
                    TemplateMethodHandler.withMethods(dataModel);
                    // 常量注入
                    dataModel.put("IGNORE_BEAN_TO_JSON_CLASS", Constants.IGNORE_BEAN_TOJSON_CLASS);
                    dataModel.put("IGNORE_BEAN_TO_JSON_EXPRESSION", Constants.IGNORE_BEAN_TOJSON_EXPRESSION);
                    dataModel.put("BEAN_DATE_EXPRESSION_CLASS", Constants.BEAN_DATE_EXPRESSION_CLASS);
                    dataModel.put("BEAN_DATE_FORMAT_CLASS", Constants.BEAN_DATE_FORMAT_CLASS);
                    dataModel.put("SUFFIX_PROPERTY_FUZZY", Constants.SUFFIX_PROPERTY_FUZZY);
                    dataModel.put("IGNORE_FUZZY_PROPERTY", "qqAvatar,password");
                    dataModel.put("TYPE_STRING", Constants.TYPE_STRING);
                    dataModel.put("SUFFIX_BEAN_PARAM_TIME_START", Constants.SUFFIX_BEAN_PARAM_TIME_START);
                    dataModel.put("SUFFIX_BEAN_PARAM_TIME_END", Constants.SUFFIX_BEAN_PARAM_TIME_END);

                    List<String> imports = new ArrayList<>();
                    String[] depends = config.getString(key + ".depends", "").split(",");
                    System.out.println( key + ":" + Arrays.asList(depends));
                    for (String depend : depends) {
                        if (StringUtils.isNotBlank(config.getString(depend + ".name"))) {
                           depend = parseContent(config.getString(depend + ".name"), dataModel);
                       }
                        String importPath = basePackage + (StringUtils.isNotBlank(config.getString(depend + ".package", "")) ? "." + config.getString(depend + ".package") : "") + "." + depend;
                        imports.add(importPath);
                    }
                    dataModel.put("imports", imports);
                    // 渲染模板
                    String content = FreeMarkerTemplateUtils.processTemplateIntoString(tpl, dataModel);
                   String output = "E:\\Workspace\\Project\\code-template/" + template + "/" + pkgPath.replaceAll("\\.", "/");
                   File outputDir = new File(output);
                   if (!outputDir.exists()) {
                       outputDir.mkdirs();
                   }
                   String ext = config.getString(key + ".ext", "java");
                   String fileName = key;
                   if (StringUtils.isNotBlank(config.getString(key + ".name"))) {
                       fileName = parseContent(config.getString(key + ".name"), dataModel);
                   }
                    File file = new File(  output+ "/" + fileName + "." + ext);
                    try (FileWriter writer = new FileWriter(file)) {
                        writer.write(content);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (TemplateException e) {
                    throw new RuntimeException(e);
                }
            }
        });

    }


    private static Configuration loadConfig(String template) throws ConfigurationException {
        Configurations configs = new Configurations();
        URL resource = Thread.currentThread().getContextClassLoader().getResource(TEMPLATES_NAME + "/" + template + "/" + CONFIG_FILE_NAME);
        if (resource == null) {
            log.warn("未找到模板 {} 的配置文件", template);
            throw new ConfigurationException("未找到模板 " + template + " 的配置文件");
        }
        try {
            return configs.properties(resource.getFile());
        } catch (ConfigurationException e) {
            log.error("加载配置文件时出错", e);
            throw e;
        }
    }

    public static <T> Stream<T> iteratorToStream(Iterator<T> iterator) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, 0), false);
    }

    private static Stream<String> getPropertiesWithPrefix(Configuration configuration, String prefix, Function<String, String> mapper) {
        return iteratorToStream(configuration.getKeys()).filter(key -> key.startsWith(prefix)).map(mapper);
    }

    private static Stream<String> getPropertiesWithPrefix(Configuration configuration, String prefix) {
        return getPropertiesWithPrefix(configuration, prefix, key -> key);
    }


    private String parseContent(String content, Map<String, Object> dataModel) throws IOException, TemplateException {
        // 创建模板对象
        Template template = new Template("inlineTemplate", content, freeMarkerConfig);
        // 渲染模板
        return FreeMarkerTemplateUtils.processTemplateIntoString(template, dataModel);
    }

}
