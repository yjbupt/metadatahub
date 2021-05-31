package com.bupt.cad.metadatahub.model.datasource;

import com.bupt.cad.metadatahub.util.DataSourceUtil;
import lombok.Data;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Data
public class MySQLDataSource implements BaseDataSource {

    private Integer id;

    private String name;

    private String type;

    private String description;

    private MySQLDataSourceDetailInfo detailInfo;

    private String createTime;

    private String updateTime;


    @Override
    public boolean testConnection() {

        JdbcTemplate jdbcTemplate = DataSourceUtil.getJdbcTemplate(this);
        try {
            jdbcTemplate.queryForList("show databases");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public List<String> getTables() {

        JdbcTemplate jdbcTemplate = DataSourceUtil.getJdbcTemplate(this);
        MySQLDataSourceDetailInfo detailInfo = this.getDetailInfo();
        String sql = String.format("select table_name from information_schema.tables " +
                "where table_schema='%s' and table_type='BASE TABLE'", detailInfo.getDatabase());
        List<String> tables = jdbcTemplate.query(sql, new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet resultSet, int i) throws SQLException {
                return resultSet.getString(1);
            }
        });
        return tables;
    }

}
