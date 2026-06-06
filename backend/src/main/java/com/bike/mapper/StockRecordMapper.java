package com.bike.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bike.entity.StockRecord;
import com.bike.entity.dto.StockInOutTrendVO;
import com.bike.entity.dto.TurnoverRateVO;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface StockRecordMapper extends BaseMapper<StockRecord> {

    IPage<StockRecord> selectByPartId(Page<StockRecord> page, @Param("partId") Long partId);

    List<TurnoverRateVO> selectTurnoverRateByCategory(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("period") String period,
            @Param("supplyPointId") Long supplyPointId
    );

    List<TurnoverRateVO> selectTurnoverRateBySupplyPoint(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("period") String period,
            @Param("category") String category
    );

    List<StockInOutTrendVO> selectStockInOutTrend(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("category") String category,
            @Param("supplyPointId") Long supplyPointId
    );

    BigDecimal selectAvgTurnoverRate(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
