package com.bupt.cad.metadatahub.controller;

import com.bupt.cad.metadatahub.common.RetResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: yuejie
 * @Date: 2021/05/31/8:57
 * @Description:
 */
@RestController
public class LineageController {

    @GetMapping("/linegae/flink/${sql}")
    public RetResult getFlinkLineageInfo(@RequestParam String sql){

        return null;

    }
}
