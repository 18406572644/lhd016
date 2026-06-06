package com.bike.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bike.common.DistanceUtil;
import com.bike.entity.HelpRequest;
import com.bike.entity.RepairShop;
import com.bike.entity.SparePart;
import com.bike.entity.SupplyPoint;
import com.bike.entity.dto.MapOverviewVO;
import com.bike.mapper.HelpRequestMapper;
import com.bike.mapper.RepairShopMapper;
import com.bike.mapper.SparePartMapper;
import com.bike.mapper.SupplyPointMapper;
import com.bike.service.MapService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MapServiceImpl implements MapService {

    @Autowired
    private SupplyPointMapper supplyPointMapper;

    @Autowired
    private RepairShopMapper repairShopMapper;

    @Autowired
    private HelpRequestMapper helpRequestMapper;

    @Autowired
    private SparePartMapper sparePartMapper;

    @Override
    public MapOverviewVO getMapOverview() {
        MapOverviewVO vo = new MapOverviewVO();

        List<SupplyPoint> supplyPoints = supplyPointMapper.selectList(null);
        List<RepairShop> repairShops = repairShopMapper.selectList(null);
        List<HelpRequest> activeHelpRequests = helpRequestMapper.selectList(
                new LambdaQueryWrapper<HelpRequest>()
                        .in(HelpRequest::getStatus, "pending", "processing")
        );

        List<SparePart> lowStockParts = sparePartMapper.selectList(
                new LambdaQueryWrapper<SparePart>()
                        .apply("stock_quantity <= warning_threshold")
        );

        Map<Long, List<SparePart>> lowStockBySupplyPoint = lowStockParts.stream()
                .filter(p -> p.getSupplyPointId() != null)
                .collect(Collectors.groupingBy(SparePart::getSupplyPointId));

        List<MapOverviewVO.SupplyPointWithWarning> supplyPointWithWarnings = new ArrayList<>();
        for (SupplyPoint sp : supplyPoints) {
            MapOverviewVO.SupplyPointWithWarning warning = new MapOverviewVO.SupplyPointWithWarning();
            BeanUtils.copyProperties(sp, warning);
            List<SparePart> parts = lowStockBySupplyPoint.get(sp.getId());
            if (parts != null && !parts.isEmpty()) {
                warning.setHasLowStock(true);
                warning.setLowStockCount(parts.size());
            } else {
                warning.setHasLowStock(false);
                warning.setLowStockCount(0);
            }
            supplyPointWithWarnings.add(warning);
        }

        Map<Long, RepairShop> repairShopMap = repairShops.stream()
                .collect(Collectors.toMap(RepairShop::getId, rs -> rs));

        List<MapOverviewVO.HelpRequestWithRoute> helpRequestWithRoutes = new ArrayList<>();
        for (HelpRequest hr : activeHelpRequests) {
            MapOverviewVO.HelpRequestWithRoute route = new MapOverviewVO.HelpRequestWithRoute();
            BeanUtils.copyProperties(hr, route);

            if (hr.getRepairShopId() != null) {
                RepairShop shop = repairShopMap.get(hr.getRepairShopId());
                if (shop != null) {
                    route.setRepairShop(shop);

                    if (hr.getLongitude() != null && hr.getLatitude() != null
                            && shop.getLongitude() != null && shop.getLatitude() != null) {
                        double distance = DistanceUtil.calculateDistance(
                                hr.getLongitude(), hr.getLatitude(),
                                shop.getLongitude(), shop.getLatitude()
                        );
                        if (distance >= 0) {
                            route.setDistance(distance);
                            route.setDistanceText(String.format("%.2f公里", distance));
                            int eta = DistanceUtil.estimateArrivalTime(distance);
                            if (eta >= 0) {
                                route.setEtaText(String.format("%d分钟", eta));
                            }
                        }
                    }
                }
            }
            helpRequestWithRoutes.add(route);
        }

        vo.setSupplyPoints(supplyPointWithWarnings);
        vo.setRepairShops(repairShops);
        vo.setHelpRequests(helpRequestWithRoutes);

        return vo;
    }
}
