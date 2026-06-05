package com.bike.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bike.common.PageQuery;
import com.bike.entity.SupplyPoint;

import java.util.List;
import java.util.Map;

public interface SupplyPointService extends IService<SupplyPoint> {

    PageQuery.PageResult<SupplyPoint> pageByCondition(Integer pageNum, Integer pageSize, Map<String, Object> params);

    List<SupplyPoint> listAll();
}
