package com.bupt.cad.metadatahub.model.vo;

import lombok.Data;

@Data
public class DataSourceVO {
    private Integer id;

    private String name;

    private String type;

    private String description;

    private Object detail_info;

    private String createTime;

    private String updateTime;
}
