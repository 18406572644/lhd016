package com.bike.entity.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class TurnoverRateVO implements Serializable {

    private String category;
    private String supplyPointName;
    private BigDecimal turnoverRate;
    private String period;
    private Integer outgoingQuantity;
    private BigDecimal averageStock;
    private String turnoverLevel;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSupplyPointName() {
        return supplyPointName;
    }

    public void setSupplyPointName(String supplyPointName) {
        this.supplyPointName = supplyPointName;
    }

    public BigDecimal getTurnoverRate() {
        return turnoverRate;
    }

    public void setTurnoverRate(BigDecimal turnoverRate) {
        this.turnoverRate = turnoverRate;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public Integer getOutgoingQuantity() {
        return outgoingQuantity;
    }

    public void setOutgoingQuantity(Integer outgoingQuantity) {
        this.outgoingQuantity = outgoingQuantity;
    }

    public BigDecimal getAverageStock() {
        return averageStock;
    }

    public void setAverageStock(BigDecimal averageStock) {
        this.averageStock = averageStock;
    }

    public String getTurnoverLevel() {
        return turnoverLevel;
    }

    public void setTurnoverLevel(String turnoverLevel) {
        this.turnoverLevel = turnoverLevel;
    }
}
