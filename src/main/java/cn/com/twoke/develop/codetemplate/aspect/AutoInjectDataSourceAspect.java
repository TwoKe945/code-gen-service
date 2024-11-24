package cn.com.twoke.develop.codetemplate.aspect;
import cn.com.twoke.develop.codetemplate.bean.ConfigContext;
import cn.com.twoke.develop.codetemplate.config.datasource.DataSourceContextHolder;
import cn.com.twoke.develop.codetemplate.config.datasource.DynamicDataSource;
import cn.com.twoke.develop.codetemplate.context.DataSourceKeyHolder;
import cn.com.twoke.develop.codetemplate.context.ConfigContextHolder;
import cn.com.twoke.develop.codetemplate.context.MetaDataServiceHolder;
import cn.com.twoke.develop.codetemplate.enums.DatabaseId;
import cn.com.twoke.develop.codetemplate.service.MetaDataService;
import cn.com.twoke.develop.codetemplate.service.MetaDataServiceFactory;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.MD5;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;

/**
 * 自动设置数据源
 */
@Aspect
@Component("autoDataSourceAspect")
@RequiredArgsConstructor
public class AutoInjectDataSourceAspect {
    private final DynamicDataSource dynamicDataSource;
    private final MetaDataServiceFactory metaDataServiceFactory;

    @Pointcut("@annotation(cn.com.twoke.develop.codetemplate.aspect.AutoInjectDataSource)")
    public void  pointcut() {}


    @Before("pointcut()")
    public void onBefore(JoinPoint joinPoint) throws NoSuchMethodException {
        Method method = getMethod(joinPoint);
        AutoInjectDataSource autoDataSource = method.getAnnotation(AutoInjectDataSource.class);
        if (Objects.nonNull(autoDataSource)) {
            setDataSource(autoDataSource);
        }
    }

    @After("pointcut()")
    public void onAfter() throws NoSuchMethodException {
        cleanThreadLocal();
    }

    /**
     * 清除当前线程的OutputConfigContext
     *
     * 使用此方法的主要目的是避免线程之间的数据污染
     * 在某些情况下，如在多线程环境下执行任务时，可能会使用到OutputConfigContext来存储线程本地变量
     * 任务完成后，应当调用此方法清除线程本地变量，以防止内存泄漏或数据不一致的问题
     */
    private void cleanThreadLocal() {
        // 清除线程变量
        ConfigContextHolder.remove();
        MetaDataServiceHolder.remove();
    }


    private void setDataSource(AutoInjectDataSource autoDataSource) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        if (autoDataSource.enable()) {
            String url = request.getHeader("url");
            String username = request.getHeader("username");
            String password = request.getHeader("password");
            String driverType = Optional.ofNullable(request.getHeader("type")).orElse(DatabaseId.MYSQL.name());
            if (StrUtil.isBlank(url) || StrUtil.isBlank(username) || StrUtil.isBlank(password)) {
                throw new RuntimeException("参数缺失");
            }
            DatabaseId databaseId = getDatabaseId(driverType);
            MetaDataServiceHolder.set(metaDataServiceFactory.getService(databaseId));
            // 设置数据源
            setDataSource(url, username, password, databaseId.getDriverClassName());

        }
        if (autoDataSource.config()) {
            String basePackage = request.getHeader("package");
            String cwd = request.getHeader("cwd");
            String driverType = Optional.ofNullable(request.getHeader("type")).orElse("mysql");
            DatabaseId databaseType = DatabaseId.valueOf(driverType.toUpperCase());

            if (StrUtil.isBlank(basePackage) || StrUtil.isBlank(cwd)) {
                throw new RuntimeException("参数缺失");
            }
            ConfigContextHolder.set(ConfigContext.builder()
                            .basePackage(basePackage)
                            .databaseId(databaseType)
                            .cwdPath(cwd)
                    .build());
        }
    }


    private static Method getMethod(JoinPoint joinPoint) throws NoSuchMethodException {
        Object target = joinPoint.getTarget();
        String methodName = joinPoint.getSignature().getName();
        Class<?>[] parameterTypes = ((MethodSignature) joinPoint.getSignature()).getParameterTypes();
        return target.getClass().getMethod(methodName, parameterTypes);
    }

    private void setDataSource(String url, String username, String password, String driverClassName) {
        String dataSourceKey = generateDataSourceKey(url, username, password);
        if (!DataSourceKeyHolder.containsKey(dataSourceKey)) {
            DataSource dataSource = createDataSource(url, username, password, driverClassName);
            DataSourceKeyHolder.addDataSourceKey(dataSourceKey);
            dynamicDataSource.addDataSource(dataSourceKey, dataSource);
            DataSourceContextHolder.setDataSourceKey(dataSourceKey);
        }
        DataSourceContextHolder.setDataSourceKey(dataSourceKey);
    }


    private String generateDataSourceKey(String url, String username, String password) {
         return MD5.create().digestHex16(url + username + password);
    }

     private DatabaseId getDatabaseId(String type) {
        try {
            DatabaseId databaseType = DatabaseId.valueOf(type.toUpperCase());
            return databaseType;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid database type: " + type);
        }
    }

     private DataSource createDataSource(String url, String username, String password, String driverClassName) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName(driverClassName);
        return dataSource;
    }
}
