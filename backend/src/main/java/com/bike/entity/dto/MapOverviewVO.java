package com.bike.entity.dto;

import com.bike.entity.HelpRequest;
import com.bike.entity.RepairShop;
import com.bike.entity.SupplyPoint;

import java.io.Serializable;
import java.util.List;

public class MapOverviewVO implements Serializable {

    private List<SupplyPointWithWarning> supplyPoints;

    private List<RepairShop> repairShops;

    private List<HelpRequestWithRoute> helpRequests;

    public List<SupplyPointWithWarning> getSupplyPoints() {
        return supplyPoints;
    }

    public void setSupplyPoints(List<SupplyPointWithWarning> supplyPoints) {
        this.supplyPoints = supplyPoints;
    }

    public List<RepairShop> getRepairShops() {
        return repairShops;
    }

    public void setRepairShops(List<RepairShop> repairShops) {
        this.repairShops = repairShops;
    }

    public List<HelpRequestWithRoute> getHelpRequests() {
        return helpRequests;
    }

    public void setHelpRequests(List<HelpRequestWithRoute> helpRequests) {
        this.helpRequests = helpRequests;
    }

    public static class SupplyPointWithWarning extends SupplyPoint {
        private boolean hasLowStock;
        private Integer lowStockCount;

        public boolean isHasLowStock() {
            return hasLowStock;
        }

        public void setHasLowStock(boolean hasLowStock) {
            this.hasLowStock = hasLowStock;
        }

        public Integer getLowStockCount() {
            return lowStockCount;
        }

        public void setLowStockCount(Integer lowStockCount) {
            this.lowStockCount = lowStockCount;
        }
    }

    public static class HelpRequestWithRoute extends HelpRequest {
        private RepairShop repairShop;
        private Double distance;
        private String distanceText;
        private String etaText;

        public RepairShop getRepairShop() {
            return repairShop;
        }

        public void setRepairShop(RepairShop repairShop) {
            this.repairShop = repairShop;
        }

        public Double getDistance() {
            return distance;
        }

        public void setDistance(Double distance) {
            this.distance = distance;
        }

        public String getDistanceText() {
            return distanceText;
        }

        public void setDistanceText(String distanceText) {
            this.distanceText = distanceText;
        }

        public String getEtaText() {
            return etaText;
        }

        public void setEtaText(String etaText) {
            this.etaText = etaText;
        }
    }
}
