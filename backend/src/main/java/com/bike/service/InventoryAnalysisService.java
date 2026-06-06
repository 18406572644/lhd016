package com.bike.service;

import com.bike.entity.dto.*;

import java.util.List;
import java.util.Map;

public interface InventoryAnalysisService {

    InventoryAnalysisOverviewVO getOverview();

    List<TurnoverRateVO> getTurnoverRate(String groupBy, String period, Long supplyPointId, String category);

    List<AbcClassificationVO> getAbcClassification();

    List<InventoryValueVO> getInventoryValueDistribution(String dimension);

    List<StockInOutTrendVO> getStockInOutTrend(String category, Long supplyPointId, Integer months);

    InventoryHealthScoreVO getInventoryHealthScore();

    List<SupplyPointComparisonVO> getSupplyPointComparison();

    Map<String, Object> getFilterOptions();
}
