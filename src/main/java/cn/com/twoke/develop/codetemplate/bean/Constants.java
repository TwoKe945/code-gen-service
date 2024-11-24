package cn.com.twoke.develop.codetemplate.bean;

import cn.hutool.extra.spring.SpringUtil;
import org.springframework.core.env.Environment;

import javax.swing.*;

public class Constants {

    /**
     * 包路径
     */
    public static String PACKAGE_BASE = null;
    public static String PACKAGE_BEAN = null;
    public static String PACKAGE_PARAM = null;
    public static String PACKAGE_ENUMS = null;
    public static String PACKAGE_VO = null;
    public static String PACKAGE_MAPPER = null;
    public static String PACKAGE_SERVICE = null;
    public static String PACKAGE_SERVICE_IMPL = null;
    public static String PACKAGE_CONTROLLER = null;
    public static String PACKAGE_EXCEPTION = null;
    public static String PACKAGE_UTILS = null;
    /**
     * 文件存储路径
     */
    public static String PATH_BASE = null;

    public static String PATH_BEAN = null;
    public static String PATH_PARAM = null;
    public static String PATH_ENUMS = null;
    public static String PATH_VO = null;
    public static String PATH_MAPPER = null;
    public static String PATH_MAPPER_XML = null;
    public static String PATH_SERVICE = null;
    public static String PATH_SERVICE_IMPL = null;
    public static String PATH_CONTROLLER = null;
    public static String PATH_EXCEPTION = null;
    public static String PATH_UTILS = null;
    /**
     * 后缀
     */
    public static String SUFFIX_MAPPER = null;
    public static String SUFFIX_MAPPER_XML = null;
    public static String SUFFIX_SERVICE = null;
    public static String SUFFIX_SERVICE_IMPL = null;
    public static String SUFFIX_CONTROLLER = null;
    public static String SUFFIX_PROPERTY_FUZZY = null;
    public static String SUFFIX_BEAN_PARAM = null;
    public static String SUFFIX_BEAN_PARAM_TIME_START = null;
    public static String SUFFIX_BEAN_PARAM_TIME_END = null;


    /**
     * 是否忽略表前缀
     */

    public static Boolean IGNORE_TABLE_PREFIX;

    public static String TABLE_SPLIT_PREFIX;


    public static String[] IGNORE_BEAN_TOSTRING_COLUMN;
    public static String[] IGNORE_BEAN_TOJSON_COLUMN;
    public static String IGNORE_BEAN_TOJSON_EXPRESSION;
    public static String IGNORE_BEAN_TOJSON_CLASS;

    /**
     * 格式化日期配置
     */
    public static String BEAN_DATE_EXPRESSION;
    public static String BEAN_DATE_EXPRESSION_CLASS;
    public static String BEAN_DATE_FORMAT;
    public static String BEAN_DATE_FORMAT_CLASS;

    private static String MAVEN_PATH = "/src/main/";

    private static String PATH_JAVA = MAVEN_PATH + "java/";

    private static String PATH_RESOURCES = MAVEN_PATH + "resources/";

    static {
        Environment environment = SpringUtil.getBean(Environment.class);
        PACKAGE_BEAN = environment.getProperty("package.bean");
        PACKAGE_PARAM =environment.getProperty("package.param");
        PACKAGE_ENUMS = environment.getProperty("package.enums");
        PACKAGE_VO = environment.getProperty("package.vo");
        PACKAGE_MAPPER = environment.getProperty("package.mapper");
        PACKAGE_SERVICE = environment.getProperty("package.service");
        PACKAGE_SERVICE_IMPL = environment.getProperty("package.service.impl");
        PACKAGE_CONTROLLER = environment.getProperty("package.controller");
        PACKAGE_EXCEPTION = ".exception";
        PACKAGE_UTILS = ".utils";
        PATH_BEAN = PACKAGE_BEAN.replace(".", "/");
        PATH_PARAM = PACKAGE_PARAM.replace(".", "/");
        PATH_ENUMS = PACKAGE_ENUMS.replace(".", "/");
        PATH_VO = PACKAGE_VO.replace(".", "/");
        PATH_MAPPER =  PACKAGE_MAPPER.replace(".", "/");
        PATH_MAPPER_XML = environment.getProperty("path.base") + PATH_RESOURCES + PACKAGE_MAPPER.replace(".", "/");
        PATH_SERVICE = PACKAGE_SERVICE.replace(".", "/");
        PATH_SERVICE_IMPL = PACKAGE_SERVICE_IMPL.replace(".", "/");
        PATH_CONTROLLER = PACKAGE_CONTROLLER.replace(".", "/");
        PATH_EXCEPTION = PACKAGE_EXCEPTION.replace(".", "/");
        PATH_UTILS = PACKAGE_UTILS.replace(".", "/");

        SUFFIX_BEAN_PARAM = environment.getProperty("suffix.bean.param");
        SUFFIX_PROPERTY_FUZZY = environment.getProperty("suffix.property.fuzzy");
        SUFFIX_MAPPER = environment.getProperty("suffix.mapper");
        SUFFIX_MAPPER_XML = environment.getProperty("suffix.mapper.xml");
        SUFFIX_SERVICE = environment.getProperty("suffix.service");
        SUFFIX_SERVICE_IMPL = environment.getProperty("suffix.service.impl");
        SUFFIX_CONTROLLER = environment.getProperty("suffix.controller");
        SUFFIX_BEAN_PARAM_TIME_START = environment.getProperty("suffix.bean.param.time.start");
        SUFFIX_BEAN_PARAM_TIME_END = environment.getProperty("suffix.bean.param.time.end");
        IGNORE_TABLE_PREFIX = environment.getProperty("ignore.table.prefix") == null ? false : Boolean.parseBoolean(environment.getProperty("ignore.table.prefix"));
        /**
         * 是否分表
         */
        TABLE_SPLIT_PREFIX = environment.getProperty("table.split.prefix");

        /**
         * 处理忽略tostring的属性
         */
        String ignore_bean_tostring_columnstr = environment.getProperty("ignore.bean.tostring.column");
        if (null != ignore_bean_tostring_columnstr) {
            IGNORE_BEAN_TOSTRING_COLUMN = ignore_bean_tostring_columnstr.split(",");
        }

        /**
         * 返回json时不需要返回的属性
         */
        String ignore_bean_tojson_columnstr = environment.getProperty("ignore.bean.tojson.column");
        if (null != ignore_bean_tojson_columnstr) {
            IGNORE_BEAN_TOJSON_COLUMN = ignore_bean_tojson_columnstr.split(",");
        }
        IGNORE_BEAN_TOJSON_EXPRESSION = environment.getProperty("ignore.bean.tojson.expression");

        String ignore_bean_tojson_classstr = environment.getProperty("ignore.bean.tojson.class");
        if (null != ignore_bean_tojson_classstr && !"".equals(ignore_bean_tojson_classstr)) {
            IGNORE_BEAN_TOJSON_CLASS = ignore_bean_tojson_classstr;
        }


        // 日期格式化配置
        BEAN_DATE_EXPRESSION = environment.getProperty("bean.date.expression");
        BEAN_DATE_EXPRESSION_CLASS = environment.getProperty("bean.date.expression.class");
        BEAN_DATE_FORMAT = environment.getProperty("bean.data.format");
        BEAN_DATE_FORMAT_CLASS = environment.getProperty("bean.date.format.class");
    }

    public final static String[] SQL_DATE_TIIME_TYPES = new String[]{"datetime", "timestamp", "TIME", "TIMESTAMP"};

    public final static String[] SQL_DATE_TYPES = new String[]{"date", "DATE"};

    public static final String[] SQL_DECIMAL_TYPE = new String[]{"decimal", "double", "float", "FLOAT", "DOUBLE", "DECIMAL", "DOUBLE PRECISION"};

    public static final String[] SQL_STRING_TYPE = new String[]{"char", "varchar", "text", "mediumtext", "longtext", "VARCHAR"};

    //Integer
    public static final String[] SQL_INTEGER_TYPE = new String[]{"int", "tinyint", "INT", "NUMERIC", "TINYINT", "INTEGER"};

    //Long
    public static final String[] SQL_LONG_TYPE = new String[]{"bigint", "BIGINT"};

    /**
     * 常量
     */
    public static String TYPE_STRING = "String";

    public static String TYPE_DATE = "Date";
}
