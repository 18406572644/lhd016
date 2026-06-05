package com.bike.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bike.entity.StockRecord;
import org.apache.ibatis.annotations.Param;

public interface StockRecordMapper extends BaseMapper<StockRecord> {

    IPage<StockRecord> selectByPartId(Page<StockRecord> page, @Param("partId") Long partId);
}
