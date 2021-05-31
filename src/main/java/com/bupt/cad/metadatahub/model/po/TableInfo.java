package com.bupt.cad.metadatahub.model.po;

import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author yuejie
 * @since 2021-03-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TableInfo implements Serializable {

    private static final long serialVersionUID=1L;

    private Integer id;

    private Integer datasourceId;

    private String tableName;

    private String schema;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;


}
