package com.bike.controller;

import com.bike.common.Result;
import com.bike.entity.dto.MapOverviewVO;
import com.bike.service.MapService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "地图总览")
@RestController
@RequestMapping("/api/map")
public class MapController {

    @Autowired
    private MapService mapService;

    @ApiOperation("获取地图总览数据")
    @GetMapping("/overview")
    public Result<MapOverviewVO> getOverview() {
        return Result.success(mapService.getMapOverview());
    }
}
