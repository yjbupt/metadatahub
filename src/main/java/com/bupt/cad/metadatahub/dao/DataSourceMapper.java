package com.bupt.cad.metadatahub.dao;

import com.bupt.cad.metadatahub.model.po.DataSource;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author yuejie
 * @since 2021-03-04
 */

public interface DataSourceMapper extends BaseMapper<DataSource> {


    List<DataSource> SelectAll();

    int updateByPrimaryKeySelective(DataSource dataSource);






}
