package ${package};

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import ${basePackage}.enums.PageSizeEnum;
<#list imports as import>
import ${import};
</#list>
import ${basePackage}.entity.vo.PaginationResultVO;
import ${basePackage}.entity.query.SimplePage;
import ${basePackage}.utils.StringUtil;

<#assign varBeanName=getBeanVarName(table.beanName), varMapperName=getBeanVarName(table.beanName)+"Mapper">

/**
 * ${table.comment!} 业务实现类
 */
@Service("${varBeanName}Service")
@RequiredArgsConstructor
public class ${table.beanName}ServiceImpl implements ${table.beanName}Service {

    private final ${table.beanName}Mapper<${table.beanName},${table.beanParamName}> ${varMapperName};

    /**
     * 根据条件查询列表
     */
    @Override
    public List<${table.beanName}> findListByParam(${table.beanParamName} param) {
        return this.${varMapperName}.selectList(param);
    }

    /**
     * 根据条件查询数量
     */
     @Override
     public Integer findCountByParam(${table.beanParamName} param) {
         return this.${varMapperName}.selectCount(param);
     }

    /**
     * 分页查询
     */
     @Override
    public PaginationResultVO<${table.beanName}> findListByPage(${table.beanParamName} param) {
         int count = this.findCountByParam(param);
         int pageSize = param.getPageSize() == null ? PageSizeEnum.SIZE15.getSize() : param.getPageSize();
         SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
         param.setSimplePage(page);
         List<${table.beanName}> list = this.findListByParam(param);
         PaginationResultVO<${table.beanName}> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
         return result;
   }

   /**
    * 新增
    */
    @Override
    public Integer add(${table.beanName} bean) {
        return this.${varMapperName}.insert(bean);
    }

   /**
    * 批量新增
    */
    @Override
    public Integer addBatch(List<${table.beanName}> listBean) {
       if (listBean == null || listBean.isEmpty()) {
          return 0;
        }
        return this.${varMapperName}.insertBatch(listBean);
    }

    /**
     * 批量新增/修改
     */
    @Override
    public Integer addOrUpdateBatch(List<${table.beanName}> listBean) {
        if (listBean == null || listBean.isEmpty()) {
           return 0;
        }
        return this.${varMapperName}.insertOrUpdateBatch(listBean);
    }

    /**
     * 多条件更新
     */
    @Override
    public Integer updateByParam(${table.beanName} bean, ${table.beanParamName} param) {
        StringUtil.checkParam(param);
        return this.${varMapperName}.updateByParam(bean, param);
    }

    /**
     * 多条件删除
     */
    @Override
    public Integer deleteByParam(${table.beanParamName} param) {
        StringUtil.checkParam(param);
        return this.${varMapperName}.deleteByParam(param);
    }

    ${createServiceImplMethod(table.beanName, table.keyIndexMap, table.fieldList)}

}