package com.bike.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bike.entity.SupplyPoint;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

public interface SupplyPointMapper extends BaseMapper<SupplyPoint> {

    IPage<SupplyPoint> selectPageByCondition(Page<SupplyPoint> page, @Param("params") Map<String, Object> params);
}
