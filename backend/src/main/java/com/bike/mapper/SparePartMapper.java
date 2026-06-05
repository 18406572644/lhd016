package com.bike.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bike.entity.SparePart;
import com.bike.entity.dto.CategoryStatsVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface SparePartMapper extends BaseMapper<SparePart> {

    IPage<SparePart> selectPageByCondition(Page<SparePart> page, @Param("params") Map<String, Object> params);

    List<SparePart> selectLowStockList();

    List<SparePart> selectBySupplyPointId(@Param("supplyPointId") Long supplyPointId);

    List<CategoryStatsVO> selectCategoryStats();
}
