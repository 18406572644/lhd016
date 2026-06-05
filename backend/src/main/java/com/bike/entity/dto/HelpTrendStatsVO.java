package com.bike.entity.dto;

import java.io.Serializable;

public class HelpTrendStatsVO implements Serializable {

    private String date;
    private Integer totalCount;
    private Integer handledCount;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getHandledCount() {
        return handledCount;
    }

    public void setHandledCount(Integer handledCount) {
        this.handledCount = handledCount;
    }
}
