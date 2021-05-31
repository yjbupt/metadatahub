package com.bupt.cad.metadatahub.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: yuejie
 * @Date: 2021/04/13/10:14
 * @Description:
 */
public interface MetadataCollectService {

    Map collectMetadata(String id) throws SQLException;


}
