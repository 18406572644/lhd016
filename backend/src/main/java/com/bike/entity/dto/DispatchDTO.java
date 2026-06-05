package com.bike.entity.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class DispatchDTO implements Serializable {

    private Long helpRequestId;

    private Long repairShopId;

    private BigDecimal longitude;

    private BigDecimal latitude;

    private String helpType;

    private Boolean notifyContact = true;

    public Long getHelpRequestId() {
        return helpRequestId;
    }

    public void setHelpRequestId(Long helpRequestId) {
        this.helpRequestId = helpRequestId;
    }

    public Long getRepairShopId() {
        return repairShopId;
    }

    public void setRepairShopId(Long repairShopId) {
        this.repairShopId = repairShopId;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public String getHelpType() {
        return helpType;
    }

    public void setHelpType(String helpType) {
        this.helpType = helpType;
    }

    public Boolean getNotifyContact() {
        return notifyContact;
    }

    public void setNotifyContact(Boolean notifyContact) {
        this.notifyContact = notifyContact;
    }
}
