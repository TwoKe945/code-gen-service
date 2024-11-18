package cn.com.twoke.develop.codetemplate.context;

import cn.com.twoke.develop.codetemplate.bean.ConfigContext;

public class ConfigContextHolder {

    private static ThreadLocal<ConfigContext> outputConfigs = new ThreadLocal<>();

    public static ConfigContext get() {
        return outputConfigs.get();
    }

    public static void set(ConfigContext outputConfig) {
        outputConfigs.set(outputConfig);
    }

    public static void remove() {
        outputConfigs.remove();
    }

}
