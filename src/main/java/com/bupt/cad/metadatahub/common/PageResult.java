package com.bupt.cad.metadatahub.common;

import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {

    Integer page;

    Integer pageSize;

    Long total;

    List<T> datas;
}
