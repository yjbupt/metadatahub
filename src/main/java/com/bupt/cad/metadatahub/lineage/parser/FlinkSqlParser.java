package com.bupt.cad.metadatahub.lineage.parser;

import com.bupt.cad.metadatahub.lineage.sql.SqlNodeAction;
import com.bupt.cad.metadatahub.lineage.sql.SqlNodeType;
import com.bupt.cad.metadatahub.lineage.sql.SqlParseColumn;
import com.bupt.cad.metadatahub.lineage.sql.SqlParseNode;
import org.apache.calcite.config.Lex;
import org.apache.calcite.sql.*;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.flink.sql.parser.ddl.SqlCreateTable;
import org.apache.flink.sql.parser.ddl.SqlCreateView;
import org.apache.flink.sql.parser.ddl.SqlTableColumn;
import org.apache.flink.sql.parser.ddl.SqlTableOption;
import org.apache.flink.sql.parser.impl.FlinkSqlParserImpl;
import org.apache.flink.sql.parser.validate.FlinkSqlConformance;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import static com.bupt.cad.metadatahub.util.Constants.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: yuejie
 * @Date: 2021/06/01/9:18
 * @Description:
 */
public class FlinkSqlParser {

    private Map<String, SqlParseNode> parseNodeMap;
    private List<String> sinks;

    private FlinkSqlParser(Map<String, SqlParseNode> parseNodeMap, List<String> sinks) {
        this.parseNodeMap = parseNodeMap;
        this.sinks = sinks;
    }

    //设置config信息
    public static SqlParser.Config sqlParserConfig = SqlParser.config()
            .withParserFactory(FlinkSqlParserImpl.FACTORY)
            //设置sql的一致性级别
            .withConformance(FlinkSqlConformance.DEFAULT)
            //词法分析工具
            .withLex(Lex.JAVA)
            .withIdentifierMaxLength(256);

    //创建parser
    public static SqlParser createParser(String sql){
        return SqlParser.create(sql,sqlParserConfig);
    }

    public static FlinkSqlParser parseFlinkSql(String sql) throws SqlParseException {
        //get parser
        SqlParser parser = FlinkSqlParser.createParser(sql);
        //parse
        SqlNodeList sqlNodes = parser.parseStmtList();
        Map<String,SqlParseNode> sqlParseNodeMap = new HashMap<>();
        List<String> sinks = new ArrayList<>();
        //对多条sql语句逐个解析
        for(SqlNode sqlNode:sqlNodes){
            // create table
            if(sqlNode instanceof SqlCreateTable){
                SqlCreateTable sqlCreateTable = (SqlCreateTable) sqlNode;
                SqlParseNode sqlParseNode = new SqlParseNode();
                sqlParseNode.setSql(sql);
                sqlParseNode.setType(SqlNodeType.TABLE);
                //TableName
                sqlParseNode.setName(sqlCreateTable.getTableName().getSimple());
                sqlParseNode.getActions().add(SqlNodeAction.SOURCE);
                //column
                List<SqlParseColumn> sqlParseColumns = sqlCreateTable.getColumnList().getList().stream().map(sqlNode1 -> {
                    SqlParseColumn sqlParseColumn = new SqlParseColumn();
                    if(sqlNode1 instanceof SqlTableColumn){
                        SqlTableColumn sqlTableColumn = (SqlTableColumn) sqlNode1;
                        sqlParseColumn.setName(sqlTableColumn.getName().getSimple());
                        //type这个目前不知道怎么获取
                        // sqlParseColumn.setType();
                        if (sqlTableColumn.getComment().isPresent()) {
                            sqlParseColumn.setComment(sqlTableColumn.getComment().get().toString());
                        }


                    }else if(sqlNode1 instanceof SqlBasicCall && ((SqlBasicCall) sqlNode1).getOperator() instanceof SqlAsOperator){
                        SqlNode[] operands = ((SqlBasicCall) sqlNode1).getOperands();
                        sqlParseColumn.setName(operands[1].toString());
                        sqlParseColumn.setType(operands[0].toString());
                        sqlParseColumn.setComment(sqlNode1.toString());
                    }else {
                        throw new RuntimeException("not support operation: " + sqlNode1.getClass().getSimpleName());
                    }
                    return sqlParseColumn;
                }).collect(Collectors.toList());
                sqlParseNode.setColumnList(sqlParseColumns);
                //properties信息
                Map<String,String> properties = sqlCreateTable.getPropertyList().getList().stream().map(x ->((SqlTableOption) x))
                        .collect(Collectors.toMap(SqlTableOption::getKeyString,SqlTableOption::getValueString));
                sqlParseNode.setProperties(properties);
                sqlParseNode.setCalciteSqlNode(sqlNode);
                //comment
                if(sqlCreateTable.getComment().isPresent()){
                    sqlParseNode.setComment(sqlCreateTable.getComment().get().toString());
                }else{
                    //default comment
                    sqlParseNode.setComment(sqlCreateTable.getTableName().toString());
                }
                sqlParseNodeMap.put(sqlParseNode.getName(),sqlParseNode);
            }

            //create view
            else if(sqlNode instanceof SqlCreateView){
                SqlCreateView sqlCreateView = (SqlCreateView) sqlNode;
                String viewName = sqlCreateView.getViewName().toString();
                SqlParseNode sqlParseNode = new SqlParseNode();
                sqlParseNode.setComment(viewName);
                sqlParseNode.setName(viewName);
                sqlParseNode.setType(SqlNodeType.VIEW);
                sqlParseNode.setSql(sql);
                sqlParseNode.getActions().add(SqlNodeAction.VIEW);
                //其他血缘等信息
                fillColumnInfoToSqlParseNode(sqlParseNode,sqlParseNodeMap,convertSqlNodeToSqlSelect(sqlCreateView.getQuery()));
            }
            //Insert
            else if(sqlNode instanceof SqlInsert){
                SqlInsert sqlInsert = (SqlInsert) sqlNode;
                String sinkTableName = sqlInsert.getTargetTable().toString();
                SqlParseNode sqlParseNode = new SqlParseNode();
                sqlParseNode.setSql(sql);
                sqlParseNode.setType(SqlNodeType.INSERT);
                sqlParseNode.setName(sinkTableName);
                sqlParseNode.setComment(sinkTableName);
                sinks.add(sinkTableName);
                SqlSelect sqlSelect = convertSqlNodeToSqlSelect(sqlInsert.getSource());
                fillColumnInfoToSqlParseNode(sqlParseNode,sqlParseNodeMap,sqlSelect);
            }
        }
        return new FlinkSqlParser(sqlParseNodeMap,sinks);
    }

    //todo
    // 1.从sqlparsenode中构建Edge和Vertex，并存入数据库中
    // 2.sqlnode验证

    private static void fillColumnInfoToSqlParseNode(SqlParseNode sqlParseNode,Map<String,SqlParseNode> sqlParseNodeMap,
                                                     SqlNode sqlNode){
        AtomicInteger i = new AtomicInteger();
        SqlSelect sqlSelect = convertSqlNodeToSqlSelect(sqlNode);
        List<SqlParseColumn> sqlParseColumns = sqlSelect.getSelectList().getList().stream()
                .map(FlinkSqlParser::getColumnAlias)
                .filter(tableColumn -> !tableColumn.equals(""))
                .map(tableColumn ->{
                    SqlParseColumn sqlParseColumn = new SqlParseColumn();
                    SqlNode node = sqlSelect.getSelectList().get(i.get());
                    if(node instanceof SqlBasicCall){
                        SqlBasicCall sqlBasicCall = (SqlBasicCall)node;
                        SqlOperator sqlOperator = sqlBasicCall.getOperator();
                        String selectNodeName = getSqlIdentifierName(sqlBasicCall.getOperands()[0]);
                        if (SqlKind.AS.equals(sqlOperator.getKind())){
                            sqlParseColumn.setSelectName(selectNodeName);
                            sqlParseColumn.setSelectAlias(getSqlIdentifierName(sqlBasicCall.getOperands()[1]));
                        }else {
                            sqlParseColumn.setSelectName(selectNodeName);
                            sqlParseColumn.setSelectAlias(selectNodeName);
                        }
                    }else if (node instanceof SqlIdentifier){
                        sqlParseColumn.setSelectName(node.toString());
                    }else if (node instanceof SqlCharStringLiteral){
                        sqlParseColumn.setIsConstant(true);
                    }
                    //parent
                    String columnParent = findColumnParent(sqlParseColumn.getSelectName(),sqlSelect.getFrom());
                    sqlParseColumn.setParents(columnParent);
                    sqlParseColumn.setName(tableColumn);
                    if (tableColumn.contains(TABLE_COLUMN_SEPARATOR)){
                        sqlParseColumn.setName(tableColumn.split(TRANSFERRED_TABLE_COLUMN_SEPARATOR)[1]);
                    }
                    i.addAndGet(1);
                    return sqlParseColumn;
                })
                .collect(Collectors.toList());
        sqlParseNode.setColumnList(sqlParseColumns);
        sqlParseNode.setCalciteSqlNode(sqlNode);
        sqlParseNodeMap.put(sqlParseNode.getName(),sqlParseNode);
        sqlParseNode.setParent(findTableNameList(sqlSelect,""));

    }

    private static String findColumnParent(String columnName, SqlNode sqlNode){
        String tableName = "";
        if(columnName.contains(".")){
            tableName = columnName.split(TRANSFERRED_TABLE_COLUMN_SEPARATOR)[0];
            columnName = columnName.split(TRANSFERRED_TABLE_COLUMN_SEPARATOR)[1];
        }
        //如果有子查询语句
        if(sqlNode instanceof SqlSelect){
            SqlSelect sqlSelect = (SqlSelect) sqlNode;
            SqlNode fromNode = sqlSelect.getFrom();
            //不含嵌套查询
            if(fromNode instanceof SqlIdentifier){
                List<SqlNode> sqlNodes = sqlSelect.getSelectList().getList();
                //子查询语句分析
                for(SqlNode column : sqlNodes){
                    String realColumn = "";
                    String aliasColumn = "";
                    if(column instanceof SqlBasicCall){
                        SqlBasicCall sqlBasicCall = (SqlBasicCall) column;
                        SqlOperator sqlOperator = sqlBasicCall.getOperator();
                        realColumn = getSqlIdentifierName(sqlBasicCall.getOperands()[0]);
                        aliasColumn = realColumn;
                        if(SqlKind.AS.equals(sqlOperator.getKind())){
                            aliasColumn = getSqlIdentifierName(sqlBasicCall.getOperands()[1]);
                        }
                    }else if (column instanceof SqlIdentifier){
                        realColumn =column.toString();
                        aliasColumn = realColumn;
                    }
                    //判断和当前要找的列是否为同一个，相同则返回
                    if(aliasColumn.equals(columnName)){
                        return fromNode.toString() + TABLE_COLUMN_SEPARATOR + realColumn;
                    }
                }
                //from语句是SqlBasicCall的情况，比如AS等，并且SqlbasicCall的第一个节点为表名
            }else if (fromNode instanceof SqlBasicCall && ((SqlBasicCall) fromNode).getOperands()[0] instanceof SqlIdentifier){
                List<SqlNode> sqlNodes = sqlSelect.getSelectList().getList();
                //下面的逻辑同上述所示
                for(SqlNode column : sqlNodes){
                    String realColumn = "";
                    if(column instanceof SqlBasicCall){
                        SqlBasicCall sqlBasicCall = (SqlBasicCall) column;
                        realColumn = getSqlIdentifierName(sqlBasicCall.getOperands()[0]);
                    }else if (column instanceof SqlIdentifier){
                        realColumn = column.toString();
                    }
                    if(realColumn.equals(columnName)){
                        return ((SqlBasicCall) fromNode).getOperands()[0].toString() + TABLE_COLUMN_SEPARATOR + realColumn;
                    }
                }
                //这种情况是需要在子查询语句中寻找parent
            }else {
                return findColumnParent(Objects.requireNonNull(findColumnParentFromSelect(sqlSelect.getSelectList().getList(),columnName)),fromNode);
            }
        //from为join语句
        }else if (sqlNode instanceof SqlJoin){
            SqlJoin sqlJoin = (SqlJoin) sqlNode;

            String leftTableName = findTableNameFromSqlNode(sqlJoin.getLeft());
            String rightTableName = findTableNameFromSqlNode(sqlJoin.getRight());
            if(tableName.equals("")){
                String leftParent = findColumnParent(columnName,sqlJoin.getLeft());
                if(leftParent == null){
                    return findColumnParent(columnName,sqlJoin.getRight());
                }else {
                    return leftParent;
                }
            }else if (tableName.equals(leftTableName)){
                return findColumnParent(columnName,sqlJoin.getLeft());
            }else if (tableName.equals(rightTableName)){
                return findColumnParent(columnName,sqlJoin.getRight());
            }
        }
        //tableName as alias
        else if (sqlNode instanceof  SqlBasicCall){
            return findColumnParent(columnName,((SqlBasicCall) sqlNode).getOperands()[0]);
        }
        //最简单的情况，直接是表名称
        else if (sqlNode instanceof SqlIdentifier){
            StringBuilder fullColumnName = new StringBuilder();
            for(String cName :  columnName.split("&")){
                fullColumnName.append(sqlNode.toString()).append(TABLE_COLUMN_SEPARATOR).append("&");
            }
            return fullColumnName.substring(0,fullColumnName.length()-1);
        }else {
            throw new RuntimeException("operation" + sqlNode.getClass() + "not support");
        }
        return null;

    }
    private static String findTableNameFromSqlNode(SqlNode sqlNode){
        if(sqlNode instanceof SqlBasicCall){
            SqlBasicCall sqlBasicCall = (SqlBasicCall) sqlNode;
            SqlOperator operator = sqlBasicCall.getOperator();
            if(SqlKind.AS.equals(operator.getKind())){
                return sqlBasicCall.getOperands()[1].toString();
            }else {
                return null;
            }
        }else if (sqlNode instanceof SqlIdentifier){
            return sqlNode.toString();
        }
        return null;
    }

    private static String findColumnParentFromSelect(List<SqlNode> sqlNodes,String columnName){
        for (SqlNode column: sqlNodes) {
            if (column instanceof SqlBasicCall) {
                SqlBasicCall sqlBasicCall = (SqlBasicCall) column;
                SqlOperator operator = sqlBasicCall.getOperator();
                String realColumnName = "";
                if (SqlKind.AS.equals(operator.getKind())) {
                    realColumnName = getSqlIdentifierName(sqlBasicCall.getOperands()[1]);
                } else {
                    realColumnName = getSqlIdentifierName(sqlBasicCall.getOperands()[0]);
                }

                if (realColumnName.contains(".")) {
                    realColumnName = realColumnName.split(TRANSFERRED_TABLE_COLUMN_SEPARATOR)[1];
                }
                if (realColumnName.equals(columnName)) {
                    return column.toString();
                }
            } else if (column instanceof SqlIdentifier) {
                String selectName = column.toString();
                if (selectName.contains(".")) {
                    selectName = selectName.split(TRANSFERRED_TABLE_COLUMN_SEPARATOR)[1];
                }
                if (selectName.equals(columnName)) {
                    return column.toString();
                }
            }
        }

        return null;
    }

    /**
     * 找到sql语句中的所有的表名称
     * @param sqlNode
     * @param alias
     * @return
     */
    private static List<String> findTableNameList(SqlNode sqlNode,String alias){
        List<String> tableNameList = new ArrayList<>();
        if(sqlNode instanceof SqlSelect){
            SqlNode fromNode = ((SqlSelect) sqlNode).getFrom();
            tableNameList.addAll(findTableNameList(fromNode,alias));
        }else if (sqlNode instanceof SqlJoin){
            SqlJoin sqlJoin = (SqlJoin) sqlNode;
            tableNameList.addAll(findTableNameList(sqlJoin.getLeft(),alias));
            tableNameList.addAll(findTableNameList(sqlJoin.getRight(),alias));
        }else if(sqlNode instanceof SqlBasicCall){
            SqlBasicCall sqlBasicCall = (SqlBasicCall) sqlNode;
            SqlOperator sqlOperator = sqlBasicCall.getOperator();
            if(SqlKind.AS.equals(sqlOperator.getKind())){
                tableNameList.addAll(findTableNameList(sqlBasicCall.getOperands()[0],sqlBasicCall.getOperands()[1].toString()));
            }else if(SqlKind.UNION.equals(sqlOperator.getKind())){
                for(SqlNode operandNode: sqlBasicCall.getOperands()){
                    tableNameList.addAll(findTableNameList(operandNode,alias));
                }
            }else if (SqlKind.LATERAL.equals(sqlOperator.getKind())
            || SqlKind.COLLECTION_TABLE.equals(sqlOperator.getKind())
            || SqlKind.OTHER_FUNCTION.equals(sqlOperator.getKind())){
                tableNameList.addAll(findTableNameList(sqlBasicCall.getOperands()[0],alias));
            }else {
                throw new RuntimeException("operation" + sqlOperator.getKind() + "not support");
            }
        }else if(sqlNode instanceof SqlIdentifier){
            if(!alias.equals("")){
                tableNameList.add(sqlNode.toString()+TABLE_COLUMN_SEPARATOR+alias);
            }else {
                tableNameList.add(sqlNode.toString());
            }
        }else {
            throw new RuntimeException("operation " + sqlNode.getClass() + "not support");
        }
        return tableNameList;
    }
    private static String getSqlIdentifierName(SqlNode sqlNode){
        if(sqlNode instanceof SqlIdentifier){
            return sqlNode.toString();
        }else if(sqlNode instanceof SqlBasicCall){
            SqlBasicCall sqlBasicCall = (SqlBasicCall) sqlNode;
            String identifierName = "";
            for(SqlNode operand : sqlBasicCall.getOperands()){
                identifierName += getSqlIdentifierName(operand) + "&";
            }
            while (identifierName.endsWith("&")){
                identifierName = identifierName.substring(0,identifierName.length()-1);
            }
            return identifierName;
        }else if (sqlNode instanceof SqlIntervalLiteral){
            return "";
        }
        return " ";
    }

    private static String getColumnAlias(SqlNode sqlNode){
        if (sqlNode instanceof SqlBasicCall) {
            SqlNode[] nodes = ((SqlBasicCall) sqlNode).getOperands();
            return getColumnAlias(nodes[nodes.length-1]);
        } else if (sqlNode instanceof SqlIdentifier) {
            return sqlNode.toString();
        }
        return "";

    }

    private static SqlSelect convertSqlNodeToSqlSelect(SqlNode sqlNode){
        if(sqlNode instanceof SqlSelect){
            return (SqlSelect) sqlNode;
        }else if(sqlNode instanceof SqlBasicCall){
            SqlBasicCall sqlBasicCall = (SqlBasicCall) sqlNode;
            SqlOperator operator  = sqlBasicCall.getOperator();
            if(SqlKind.UNION.equals(operator.getKind())){
                return convertSqlNodeToSqlSelect(sqlBasicCall.getOperands()[0]);
            }
        }else if(sqlNode instanceof SqlOrderBy){
            SqlOrderBy sqlOrderBy = (SqlOrderBy) sqlNode;
            return convertSqlNodeToSqlSelect(sqlOrderBy.query);
        }
        return null;
    }

}
