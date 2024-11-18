package ${package};

<#if table.haveBigDecimal?? && table.haveBigDecimal>
import java.math.BigDecimal;
</#if>
<#if table.haveDate?? && table.haveDate || table.haveDateTime?? && table.haveDateTime>
import java.util.Date;
</#if>

/**
 * ${table.comment!}
 */
public class ${table.beanName}Query extends BaseParam {

<#-- 字段信息 -->
<#list table.fieldList! as field>
    <#-- 字段信息-->
   <#if !isJavaDateType(field.javaType)>
   /**
   * ${field.comment!}
   */
   private ${field.javaType} ${field.propertyName};

   </#if>
    <#-- 模糊查询条件-->
   <#if isJavaStringType(field.javaType) && !isIgnoreFuzzyProperty(IGNORE_FUZZY_PROPERTY,field.propertyName)>
   /**
   * ${field.comment!}模糊查询
   */
   private ${field.javaType} ${field.propertyName}${SUFFIX_PROPERTY_FUZZY};

   </#if>
   <#if isJavaDateType(field.javaType)>
   /**
    * ${field.comment!}
    */
   private ${TYPE_STRING} ${field.propertyName};

   /**
    * ${field.comment!}开始时间
    */
   private ${TYPE_STRING} ${field.propertyName}${SUFFIX_BEAN_PARAM_TIME_START};

   /**
    * ${field.comment!}结束时间
    */
   private ${TYPE_STRING} ${field.propertyName}${SUFFIX_BEAN_PARAM_TIME_END};

   </#if>
</#list>

<#-- getter and setter -->
<#list table.fieldList! as field>
   <#assign  tempName = toUpperCaseFirstLetter(field.propertyName)>
   <#if !isJavaDateType(field.javaType)>
   public void set${tempName}(${field.javaType} ${field.propertyName}){
      this.${field.propertyName} = ${field.propertyName};
   }

   public ${field.javaType} get${tempName}(){
      return this.${field.propertyName};
   }

<#--   模糊查询-->
   <#else>
   public void set${tempName}(${TYPE_STRING} ${field.propertyName}){
      this.${field.propertyName} = ${field.propertyName};
   }

   public ${TYPE_STRING} get${tempName}(){
      return this.${field.propertyName};
   }

   </#if>
   <#if isJavaStringType(field.javaType) && !isIgnoreFuzzyProperty(IGNORE_FUZZY_PROPERTY,field.propertyName)>
   public void set${tempName}${SUFFIX_PROPERTY_FUZZY}(${field.javaType} ${field.propertyName}${SUFFIX_PROPERTY_FUZZY}){
      this.${field.propertyName}${SUFFIX_PROPERTY_FUZZY} = ${field.propertyName}${SUFFIX_PROPERTY_FUZZY};
   }

   public ${field.javaType} get${tempName}${SUFFIX_PROPERTY_FUZZY}(){
      return this.${field.propertyName}${SUFFIX_PROPERTY_FUZZY};
   }
   </#if>
    <#if isJavaDateType(field.javaType)>
   public void set${tempName}${SUFFIX_BEAN_PARAM_TIME_START}(${TYPE_STRING} ${field.propertyName}${SUFFIX_BEAN_PARAM_TIME_START}){
      this.${field.propertyName}${SUFFIX_BEAN_PARAM_TIME_START} = ${field.propertyName}${SUFFIX_BEAN_PARAM_TIME_START};
   }

   public ${TYPE_STRING} get${tempName}${SUFFIX_BEAN_PARAM_TIME_START}(){
      return this.${field.propertyName}${SUFFIX_BEAN_PARAM_TIME_START};
   }

   public void set${tempName}${SUFFIX_BEAN_PARAM_TIME_END}(${TYPE_STRING} ${field.propertyName}${SUFFIX_BEAN_PARAM_TIME_END}){
      this.${field.propertyName}${SUFFIX_BEAN_PARAM_TIME_END} = ${field.propertyName}${SUFFIX_BEAN_PARAM_TIME_END};
   }

   public ${TYPE_STRING} get${tempName}${SUFFIX_BEAN_PARAM_TIME_END}(){
      return this.${field.propertyName}${SUFFIX_BEAN_PARAM_TIME_END};
   }
   </#if>

</#list>

}