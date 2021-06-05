package com.bupt.cad.metadatahub.lineage.sql;

import lombok.Data;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: yuejie
 * @Date: 2021/06/01/10:24
 * @Description:
 */
@Data
public class SqlParseNode {
    private String name;
    private SqlNodeType type;
    private List<SqlParseColumn> columnList;
    private String comment;
    private List<String> parent;
    private String sql;
    private Map<String, String> properties;
    private Set<SqlNodeAction> actions = new HashSet<>();

    private transient Object calciteSqlNode;
}
