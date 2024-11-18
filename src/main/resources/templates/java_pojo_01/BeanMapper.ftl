package ${package};

import org.apache.ibatis.annotations.Param;

/**
 * ${table.comment!} Mapper
 */
public interface ${table.beanName}Mapper<T,P> extends BaseMapper<T,P> {

    <#list table.keyIndexMap as key, value>${createMapperMethod(value)}
    </#list>
}