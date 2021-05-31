package com.bupt.cad.metadatahub.model.dto;

import lombok.Data;

import java.util.Map;

@Data
public class DataSourceDTO {
    private Integer id;

    private String name;

    private String type;

    private String description;

    private Map<String, String> detail_info;
}
