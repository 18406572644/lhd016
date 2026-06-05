package com.bike.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bike.common.PageQuery;
import com.bike.entity.SupplyPoint;
import com.bike.mapper.SupplyPointMapper;
import com.bike.service.SupplyPointService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class SupplyPointServiceImpl extends ServiceImpl<SupplyPointMapper, SupplyPoint> implements SupplyPointService {

    @Override
    public PageQuery.PageResult<SupplyPoint> pageByCondition(Integer pageNum, Integer pageSize, Map<String, Object> params) {
        Page<SupplyPoint> page = new Page<>(pageNum, pageSize);
        IPage<SupplyPoint> result = baseMapper.selectPageByCondition(page, params);
        return PageQuery.of(result);
    }

    @Override
    public List<SupplyPoint> listAll() {
        return list();
    }

    @Override
    public boolean save(SupplyPoint entity) {
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        return super.save(entity);
    }

    @Override
    public boolean updateById(SupplyPoint entity) {
        entity.setUpdateTime(LocalDateTime.now());
        return super.updateById(entity);
    }
}
