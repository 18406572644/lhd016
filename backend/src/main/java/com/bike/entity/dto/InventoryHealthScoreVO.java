package com.bike.entity.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class InventoryHealthScoreVO implements Serializable {

    private BigDecimal totalScore;
    private String healthLevel;
    private BigDecimal lowStockRatio;
    private BigDecimal lowStockScore;
    private BigDecimal deadStockRatio;
    private BigDecimal deadStockScore;
    private BigDecimal inventoryDiffRatio;
    private BigDecimal inventoryDiffScore;
    private BigDecimal turnoverScore;
    private Integer totalItems;
    private Integer lowStockItems;
    private Integer deadStockItems;
    private Integer recentCheckCount;
    private Integer diffItems;

    public BigDecimal getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(BigDecimal totalScore) {
        this.totalScore = totalScore;
    }

    public String getHealthLevel() {
        return healthLevel;
    }

    public void setHealthLevel(String healthLevel) {
        this.healthLevel = healthLevel;
    }

    public BigDecimal getLowStockRatio() {
        return lowStockRatio;
    }

    public void setLowStockRatio(BigDecimal lowStockRatio) {
        this.lowStockRatio = lowStockRatio;
    }

    public BigDecimal getLowStockScore() {
        return lowStockScore;
    }

    public void setLowStockScore(BigDecimal lowStockScore) {
        this.lowStockScore = lowStockScore;
    }

    public BigDecimal getDeadStockRatio() {
        return deadStockRatio;
    }

    public void setDeadStockRatio(BigDecimal deadStockRatio) {
        this.deadStockRatio = deadStockRatio;
    }

    public BigDecimal getDeadStockScore() {
        return deadStockScore;
    }

    public void setDeadStockScore(BigDecimal deadStockScore) {
        this.deadStockScore = deadStockScore;
    }

    public BigDecimal getInventoryDiffRatio() {
        return inventoryDiffRatio;
    }

    public void setInventoryDiffRatio(BigDecimal inventoryDiffRatio) {
        this.inventoryDiffRatio = inventoryDiffRatio;
    }

    public BigDecimal getInventoryDiffScore() {
        return inventoryDiffScore;
    }

    public void setInventoryDiffScore(BigDecimal inventoryDiffScore) {
        this.inventoryDiffScore = inventoryDiffScore;
    }

    public BigDecimal getTurnoverScore() {
        return turnoverScore;
    }

    public void setTurnoverScore(BigDecimal turnoverScore) {
        this.turnoverScore = turnoverScore;
    }

    public Integer getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(Integer totalItems) {
        this.totalItems = totalItems;
    }

    public Integer getLowStockItems() {
        return lowStockItems;
    }

    public void setLowStockItems(Integer lowStockItems) {
        this.lowStockItems = lowStockItems;
    }

    public Integer getDeadStockItems() {
        return deadStockItems;
    }

    public void setDeadStockItems(Integer deadStockItems) {
        this.deadStockItems = deadStockItems;
    }

    public Integer getRecentCheckCount() {
        return recentCheckCount;
    }

    public void setRecentCheckCount(Integer recentCheckCount) {
        this.recentCheckCount = recentCheckCount;
    }

    public Integer getDiffItems() {
        return diffItems;
    }

    public void setDiffItems(Integer diffItems) {
        this.diffItems = diffItems;
    }
}
