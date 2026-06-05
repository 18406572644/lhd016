package com.bike.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bike.entity.RepairShop;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

public interface RepairShopMapper extends BaseMapper<RepairShop> {

    IPage<RepairShop> selectPageByCondition(Page<RepairShop> page, @Param("params") Map<String, Object> params);
}
