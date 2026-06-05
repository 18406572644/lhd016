package com.bike.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bike.common.PageQuery;
import com.bike.entity.RepairShop;

import java.util.Map;

public interface RepairShopService extends IService<RepairShop> {

    PageQuery.PageResult<RepairShop> pageByCondition(Integer pageNum, Integer pageSize, Map<String, Object> params);
}
