package ${package};

import java.util.List;

<#list imports as import>
import ${import};
</#list>
import ${basePackage}.entity.vo.PaginationResultVO;

/**
 * ${table.comment!} 业务接口
 */
public interface ${table.beanName}Service {

    /**
     * 根据条件查询列表
     */
    List<${table.beanName}> findListByParam(${table.beanParamName} param);

    /**
     * 根据条件查询数量
     */
    Integer findCountByParam(${table.beanParamName} param);

    /**
     * 分页查询
     */
    PaginationResultVO<${table.beanName}> findListByPage(${table.beanParamName} param);

    /**
     * 新增
     */
    Integer add(${table.beanName} bean);

    /**
     * 批量新增
     */
    Integer addBatch(List<${table.beanName}> listBean);

    /**
     * 批量新增/修改
     */
    Integer addOrUpdateBatch(List<${table.beanName}> listBean);

    /**
     * 多条件更新
     */
    Integer updateByParam(${table.beanName} bean, ${table.beanParamName} param);

    /**
     * 多条件删除
     */
    Integer deleteByParam(${table.beanParamName} param);

    ${createServiceMethod(table.beanName, table.keyIndexMap, table.fieldList)}
}