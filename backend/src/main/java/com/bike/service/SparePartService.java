package com.bike.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bike.common.PageQuery;
import com.bike.entity.SparePart;
import com.bike.entity.StockRecord;
import com.bike.entity.dto.StockInOutDTO;

import java.util.List;
import java.util.Map;

public interface SparePartService extends IService<SparePart> {

    PageQuery.PageResult<SparePart> pageByCondition(Integer pageNum, Integer pageSize, Map<String, Object> params);

    List<SparePart> getLowStockList();

    List<SparePart> getBySupplyPointId(Long supplyPointId);

    void stockIn(Long id, StockInOutDTO dto);

    void stockOut(Long id, StockInOutDTO dto);

    PageQuery.PageResult<StockRecord> getStockRecords(Integer pageNum, Integer pageSize, Long partId);
}
