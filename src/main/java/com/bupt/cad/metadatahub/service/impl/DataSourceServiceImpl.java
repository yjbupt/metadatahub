package com.bupt.cad.metadatahub.service.impl;

import com.bupt.cad.metadatahub.common.PageResult;
import com.bupt.cad.metadatahub.model.datasource.BaseDataSource;
import com.bupt.cad.metadatahub.model.po.DataSource;
import com.bupt.cad.metadatahub.dao.DataSourceMapper;
import com.bupt.cad.metadatahub.model.vo.DataSourceVO;
import com.bupt.cad.metadatahub.service.DataSourceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bupt.cad.metadatahub.util.DataSourceUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author yuejie
 * @since 2021-03-04
 */
@Service
public class DataSourceServiceImpl extends ServiceImpl<DataSourceMapper, DataSource> implements DataSourceService {

    @Autowired
    DataSourceMapper dataSourceMapper;

    @Override
    public PageResult<DataSource> getAllDataSources( Integer page, Integer pageSize) {

        PageHelper.startPage(page,pageSize);
        List<DataSource>  dataSources = dataSourceMapper.SelectAll();
        PageInfo<DataSource> pageInfo = new PageInfo<>(dataSources);
        PageResult<DataSource> pageResult = new PageResult<>();
        pageResult.setPage(pageInfo.getPageNum());
        pageResult.setPageSize(pageInfo.getPageSize());
        pageResult.setTotal(pageInfo.getTotal());
        pageResult.setDatas(dataSources);

        return pageResult;
    }

    @Override
    public boolean testConnection(DataSource dataSource) {

        BaseDataSource baseDataSource = DataSourceUtil.convertToBaseDataSource(dataSource);
        boolean res = baseDataSource.testConnection();
        return res;

    }

    @Override
    public BaseDataSource saveDataSource(DataSource dataSource) {

        dataSourceMapper.insert(dataSource);
        BaseDataSource baseDataSource = DataSourceUtil.convertToBaseDataSource(dataSource);
        return baseDataSource;
    }

    @Override
    public List<String> getTables(Integer id) {

        DataSource dataSource = dataSourceMapper.selectById(id);
        BaseDataSource baseDataSource = DataSourceUtil.convertToBaseDataSource(dataSource);
        List<String> tables = baseDataSource.getTables();
        return tables;
    }

    @Override
    public DataSourceVO getSelectDataSource(Integer id) {

        DataSource dataSource = dataSourceMapper.selectById(id);
        DataSourceVO dataSourceVO = new DataSourceVO();
        dataSourceVO.setId(dataSource.getId());
        dataSourceVO.setName(dataSource.getName());
        dataSourceVO.setDescription(dataSource.getDescription());
        dataSourceVO.setDetail_info(dataSource.getDetailInfo());
        dataSourceVO.setCreateTime(dataSource.getCreateTime().toString());
        dataSourceVO.setUpdateTime(dataSource.getUpdateTime().toString());
        return dataSourceVO;
    }

    @Override
    public int updateDataSource(DataSource dataSource) {

        int res = dataSourceMapper.updateByPrimaryKeySelective(dataSource);
        return res;
    }
}
