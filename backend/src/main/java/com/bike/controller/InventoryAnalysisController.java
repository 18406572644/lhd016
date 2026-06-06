package com.bike.controller;

import com.bike.common.Result;
import com.bike.entity.dto.*;
import com.bike.service.InventoryAnalysisService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Api(tags = "库存分析")
@RestController
@RequestMapping("/api/inventory-analysis")
public class InventoryAnalysisController {

    @Autowired
    private InventoryAnalysisService inventoryAnalysisService;

    @ApiOperation("获取库存分析概览数据")
    @GetMapping("/overview")
    public Result<InventoryAnalysisOverviewVO> getOverview() {
        return Result.success(inventoryAnalysisService.getOverview());
    }

    @ApiOperation("获取库存周转率分析")
    @GetMapping("/turnover-rate")
    public Result<List<TurnoverRateVO>> getTurnoverRate(
            @ApiParam("分组维度：category-按分类，supplyPoint-按补给点")
            @RequestParam(defaultValue = "category") String groupBy,
            @ApiParam("统计周期：month-月度，quarter-季度，year-年度")
            @RequestParam(defaultValue = "month") String period,
            @ApiParam("补给点ID（可选）")
            @RequestParam(required = false) Long supplyPointId,
            @ApiParam("分类（可选）")
            @RequestParam(required = false) String category
    ) {
        return Result.success(inventoryAnalysisService.getTurnoverRate(groupBy, period, supplyPointId, category));
    }

    @ApiOperation("获取库存ABC分类")
    @GetMapping("/abc-classification")
    public Result<List<AbcClassificationVO>> getAbcClassification() {
        return Result.success(inventoryAnalysisService.getAbcClassification());
    }

    @ApiOperation("获取库存价值分布")
    @GetMapping("/value-distribution")
    public Result<List<InventoryValueVO>> getInventoryValueDistribution(
            @ApiParam("统计维度：category-按分类，supplyPoint-按补给点")
            @RequestParam(defaultValue = "category") String dimension
    ) {
        return Result.success(inventoryAnalysisService.getInventoryValueDistribution(dimension));
    }

    @ApiOperation("获取出入库趋势")
    @GetMapping("/inout-trend")
    public Result<List<StockInOutTrendVO>> getStockInOutTrend(
            @ApiParam("分类（可选）")
            @RequestParam(required = false) String category,
            @ApiParam("补给点ID（可选）")
            @RequestParam(required = false) Long supplyPointId,
            @ApiParam("统计月数，默认6个月")
            @RequestParam(defaultValue = "6") Integer months
    ) {
        return Result.success(inventoryAnalysisService.getStockInOutTrend(category, supplyPointId, months));
    }

    @ApiOperation("获取库存健康度评分")
    @GetMapping("/health-score")
    public Result<InventoryHealthScoreVO> getInventoryHealthScore() {
        return Result.success(inventoryAnalysisService.getInventoryHealthScore());
    }

    @ApiOperation("获取补给点对比数据")
    @GetMapping("/supply-point-comparison")
    public Result<List<SupplyPointComparisonVO>> getSupplyPointComparison() {
        return Result.success(inventoryAnalysisService.getSupplyPointComparison());
    }

    @ApiOperation("获取筛选条件选项（补给点、分类）")
    @GetMapping("/filter-options")
    public Result<Map<String, Object>> getFilterOptions() {
        return Result.success(inventoryAnalysisService.getFilterOptions());
    }
}
