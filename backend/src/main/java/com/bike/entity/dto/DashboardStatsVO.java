package com.bike.entity.dto;

import com.bike.entity.HelpRequest;
import com.bike.entity.SparePart;

import java.io.Serializable;
import java.util.List;

public class DashboardStatsVO implements Serializable {

    private Long supplyPointCount;
    private Long repairShopCount;
    private Long lowStockCount;
    private Long pendingHelpCount;
    private Long todayCheckCount;
    private List<SparePart> stockWarningList;
    private List<HelpRequest> pendingHelpList;

    public Long getSupplyPointCount() {
        return supplyPointCount;
    }

    public void setSupplyPointCount(Long supplyPointCount) {
        this.supplyPointCount = supplyPointCount;
    }

    public Long getRepairShopCount() {
        return repairShopCount;
    }

    public void setRepairShopCount(Long repairShopCount) {
        this.repairShopCount = repairShopCount;
    }

    public Long getLowStockCount() {
        return lowStockCount;
    }

    public void setLowStockCount(Long lowStockCount) {
        this.lowStockCount = lowStockCount;
    }

    public Long getPendingHelpCount() {
        return pendingHelpCount;
    }

    public void setPendingHelpCount(Long pendingHelpCount) {
        this.pendingHelpCount = pendingHelpCount;
    }

    public Long getTodayCheckCount() {
        return todayCheckCount;
    }

    public void setTodayCheckCount(Long todayCheckCount) {
        this.todayCheckCount = todayCheckCount;
    }

    public List<SparePart> getStockWarningList() {
        return stockWarningList;
    }

    public void setStockWarningList(List<SparePart> stockWarningList) {
        this.stockWarningList = stockWarningList;
    }

    public List<HelpRequest> getPendingHelpList() {
        return pendingHelpList;
    }

    public void setPendingHelpList(List<HelpRequest> pendingHelpList) {
        this.pendingHelpList = pendingHelpList;
    }
}
