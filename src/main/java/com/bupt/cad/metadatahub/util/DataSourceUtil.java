package com.bupt.cad.metadatahub.util;

import com.alibaba.fastjson.JSON;
import com.bupt.cad.metadatahub.model.datasource.BaseDataSource;
import com.bupt.cad.metadatahub.model.datasource.MySQLDataSource;
import com.bupt.cad.metadatahub.model.datasource.MySQLDataSourceDetailInfo;
import com.bupt.cad.metadatahub.model.dto.DataSourceDTO;
import com.bupt.cad.metadatahub.model.po.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class DataSourceUtil {

    public static BaseDataSource convertToBaseDataSource(DataSource dataSource){

        String type = dataSource.getType();
        BaseDataSource baseDataSource = null;
        if("MySQL".equals(type)){
            baseDataSource = new MySQLDataSource();
            MySQLDataSourceDetailInfo mySQLDataSourceDetailInfo = JSON.parseObject(dataSource.getDetailInfo(),MySQLDataSourceDetailInfo.class);
            ((MySQLDataSource)baseDataSource).setDetailInfo(mySQLDataSourceDetailInfo);
        }else if("Hive".equals(type)){
         //todo
        }
        baseDataSource.setId(dataSource.getId());
        baseDataSource.setName(dataSource.getName());
        baseDataSource.setType(dataSource.getType());
        baseDataSource.setDescription(dataSource.getDescription());
        baseDataSource.setCreateTime(DateUtil.format(dataSource.getCreateTime()));
        baseDataSource.setUpdateTime(DateUtil.format(dataSource.getUpdateTime()));
        return baseDataSource;
    }

    public static DataSource dataSourceDTOConvert(DataSourceDTO dataSourceDTO){

        DataSource dataSource = new DataSource();
        dataSource.setId(dataSourceDTO.getId());
        dataSource.setName(dataSourceDTO.getName());
        dataSource.setDetailInfo(JSON.toJSONString(dataSourceDTO.getDetail_info()));
        dataSource.setType(dataSourceDTO.getType());
        dataSource.setDescription(dataSourceDTO.getDescription());
        return dataSource;

    }

    public static JdbcTemplate getJdbcTemplate(BaseDataSource dataSource){

        DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
        if(dataSource.getType().equals("MySQL")){
            MySQLDataSourceDetailInfo detailInfofo = (MySQLDataSourceDetailInfo) dataSource.getDetailInfo();
            driverManagerDataSource.setDriverClassName("com.mysql.jdbc.Driver");
            driverManagerDataSource.setUrl(detailInfofo.getUrl());
            driverManagerDataSource.setSchema(detailInfofo.getDatabase());
            driverManagerDataSource.setPassword(detailInfofo.getPassword());
            driverManagerDataSource.setUsername(detailInfofo.getUsername());
        }else if(dataSource.getType().equals("Hive")){
            //todo
        }
        JdbcTemplate jdbcTemplate = new JdbcTemplate(driverManagerDataSource);
        return jdbcTemplate;

    }
}
