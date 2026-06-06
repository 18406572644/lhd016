package com.bike.service.impl;

import com.bike.entity.SparePart;
import com.bike.entity.dto.*;
import com.bike.mapper.InventoryCheckMapper;
import com.bike.mapper.SparePartMapper;
import com.bike.mapper.StockRecordMapper;
import com.bike.service.InventoryAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class InventoryAnalysisServiceImpl implements InventoryAnalysisService {

    @Autowired
    private SparePartMapper sparePartMapper;

    @Autowired
    private StockRecordMapper stockRecordMapper;

    @Autowired
    private InventoryCheckMapper inventoryCheckMapper;

    @Override
    public InventoryAnalysisOverviewVO getOverview() {
        InventoryAnalysisOverviewVO vo = new InventoryAnalysisOverviewVO();

        Integer totalItems = Math.toIntExact(sparePartMapper.selectCount(null));
        vo.setTotalItems(totalItems);

        BigDecimal totalValue = sparePartMapper.selectTotalInventoryValue();
        vo.setTotalInventoryValue(totalValue);

        vo.setTotalCategories(sparePartMapper.countDistinctCategories());
        vo.setTotalSupplyPoints(sparePartMapper.countDistinctSupplyPoints());

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = now.minusMonths(1);
        BigDecimal avgTurnover = stockRecordMapper.selectAvgTurnoverRate(startDate, now);
        vo.setAvgTurnoverRate(avgTurnover);

        Integer lowStockItems = sparePartMapper.countLowStockItems();
        vo.setLowStockItems(lowStockItems);
        if (totalItems > 0) {
            vo.setLowStockRatio(BigDecimal.valueOf(lowStockItems)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalItems), 2, RoundingMode.HALF_UP));
        } else {
            vo.setLowStockRatio(BigDecimal.ZERO);
        }

        LocalDateTime deadStockDate = now.minusMonths(3);
        Integer deadStockItems = sparePartMapper.countDeadStockItems(deadStockDate);
        vo.setDeadStockItems(deadStockItems);
        if (totalItems > 0) {
            vo.setDeadStockRatio(BigDecimal.valueOf(deadStockItems)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalItems), 2, RoundingMode.HALF_UP));
        } else {
            vo.setDeadStockRatio(BigDecimal.ZERO);
        }

        vo.setHealthScore(getInventoryHealthScore());

        return vo;
    }

    @Override
    public List<TurnoverRateVO> getTurnoverRate(String groupBy, String period, Long supplyPointId, String category) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate;
        String periodLabel;

        switch (period != null ? period : "month") {
            case "quarter":
                startDate = now.minusMonths(3);
                periodLabel = "季度";
                break;
            case "year":
                startDate = now.minusYears(1);
                periodLabel = "年度";
                break;
            case "month":
            default:
                startDate = now.minusMonths(1);
                periodLabel = "月度";
        }

        List<TurnoverRateVO> result;
        if ("supplyPoint".equals(groupBy)) {
            result = stockRecordMapper.selectTurnoverRateBySupplyPoint(startDate, now, periodLabel, category);
        } else {
            result = stockRecordMapper.selectTurnoverRateByCategory(startDate, now, periodLabel, supplyPointId);
        }

        for (TurnoverRateVO vo : result) {
            BigDecimal avgStock = vo.getAverageStock();
            if (avgStock != null && avgStock.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal turnoverRate = BigDecimal.valueOf(vo.getOutgoingQuantity())
                        .divide(avgStock, 4, RoundingMode.HALF_UP);
                vo.setTurnoverRate(turnoverRate);

                if (turnoverRate.compareTo(new BigDecimal("2.0")) >= 0) {
                    vo.setTurnoverLevel("快销");
                } else if (turnoverRate.compareTo(new BigDecimal("0.5")) >= 0) {
                    vo.setTurnoverLevel("正常");
                } else if (turnoverRate.compareTo(new BigDecimal("0.1")) >= 0) {
                    vo.setTurnoverLevel("慢销");
                } else {
                    vo.setTurnoverLevel("滞销");
                }
            } else {
                vo.setTurnoverRate(BigDecimal.ZERO);
                vo.setTurnoverLevel("滞销");
            }
        }

        result.sort((a, b) -> b.getTurnoverRate().compareTo(a.getTurnoverRate()));

        return result;
    }

    @Override
    public List<AbcClassificationVO> getAbcClassification() {
        List<AbcClassificationVO> rawList = sparePartMapper.selectAbcClassificationRaw();

        BigDecimal totalValue = BigDecimal.ZERO;
        for (AbcClassificationVO vo : rawList) {
            if (vo.getTotalValue() != null) {
                totalValue = totalValue.add(vo.getTotalValue());
            }
        }

        BigDecimal cumulativeValue = BigDecimal.ZERO;
        for (int i = 0; i < rawList.size(); i++) {
            AbcClassificationVO vo = rawList.get(i);

            if (vo.getTotalValue() != null && totalValue.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal percentage = vo.getTotalValue()
                        .multiply(BigDecimal.valueOf(100))
                        .divide(totalValue, 2, RoundingMode.HALF_UP);
                vo.setValuePercentage(percentage);

                cumulativeValue = cumulativeValue.add(vo.getTotalValue());
                BigDecimal cumulativePercentage = cumulativeValue
                        .multiply(BigDecimal.valueOf(100))
                        .divide(totalValue, 2, RoundingMode.HALF_UP);
                vo.setCumulativePercentage(cumulativePercentage);

                if (cumulativePercentage.compareTo(new BigDecimal("70")) <= 0) {
                    vo.setAbcClass("A");
                } else if (cumulativePercentage.compareTo(new BigDecimal("90")) <= 0) {
                    vo.setAbcClass("B");
                } else {
                    vo.setAbcClass("C");
                }
            } else {
                vo.setValuePercentage(BigDecimal.ZERO);
                vo.setCumulativePercentage(BigDecimal.ZERO);
                vo.setAbcClass("C");
            }
        }

        return rawList;
    }

    @Override
    public List<InventoryValueVO> getInventoryValueDistribution(String dimension) {
        List<InventoryValueVO> list;

        if ("supplyPoint".equals(dimension)) {
            list = sparePartMapper.selectInventoryValueBySupplyPoint();
        } else {
            list = sparePartMapper.selectInventoryValueByCategory();
        }

        BigDecimal totalValue = BigDecimal.ZERO;
        for (InventoryValueVO vo : list) {
            if (vo.getValue() != null) {
                totalValue = totalValue.add(vo.getValue());
            }
        }

        for (InventoryValueVO vo : list) {
            if (vo.getValue() != null && totalValue.compareTo(BigDecimal.ZERO) > 0) {
                vo.setPercentage(vo.getValue()
                        .multiply(BigDecimal.valueOf(100))
                        .divide(totalValue, 2, RoundingMode.HALF_UP));
            } else {
                vo.setPercentage(BigDecimal.ZERO);
            }
        }

        return list;
    }

    @Override
    public List<StockInOutTrendVO> getStockInOutTrend(String category, Long supplyPointId, Integer months) {
        int monthCount = months != null ? months : 6;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = now.minusMonths(monthCount).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endDate = now.withDayOfMonth(1).plusMonths(1).withHour(0).withMinute(0).withSecond(0).withNano(0);

        List<StockInOutTrendVO> dbResult = stockRecordMapper.selectStockInOutTrend(startDate, endDate, category, supplyPointId);

        Map<String, StockInOutTrendVO> monthMap = new HashMap<>();
        for (StockInOutTrendVO vo : dbResult) {
            String key = vo.getMonth() + "_" + (vo.getCategory() != null ? vo.getCategory() : "ALL");
            StockInOutTrendVO existing = monthMap.get(key);
            if (existing == null) {
                monthMap.put(key, vo);
            } else {
                existing.setStockInQuantity(existing.getStockInQuantity() + vo.getStockInQuantity());
                existing.setStockOutQuantity(existing.getStockOutQuantity() + vo.getStockOutQuantity());
                existing.setNetChange(existing.getNetChange() + vo.getNetChange());
            }
        }

        Set<String> categories = dbResult.stream()
                .map(StockInOutTrendVO::getCategory)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (categories.isEmpty()) {
            categories.add(null);
        }

        List<StockInOutTrendVO> result = new ArrayList<>();
        for (int i = monthCount; i >= 0; i--) {
            LocalDateTime monthDate = now.minusMonths(i).withDayOfMonth(1);
            String monthStr = String.format("%04d-%02d", monthDate.getYear(), monthDate.getMonthValue());

            for (String cat : categories) {
                String key = monthStr + "_" + (cat != null ? cat : "ALL");
                StockInOutTrendVO vo = monthMap.get(key);
                if (vo == null) {
                    vo = new StockInOutTrendVO();
                    vo.setMonth(monthStr);
                    vo.setCategory(cat);
                    vo.setStockInQuantity(0);
                    vo.setStockOutQuantity(0);
                    vo.setNetChange(0);
                }
                result.add(vo);
            }
        }

        result.sort(Comparator.comparing(StockInOutTrendVO::getMonth)
                .thenComparing(vo -> vo.getCategory() != null ? vo.getCategory() : ""));

        return result;
    }

    @Override
    public InventoryHealthScoreVO getInventoryHealthScore() {
        InventoryHealthScoreVO vo = new InventoryHealthScoreVO();

        Integer totalItems = Math.toIntExact(sparePartMapper.selectCount(null));
        vo.setTotalItems(totalItems);

        Integer lowStockItems = sparePartMapper.countLowStockItems();
        vo.setLowStockItems(lowStockItems);

        BigDecimal lowStockRatio = BigDecimal.ZERO;
        if (totalItems > 0) {
            lowStockRatio = BigDecimal.valueOf(lowStockItems)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalItems), 2, RoundingMode.HALF_UP);
        }
        vo.setLowStockRatio(lowStockRatio);

        BigDecimal lowStockScore = calculateScore(lowStockRatio, 25, 5, 15);
        vo.setLowStockScore(lowStockScore);

        LocalDateTime deadStockDate = LocalDateTime.now().minusMonths(3);
        Integer deadStockItems = sparePartMapper.countDeadStockItems(deadStockDate);
        vo.setDeadStockItems(deadStockItems);

        BigDecimal deadStockRatio = BigDecimal.ZERO;
        if (totalItems > 0) {
            deadStockRatio = BigDecimal.valueOf(deadStockItems)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalItems), 2, RoundingMode.HALF_UP);
        }
        vo.setDeadStockRatio(deadStockRatio);

        BigDecimal deadStockScore = calculateScore(deadStockRatio, 20, 10, 25);
        vo.setDeadStockScore(deadStockScore);

        LocalDateTime checkStartDate = LocalDateTime.now().minusMonths(3);
        Integer totalCheckedItems = inventoryCheckMapper.selectTotalCheckedItems(checkStartDate);
        Integer diffItems = inventoryCheckMapper.selectTotalDiffItems(checkStartDate);
        Integer recentCheckCount = inventoryCheckMapper.countRecentCompletedChecks(checkStartDate);
        vo.setRecentCheckCount(recentCheckCount);
        vo.setDiffItems(diffItems);

        BigDecimal inventoryDiffRatio = BigDecimal.ZERO;
        if (totalCheckedItems != null && totalCheckedItems > 0) {
            inventoryDiffRatio = BigDecimal.valueOf(diffItems)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalCheckedItems), 2, RoundingMode.HALF_UP);
        }
        vo.setInventoryDiffRatio(inventoryDiffRatio);

        BigDecimal inventoryDiffScore = calculateScore(inventoryDiffRatio, 25, 3, 10);
        vo.setInventoryDiffScore(inventoryDiffScore);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime turnoverStartDate = now.minusMonths(3);
        BigDecimal avgTurnoverRate = stockRecordMapper.selectAvgTurnoverRate(turnoverStartDate, now);

        BigDecimal turnoverScore;
        if (avgTurnoverRate.compareTo(new BigDecimal("1.0")) >= 0) {
            turnoverScore = new BigDecimal("30");
        } else if (avgTurnoverRate.compareTo(new BigDecimal("0.5")) >= 0) {
            turnoverScore = new BigDecimal("20");
        } else if (avgTurnoverRate.compareTo(new BigDecimal("0.2")) >= 0) {
            turnoverScore = new BigDecimal("10");
        } else {
            turnoverScore = new BigDecimal("5");
        }
        vo.setTurnoverScore(turnoverScore);

        BigDecimal totalScore = lowStockScore
                .add(deadStockScore)
                .add(inventoryDiffScore)
                .add(turnoverScore);
        vo.setTotalScore(totalScore);

        if (totalScore.compareTo(new BigDecimal("80")) >= 0) {
            vo.setHealthLevel("优秀");
        } else if (totalScore.compareTo(new BigDecimal("60")) >= 0) {
            vo.setHealthLevel("良好");
        } else if (totalScore.compareTo(new BigDecimal("40")) >= 0) {
            vo.setHealthLevel("一般");
        } else {
            vo.setHealthLevel("较差");
        }

        return vo;
    }

    private BigDecimal calculateScore(BigDecimal ratio, int maxScore, int excellentThreshold, int poorThreshold) {
        if (ratio.compareTo(BigDecimal.valueOf(excellentThreshold)) <= 0) {
            return BigDecimal.valueOf(maxScore);
        } else if (ratio.compareTo(BigDecimal.valueOf(poorThreshold)) >= 0) {
            return BigDecimal.ZERO;
        } else {
            double score = maxScore * (1.0 - (ratio.doubleValue() - excellentThreshold) / (poorThreshold - excellentThreshold));
            return BigDecimal.valueOf(score).setScale(2, RoundingMode.HALF_UP);
        }
    }

    @Override
    public List<SupplyPointComparisonVO> getSupplyPointComparison() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = now.minusMonths(3);

        List<SupplyPointComparisonVO> rawList = sparePartMapper.selectSupplyPointComparison(startDate, now);

        Map<String, SupplyPointComparisonVO> aggregatedMap = new LinkedHashMap<>();

        for (SupplyPointComparisonVO vo : rawList) {
            String name = vo.getSupplyPointName() != null ? vo.getSupplyPointName() : "未分配";
            SupplyPointComparisonVO aggregated = aggregatedMap.get(name);

            if (aggregated == null) {
                aggregated = new SupplyPointComparisonVO();
                aggregated.setSupplyPointName(name);
                aggregated.setTotalItems(0);
                aggregated.setTotalValue(BigDecimal.ZERO);
                aggregated.setLowStockItems(0);
                aggregated.setStockInQuantity(0);
                aggregated.setStockOutQuantity(0);
                aggregated.setAvgTurnoverRate(BigDecimal.ZERO);
                aggregatedMap.put(name, aggregated);
            }

            aggregated.setTotalItems(aggregated.getTotalItems() + vo.getTotalItems());
            aggregated.setTotalValue(aggregated.getTotalValue().add(vo.getTotalValue() != null ? vo.getTotalValue() : BigDecimal.ZERO));
            aggregated.setLowStockItems(aggregated.getLowStockItems() + vo.getLowStockItems());
            aggregated.setStockInQuantity(aggregated.getStockInQuantity() + (vo.getStockInQuantity() != null ? vo.getStockInQuantity() : 0));
            aggregated.setStockOutQuantity(aggregated.getStockOutQuantity() + (vo.getStockOutQuantity() != null ? vo.getStockOutQuantity() : 0));

            if (vo.getAvgTurnoverRate() != null) {
                aggregated.setAvgTurnoverRate(aggregated.getAvgTurnoverRate().add(vo.getAvgTurnoverRate()));
            }
        }

        List<SupplyPointComparisonVO> result = new ArrayList<>();
        for (SupplyPointComparisonVO vo : aggregatedMap.values()) {
            if (vo.getTotalItems() > 0) {
                vo.setAvgTurnoverRate(vo.getAvgTurnoverRate()
                        .divide(BigDecimal.valueOf(vo.getTotalItems()), 4, RoundingMode.HALF_UP));

                vo.setLowStockRatio(BigDecimal.valueOf(vo.getLowStockItems())
                        .multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(vo.getTotalItems()), 2, RoundingMode.HALF_UP));

                BigDecimal healthScore = calculateHealthScoreForSupplyPoint(vo);
                vo.setInventoryHealthScore(healthScore);
            } else {
                vo.setAvgTurnoverRate(BigDecimal.ZERO);
                vo.setLowStockRatio(BigDecimal.ZERO);
                vo.setInventoryHealthScore(BigDecimal.ZERO);
            }
            result.add(vo);
        }

        result.sort((a, b) -> b.getTotalValue().compareTo(a.getTotalValue()));

        return result;
    }

    private BigDecimal calculateHealthScoreForSupplyPoint(SupplyPointComparisonVO vo) {
        BigDecimal score = BigDecimal.ZERO;

        BigDecimal lowStockRatio = vo.getLowStockRatio() != null ? vo.getLowStockRatio() : BigDecimal.ZERO;
        if (lowStockRatio.compareTo(BigDecimal.valueOf(5)) <= 0) {
            score = score.add(new BigDecimal("40"));
        } else if (lowStockRatio.compareTo(BigDecimal.valueOf(15)) <= 0) {
            score = score.add(new BigDecimal("25"));
        } else if (lowStockRatio.compareTo(BigDecimal.valueOf(30)) <= 0) {
            score = score.add(new BigDecimal("10"));
        }

        BigDecimal turnover = vo.getAvgTurnoverRate() != null ? vo.getAvgTurnoverRate() : BigDecimal.ZERO;
        if (turnover.compareTo(new BigDecimal("1.0")) >= 0) {
            score = score.add(new BigDecimal("30"));
        } else if (turnover.compareTo(new BigDecimal("0.5")) >= 0) {
            score = score.add(new BigDecimal("20"));
        } else if (turnover.compareTo(new BigDecimal("0.2")) >= 0) {
            score = score.add(new BigDecimal("10"));
        }

        BigDecimal totalValue = vo.getTotalValue() != null ? vo.getTotalValue() : BigDecimal.ZERO;
        if (totalValue.compareTo(new BigDecimal("10000")) >= 0) {
            score = score.add(new BigDecimal("30"));
        } else if (totalValue.compareTo(new BigDecimal("5000")) >= 0) {
            score = score.add(new BigDecimal("20"));
        } else if (totalValue.compareTo(new BigDecimal("1000")) >= 0) {
            score = score.add(new BigDecimal("10"));
        }

        return score;
    }
}
