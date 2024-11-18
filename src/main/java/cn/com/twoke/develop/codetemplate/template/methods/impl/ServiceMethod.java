package cn.com.twoke.develop.codetemplate.template.methods.impl;

import cn.com.twoke.develop.codetemplate.bean.FieldInfo;
import cn.com.twoke.develop.codetemplate.template.annotation.TemplateMethod;
import cn.com.twoke.develop.codetemplate.utils.CommentUtils;
import cn.com.twoke.develop.codetemplate.utils.StringTools;

import java.util.List;
import java.util.Map;

public class ServiceMethod {

    @TemplateMethod
    public String createServiceMethod(String beanName, Map<String, List<FieldInfo>> keyMap, List<FieldInfo> fieldInfoList) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, List<FieldInfo>> entry : keyMap.entrySet()) {
                List<FieldInfo> keyfieldInfoList = entry.getValue();
                StringBuffer paramStr = new StringBuffer();
                StringBuffer methodName = new StringBuffer();
                int index = 0;
                for (FieldInfo column : keyfieldInfoList) {
                    if (index > 0) {
                        paramStr.append(",");
                        methodName.append("And");
                    }
                    paramStr.append(column.getJavaType() + " " + column.getPropertyName() + "");
                    methodName.append(StringTools.upperCaseFirstLetter(column.getPropertyName()));
                    index++;
                }
                if (paramStr.length() > 0) {
                    //根据主键查询
                    CommentUtils.buildMethodComment(sb, "根据" + methodName + "查询对象");
                    sb.append("\n");
                   sb.append("\t" + beanName + " get" + beanName + "By" + methodName.toString() + "("
                            + paramStr.toString() + ");");
                    sb.append("\n");
                    sb.append("\n");

                    //根据主键方法
                    CommentUtils.buildMethodComment(sb, "根据" + methodName + "修改");
                    sb.append("\n");
                    sb.append("\tInteger update" + beanName + "By" + methodName.toString() + "(" + beanName + " bean,"
                            + paramStr.toString() + ");");
                    sb.append("\n");
                    sb.append("\n");
                    //根据主键删除
                    CommentUtils.buildMethodComment(sb, "根据" + methodName + "删除");
                    sb.append("\n");
                    sb.append("\tInteger delete" + beanName + "By" + methodName.toString() + "(" + paramStr.toString() + ");");
                    sb.append("\n");
                    sb.append("\n");

                }
            }
        return sb.toString();
    }
   @TemplateMethod
    public String createServiceImplMethod(String beanName, Map<String, List<FieldInfo>> keyMap, List<FieldInfo> fieldInfoList) {
        StringBuilder sb = new StringBuilder();
        String paramMapper = StringTools.lowerCaseFirstLetter(beanName) + "Mapper";
        for (Map.Entry<String, List<FieldInfo>> entry : keyMap.entrySet()) {
                List<FieldInfo> keyColumnList = entry.getValue();
                StringBuffer paramStr = new StringBuffer();
                StringBuffer paramValueStr = new StringBuffer();
                StringBuffer methodName = new StringBuffer();
                int index = 0;
                for (FieldInfo column : keyColumnList) {
                    if (index > 0) {
                        paramStr.append(", ");
                        methodName.append("And");
                        paramValueStr.append(", ");
                    }
                    paramStr.append(column.getJavaType() + " " + column.getPropertyName());
                    paramValueStr.append(column.getPropertyName());
                    methodName.append(StringTools.upperCaseFirstLetter(column.getPropertyName()));
                    index++;
                }
                if (paramStr.length() > 0) {
                    //根据主键查询
                   CommentUtils.buildMethodComment(sb, "根据" + methodName + "获取对象");
                    sb.append("\n");
                    sb.append("\t@Override");
                    sb.append("\n");
                    sb.append("\tpublic " + beanName + " get" + beanName + "By" + methodName.toString() + "("
                            + paramStr.toString() + ") {");
                    sb.append("\n");
                    sb.append("\t\treturn this." + paramMapper + ".selectBy" + methodName.toString() + "(" + paramValueStr.toString() + ");");

                    sb.append("\n");
                    sb.append("\t}");
                    sb.append("\n");


                    //根据主键修改
                    CommentUtils.buildMethodComment(sb, "根据" + methodName + "修改");
                    sb.append("\n");
                    sb.append("\t@Override");
                    sb.append("\n");
                    sb.append("\tpublic Integer update" + beanName + "By" + methodName.toString() + "(" +beanName + " bean, "
                            + paramStr.toString() + ") {");
                    sb.append("\n");
                    sb.append("\t\treturn this." + paramMapper + ".updateBy" + methodName.toString() + "(bean, " + paramValueStr.toString() + ");");


                    sb.append("\n");
                    sb.append("\t}");
                    sb.append("\n");


                    //根据主键删除
                    CommentUtils.buildMethodComment(sb, "根据" + methodName + "删除");
                    sb.append("\n");
                    sb.append("\t@Override");
                    sb.append("\n");
                    sb.append("\tpublic Integer delete" + beanName + "By" + methodName.toString() + "(" + paramStr.toString() + ") {");
                    sb.append("\n");
                    sb.append("\t\treturn this." + paramMapper + ".deleteBy" + methodName.toString() + "(" + paramValueStr.toString() + ");");

                    sb.append("\n");
                    sb.append("\t}");
                    sb.append("\n");
                }
            }
        return sb.toString();
    }

}
