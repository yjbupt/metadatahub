package com.bupt.cad.metadatahub.service;

import com.bupt.cad.metadatahub.common.PageResult;
import com.bupt.cad.metadatahub.model.datasource.BaseDataSource;
import com.bupt.cad.metadatahub.model.po.DataSource;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bupt.cad.metadatahub.model.vo.DataSourceVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author yuejie
 * @since 2021-03-04
 */
public interface DataSourceService extends IService<DataSource> {

    PageResult<DataSource> getAllDataSources(Integer page, Integer pageSize);

    boolean testConnection(DataSource dataSource);

    BaseDataSource saveDataSource(DataSource dataSource);

    List<String> getTables(Integer id);

    DataSourceVO getSelectDataSource(Integer id);

    int updateDataSource(DataSource dataSource);





}
