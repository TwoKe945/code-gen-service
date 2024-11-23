<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${basePackage}.mappers.${table.beanName}Mapper">
    <#assign  BASE_RESULT_MAP="base_result_map">
    <#assign  BASE_COLUMN_LIST="base_column_list">
    <#assign  BASE_CONDITION="base_condition">
    <#assign  QUERY_CONDITION="query_condition">
    <#assign  BASE_CONDITION_FIELD="base_condition_filed">
    <!--实体映射-->
    <resultMap id="${BASE_RESULT_MAP}" type="${basePackage}.entity.po.${table.beanName}">
        <#list table.fieldList as field>
        <!-- ${field.comment} -->
         <#if field.isAutoIncrement?? && field.isAutoIncrement>
        <id column="${field.fieldName}"  property="${field.propertyName}"/>
         <#else>
        <result column="${field.fieldName}" property="${field.propertyName}"/>
         </#if>
        </#list>
    </resultMap>

    <#assign  tableAlias=getTableAliasName(table.tableName)>
    <!-- 通用查询结果列-->
    <sql id="${BASE_COLUMN_LIST}">
        <#list table.fieldList as field>
        ${tableAlias}.${field.fieldName}<#if field?has_next>,</#if>
        </#list>
    </sql>

    <sql id="${BASE_CONDITION_FIELD}">
        <#list table.fieldList as field>
         <if test="query.${field.propertyName}!=null<#if isJavaStringType(field.javaType) || isJavaDateType(field.javaType)> and query.${field.propertyName}!=''</#if>">
             <#if isJavaDateType(field.javaType)>
                 <![CDATA[ and ${tableAlias}.${field.fieldName}=str_to_date(<#noparse>#{</#noparse>query.${field.propertyName}<#noparse>}</#noparse>, '%Y-%m-%d') ]]>
             <#else>
                 and ${tableAlias}.${field.fieldName}=<#noparse>#{</#noparse>query.${field.propertyName}<#noparse>}</#noparse>
             </#if>
         </if>
        </#list>
    </sql>

    <!-- 通用条件列-->
    <sql id="${BASE_CONDITION}">
       <where>
           <include refid="${BASE_CONDITION_FIELD}"/>
       </where>
    </sql>

    <!-- 通用查询条件列-->
    <sql id="${QUERY_CONDITION}">
        <where>
            <include refid="${BASE_CONDITION_FIELD}"/>
            <#list table.fieldList as field>
             <#if isJavaStringType(field.javaType) && !isIgnoreFuzzyProperty(IGNORE_FUZZY_PROPERTY, field.propertyName)>
                 <if test="query.${field.propertyName}${SUFFIX_PROPERTY_FUZZY}!=null and query.${field.propertyName}${SUFFIX_PROPERTY_FUZZY}!=''">
                     and ${tableAlias}.${field.fieldName} like concat('%',<#noparse>#{</#noparse>query.${field.propertyName}${SUFFIX_PROPERTY_FUZZY}<#noparse>}</#noparse>, '%')
                 </if>
             <#elseif isJavaDateType(field.javaType)>
                 <#assign start = field.propertyName + SUFFIX_BEAN_PARAM_TIME_START, end = field.propertyName + SUFFIX_BEAN_PARAM_TIME_END>
                 <if test="query.${start}!=null and query.${start}!=''">
                     <![CDATA[ and ${tableAlias}.${field.fieldName} >= str_to_date(<#noparse>#{</#noparse>query.${start}<#noparse>}</#noparse>, '%Y-%m-%d') ]]>
                 </if>
                 <if test="query.${end}!=null and query.${end}!=''">
                     <![CDATA[ and ${tableAlias}.${field.fieldName} < date_sub(str_to_date(<#noparse>#{</#noparse>query.${end}<#noparse>}</#noparse>, '%Y-%m-%d'), interval -1 day)  ]]>
                 </if>
             </#if>
            </#list>
        </where>
    </sql>

    <!-- 查询集合-->
    <select id="selectList" resultMap="${BASE_RESULT_MAP}">
        SELECT
        <include refid="${BASE_COLUMN_LIST}"/>
        FROM ${table.tableName} ${tableAlias}
        <include refid="${QUERY_CONDITION}"/>
        <if test="query.orderBy!=null and query.orderBy!=''">
            order by <#noparse>${</#noparse>query.orderBy<#noparse>}</#noparse>
        </if>
        <if test="query.simplePage!=null">
            limit <#noparse>#{</#noparse>query.simplePage.start<#noparse>}</#noparse>,<#noparse>#{</#noparse>query.simplePage.end<#noparse>}</#noparse>
        </if>
    </select>

    <!-- 查询数量-->
    <select id="selectCount" resultType="java.lang.Integer">
        SELECT count(1)
        FROM ${table.tableName} ${tableAlias}
        <include refid="${QUERY_CONDITION}"/>
    </select>

    <#assign autoIncrementColumn=getAutoIncrementColumn(table.fieldList)>
    <!-- 插入 （匹配有值的字段）-->
    <insert id="insert" parameterType="${basePackage}.entity.po.${table.beanName}">
        <#if autoIncrementColumn.column??>
            <selectKey keyProperty="bean.${autoIncrementColumn.column.propertyName}" resultType="${autoIncrementColumn.column.javaType}" order="AFTER">
                SELECT LAST_INSERT_ID()
            </selectKey>
        </#if>
        INSERT INTO ${table.tableName}
        <trim prefix="(" suffix=")" suffixOverrides=",">
            <#list table.fieldList as field>
              <#if field.isAutoIncrement == false>
                <if test="bean.${field.propertyName}!=null">
                    ${field.fieldName},
                </if>
              </#if>
            </#list>
        </trim>
        <trim prefix="VALUES (" suffix=")" suffixOverrides=",">
            <#list table.fieldList as field>
              <#if  field.isAutoIncrement == false>
            <if test="bean.${field.propertyName}!=null">
                <#noparse>#{</#noparse>bean.${field.propertyName}<#noparse>}</#noparse>,
            </if>
                </#if>
            </#list>
        </trim>
    </insert>

    <!-- 插入或者更新 （匹配有值的字段）-->
    <insert id="insertOrUpdate" parameterType="${basePackage}.entity.po.${table.beanName}">
        INSERT INTO ${table.tableName}
        <trim prefix="(" suffix=")" suffixOverrides=",">
            <#list table.fieldList as field>
              <#if field.isAutoIncrement == false>
                <if test="bean.${field.propertyName}!=null">
                    ${field.fieldName},
                </if>
              </#if>
            </#list>
        </trim>
        <trim prefix="VALUES (" suffix=")" suffixOverrides=",">
            <#list table.fieldList as field>
                 <#if field.isAutoIncrement == false>
                  <if test="bean.${field.propertyName}!=null">
                    <#noparse>#{</#noparse>bean.${field.propertyName}<#noparse>}</#noparse>,
                    </if>
                </#if>
            </#list>
        </trim>
         on DUPLICATE key update
         <trim prefix="" suffix="" suffixOverrides=",">
            <#list table.fieldList as field>
              <#if field.isAutoIncrement == false>
            <if test="bean.${field.propertyName}!=null">
                ${field.fieldName} = VALUES(${field.fieldName}),
            </if>
                </#if>
            </#list>
        </trim>
    </insert>

    <!-- 添加 （批量插入）-->
    <insert id="insertBatch" parameterType="${basePackage}.entity.po.${table.beanName}" <#if autoIncrementColumn.column??>useGeneratedKeys="true" keyProperty="${autoIncrementColumn.column.propertyName}"</#if>>
        INSERT INTO ${table.tableName}
            (<#list table.fieldList as field>
              <#if field.isAutoIncrement == false>${field.fieldName}<#if field?has_next>,</#if></#if></#list>
            )
        VALUES
        <foreach collection="list" item="item" separator=",">
            (<#list table.fieldList as field>
              <#if field.isAutoIncrement == false><#noparse>#{</#noparse>item.${field.propertyName}<#noparse>}</#noparse><#if field?has_next>,</#if></#if></#list>
            )
        </foreach>
    </insert>

    <!-- 批量新增修改 （批量插入）-->
    <insert id="insertOrUpdateBatch" parameterType="${basePackage}.entity.po.${table.beanName}">
        INSERT INTO ${table.tableName}
            (<#list table.fieldList as field>
              <#if field.isAutoIncrement == false>${field.fieldName}<#if field?has_next>,</#if></#if></#list>
            )
        VALUES
        <foreach collection="list" item="item" separator=",">
            (<#list table.fieldList as field>
              <#if field.isAutoIncrement == false><#noparse>#{</#noparse>item.${field.propertyName}<#noparse>}</#noparse><#if field?has_next>,</#if></#if></#list>
            )
        </foreach>
        on DUPLICATE key update
        <#list table.fieldList as field>
          <#if field.isAutoIncrement == false>
                ${field.fieldName} = VALUES(${field.fieldName})<#if field?has_next>,</#if>
            </#if>
        </#list>
    </insert>

    <!--多条件修改-->
    <update id="updateByParam" parameterType="${basePackage}.entity.query.${table.beanParamName}">
        UPDATE ${table.tableName} ${tableAlias}
        <set>
            <#list table.fieldList as field>
                <if test="bean.${field.propertyName}!=null">
                    ${field.fieldName} = <#noparse>#{</#noparse>bean.${field.propertyName}<#noparse>}</#noparse>,
                </if>
            </#list>
        </set>
        <include refid="${QUERY_CONDITION}"/>
    </update>

    <!--多条件删除-->
    <delete id="deleteByParam">
        DELETE ${tableAlias} FROM ${table.tableName} ${tableAlias}
        <include refid="${QUERY_CONDITION}"/>
    </delete>

    ${createIndexQuery(basePackage+".entity.query."+table.beanParamName,table.tableName,BASE_RESULT_MAP, BASE_COLUMN_LIST, tableAlias, table.keyIndexMap, table.fieldList)}

</mapper>