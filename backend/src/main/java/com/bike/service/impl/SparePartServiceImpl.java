package com.bike.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bike.common.BusinessException;
import com.bike.common.PageQuery;
import com.bike.entity.SparePart;
import com.bike.entity.StockRecord;
import com.bike.entity.dto.StockInOutDTO;
import com.bike.mapper.SparePartMapper;
import com.bike.mapper.StockRecordMapper;
import com.bike.service.SparePartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class SparePartServiceImpl extends ServiceImpl<SparePartMapper, SparePart> implements SparePartService {

    @Autowired
    private StockRecordMapper stockRecordMapper;

    @Override
    public PageQuery.PageResult<SparePart> pageByCondition(Integer pageNum, Integer pageSize, Map<String, Object> params) {
        Page<SparePart> page = new Page<>(pageNum, pageSize);
        IPage<SparePart> result = baseMapper.selectPageByCondition(page, params);
        return PageQuery.of(result);
    }

    @Override
    public List<SparePart> getLowStockList() {
        return baseMapper.selectLowStockList();
    }

    @Override
    public List<SparePart> getBySupplyPointId(Long supplyPointId) {
        return baseMapper.selectBySupplyPointId(supplyPointId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void stockIn(Long id, StockInOutDTO dto) {
        SparePart part = getById(id);
        if (part == null) {
            throw new BusinessException("配件不存在");
        }
        part.setStockQuantity(part.getStockQuantity() + dto.getQuantity());
        part.setUpdateTime(LocalDateTime.now());
        updateById(part);

        StockRecord record = new StockRecord();
        record.setPartId(id);
        record.setType("in");
        record.setQuantity(dto.getQuantity());
        record.setOperator(dto.getOperator());
        record.setOperateTime(LocalDateTime.now());
        record.setRemark(dto.getRemark());
        stockRecordMapper.insert(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void stockOut(Long id, StockInOutDTO dto) {
        SparePart part = getById(id);
        if (part == null) {
            throw new BusinessException("配件不存在");
        }
        if (part.getStockQuantity() < dto.getQuantity()) {
            throw new BusinessException("库存不足，当前库存: " + part.getStockQuantity());
        }
        part.setStockQuantity(part.getStockQuantity() - dto.getQuantity());
        part.setUpdateTime(LocalDateTime.now());
        updateById(part);

        StockRecord record = new StockRecord();
        record.setPartId(id);
        record.setType("out");
        record.setQuantity(dto.getQuantity());
        record.setOperator(dto.getOperator());
        record.setOperateTime(LocalDateTime.now());
        record.setRemark(dto.getRemark());
        stockRecordMapper.insert(record);
    }

    @Override
    public PageQuery.PageResult<StockRecord> getStockRecords(Integer pageNum, Integer pageSize, Long partId) {
        Page<StockRecord> page = new Page<>(pageNum, pageSize);
        IPage<StockRecord> result = stockRecordMapper.selectByPartId(page, partId);
        return PageQuery.of(result);
    }

    @Override
    public boolean save(SparePart entity) {
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        return super.save(entity);
    }

    @Override
    public boolean updateById(SparePart entity) {
        entity.setUpdateTime(LocalDateTime.now());
        return super.updateById(entity);
    }
}
