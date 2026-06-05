package com.bike.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

@TableName("check_detail")
public class CheckDetail implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("check_id")
    private Long checkId;

    @TableField("part_id")
    private Long partId;

    @TableField("part_name")
    private String partName;

    @TableField("system_quantity")
    private Integer systemQuantity;

    @TableField("actual_quantity")
    private Integer actualQuantity;

    @TableField("diff_quantity")
    private Integer diffQuantity;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCheckId() {
        return checkId;
    }

    public void setCheckId(Long checkId) {
        this.checkId = checkId;
    }

    public Long getPartId() {
        return partId;
    }

    public void setPartId(Long partId) {
        this.partId = partId;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public Integer getSystemQuantity() {
        return systemQuantity;
    }

    public void setSystemQuantity(Integer systemQuantity) {
        this.systemQuantity = systemQuantity;
    }

    public Integer getActualQuantity() {
        return actualQuantity;
    }

    public void setActualQuantity(Integer actualQuantity) {
        this.actualQuantity = actualQuantity;
    }

    public Integer getDiffQuantity() {
        return diffQuantity;
    }

    public void setDiffQuantity(Integer diffQuantity) {
        this.diffQuantity = diffQuantity;
    }
}
