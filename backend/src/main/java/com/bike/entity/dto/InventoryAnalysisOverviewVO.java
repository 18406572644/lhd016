package com.bike.entity.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class InventoryAnalysisOverviewVO implements Serializable {

    private Integer totalItems;
    private BigDecimal totalInventoryValue;
    private Integer totalCategories;
    private Integer totalSupplyPoints;
    private BigDecimal avgTurnoverRate;
    private Integer lowStockItems;
    private BigDecimal lowStockRatio;
    private Integer deadStockItems;
    private BigDecimal deadStockRatio;
    private InventoryHealthScoreVO healthScore;

    public Integer getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(Integer totalItems) {
        this.totalItems = totalItems;
    }

    public BigDecimal getTotalInventoryValue() {
        return totalInventoryValue;
    }

    public void setTotalInventoryValue(BigDecimal totalInventoryValue) {
        this.totalInventoryValue = totalInventoryValue;
    }

    public Integer getTotalCategories() {
        return totalCategories;
    }

    public void setTotalCategories(Integer totalCategories) {
        this.totalCategories = totalCategories;
    }

    public Integer getTotalSupplyPoints() {
        return totalSupplyPoints;
    }

    public void setTotalSupplyPoints(Integer totalSupplyPoints) {
        this.totalSupplyPoints = totalSupplyPoints;
    }

    public BigDecimal getAvgTurnoverRate() {
        return avgTurnoverRate;
    }

    public void setAvgTurnoverRate(BigDecimal avgTurnoverRate) {
        this.avgTurnoverRate = avgTurnoverRate;
    }

    public Integer getLowStockItems() {
        return lowStockItems;
    }

    public void setLowStockItems(Integer lowStockItems) {
        this.lowStockItems = lowStockItems;
    }

    public BigDecimal getLowStockRatio() {
        return lowStockRatio;
    }

    public void setLowStockRatio(BigDecimal lowStockRatio) {
        this.lowStockRatio = lowStockRatio;
    }

    public Integer getDeadStockItems() {
        return deadStockItems;
    }

    public void setDeadStockItems(Integer deadStockItems) {
        this.deadStockItems = deadStockItems;
    }

    public BigDecimal getDeadStockRatio() {
        return deadStockRatio;
    }

    public void setDeadStockRatio(BigDecimal deadStockRatio) {
        this.deadStockRatio = deadStockRatio;
    }

    public InventoryHealthScoreVO getHealthScore() {
        return healthScore;
    }

    public void setHealthScore(InventoryHealthScoreVO healthScore) {
        this.healthScore = healthScore;
    }
}
