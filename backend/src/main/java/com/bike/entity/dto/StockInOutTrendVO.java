package com.bike.entity.dto;

import java.io.Serializable;

public class StockInOutTrendVO implements Serializable {

    private String month;
    private String category;
    private Integer stockInQuantity;
    private Integer stockOutQuantity;
    private Integer netChange;

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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

    public Integer getNetChange() {
        return netChange;
    }

    public void setNetChange(Integer netChange) {
        this.netChange = netChange;
    }
}
