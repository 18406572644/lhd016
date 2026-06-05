package com.bike.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bike.entity.HelpRequest;
import com.bike.entity.RepairShop;
import com.bike.entity.SparePart;
import com.bike.entity.SupplyPoint;
import com.bike.entity.dto.DashboardStatsVO;
import com.bike.mapper.HelpRequestMapper;
import com.bike.mapper.InventoryCheckMapper;
import com.bike.mapper.RepairShopMapper;
import com.bike.mapper.SparePartMapper;
import com.bike.mapper.SupplyPointMapper;
import com.bike.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private SupplyPointMapper supplyPointMapper;

    @Autowired
    private RepairShopMapper repairShopMapper;

    @Autowired
    private SparePartMapper sparePartMapper;

    @Autowired
    private HelpRequestMapper helpRequestMapper;

    @Autowired
    private InventoryCheckMapper inventoryCheckMapper;

    @Override
    public DashboardStatsVO getStats() {
        DashboardStatsVO vo = new DashboardStatsVO();

        Long supplyPointCount = supplyPointMapper.selectCount(new LambdaQueryWrapper<SupplyPoint>());
        vo.setSupplyPointCount(supplyPointCount);

        Long repairShopCount = repairShopMapper.selectCount(new LambdaQueryWrapper<RepairShop>());
        vo.setRepairShopCount(repairShopCount);

        Long lowStockCount = sparePartMapper.selectCount(
                new LambdaQueryWrapper<SparePart>()
                        .apply("stock_quantity <= warning_threshold")
        );
        vo.setLowStockCount(lowStockCount);

        Long pendingHelpCount = helpRequestMapper.selectCount(
                new LambdaQueryWrapper<HelpRequest>()
                        .in(HelpRequest::getStatus, "pending", "processing")
        );
        vo.setPendingHelpCount(pendingHelpCount);

        Long todayCheckCount = inventoryCheckMapper.countTodayChecks(LocalDate.now());
        vo.setTodayCheckCount(todayCheckCount != null ? todayCheckCount : 0L);

        List<SparePart> warningList = sparePartMapper.selectLowStockList();
        vo.setStockWarningList(warningList);

        List<HelpRequest> pendingList = helpRequestMapper.selectPendingList();
        vo.setPendingHelpList(pendingList);

        return vo;
    }
}
