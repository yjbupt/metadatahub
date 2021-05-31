package com.bupt.cad.metadatahub.service;

import com.bupt.cad.metadatahub.model.datasource.BaseDataSource;
import com.bupt.cad.metadatahub.model.po.TableInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.sql.DataSource;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author yuejie
 * @since 2021-03-04
 */
public interface TableInfoService extends IService<TableInfo> {

    List<TableInfo> getTables(DataSource dataSource);




}
