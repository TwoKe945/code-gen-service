package ${package};

import java.util.List;
import lombok.RequiredArgsConstructor;

<#list imports as import>
import ${import};
</#list>

import ${basePackage}.entity.vo.ResponseVO;
import ${basePackage}.entity.query.${table.beanParamName};
import ${basePackage}.entity.po.${table.beanName};

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

<#assign varBeanName=getBeanVarName(table.beanName), serviceBeanName=getBeanVarName(table.beanName)+"Service">

/**
 * ${table.comment!} Controller
 */
@RestController("${varBeanName}Controller")
@RequiredArgsConstructor
@RequestMapping("/${varBeanName}")
public class ${table.beanName}Controller extends ABaseController {

    private final ${table.beanName}Service ${serviceBeanName};

    /**
     * 分页查询
     */
    @GetMapping("loadDataList")
    public ResponseVO loadDataList(${table.beanParamName} param) {
        return success(this.${serviceBeanName}.findListByPage(param));
    }

    /**
     * 新增
     */
    @PostMapping("add")
    public ResponseVO add(${table.beanName} bean) {
        return success(this.${serviceBeanName}.add(bean));
    }

    /**
     * 批量新增
     */
    @PostMapping("addBatch")
    public ResponseVO addBatch(@RequestBody List<${table.beanName}> listBean) {
        this.${serviceBeanName}.addBatch(listBean);
        return success(null);
    }

    /**
     * 批量新增
     */
    @PostMapping("addOrUpdateBatch")
    public ResponseVO addOrUpdateBatch(@RequestBody List<${table.beanName}> listBean) {
        this.${serviceBeanName}.addOrUpdateBatch(listBean);
        return success(null);
    }

    <#list table.keyIndexMap as key, value>
        <#assign paramStr="", paramNameStr="", methodName="">
        <#list value as field>
            <#if field?index gt 0 >
                <#assign paramStr=paramStr+", ", paramNameStr=paramNameStr+", ",  methodName=methodName+"And">
            </#if>
            <#assign paramStr=paramStr+field.javaType+" "+field.propertyName,
            methodName=methodName+field.propertyName?cap_first,
            paramNameStr=paramNameStr+field.propertyName>
        </#list>
        <#if paramStr?length gt 0>
    <#assign methodNameStr="get" + table.beanName + "By" + methodName>
    /**
     * 根据${methodName}查询
     */
    @GetMapping("${methodNameStr}")
    public ResponseVO ${methodNameStr}(${paramStr}) {
        return this.success(this.${serviceBeanName}.${methodNameStr}(${paramNameStr}));
    }

    <#assign methodNameStr="update" + table.beanName + "By" + methodName>
    /**
     * 根据${methodName}修改
     */
    @PostMapping("${methodNameStr}")
    public ResponseVO ${methodNameStr}(${table.beanName} bean,${paramStr}) {
        this.${serviceBeanName}.${methodNameStr}(bean, ${paramNameStr});
        return success(null);
    }

    <#assign methodNameStr="delete" + table.beanName + "By" + methodName>
    /**
     * 根据${methodName}删除
     */
    @PostMapping("${methodNameStr}")
    public ResponseVO ${methodNameStr}(${paramStr}) {
        this.${serviceBeanName}.${methodNameStr}(${paramNameStr});
        return success(null);
    }
        </#if>
    </#list>
}