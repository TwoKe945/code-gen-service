package cn.com.twoke.develop.codetemplate.context;

import cn.com.twoke.develop.codetemplate.service.MetaDataService;

public class MetaDataServiceHolder {

    private static ThreadLocal<MetaDataService> metaDataServiceThreadLocal = new ThreadLocal<>();

    public static MetaDataService get() {
        return metaDataServiceThreadLocal.get();
    }

    public static void set(MetaDataService metaDataService) {
        metaDataServiceThreadLocal.set(metaDataService);
    }

    public static void remove() {
        metaDataServiceThreadLocal.remove();
    }

}
