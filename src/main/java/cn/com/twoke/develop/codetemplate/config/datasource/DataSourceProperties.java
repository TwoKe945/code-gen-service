package cn.com.twoke.develop.codetemplate.config.datasource;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "datasource")
public class DataSourceProperties {

    private String defaultUrl;
    private String defaultUsername;
    private String defaultPassword;

    // Getters and Setters
}