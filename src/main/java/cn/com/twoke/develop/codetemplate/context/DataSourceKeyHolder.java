package cn.com.twoke.develop.codetemplate.context;

import java.util.LinkedHashSet;
import java.util.Set;

public abstract class DataSourceKeyHolder {

    public static Set<String> dataSourceKeys = new LinkedHashSet<>();

    public static void addDataSourceKey(String key) {
        synchronized(DataSourceKeyHolder.class) {
           if (!dataSourceKeys.contains(key)) {
              dataSourceKeys.add(key);
           }
        }
    }

    public static  boolean containsKey(String key) {
        return dataSourceKeys.contains(key);
    }

    public static void removeDataSourceKey(String key) {
        synchronized(DataSourceKeyHolder.class) {
            if (dataSourceKeys.contains(key)) {
                 dataSourceKeys.remove(key);
            }
        }
    }

}
