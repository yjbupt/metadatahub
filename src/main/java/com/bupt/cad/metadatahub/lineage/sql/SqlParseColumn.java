package com.bupt.cad.metadatahub.lineage.sql;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: yuejie
 * @Date: 2021/06/01/10:27
 * @Description:
 */
@Data
public class SqlParseColumn {
    private String name;
    private String selectName;
    private String selectAlias;
    private String type;
    private Boolean nullable;
    private String constraint;
    private String comment;
    // 是否计算列
    private Boolean isPhysical = true;
    // 是否常量列
    private Boolean isConstant = false;
    //tableName.columnName
    //还要考虑怎么和表关联起来
    private String parents;
}
