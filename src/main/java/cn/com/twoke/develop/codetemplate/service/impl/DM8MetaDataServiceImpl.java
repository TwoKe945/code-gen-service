package cn.com.twoke.develop.codetemplate.service.impl;

import cn.com.twoke.develop.codetemplate.enums.DatabaseId;
import cn.hutool.core.util.StrUtil;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


@Service("dM8MetaDataServiceImpl")
public class DM8MetaDataServiceImpl extends MetaDataServiceImpl{

    private static final String SQL_SHOW_TABLE_STATUS = "SELECT * FROM USER_TAB_COMMENTS utc WHERE utc.TABLE_TYPE = 'TABLE'";
    private static final String SQL_SHOW_COLUMNS_FROM = "SELECT\n" +
            "    T.COLUMN_NAME,\n" +
            "    C.COMMENTS,\n" +
            "    (select (CASE  WHEN UC.CONSTRAINT_NAME IS NOT NULL THEN 'YES' ELSE 'NO' END ) from USER_CONSTRAINTS UC\n" +
            "     left join USER_IND_COLUMNS UIC ON  UC.INDEX_NAME = UIC.INDEX_NAME AND UIC.TABLE_NAME = UC.TABLE_NAME\n" +
            "     WHERE UC.TABLE_NAME = '%s'  AND UIC.COLUMN_NAME =  T.COLUMN_NAME) AS IS_PK,\n" +
            "    T.DATA_TYPE\n" +
            "FROM\n" +
            "    USER_TAB_COLUMNS T\n" +
            "    LEFT join USER_COL_COMMENTS C on T.TABLE_NAME = C.TABLE_NAME AND T.COLUMN_NAME = C.COLUMN_NAME\n" +
            "WHERE  T.TABLE_NAME = '%s';";
    private static final String SQL_SHOW_INDEX_FROM = "SELECT\n" +
            "    IC.INDEX_NAME,\n" +
            "    IC.COLUMN_NAME,\n" +
            "    I.UNIQUENESS\n" +
            "FROM\n" +
            "    USER_IND_COLUMNS IC\n" +
            "        JOIN\n" +
            "    USER_INDEXES I ON IC.INDEX_NAME = I.INDEX_NAME\n" +
            "WHERE\n" +
            "    IC.TABLE_NAME = '%s'\n" +
            "ORDER BY\n" +
            "    IC.INDEX_NAME,\n" +
            "    IC.COLUMN_POSITION;";

    @Override
    protected PreparedStatement getTableStatement(Connection conn, String queryTableName) throws SQLException {
        if (StrUtil.isNotBlank(queryTableName)) {
            return conn.prepareStatement(SQL_SHOW_TABLE_STATUS + " AND utc.TABLE_NAME = ?");
        }
        return conn.prepareStatement(SQL_SHOW_TABLE_STATUS);
    }

    @Override
    protected PreparedStatement getFieldStatement(Connection conn, String tableName) throws SQLException {
        return conn.prepareStatement(String.format(SQL_SHOW_COLUMNS_FROM, tableName, tableName));
    }

    @Override
    protected PreparedStatement getIndexStatement(Connection conn, String tableName) throws SQLException {
        return conn.prepareStatement(String.format(SQL_SHOW_INDEX_FROM, tableName));
    }

    @Override
    protected String getTableName(ResultSet rs) throws SQLException {
        return rs.getString("TABLE_NAME");
    }

    @Override
    protected String getTableComment(ResultSet rs) throws SQLException {
        return  rs.getString("COMMENTS");
    }

    @Override
    protected String getFieldName(ResultSet rs) throws SQLException {
        return rs.getString("COLUMN_NAME");
    }

    @Override
    protected String getFieldType(ResultSet rs) throws SQLException {
        return rs.getString("DATA_TYPE");
    }

    @Override
    protected String getFieldComment(ResultSet rs) throws SQLException {
        return rs.getString("COMMENTS");
    }

    /**
     * TODO 未实现
     * @param rs
     * @return
     * @throws SQLException
     */
    @Override
    protected Boolean isAutoIncrement(ResultSet rs) throws SQLException {
        return false;
    }

    @Override
    protected Boolean isPrimaryKey(ResultSet rs) throws SQLException {
        System.out.println(rs.getString("COLUMN_NAME") + ":" + "YES".equals(rs.getString("IS_PK")));
        return "YES".equals(rs.getString("IS_PK"));
    }

    @Override
    protected String getIndexKeyName(ResultSet rs) throws SQLException {
        return rs.getString("INDEX_NAME");
    }

    @Override
    protected Boolean getIndexIsUnique(ResultSet rs) throws SQLException {
        return "UNIQUE".equals(rs.getString("UNIQUENESS"));
    }

    @Override
    protected String getIndexColumnName(ResultSet rs) throws SQLException {
        return rs.getString("COLUMN_NAME");
    }

    @Override
    public DatabaseId getId() {
        return DatabaseId.DM8;
    }
}
