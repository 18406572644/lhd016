package com.bike.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bike.common.PageQuery;
import com.bike.entity.InventoryCheck;

import java.util.Map;

public interface InventoryCheckService extends IService<InventoryCheck> {

    PageQuery.PageResult<InventoryCheck> pageByCondition(Integer pageNum, Integer pageSize, Map<String, Object> params);

    InventoryCheck getDetailById(Long id);

    void createCheck(InventoryCheck check);

    void completeCheck(Long id, InventoryCheck check);
}
