package com.bike.entity.dto;

import com.bike.entity.HelpRequest;
import com.bike.entity.RepairShop;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

public class HelpRequestVO extends HelpRequest implements Serializable {

    private RepairShop repairShop;

    private String distanceText;

    private String etaText;

    public RepairShop getRepairShop() {
        return repairShop;
    }

    public void setRepairShop(RepairShop repairShop) {
        this.repairShop = repairShop;
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
