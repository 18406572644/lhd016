package com.bike.entity.dto;

import java.io.Serializable;

public class SensitiveOperationDTO implements Serializable {

    private String reason;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
