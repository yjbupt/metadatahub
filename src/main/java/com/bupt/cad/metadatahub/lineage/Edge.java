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
public class Edge {

    private Vertex source;

    private Vertex sink;

}
