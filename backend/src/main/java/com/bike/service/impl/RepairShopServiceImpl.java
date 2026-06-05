package com.bike.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bike.common.PageQuery;
import com.bike.entity.RepairShop;
import com.bike.mapper.RepairShopMapper;
import com.bike.service.RepairShopService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class RepairShopServiceImpl extends ServiceImpl<RepairShopMapper, RepairShop> implements RepairShopService {

    @Override
    public PageQuery.PageResult<RepairShop> pageByCondition(Integer pageNum, Integer pageSize, Map<String, Object> params) {
        Page<RepairShop> page = new Page<>(pageNum, pageSize);
        IPage<RepairShop> result = baseMapper.selectPageByCondition(page, params);
        return PageQuery.of(result);
    }

    @Override
    public boolean save(RepairShop entity) {
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        return super.save(entity);
    }

    @Override
    public boolean updateById(RepairShop entity) {
        entity.setUpdateTime(LocalDateTime.now());
        return super.updateById(entity);
    }
}
