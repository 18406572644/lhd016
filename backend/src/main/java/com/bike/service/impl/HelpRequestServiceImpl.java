package com.bike.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bike.common.BusinessException;
import com.bike.common.PageQuery;
import com.bike.entity.HelpRequest;
import com.bike.entity.dto.HandleHelpDTO;
import com.bike.mapper.HelpRequestMapper;
import com.bike.service.HelpRequestService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class HelpRequestServiceImpl extends ServiceImpl<HelpRequestMapper, HelpRequest> implements HelpRequestService {

    @Override
    public PageQuery.PageResult<HelpRequest> pageByCondition(Integer pageNum, Integer pageSize, Map<String, Object> params) {
        Page<HelpRequest> page = new Page<>(pageNum, pageSize);
        IPage<HelpRequest> result = baseMapper.selectPageByCondition(page, params);
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
}
