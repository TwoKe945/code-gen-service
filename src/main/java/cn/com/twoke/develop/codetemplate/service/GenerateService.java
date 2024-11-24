package cn.com.twoke.develop.codetemplate.service;

import cn.com.twoke.develop.codetemplate.bean.TableInfo;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.IOException;
import java.net.URISyntaxException;

public interface GenerateService {

    void generateTemplate(String template, String table, TableInfo info) throws ConfigurationException, URISyntaxException, IOException;


}
