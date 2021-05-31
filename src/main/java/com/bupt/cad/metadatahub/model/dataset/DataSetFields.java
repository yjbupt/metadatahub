package com.bupt.cad.metadatahub.model.dataset;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigInteger;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: yuejie
 * @Date: 2021/03/12/0:00
 * @Description:
 */
@Data
@AllArgsConstructor
public class DataSetFields {

    private String fieldName;

    private String type;

    private String Description;

    //有待完善，可添加数据owner，数据敏感级别等信息，此信息存放在MySQL中的columns表中
}
