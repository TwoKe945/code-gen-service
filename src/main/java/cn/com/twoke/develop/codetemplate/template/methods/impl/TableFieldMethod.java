package cn.com.twoke.develop.codetemplate.template.methods.impl;

import cn.com.twoke.develop.codetemplate.bean.Constants;
import cn.com.twoke.develop.codetemplate.bean.FieldInfo;
import cn.com.twoke.develop.codetemplate.template.annotation.TemplateMethod;
import cn.com.twoke.develop.codetemplate.utils.StringTools;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Map;

public class TableFieldMethod {

    /**
     * 获取日期格式化表达式
     * @param sqlDateType
     * @return
     */
    @TemplateMethod
    public String getDatePattern(String sqlDateType) {
        String dateTimePattern = "yyyy-MM-dd HH:mm:ss";
        if (ArrayUtils.contains(Constants.SQL_DATE_TYPES, sqlDateType)) {
            dateTimePattern = "yyyy-MM-dd";
        }
        String beanDateExpression = String.format(Constants.BEAN_DATE_EXPRESSION, dateTimePattern);
        String beanDateFormat = String.format(Constants.BEAN_DATE_FORMAT, dateTimePattern);
        return beanDateExpression + "\n   " + beanDateFormat;
    }

    /**
     * 判断是否是日期类型
     * @param sqlDateType
     * @param formatType
     * @return
     */
    @TemplateMethod
    public boolean isDateOrDateTime(String sqlDateType,@Nullable String formatType) {
        if (null != formatType) {
            if (("date".equals(formatType) && ArrayUtils.contains(Constants.SQL_DATE_TYPES, sqlDateType))
                    || ("datetime".equals(formatType) && ArrayUtils.contains(Constants.SQL_DATE_TIIME_TYPES, sqlDateType))) {
                return true;
            }
            return false;
        }
        if (ArrayUtils.contains(Constants.SQL_DATE_TYPES, sqlDateType) ||
        ArrayUtils.contains(Constants.SQL_DATE_TIIME_TYPES, sqlDateType)) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否忽略模糊查询
     * @param ignoreFuzzyText
     * @param propertyName
     * @return
     */
    @TemplateMethod
    public boolean isIgnoreFuzzyProperty(String ignoreFuzzyText, String propertyName) {
        return ignoreFuzzyText.indexOf(String.valueOf(propertyName)) > -1;
    }

    /**
     * 判断是否是Java时间类型
     * @param javaType
     * @return
     */
    @TemplateMethod
    public boolean isJavaDateType(String javaType) {
        return Constants.TYPE_DATE.equals(javaType);
    }

    /**
     * 判断是否是Java字符串类型
     * @param javaType
     * @return
     */
    @TemplateMethod
    public boolean isJavaStringType(String javaType) {
        return Constants.TYPE_STRING.equals(javaType);
    }

    /**
     * 判断是否是Json忽略字段
     * @param propertyName
     * @return
     */
    @TemplateMethod
    public boolean isJsonIgnoreField(String propertyName) {
        return Constants.IGNORE_BEAN_TOJSON_COLUMN != null &&  ArrayUtil.contains(Constants.IGNORE_BEAN_TOJSON_COLUMN, propertyName);
    }

    /**
     * 首字母大写
     * @param propertyName
     * @return
     */
    @TemplateMethod
    public String toUpperCaseFirstLetter(String propertyName) {
        return StringTools.upperCaseFirstLetter(String.valueOf(propertyName));
    }

    /**
     * 获取自增列
     * @param fieldInfoList
     * @return
     */
    @TemplateMethod
    public Map<String, FieldInfo> getAutoIncrementColumn(List<FieldInfo> fieldInfoList) {
        FieldInfo autoIncrementColumn = null;
        for (FieldInfo FieldInfo : fieldInfoList) {
            if (null != FieldInfo.getAutoIncrement() && FieldInfo.getAutoIncrement()) {
                autoIncrementColumn = FieldInfo;
                break;
            }
        }
        return MapUtil.<String, FieldInfo>builder().put("column", autoIncrementColumn).map();
    }



}
