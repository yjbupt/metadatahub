package com.bupt.cad.metadatahub.controller;

import com.bupt.cad.metadatahub.common.RetResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yuejie
 * @date 2021/3/8 18:15
 */
@RestController
public class DataCollectController {

    @GetMapping("/collect/{id}")
    public RetResult collectMetadata(@RequestParam String id){
        return new RetResult(1,"采集成功");

    }


}
