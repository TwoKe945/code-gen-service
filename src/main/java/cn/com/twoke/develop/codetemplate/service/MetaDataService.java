package cn.com.twoke.develop.codetemplate.service;

import cn.com.twoke.develop.codetemplate.bean.TableInfo;

import java.util.List;

public interface MetaDataService {
    /**
     * 查询表数据
     * @param tableName
     * @return
     */
    TableInfo queryTable(String tableName);

    /**
     * 查询所有表携带字段信息
     * @return
     */
    List<TableInfo> queryTableWithColumns();

    /**
     * 查询所有表不携带字段信息
     * @return
     */
    List<TableInfo> queryTables();

}
