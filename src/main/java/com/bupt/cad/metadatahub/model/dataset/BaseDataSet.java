package com.bupt.cad.metadatahub.model.dataset;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: yuejie
 * @Date: 2021/03/11/23:48
 * @Description:
 */
public interface BaseDataSet {

    String getCreateTime();

    void setCreateTime(String createTime);

    String getUpdateTime();

    void setUpdateTime(String UpdateTime);

    Object getFieldsInfo();

    void setFieldsInfo(Map<String,String> fields);

    String getType();

    void setType(String type);

    String getSchemaName();

    void setSchemaName();



}
