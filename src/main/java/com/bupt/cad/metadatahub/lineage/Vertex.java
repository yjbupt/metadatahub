package com.bupt.cad.metadatahub.lineage;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: yuejie
 * @Date: 2021/05/31/14:21
 * @Description:
 */
@Data
public class Vertex {
    private String databaseName;

    private String tableName;

    private String column;

}
