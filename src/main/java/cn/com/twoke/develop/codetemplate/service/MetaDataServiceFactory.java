package cn.com.twoke.develop.codetemplate.service;

import cn.com.twoke.develop.codetemplate.enums.DatabaseId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MetaDataServiceFactory {

    private final List<MetaDataService> metaDataServices;

    public MetaDataService getService(DatabaseId databaseId) {
        return metaDataServices.stream().filter(metaDataService -> metaDataService.getId().equals(databaseId)).findFirst().orElse(null);
    }



}
