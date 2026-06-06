package com.bike.entity.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class SupplyPointComparisonVO implements Serializable {

    private String supplyPointName;
    private Integer totalItems;
    private BigDecimal totalValue;
    private Integer lowStockItems;
    private BigDecimal lowStockRatio;
    private BigDecimal avgTurnoverRate;
    private BigDecimal inventoryHealthScore;
    private Integer stockInQuantity;
    private Integer stockOutQuantity;

    public String getSupplyPointName() {
        return supplyPointName;
    }

    public void setSupplyPointName(String supplyPointName) {
        this.supplyPointName = supplyPointName;
    }

    public Integer getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(Integer totalItems) {
        this.totalItems = totalItems;
    }

    public BigDecimal getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(BigDecimal totalValue) {
        this.totalValue = totalValue;
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

    public BigDecimal getAvgTurnoverRate() {
        return avgTurnoverRate;
    }

    public void setAvgTurnoverRate(BigDecimal avgTurnoverRate) {
        this.avgTurnoverRate = avgTurnoverRate;
    }

    public BigDecimal getInventoryHealthScore() {
        return inventoryHealthScore;
    }

    public void setInventoryHealthScore(BigDecimal inventoryHealthScore) {
        this.inventoryHealthScore = inventoryHealthScore;
    }

    public Integer getStockInQuantity() {
        return stockInQuantity;
    }

    public void setStockInQuantity(Integer stockInQuantity) {
        this.stockInQuantity = stockInQuantity;
    }

    public Integer getStockOutQuantity() {
        return stockOutQuantity;
    }

    public void setStockOutQuantity(Integer stockOutQuantity) {
        this.stockOutQuantity = stockOutQuantity;
    }
}
