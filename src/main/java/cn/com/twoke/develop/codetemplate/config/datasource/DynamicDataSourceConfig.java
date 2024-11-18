package cn.com.twoke.develop.codetemplate.config.datasource;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DynamicDataSourceConfig {

    @Autowired
    private DataSourceProperties dataSourceProperties;

    @Bean
    public DynamicDataSource dynamicDataSource() {
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put("default", createDataSource(dataSourceProperties.getDefaultUrl(),
                dataSourceProperties.getDefaultUsername(), dataSourceProperties.getDefaultPassword()));
        return new DynamicDataSource(null, targetDataSources, "default");
    }

    // @Bean
    // public SqlSessionFactory sqlSessionFactory(DynamicDataSource dynamicDataSource) throws Exception {
    //     SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
    //     sessionFactory.setDataSource(dynamicDataSource);
    //     return sessionFactory.getObject();
    // }
    //
    // @Bean
    // public DataSourceTransactionManager transactionManager(DynamicDataSource dynamicDataSource) {
    //     return new DataSourceTransactionManager(dynamicDataSource);
    // }

    private DataSource createDataSource(String url, String username, String password) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        return dataSource;
    }
}
