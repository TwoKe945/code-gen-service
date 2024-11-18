package cn.com.twoke.develop.codetemplate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
@SpringBootApplication
public class CodeTemplateApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodeTemplateApplication.class, args);
    }

}
