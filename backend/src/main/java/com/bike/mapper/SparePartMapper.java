package com.bike.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bike.entity.SparePart;
import com.bike.entity.dto.AbcClassificationVO;
import com.bike.entity.dto.CategoryStatsVO;
import com.bike.entity.dto.InventoryValueVO;
import com.bike.entity.dto.SupplyPointComparisonVO;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface SparePartMapper extends BaseMapper<SparePart> {

    IPage<SparePart> selectPageByCondition(Page<SparePart> page, @Param("params") Map<String, Object> params);

    List<SparePart> selectLowStockList();

    List<SparePart> selectBySupplyPointId(@Param("supplyPointId") Long supplyPointId);

    List<CategoryStatsVO> selectCategoryStats();

    List<InventoryValueVO> selectInventoryValueByCategory();

    List<InventoryValueVO> selectInventoryValueBySupplyPoint();

    List<AbcClassificationVO> selectAbcClassificationRaw();

    List<SparePart> selectDeadStockItems(@Param("deadStockDate") LocalDateTime deadStockDate);

    Integer countLowStockItems();

    Integer countDeadStockItems(@Param("deadStockDate") LocalDateTime deadStockDate);

    BigDecimal selectTotalInventoryValue();

    Integer countDistinctCategories();

    Integer countDistinctSupplyPoints();

    List<SupplyPointComparisonVO> selectSupplyPointComparison(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
