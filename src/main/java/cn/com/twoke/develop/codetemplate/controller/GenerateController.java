package cn.com.twoke.develop.codetemplate.controller;

import cn.com.twoke.develop.codetemplate.aspect.AutoInjectDataSource;
import cn.com.twoke.develop.codetemplate.bean.TableInfo;
import cn.com.twoke.develop.codetemplate.service.GenerateService;
import cn.com.twoke.develop.codetemplate.service.MetaDataService;
import cn.hutool.core.map.MapUtil;
import freemarker.template.TemplateException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/generate")
public class GenerateController {

    private final GenerateService generateService;
    private final MetaDataService metaDataService;


    @GetMapping("/query/{tableName}")
    @AutoInjectDataSource()
    public TableInfo queryTable(@PathVariable("tableName") String tableName) throws IOException, TemplateException {
        return metaDataService.queryTable(tableName);
    }

    @GetMapping("/query/tables")
    @AutoInjectDataSource()
    public List<TableInfo> queryTables(boolean withFields) throws IOException, TemplateException {
        if (withFields) {
            return metaDataService.queryTableWithColumns();
        } else {
            return metaDataService.queryTables();
        }
    }

    /**
     * 生成基础文件
     *
     * 通过 template 参数来指定模板文件夹，通过模板引擎导出对应的文件
     *
     * @param template 传递模板参数的文件名
     * @return
     */
    @PostMapping("/file/{template}/{tables}")
    @AutoInjectDataSource(config = true)
    public Map<String ,Object> generateFiles(@PathVariable("template") String template,@PathVariable("tables") String tables) throws IOException, ConfigurationException, URISyntaxException {
        // 验证模板参数是否合法
        if (template == null || !template.matches("^[a-zA-Z0-9_-]+$")) {
            throw new IllegalArgumentException("Invalid template name");
        }
        if ("base".equals(tables)) {
            generateService.generateTemplate(template, tables);
        } else {
            String[] split = tables.split(",");
            for (String table : split) {
                generateService.generateTemplate(template, table);
            }
        }
        // 其他业务逻辑
        return MapUtil.<String, Object>builder()
                .put("code", 200)
                .put("message", "请求成功")
                .map();
    }

}
