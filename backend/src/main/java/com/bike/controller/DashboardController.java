package com.bike.controller;

import com.bike.common.Result;
import com.bike.entity.dto.CategoryStatsVO;
import com.bike.entity.dto.DashboardStatsVO;
import com.bike.entity.dto.HelpTrendStatsVO;
import com.bike.service.DashboardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "仪表盘")
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @ApiOperation("获取统计数据")
    @GetMapping("/stats")
    public Result<DashboardStatsVO> getStats() {
        return Result.success(dashboardService.getStats());
    }

    @ApiOperation("获取库存分类统计")
    @GetMapping("/category-stats")
    public Result<List<CategoryStatsVO>> getCategoryStats() {
        return Result.success(dashboardService.getCategoryStats());
    }

    @ApiOperation("获取近7天求助趋势统计")
    @GetMapping("/help-trend")
    public Result<List<HelpTrendStatsVO>> getHelpTrendStats() {
        return Result.success(dashboardService.getHelpTrendStats());
    }
}
