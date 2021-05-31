package com.bupt.cad.metadatahub.service.impl;

import com.alibaba.fastjson.JSON;
import com.bupt.cad.metadatahub.model.dataset.DataSetFields;
import com.bupt.cad.metadatahub.model.datasource.BaseDataSource;
import com.bupt.cad.metadatahub.model.datasource.MySQLDataSourceDetailInfo;
import com.bupt.cad.metadatahub.model.po.DataSource;
import com.bupt.cad.metadatahub.service.DataSourceService;
import com.bupt.cad.metadatahub.service.MetadataCollectService;
import com.bupt.cad.metadatahub.util.DataSourceUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: yuejie
 * @Date: 2021/04/13/10:16
 * @Description:
 */
public class MetaDataCollectServiceImpl implements MetadataCollectService {
    @Autowired
    private DataSourceService dataSourceService;

    @Override
    public Map collectMetadata(String id) throws SQLException {
        DataSource dataSource = dataSourceService.getById(id);
        Map<String, List<DataSetFields>> fields = new LinkedHashMap<>();
        //获取数据库信息的detail信息
        if(dataSource.getType() .equals("MySQL")){
            MySQLDataSourceDetailInfo mySQLDataSourceDetailInfo = null;
            Connection connection = null;
            try {
                mySQLDataSourceDetailInfo = JSON.parseObject(dataSource.getDetailInfo(),MySQLDataSourceDetailInfo.class);
                Class.forName("com.mysql.jdbc.Driver");
                connection = DriverManager.getConnection(mySQLDataSourceDetailInfo.getUrl(),mySQLDataSourceDetailInfo.getUsername(),mySQLDataSourceDetailInfo.getPassword());
            } catch (Exception e) {
                e.printStackTrace();
            }
            //获取获取schema中的tables
            String[] types = {"TABLE"};
            ResultSet rs = connection.getMetaData().getTables(null,mySQLDataSourceDetailInfo.getDatabase(),"%",types);
            //获取字段信息
            while (rs.next()){
                String tableName = rs.getString("TABLE_NAME");
                List<DataSetFields> dataSetFields = collectTableFields(connection,mySQLDataSourceDetailInfo.getDatabase(),tableName);
                fields.put(tableName,dataSetFields);
            }

        }
        return fields;
    }

    public List collectTableFields(Connection connection,String schema,String tableName){
        List<DataSetFields> dataSetFields = new ArrayList<>();
        try {
            ResultSet rs = connection.getMetaData().getColumns(null,schema,tableName,"%");
            while(rs.next()){
                String columnName = rs.getString("COLUMN_NAME");
                String type = rs.getString("TYPE_NAME");
                String description = rs.getString("REMARKS");
                DataSetFields setFields = new DataSetFields(columnName,type,description);
                dataSetFields.add(setFields);
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return dataSetFields;

    }
    public String collectSchema(DataSource dataSource,Connection connection,String schemaName){




        return null;
    }
}
