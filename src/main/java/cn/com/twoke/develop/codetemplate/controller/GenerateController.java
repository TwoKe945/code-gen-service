package cn.com.twoke.develop.codetemplate.controller;

import cn.com.twoke.develop.codetemplate.aspect.AutoInjectDataSource;
import cn.com.twoke.develop.codetemplate.bean.TableInfo;
import cn.com.twoke.develop.codetemplate.context.MetaDataServiceHolder;
import cn.com.twoke.develop.codetemplate.service.GenerateService;
import cn.com.twoke.develop.codetemplate.service.MetaDataService;
import cn.com.twoke.develop.codetemplate.service.MetaDataServiceFactory;
import cn.hutool.core.map.MapUtil;
import freemarker.template.TemplateException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/generate")
public class GenerateController {

    private final GenerateService generateService;


    @GetMapping("/query/{tableName}")
    @AutoInjectDataSource()
    public TableInfo queryTable(@PathVariable("tableName") String tableName) throws IOException, TemplateException {
        return MetaDataServiceHolder.get().queryTable(tableName);
    }

    @GetMapping("/query/tables")
    @AutoInjectDataSource()
    public List<TableInfo> queryTables(boolean withFields) throws IOException, TemplateException {
        if (withFields) {
            return MetaDataServiceHolder.get().queryTableWithColumns();
        } else {
            return MetaDataServiceHolder.get().queryTables();
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
    public Map<String ,Object> generateFiles(@PathVariable("template") String template,
                                             @PathVariable("tables") String tables,
                                             @RequestBody Map<String, TableInfo> data) throws IOException, ConfigurationException, URISyntaxException {
        if (template == null || !template.matches("^[a-zA-Z0-9_-]+$")) {
            throw new IllegalArgumentException("Invalid template name");
        }
        TableInfo info = null;
        if ("base".equals(tables)) {
            generateService.generateTemplate(template, tables, info);
        } else {
            String[] split = tables.split(",");
            for (String table : split) {
                if (!data.isEmpty()) {
                    info = data.get(table);
                }
                if (Objects.isNull(info)) {
                    info = MetaDataServiceHolder.get().queryTable(table);
                }
                generateService.generateTemplate(template, table, info);
            }
        }
        // 其他业务逻辑
        return MapUtil.<String, Object>builder()
                .put("code", 200)
                .put("message", "请求成功")
                .map();
    }

}
