package cn.com.twoke.develop.codetemplate.template.methods.impl;

import cn.com.twoke.develop.codetemplate.template.annotation.TemplateMethod;
import cn.com.twoke.develop.codetemplate.utils.StringTools;

public class TableMethod {

    @TemplateMethod
    public String getTableAliasName(String beanName) {
        return beanName.substring(0, 1).toLowerCase();
    }

     @TemplateMethod
    public String getBeanVarName(String beanName) {
        return StringTools.lowerCaseFirstLetter(beanName);
    }


}
