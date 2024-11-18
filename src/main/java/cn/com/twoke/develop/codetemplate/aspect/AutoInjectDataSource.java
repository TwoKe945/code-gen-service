package cn.com.twoke.develop.codetemplate.aspect;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoInjectDataSource {
    /**
     * 是否启动设置数据源
     * @return
     */
    boolean enable() default true;

    /**
     * 是否启用配置上下文
     * @return
     */
    boolean config() default false;

}
