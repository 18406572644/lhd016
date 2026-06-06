package com.bike.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bike.entity.InventoryCheck;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

public interface InventoryCheckMapper extends BaseMapper<InventoryCheck> {

    IPage<InventoryCheck> selectPageByCondition(Page<InventoryCheck> page, @Param("params") Map<String, Object> params);

    Long countTodayChecks(@Param("today") LocalDate today);

    String generateCheckNo();

    Map<String, Object> selectRecentCheckStats(@Param("startDate") LocalDateTime startDate);

    Integer countRecentCompletedChecks(@Param("startDate") LocalDateTime startDate);

    Integer selectTotalDiffItems(@Param("startDate") LocalDateTime startDate);

    Integer selectTotalCheckedItems(@Param("startDate") LocalDateTime startDate);
}
