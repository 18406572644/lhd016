package com.bike.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bike.common.BusinessException;
import com.bike.common.DistanceUtil;
import com.bike.common.PageQuery;
import com.bike.entity.HelpRequest;
import com.bike.entity.RepairShop;
import com.bike.entity.dto.DispatchDTO;
import com.bike.entity.dto.HandleHelpDTO;
import com.bike.entity.dto.HelpRequestVO;
import com.bike.entity.dto.RepairShopRecommendation;
import com.bike.mapper.HelpRequestMapper;
import com.bike.mapper.RepairShopMapper;
import com.bike.service.HelpRequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class HelpRequestServiceImpl extends ServiceImpl<HelpRequestMapper, HelpRequest> implements HelpRequestService {

    private static final Logger logger = LoggerFactory.getLogger(HelpRequestServiceImpl.class);

    private static final double MAX_SEARCH_DISTANCE = 50.0;

    private static final double DISTANCE_WEIGHT = 0.5;
    private static final double LEVEL_WEIGHT = 0.3;
    private static final double WORKLOAD_WEIGHT = 0.2;

    @Autowired
    private RepairShopMapper repairShopMapper;

    @Override
    public PageQuery.PageResult<HelpRequest> pageByCondition(Integer pageNum, Integer pageSize, Map<String, Object> params) {
        Page<HelpRequest> page = new Page<>(pageNum, pageSize);
        IPage<HelpRequest> result = baseMapper.selectPageByCondition(page, params);
        return PageQuery.of(result);
    }

    @Override
    public PageQuery.PageResult<HelpRequestVO> pageWithRepairShop(Integer pageNum, Integer pageSize, Map<String, Object> params) {
        Page<HelpRequestVO> page = new Page<>(pageNum, pageSize);
        IPage<HelpRequestVO> result = baseMapper.selectPageWithRepairShop(page, params);
        List<HelpRequestVO> records = result.getRecords();
        for (HelpRequestVO vo : records) {
            enrichVO(vo);
        }
        return PageQuery.of(result);
    }

    @Override
    public List<HelpRequest> getPendingList() {
        return baseMapper.selectPendingList();
    }

    @Override
    public void updateStatus(Long id, String status) {
        HelpRequest request = getById(id);
        if (request == null) {
            throw new BusinessException("求助记录不存在");
        }
        request.setStatus(status);
        if ("completed".equals(status)) {
            request.setHandleTime(LocalDateTime.now());
        }
        updateById(request);
    }

    @Override
    public void handleHelp(Long id, HandleHelpDTO dto) {
        HelpRequest request = getById(id);
        if (request == null) {
            throw new BusinessException("求助记录不存在");
        }
        if (dto.getStatus() != null) {
            request.setStatus(dto.getStatus());
        }
        if (dto.getHandler() != null) {
            request.setHandler(dto.getHandler());
        }
        if (dto.getHandleResult() != null) {
            request.setHandleResult(dto.getHandleResult());
        }
        if ("completed".equals(request.getStatus())) {
            request.setHandleTime(LocalDateTime.now());
        }
        updateById(request);
    }

    @Override
    public boolean save(HelpRequest entity) {
        entity.setCreateTime(LocalDateTime.now());
        if (entity.getStatus() == null) {
            entity.setStatus("pending");
        }
        return super.save(entity);
    }

    @Override
    public List<RepairShopRecommendation> getRecommendedShops(BigDecimal longitude, BigDecimal latitude, String helpType) {
        if (longitude == null || latitude == null) {
            throw new BusinessException("请提供经纬度信息");
        }

        List<RepairShop> allShops = repairShopMapper.selectList(null);
        if (allShops.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, Integer> workloadMap = getWorkloadMap();

        List<RepairShopRecommendation> recommendations = new ArrayList<>();

        for (RepairShop shop : allShops) {
            if (shop.getLongitude() == null || shop.getLatitude() == null) {
                continue;
            }

            double distance = DistanceUtil.calculateDistance(longitude, latitude, shop.getLongitude(), shop.getLatitude());
            if (distance > MAX_SEARCH_DISTANCE) {
                continue;
            }

            if (helpType != null && !helpType.isEmpty() && shop.getServiceScope() != null) {
                List<String> scopes = Arrays.asList(shop.getServiceScope().split(","));
                if (!scopes.contains(helpType.trim())) {
                    continue;
                }
            }

            RepairShopRecommendation recommendation = new RepairShopRecommendation();
            recommendation.setRepairShop(shop);
            recommendation.setDistance(distance);

            int eta = DistanceUtil.estimateArrivalTime(distance);
            recommendation.setEstimatedArrivalTime(eta);

            int currentWorkload = workloadMap.getOrDefault(shop.getId(), 0);
            recommendation.setCurrentWorkload(currentWorkload);

            int maxWorkload = shop.getStaffCount() != null ? shop.getStaffCount() * 3 : 6;
            recommendation.setMaxWorkload(maxWorkload);

            double workloadRatio = maxWorkload > 0 ? (double) currentWorkload / maxWorkload : 0;
            recommendation.setWorkloadRatio(workloadRatio);

            double score = calculateScore(distance, shop.getLevel(), workloadRatio);
            recommendation.setScore(score);

            recommendations.add(recommendation);
        }

        recommendations.sort((r1, r2) -> Double.compare(r2.getScore(), r1.getScore()));

        return recommendations;
    }

    @Override
    public void dispatchHelpRequest(DispatchDTO dto) {
        if (dto.getHelpRequestId() == null) {
            throw new BusinessException("求助ID不能为空");
        }

        HelpRequest request = getById(dto.getHelpRequestId());
        if (request == null) {
            throw new BusinessException("求助记录不存在");
        }

        BigDecimal longitude = dto.getLongitude() != null ? dto.getLongitude() : request.getLongitude();
        BigDecimal latitude = dto.getLatitude() != null ? dto.getLatitude() : request.getLatitude();
        String helpType = dto.getHelpType() != null ? dto.getHelpType() : request.getHelpType();

        Long repairShopId = dto.getRepairShopId();
        RepairShop selectedShop = null;
        Integer estimatedArrivalTime = null;

        if (repairShopId == null) {
            List<RepairShopRecommendation> recommendations = getRecommendedShops(longitude, latitude, helpType);
            if (recommendations.isEmpty()) {
                throw new BusinessException("没有找到合适的维修点");
            }
            RepairShopRecommendation best = recommendations.get(0);
            selectedShop = best.getRepairShop();
            repairShopId = selectedShop.getId();
            estimatedArrivalTime = best.getEstimatedArrivalTime();
        } else {
            selectedShop = repairShopMapper.selectById(repairShopId);
            if (selectedShop == null) {
                throw new BusinessException("维修点不存在");
            }
            if (longitude != null && latitude != null && selectedShop.getLongitude() != null && selectedShop.getLatitude() != null) {
                double distance = DistanceUtil.calculateDistance(longitude, latitude, selectedShop.getLongitude(), selectedShop.getLatitude());
                estimatedArrivalTime = DistanceUtil.estimateArrivalTime(distance);
            }
        }

        request.setRepairShopId(repairShopId);
        request.setEstimatedArrivalTime(estimatedArrivalTime);
        if (request.getLongitude() == null) {
            request.setLongitude(longitude);
        }
        if (request.getLatitude() == null) {
            request.setLatitude(latitude);
        }
        request.setStatus("processing");

        updateById(request);

        if (Boolean.TRUE.equals(dto.getNotifyContact()) && selectedShop != null) {
            notifyRepairShop(selectedShop, request);
        }
    }

    @Override
    public void adjustDispatch(Long helpRequestId, Long repairShopId, Boolean notify) {
        if (helpRequestId == null || repairShopId == null) {
            throw new BusinessException("求助ID和维修点ID不能为空");
        }

        HelpRequest request = getById(helpRequestId);
        if (request == null) {
            throw new BusinessException("求助记录不存在");
        }

        RepairShop shop = repairShopMapper.selectById(repairShopId);
        if (shop == null) {
            throw new BusinessException("维修点不存在");
        }

        Integer estimatedArrivalTime = null;
        if (request.getLongitude() != null && request.getLatitude() != null
                && shop.getLongitude() != null && shop.getLatitude() != null) {
            double distance = DistanceUtil.calculateDistance(
                    request.getLongitude(), request.getLatitude(),
                    shop.getLongitude(), shop.getLatitude()
            );
            estimatedArrivalTime = DistanceUtil.estimateArrivalTime(distance);
        }

        request.setRepairShopId(repairShopId);
        request.setEstimatedArrivalTime(estimatedArrivalTime);
        updateById(request);

        if (Boolean.TRUE.equals(notify)) {
            notifyRepairShop(shop, request);
        }
    }

    @Override
    public HelpRequestVO getDetailWithRepairShop(Long id) {
        HelpRequestVO vo = baseMapper.selectDetailWithRepairShop(id);
        if (vo == null) {
            throw new BusinessException("求助记录不存在");
        }
        enrichVO(vo);
        return vo;
    }

    private Map<Long, Integer> getWorkloadMap() {
        List<Map<String, Object>> workloadList = baseMapper.selectWorkloadByShop();
        Map<Long, Integer> workloadMap = new HashMap<>();
        for (Map<String, Object> map : workloadList) {
            Long shopId = ((Number) map.get("shopId")).longValue();
            Integer workload = ((Number) map.get("workload")).intValue();
            workloadMap.put(shopId, workload);
        }
        return workloadMap;
    }

    private double calculateScore(double distance, String level, double workloadRatio) {
        double distanceScore = 1 - (distance / MAX_SEARCH_DISTANCE);

        double levelScore = 0.0;
        if ("一级".equals(level)) {
            levelScore = 1.0;
        } else if ("二级".equals(level)) {
            levelScore = 0.7;
        } else if ("三级".equals(level)) {
            levelScore = 0.4;
        }

        double workloadScore = 1 - Math.min(workloadRatio, 1.0);

        return distanceScore * DISTANCE_WEIGHT + levelScore * LEVEL_WEIGHT + workloadScore * WORKLOAD_WEIGHT;
    }

    private void notifyRepairShop(RepairShop shop, HelpRequest request) {
        logger.info("通知维修点 [{}] 负责人 [{}] 电话 [{}]：收到新的求助工单 #{}，类型：{}，位置：{}，预计到达时间：{}分钟",
                shop.getName(),
                shop.getContactPerson(),
                shop.getContactPhone(),
                request.getId(),
                request.getHelpType(),
                request.getLocation(),
                request.getEstimatedArrivalTime()
        );
    }

    private void enrichVO(HelpRequestVO vo) {
        if (vo.getLongitude() != null && vo.getLatitude() != null
                && vo.getRepairShop() != null
                && vo.getRepairShop().getLongitude() != null
                && vo.getRepairShop().getLatitude() != null) {
            double distance = DistanceUtil.calculateDistance(
                    vo.getLongitude(), vo.getLatitude(),
                    vo.getRepairShop().getLongitude(), vo.getRepairShop().getLatitude()
            );
            if (distance >= 0) {
                vo.setDistanceText(String.format("%.2f公里", distance));
                int eta = DistanceUtil.estimateArrivalTime(distance);
                vo.setEtaText(String.format("%d分钟", eta));
            }
        }
    }
}
