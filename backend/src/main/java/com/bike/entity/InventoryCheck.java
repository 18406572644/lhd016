package com.bike.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@TableName("inventory_check")
public class InventoryCheck implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("check_no")
    private String checkNo;

    @TableField("supply_point_id")
    private Long supplyPointId;

    @TableField("supply_point_name")
    private String supplyPointName;

    @TableField("check_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkDate;

    private String checker;

    private String status;

    @TableField("total_items")
    private Integer totalItems;

    @TableField("diff_count")
    private Integer diffCount;

    private String remark;

    @TableField("create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @TableField(exist = false)
    private List<CheckDetail> details;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCheckNo() {
        return checkNo;
    }

    public void setCheckNo(String checkNo) {
        this.checkNo = checkNo;
    }

    public Long getSupplyPointId() {
        return supplyPointId;
    }

    public void setSupplyPointId(Long supplyPointId) {
        this.supplyPointId = supplyPointId;
    }

    public String getSupplyPointName() {
        return supplyPointName;
    }

    public void setSupplyPointName(String supplyPointName) {
        this.supplyPointName = supplyPointName;
    }

    public LocalDate getCheckDate() {
        return checkDate;
    }

    public void setCheckDate(LocalDate checkDate) {
        this.checkDate = checkDate;
    }

    public String getChecker() {
        return checker;
    }

    public void setChecker(String checker) {
        this.checker = checker;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(Integer totalItems) {
        this.totalItems = totalItems;
    }

    public Integer getDiffCount() {
        return diffCount;
    }

    public void setDiffCount(Integer diffCount) {
        this.diffCount = diffCount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public List<CheckDetail> getDetails() {
        return details;
    }

    public void setDetails(List<CheckDetail> details) {
        this.details = details;
    }
}
