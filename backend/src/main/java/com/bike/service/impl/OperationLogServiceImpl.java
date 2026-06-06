package com.bike.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bike.common.PageQuery;
import com.bike.entity.OperationLog;
import com.bike.mapper.OperationLogMapper;
import com.bike.service.OperationLogService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements OperationLogService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public PageQuery.PageResult<OperationLog> pageByCondition(Integer pageNum, Integer pageSize, Map<String, Object> params) {
        Page<OperationLog> page = new Page<>(pageNum, pageSize);
        IPage<OperationLog> result = baseMapper.selectPageByCondition(page, params);
        return PageQuery.of(result);
    }

    @Override
    public void logOperation(String operator, String operationType, String module, Object beforeData, Object afterData, String ipAddress, String reason) {
        OperationLog log = new OperationLog();
        log.setOperator(operator != null ? operator : "system");
        log.setOperationType(operationType);
        log.setModule(module);
        log.setOperateTime(LocalDateTime.now());
        log.setIpAddress(ipAddress);
        log.setReason(reason);
        log.setCreateTime(LocalDateTime.now());

        Map<String, Object> content = new HashMap<>();
        if (beforeData != null) {
            content.put("before", beforeData);
        }
        if (afterData != null) {
            content.put("after", afterData);
        }
        try {
            log.setOperationContent(objectMapper.writeValueAsString(content));
        } catch (JsonProcessingException e) {
            log.setOperationContent("{}");
        }

        save(log);
    }
}
