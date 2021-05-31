package com.bupt.cad.metadatahub.dao;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: yuejie
 * @Date: 2021/04/13/17:09
 * @Description:
 */
public interface MetadataMapper {

    String getSchemaCreateTime(String schema);

    String getSchemaUpdateTime(String schema);

    String getTables(String schema);

}
