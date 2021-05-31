package com.bupt.cad.metadatahub.service.impl;

import com.bupt.cad.metadatahub.model.po.TableInfo;
import com.bupt.cad.metadatahub.dao.TableInfoMapper;
import com.bupt.cad.metadatahub.service.TableInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
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
public class TableInfoServiceImpl extends ServiceImpl<TableInfoMapper, TableInfo> implements TableInfoService {

    @Override
    public List<TableInfo> getTables(DataSource dataSource) {

        return null;
    }
}
