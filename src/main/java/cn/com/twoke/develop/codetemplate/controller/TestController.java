package cn.com.twoke.develop.codetemplate.controller;

import cn.com.twoke.develop.codetemplate.aspect.AutoInjectDataSource;
import cn.com.twoke.develop.codetemplate.bean.TableInfo;
import cn.com.twoke.develop.codetemplate.context.MetaDataServiceHolder;
import cn.com.twoke.develop.codetemplate.service.MetaDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TestController {

    @AutoInjectDataSource
    @PostMapping("/test")
    public void test() throws Exception {
        List<TableInfo> tableInfos = MetaDataServiceHolder.get().queryTables();
        System.out.println();
    }

}
