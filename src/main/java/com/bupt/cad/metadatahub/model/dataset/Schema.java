package com.bupt.cad.metadatahub.model.dataset;

import lombok.Data;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: yuejie
 * @Date: 2021/03/12/0:06
 * @Description:
 */
@Data
public class Schema {

    private String SchemaName;

    private Map<String,DataSetFields> tableFields;

    private String createTime;

    private String lastModified;

    private String platform;

    private int version;

    private String owner;

}
