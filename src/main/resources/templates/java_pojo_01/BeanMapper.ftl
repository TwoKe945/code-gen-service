package ${package};

import org.apache.ibatis.annotations.Param;

/**
 * ${table.comment!} Mapper
 */
public interface ${table.beanName}Mapper<#noparse><T,P></#noparse> extends BaseMapper<#noparse><T,P></#noparse> {

    <#list table.keyIndexMap as key, value>
        <#assign paramStr="", methodName="", index=0 >
        <#list value as column>
            <#if column?index gt 0 >
            <#assign paramStr=paramStr+", ",  methodName=methodName+"And">
            </#if>
            <#assign paramStr=paramStr+"@Param(\""+column.propertyName+"\") "+column.javaType+" "+column.propertyName,
            methodName=methodName+column.propertyName?cap_first >
        </#list>
        <#if paramStr?length gt 0>
    /**
     * 根据${methodName}修改
     */
    Integer updateBy${methodName}(@Param("bean") T bean,${paramStr});

    /**
     * 根据${methodName}删除
     */
    Integer deleteBy${methodName}(${paramStr});

    /**
     * 根据${methodName}获取对象
     */
    T selectBy${methodName}(${paramStr});
        </#if>
    </#list>

}