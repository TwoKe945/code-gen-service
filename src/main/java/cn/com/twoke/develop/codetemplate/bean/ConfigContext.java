package cn.com.twoke.develop.codetemplate.bean;

import cn.com.twoke.develop.codetemplate.enums.DatabaseId;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ConfigContext {
    /**
     * 基础的包名
     */
    private String basePackage;
    /**
     * 执行代码的生成路径
     */
    private String cwdPath;

    private DatabaseId databaseId;
}
