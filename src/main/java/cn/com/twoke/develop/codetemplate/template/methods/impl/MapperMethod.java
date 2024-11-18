package cn.com.twoke.develop.codetemplate.template.methods.impl;

import cn.com.twoke.develop.codetemplate.bean.FieldInfo;
import cn.com.twoke.develop.codetemplate.template.annotation.TemplateMethod;
import cn.com.twoke.develop.codetemplate.utils.CommentUtils;
import cn.com.twoke.develop.codetemplate.utils.StringTools;

import java.util.List;
import java.util.Map;

public class MapperMethod {

    @TemplateMethod
    public String createIndexQuery(String beanPackageName,String tableName, String BASE_RESULT_MAP,String BASE_COLUMN_LIST,String tableAlias, Map<String, List<FieldInfo>> keyMap, List<FieldInfo> fieldInfoList) {
        StringBuilder sb = new StringBuilder();
      for (Map.Entry<String, List<FieldInfo>> entry : keyMap.entrySet()) {
                List<FieldInfo> keyfieldInfoList = entry.getValue();
                StringBuffer whereStr = new StringBuffer();
                StringBuffer methodName = new StringBuffer();
                int index = 0;
                for (FieldInfo column : keyfieldInfoList) {
                    if (index > 0) {
                        methodName.append("And");
                        whereStr.append(" and ");
                    }
                    whereStr.append(column.getFieldName() + "=#{" + column.getPropertyName() + "}");
                    methodName.append(StringTools.upperCaseFirstLetter(column.getPropertyName()));
                    index++;
                }
                if (whereStr.length() > 0) {
                    sb.append("\t<!-- 根据" + methodName + "修改-->");
                    sb.append("\n");
                    sb.append("\t<update id=\"updateBy" + methodName + "\" parameterType=\"" + beanPackageName + "\">");
                    sb.append("\n");
                    sb.append("\t\t UPDATE " + tableName);
                    sb.append("\n");
                    sb.append(" \t\t <set> ");
                    sb.append("\n");
                    for (FieldInfo FieldInfo : fieldInfoList) {
                        if ((null != FieldInfo.getAutoIncrement() && FieldInfo.getAutoIncrement())) {
                            continue;
                        }

                        boolean isKey = false;
                        for (FieldInfo keycolumn : keyfieldInfoList) {
                            if (keycolumn.getFieldName().equals(FieldInfo.getFieldName())) {
                                isKey = true;
                                break;
                            }
                        }
                        if (isKey) {
                            continue;
                        }

                        sb.append("\t\t\t<if test=\"bean." + FieldInfo.getPropertyName() + " != null\">");
                        sb.append("\n");
                        sb.append("\t\t\t\t " + FieldInfo.getFieldName() + " = #{bean." + FieldInfo.getPropertyName() + "},");
                        sb.append("\n");
                        sb.append("\t\t\t</if>");
                        sb.append("\n");
                    }
                    sb.append(" \t\t </set>");
                    sb.append("\n");
                    sb.append(" \t\t where " + whereStr);
                    sb.append("\n");
                    sb.append("\t</update>");
                    sb.append("\n");
                    sb.append("\n");


                    sb.append("\t<!-- 根据" + methodName + "删除-->");
                    sb.append("\n");
                    sb.append("\t<delete id=\"deleteBy" + methodName + "\">");
                    sb.append("\n");
                    sb.append("\t\tdelete from " + tableName + " where " + whereStr);
                    sb.append("\n");
                    sb.append("\t</delete>");
                    sb.append("\n");
                    sb.append("\n");

                    sb.append("\t<!-- 根据" + methodName + "删除-->");
                    sb.append("\n");
                    sb.append("\t<select id=\"selectBy" + methodName + "\" resultMap=\"" + BASE_RESULT_MAP + "\" >");
                    sb.append("\n");
                    sb.append("\t\tselect <include refid=\"" + BASE_COLUMN_LIST + "\" /> from " + tableName + " " + tableAlias + " where " +
                            whereStr);
                    sb.append("\n");
                    sb.append("\t</select>");
                    sb.append("\n");
                    sb.append("\n");
                }
            }
        return sb.toString();
    }

    @TemplateMethod
    public String createMapperMethod(List<FieldInfo> keyColumnList) {
        StringBuilder paramStr = new StringBuilder();
        StringBuilder methodName = new StringBuilder();
        StringBuilder sb = new StringBuilder();
        int index = 0;
        for (FieldInfo column : keyColumnList) {
            if (index > 0) {
                paramStr.append(",");
                methodName.append("And");
            }
            paramStr.append("@Param(\"" + column.getPropertyName() + "\") " + column.getJavaType() + " "
                    + column.getPropertyName() + "");
            methodName.append(StringTools.upperCaseFirstLetter(column.getPropertyName()));
            index++;

            if (paramStr.length() > 0) {
                generateMethods(sb, methodName.toString(), paramStr.toString());
            }
        }
        return sb.toString();
    }


    private void generateMethods(StringBuilder sb, String methodName, String paramStr) {
        String comment = "根据" + methodName + "更新";
        CommentUtils.buildMethodComment(sb, comment);
        sb.append("\n");
        sb.append("\t Integer updateBy" + methodName + "(@Param(\"bean\") T t," + paramStr + ");");
        sb.append("\n");
        sb.append("\n");

        comment = "根据" + methodName + "删除";
        CommentUtils.buildMethodComment(sb, comment);
        sb.append("\n");
        sb.append("\t Integer deleteBy" + methodName + "(" + paramStr + ");");
        sb.append("\n");
        sb.append("\n");

        comment = "根据" + methodName + "获取对象";
        CommentUtils.buildMethodComment(sb, comment);
        sb.append("\n");
        sb.append("\t T selectBy" + methodName + "(" + paramStr + ");");
        sb.append("\n");
        sb.append("\n");
    }
}
