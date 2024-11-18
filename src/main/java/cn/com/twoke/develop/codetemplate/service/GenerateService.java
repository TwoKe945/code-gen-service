package cn.com.twoke.develop.codetemplate.service;

import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.IOException;
import java.net.URISyntaxException;

public interface GenerateService {

    void generateTemplate(String template, String table) throws ConfigurationException, URISyntaxException, IOException;


}
