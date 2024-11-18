package cn.com.twoke.develop.codetemplate.service.impl;

import cn.com.twoke.develop.codetemplate.bean.Constants;
import cn.com.twoke.develop.codetemplate.bean.FieldInfo;
import cn.com.twoke.develop.codetemplate.bean.TableInfo;
import cn.com.twoke.develop.codetemplate.config.datasource.DynamicDataSource;
import cn.com.twoke.develop.codetemplate.service.MetaDataService;
import cn.com.twoke.develop.codetemplate.utils.StringTools;
import cn.hutool.core.util.ArrayUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public abstract class MetaDataServiceImpl implements MetaDataService {

    @Resource
    private DynamicDataSource dataSource;

    public TableInfo queryTable(String tableName) {
        List<TableInfo> tableInfos = queryTables(true, tableName);
        return tableInfos.get(0);
    }

    public List<TableInfo> queryTables() {
        return queryTables(false, null);
    }

    public List<TableInfo> queryTableWithColumns() {
        return queryTables(true, null);
    }

    public List<TableInfo> queryTables(Boolean withField, String queryTableName) {
        PreparedStatement ps =  null;
        ResultSet tableResults = null;
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            ps = getTableStatement(conn, queryTableName);
            if (StringUtils.isNotBlank(queryTableName)) {
                ps.setString(1, queryTableName);
            }
            tableResults = ps.executeQuery();
            List<TableInfo> tableInfoList = new ArrayList();
            //读取表
            while (tableResults.next()) {
                String tableName = getTableName(tableResults);
                String tableComment = getTableComment(tableResults);
                String beanName = tableName;
                if (Constants.IGNORE_TABLE_PREFIX) {
                    int index_prefix = tableName.indexOf("_");
                    if (index_prefix != -1) {
                        beanName = tableName.substring(index_prefix + 1);
                    }
                }
                beanName = processField(true, beanName);

                TableInfo tableInfo = new TableInfo();
                tableInfo.setTableName(tableName);
                tableInfo.setComment(tableComment);
                tableInfo.setBeanName(beanName);
                tableInfo.setBeanParamName(beanName + Constants.SUFFIX_BEAN_PARAM);
                log.info("表名：{}，表备注：{}，实体:{}", tableName, tableComment, beanName);
                if (withField) {
                    //获取字段信息
                    Map<String, FieldInfo> fieldInfoMap = new HashMap();
                    List<FieldInfo> fieldInfoList = readFieldInfo(conn, tableInfo, fieldInfoMap);
                    tableInfo.setFieldList(fieldInfoList);
                    //读取主键
                    getKeyIndexInfo(conn,tableInfo, fieldInfoMap);
                }
                tableInfoList.add(tableInfo);
            }
            return tableInfoList;
        } catch (Exception e) {
            log.error("获取数据库表失败", e);
            throw new RuntimeException("获取数据库表失败");
        } finally {
            if (null != tableResults) {
                try {
                    tableResults.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private static String processField(Boolean uperCaseFirstLetter, String field) {
        StringBuffer sb = new StringBuffer(field.length());
        String[] fields = field.toLowerCase().split("_");
        sb.append(uperCaseFirstLetter ? StringTools.upperCaseFirstLetter(fields[0]) : fields[0]);
        for (int i = 1, len = fields.length; i < len; i++) {
            sb.append(StringTools.upperCaseFirstLetter(fields[i]));
        }
        return sb.toString();
    }


   private List<FieldInfo> readFieldInfo(Connection conn,TableInfo tableInfo, Map<String, FieldInfo> fieldInfoMap) {
    PreparedStatement ps = null;
    ResultSet fieldResult = null;
    try {
        ps = getFieldStatement(conn, tableInfo.getTableName());
        fieldResult = ps.executeQuery();
        List<FieldInfo> filedInfoList = new ArrayList();
        while (fieldResult.next()) {
            String field = getFieldName(fieldResult);
            String type = getFieldType(fieldResult);
            Boolean isAutoIncrement = isAutoIncrement(fieldResult);
            String comment = getFieldComment(fieldResult);

            String propertyName = processField(false, field);
            String javaType = processFieldType(type);

            //判断是否date类型
            if (isDate(type)) {
                tableInfo.setHaveDate(true);
            }

            //判断是否有datetime类型
            if (isDateTimestamp(type)) {
                tableInfo.setHaveDateTime(true);
            }
            //判断是否有BigDecimal类型
            if (isBigDecimal(type)) {
                tableInfo.setHaveBigDecimal(true);
            }

            FieldInfo fieldInfo = new FieldInfo();
            fieldInfo.setFieldName(field);
            fieldInfo.setPropertyName(propertyName);
            fieldInfo.setSqlType(type);
            fieldInfo.setJavaType(javaType);
            fieldInfo.setComment(comment);
            if (isAutoIncrement) {
                fieldInfo.setAutoIncrement(true);
            } else {
                fieldInfo.setAutoIncrement(false);
            }
            log.info("字段名:{},类型:{}，扩展:{}，备注:{}，Java类型:{},Jave属性名:{}", field, type, isAutoIncrement , comment, javaType, propertyName);
            filedInfoList.add(fieldInfo);
            fieldInfoMap.put(field, fieldInfo);
        }
        return filedInfoList;
    } catch (Exception e) {
        log.error("读取表属性失败", e);
        throw new RuntimeException("读取表属性失败" + tableInfo.getTableName(), e);
    } finally {
        if (fieldResult != null) {
            try {
                fieldResult.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

    protected static String processFieldType(String type) {
        if (ArrayUtil.contains(Constants.SQL_INTEGER_TYPE, type)) {
            return "Integer";
        } else if (ArrayUtil.contains(Constants.SQL_LONG_TYPE, type)) {
            return "Long";
        } else if (ArrayUtil.contains(Constants.SQL_STRING_TYPE, type)) {
            return "String";
        } else if (ArrayUtil.contains(Constants.SQL_DATE_TIIME_TYPES, type) || ArrayUtil.contains(Constants.SQL_DATE_TYPES, type)) {
            return "Date";
        } else if (ArrayUtil.contains(Constants.SQL_DECIMAL_TYPE, type)) {
            return "BigDecimal";
        } else {
            throw new RuntimeException("无法识别的类型:" + type);
        }
    }

    /**
     * 判断是否是Date类型
     * @param type
     * @return
     */
    protected boolean isDate(String type) {
        return ArrayUtil.contains(Constants.SQL_DATE_TYPES, type);
    }

    /**
     * 判断是否是DateTime类型
     * @param type
     * @return
     */
    protected boolean isDateTimestamp(String type) {
        return ArrayUtil.contains(Constants.SQL_DATE_TIIME_TYPES, type);
    }

    /**
     * 判断是否是BigDecimal类型
     * @param type
     * @return
     */
    protected boolean isBigDecimal(String type) {
        return ArrayUtil.contains(Constants.SQL_DECIMAL_TYPE, type);
    }

    /**
     * 获取表预执行语句
     * @param conn
     * @return
     */
    protected abstract PreparedStatement getTableStatement(Connection conn, String getTableStatement) throws SQLException;

    /**
     * 获取字段预执行语句
     * @param conn
     * @param tableName
     * @return
     */
    protected abstract PreparedStatement getFieldStatement(Connection conn, String tableName) throws SQLException;
    protected abstract PreparedStatement getIndexStatement(Connection conn, String tableName) throws SQLException;

    /**
     * 获取表备注
     * @param rs
     * @return
     */
    protected abstract String getTableName(ResultSet rs) throws SQLException;

    /**
     * 获取表备注
     * @param rs
     * @return
     */
    protected abstract String getTableComment(ResultSet rs) throws SQLException ;

    /**
     * 获取字段名字
     * @param rs
     * @return
     */
    protected abstract String getFieldName(ResultSet rs) throws SQLException ;

    /**
     * 获取字段类型
     * @param rs
     * @return
     */
    protected abstract String getFieldType(ResultSet rs) throws SQLException ;

    /**
     * 获取字段备注
     * @param rs
     * @return
     */
    protected abstract String getFieldComment(ResultSet rs) throws SQLException ;

    /**
     * 是否为自增字段
     * @param rs
     * @return
     */
    protected abstract Boolean isAutoIncrement(ResultSet rs) throws SQLException ;

    protected abstract String getIndexKeyName(ResultSet rs) throws SQLException ;
    protected abstract Boolean getIndexIsUnique(ResultSet rs) throws SQLException ;
    protected abstract String getIndexColumnName(ResultSet rs) throws SQLException ;

    private void getKeyIndexInfo(Connection conn,TableInfo tableInfo, Map<String, FieldInfo> fieldInfoMap) {
        PreparedStatement ps = null;
        ResultSet results = null;
        try {
            ps = getIndexStatement(conn, tableInfo.getTableName());
            results = ps.executeQuery();
            //获取表信息
            while (results.next()) {
                String keyName = getIndexKeyName(results);
                Boolean isUnique = getIndexIsUnique(results);
                String columnName = getIndexColumnName(results);
                if (isUnique) {//unique  唯一索引
                    List<FieldInfo> keyColumnList = tableInfo.getKeyIndexMap().get(keyName);
                    if (null == keyColumnList) {
                        keyColumnList = new ArrayList();
                        tableInfo.getKeyIndexMap().put(keyName, keyColumnList);
                    }
                    keyColumnList.add(fieldInfoMap.get(columnName));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != results) {
                try {
                    results.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
