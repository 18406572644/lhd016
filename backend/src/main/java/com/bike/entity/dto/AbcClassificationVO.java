package com.bike.entity.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class AbcClassificationVO implements Serializable {

    private Long partId;
    private String partCode;
    private String partName;
    private String category;
    private String abcClass;
    private Integer stockQuantity;
    private BigDecimal unitPrice;
    private BigDecimal totalValue;
    private BigDecimal valuePercentage;
    private BigDecimal cumulativePercentage;
    private String supplyPointName;

    public Long getPartId() {
        return partId;
    }

    public void setPartId(Long partId) {
        this.partId = partId;
    }

    public String getPartCode() {
        return partCode;
    }

    public void setPartCode(String partCode) {
        this.partCode = partCode;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAbcClass() {
        return abcClass;
    }

    public void setAbcClass(String abcClass) {
        this.abcClass = abcClass;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(BigDecimal totalValue) {
        this.totalValue = totalValue;
    }

    public BigDecimal getValuePercentage() {
        return valuePercentage;
    }

    public void setValuePercentage(BigDecimal valuePercentage) {
        this.valuePercentage = valuePercentage;
    }

    public BigDecimal getCumulativePercentage() {
        return cumulativePercentage;
    }

    public void setCumulativePercentage(BigDecimal cumulativePercentage) {
        this.cumulativePercentage = cumulativePercentage;
    }

    public String getSupplyPointName() {
        return supplyPointName;
    }

    public void setSupplyPointName(String supplyPointName) {
        this.supplyPointName = supplyPointName;
    }
}
