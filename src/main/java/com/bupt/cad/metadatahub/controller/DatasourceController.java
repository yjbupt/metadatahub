package com.bupt.cad.metadatahub.controller;


import com.alibaba.fastjson.JSON;
import com.bupt.cad.metadatahub.common.PageResult;
import com.bupt.cad.metadatahub.common.RetResult;
import com.bupt.cad.metadatahub.model.datasource.BaseDataSource;
import com.bupt.cad.metadatahub.model.dto.DataSourceDTO;
import com.bupt.cad.metadatahub.model.po.DataSource;
import com.bupt.cad.metadatahub.model.vo.DataSourceVO;
import com.bupt.cad.metadatahub.service.DataSourceService;
import com.bupt.cad.metadatahub.util.DataSourceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author yuejie
 * @since 2021-03-04
 */
@RestController
public class DatasourceController {

    @Autowired
    private DataSourceService datasourceService;


    @GetMapping("/datasources")
    public RetResult getDataSources(Integer page, Integer pageSize){

        DataSource datasource = new DataSource();
        PageResult<DataSource> pageResult = datasourceService.getAllDataSources(page, pageSize);
        return new RetResult(0,pageResult);
    }

    @PostMapping("/datesources/connection")
    public RetResult testConnection(@RequestBody DataSourceDTO dataSourceDTO){

        DataSource dataSource = DataSourceUtil.dataSourceDTOConvert(dataSourceDTO);
        boolean res =datasourceService.testConnection(dataSource);
        if(res){
            return new RetResult(0,"测试成功");
        }else {
            return new RetResult(400,"测试失败");
        }
    }

    @PostMapping("/datasources/update")
    public RetResult updateDataSource(@RequestBody DataSourceDTO dataSourceDTO){

        DataSource dataSource = DataSourceUtil.dataSourceDTOConvert(dataSourceDTO);
        int res = datasourceService.updateDataSource(dataSource);
        if(res == 0){
            return new RetResult(400,"更新失败");
        }else {
            return new RetResult(1,"更新成功");
        }

    }

    @PostMapping("/datasources/insert")
    public RetResult saveDataSource(@RequestBody DataSourceDTO dataSourceDTO){

        DataSource dataSource = new DataSource();
        dataSource.setName(dataSourceDTO.getName());
        dataSource.setDescription(dataSourceDTO.getDescription());
        dataSource.setType(dataSourceDTO.getType());
        dataSource.setDetailInfo(JSON.toJSONString(dataSourceDTO.getDetail_info()));
        BaseDataSource baseDataSource = datasourceService.saveDataSource(dataSource);
        return new RetResult(0,"操作成功");

    }

    @GetMapping("/dataspurces/tables")
    public RetResult getTables(Integer id, String name){

        List<String> tables = datasourceService.getTables(id);
        return new RetResult(0,tables);
    }

    @GetMapping("/datasources/{id}")
    public RetResult selectDataSource(@RequestParam Integer id){

        return new RetResult(0,datasourceService.getSelectDataSource(id));
    }
}

