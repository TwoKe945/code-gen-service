package cn.com.twoke.develop.codetemplate.service.impl;

import cn.hutool.core.util.StrUtil;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Service("mysqlMetaDataService")
public class MySQLMetaDataServiceImpl extends MetaDataServiceImpl {

    private static final String SQL_SHOW_TABLE_STATUS = "show table status";
    private static final String SQL_PREFIX_SHOW_FIELDS = "show full fields from ";
    private static final String KEY_AUTO_INCREMENT = "auto_increment";
    private static final String SQL_PREFIX_SELECT_INDEX = "show index from ";

    @Override
    protected PreparedStatement getTableStatement(Connection conn, String queryTableName) throws SQLException {
        if (StrUtil.isNotBlank(queryTableName)) {
            return conn.prepareStatement(SQL_SHOW_TABLE_STATUS + " where Name = ?");
        }
        return conn.prepareStatement(SQL_SHOW_TABLE_STATUS);
    }

    @Override
    protected PreparedStatement getFieldStatement(Connection conn, String tableName) throws SQLException {
        return conn.prepareStatement(SQL_PREFIX_SHOW_FIELDS + tableName);
    }

    @Override
    protected PreparedStatement getIndexStatement(Connection conn, String tableName) throws SQLException {
        return conn.prepareStatement(SQL_PREFIX_SELECT_INDEX + tableName);
    }

    @Override
    protected String getTableName(ResultSet rs) throws SQLException {
        return  rs.getString("name");
    }

    @Override
    protected String getTableComment(ResultSet rs) throws SQLException  {
        return rs.getString("comment");
    }

    @Override
    protected String getFieldName(ResultSet rs) throws SQLException  {
        return rs.getString("FIELD");
    }

    @Override
    protected String getFieldType(ResultSet rs) throws SQLException {
        String type = rs.getString("TYPE");
        if (type.indexOf("(") > 0) {
            type = type.substring(0, type.indexOf("("));
        }
        return type;
    }

    @Override
    protected String getFieldComment(ResultSet rs) throws SQLException {
        return rs.getString("COMMENT");
    }

    @Override
    protected Boolean isAutoIncrement(ResultSet rs) throws SQLException {
        String extra = rs.getString("EXTRA");
        return KEY_AUTO_INCREMENT.equalsIgnoreCase(extra);
    }

    @Override
    protected String getIndexKeyName(ResultSet rs) throws SQLException  {
        return rs.getString("KEY_NAME");
    }

    @Override
    protected Boolean getIndexIsUnique(ResultSet rs) throws SQLException {
        int nonUnique = rs.getInt("NON_UNIQUE");
        return nonUnique == 0; // 唯一索引
    }

    @Override
    protected String getIndexColumnName(ResultSet rs) throws SQLException  {
        return rs.getString("COLUMN_NAME");
    }
}
