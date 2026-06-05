package com.bike.entity.dto;

import com.bike.entity.RepairShop;

import java.io.Serializable;

public class RepairShopRecommendation implements Serializable {

    private RepairShop repairShop;

    private Double distance;

    private Integer estimatedArrivalTime;

    private Integer currentWorkload;

    private Integer maxWorkload;

    private Double workloadRatio;

    private Double score;

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

    public Integer getEstimatedArrivalTime() {
        return estimatedArrivalTime;
    }

    public void setEstimatedArrivalTime(Integer estimatedArrivalTime) {
        this.estimatedArrivalTime = estimatedArrivalTime;
    }

    public Integer getCurrentWorkload() {
        return currentWorkload;
    }

    public void setCurrentWorkload(Integer currentWorkload) {
        this.currentWorkload = currentWorkload;
    }

    public Integer getMaxWorkload() {
        return maxWorkload;
    }

    public void setMaxWorkload(Integer maxWorkload) {
        this.maxWorkload = maxWorkload;
    }

    public Double getWorkloadRatio() {
        return workloadRatio;
    }

    public void setWorkloadRatio(Double workloadRatio) {
        this.workloadRatio = workloadRatio;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}
