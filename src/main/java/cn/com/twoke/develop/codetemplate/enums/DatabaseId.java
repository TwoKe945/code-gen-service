package cn.com.twoke.develop.codetemplate.enums;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum DatabaseId {
    MYSQL("mysql", "com.mysql.cj.jdbc.Driver"),
    DM8("dm8", "dm.jdbc.driver.DmDriver"),
    // ORACLE("oracle", "oracle.jdbc.OracleDriver"),
    // POSTGRESQL("postgresql", "org.postgresql.Driver"),
    // SQLSERVER("sqlserver", "com.microsoft.sqlserver.jdbc.SQLServerDriver"),
    // H2("h2", "org.h2.Driver"),
    // SQLITE("sqlite", "org.sqlite.JDBC"),
    // HSQLDB("hsqldb", "org.hsqldb.jdbc.JDBCDriver"),
    // DB2("db2", "com.ibm.db2.jcc.DB2Driver"),
    ;
    private String name;
    private String driverClassName;
}
