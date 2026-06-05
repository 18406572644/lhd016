package com.bike.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bike.common.BusinessException;
import com.bike.common.PageQuery;
import com.bike.entity.CheckDetail;
import com.bike.entity.InventoryCheck;
import com.bike.entity.SparePart;
import com.bike.mapper.CheckDetailMapper;
import com.bike.mapper.InventoryCheckMapper;
import com.bike.mapper.SparePartMapper;
import com.bike.service.InventoryCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class InventoryCheckServiceImpl extends ServiceImpl<InventoryCheckMapper, InventoryCheck> implements InventoryCheckService {

    @Autowired
    private CheckDetailMapper checkDetailMapper;

    @Autowired
    private SparePartMapper sparePartMapper;

    @Override
    public PageQuery.PageResult<InventoryCheck> pageByCondition(Integer pageNum, Integer pageSize, Map<String, Object> params) {
        Page<InventoryCheck> page = new Page<>(pageNum, pageSize);
        IPage<InventoryCheck> result = baseMapper.selectPageByCondition(page, params);
        return PageQuery.of(result);
    }

    @Override
    public InventoryCheck getDetailById(Long id) {
        InventoryCheck check = getById(id);
        if (check == null) {
            throw new BusinessException("盘点单不存在");
        }
        List<CheckDetail> details = checkDetailMapper.selectByCheckId(id);
        check.setDetails(details);
        return check;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createCheck(InventoryCheck check) {
        String checkNo = baseMapper.generateCheckNo();
        if (checkNo == null) {
            checkNo = "PD" + LocalDate.now().toString().replace("-", "") + "001";
        }
        check.setCheckNo(checkNo);
        check.setCreateTime(LocalDateTime.now());
        check.setStatus("draft");

        List<SparePart> parts = sparePartMapper.selectBySupplyPointId(check.getSupplyPointId());
        if (parts.isEmpty()) {
            throw new BusinessException("该站点暂无配件，无法创建盘点单");
        }

        if (check.getSupplyPointName() == null) {
            check.setSupplyPointName(parts.get(0).getSupplyPointName());
        }

        save(check);

        int totalItems = 0;
        int diffCount = 0;
        for (SparePart part : parts) {
            CheckDetail detail = new CheckDetail();
            detail.setCheckId(check.getId());
            detail.setPartId(part.getId());
            detail.setPartName(part.getPartName());
            detail.setSystemQuantity(part.getStockQuantity());
            detail.setActualQuantity(part.getStockQuantity());
            detail.setDiffQuantity(0);
            checkDetailMapper.insert(detail);
            totalItems++;
        }

        check.setTotalItems(totalItems);
        check.setDiffCount(diffCount);
        updateById(check);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeCheck(Long id, InventoryCheck check) {
        InventoryCheck existing = getById(id);
        if (existing == null) {
            throw new BusinessException("盘点单不存在");
        }
        if ("completed".equals(existing.getStatus())) {
            throw new BusinessException("该盘点单已完成，不可修改");
        }

        checkDetailMapper.deleteByCheckId(id);

        int totalItems = 0;
        int diffCount = 0;
        if (check.getDetails() != null) {
            for (CheckDetail detail : check.getDetails()) {
                detail.setCheckId(id);
                detail.setDiffQuantity(detail.getActualQuantity() - detail.getSystemQuantity());
                checkDetailMapper.insert(detail);
                totalItems++;
                if (detail.getDiffQuantity() != 0) {
                    diffCount++;
                }
            }
        }

        existing.setTotalItems(totalItems);
        existing.setDiffCount(diffCount);
        existing.setStatus("completed");
        existing.setRemark(check.getRemark());
        updateById(existing);
    }
}
