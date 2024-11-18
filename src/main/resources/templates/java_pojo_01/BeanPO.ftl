package ${package};

<#if IGNORE_BEAN_TO_JSON_CLASS??>
import ${IGNORE_BEAN_TO_JSON_CLASS};
</#if>

<#if table.haveBigDecimal?? && table.haveBigDecimal>
import java.math.BigDecimal;
</#if>
<#if table.haveDate?? && table.haveDate || table.haveDateTime?? && table.haveDateTime>
import java.util.Date;
import ${basePackage}.enums.DateTimePatternEnum;
import ${basePackage}.utils.DateUtil;
import ${BEAN_DATE_EXPRESSION_CLASS};
import ${BEAN_DATE_FORMAT_CLASS};
</#if>
import java.io.Serializable;

/**
 * ${table.comment!}
 */
public class ${table.beanName} implements Serializable {

<#-- 字段信息 -->
<#list table.fieldList! as field>
   /**
   * ${field.comment!}
   */
<#-- 忽略Json序列化-->
   <#if isJsonIgnoreField(field.fieldName)>
   ${IGNORE_BEAN_TO_JSON_EXPRESSION}
   </#if>
<#-- 时间格式化-->
   <#if isDateOrDateTime(field.sqlType)>
   ${getDatePattern(field.sqlType)}
   </#if>
   private ${field.javaType} ${field.propertyName};
</#list>

<#-- getter and setter -->
<#list table.fieldList! as field>
   public void set${toUpperCaseFirstLetter(field.propertyName)}(${field.javaType} ${field.propertyName}){
      this.${field.propertyName} = ${field.propertyName};
   }

   public ${field.javaType} get${toUpperCaseFirstLetter(field.propertyName)}(){
      return this.${field.propertyName};
   }

</#list>
   @Override
   public String toString() {
      return "${table.beanName}{" +
<#list table.fieldList! as field>
   <#if !isJsonIgnoreField(field.fieldName)>
      <#if isDateOrDateTime(field.sqlType, "date")>
       "${field.propertyName}=" + ( ${field.propertyName} == null ? "空" : DateUtil.format(${field.propertyName},DateTimePatternEnum.YYYY_MM_DD.getPattern())) +
      <#elseif isDateOrDateTime(field.sqlType, "datetime")>
       "${field.propertyName}=" + ( ${field.propertyName} == null ? "空" : DateUtil.format(${field.propertyName},DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern())) +
      <#else>
       "${field.propertyName}=" + (${field.propertyName} == null ? "空" : ${field.propertyName}) +
      </#if>
   </#if>
</#list>
         "}";
   }

}