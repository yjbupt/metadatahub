package com.bupt.cad.metadatahub.model.datasource;

import com.bupt.cad.metadatahub.model.po.DataSource;

import java.util.List;

public interface BaseDataSource {

    Integer getId();

    void setId(Integer id);

    String getName();

    void setName(String name);

    String getType();

    void setType(String type);

    String getDescription();

    void setDescription(String description);

    Object getDetailInfo();

    String getCreateTime();

    void setCreateTime(String createTime);

    String getUpdateTime();

    void setUpdateTime(String updateTime);

    boolean testConnection();

    List<String> getTables();


}
