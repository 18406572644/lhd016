package com.bike.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bike.common.PageQuery;
import com.bike.entity.OperationLog;

import java.util.Map;

public interface OperationLogService extends IService<OperationLog> {

    PageQuery.PageResult<OperationLog> pageByCondition(Integer pageNum, Integer pageSize, Map<String, Object> params);

    void logOperation(String operator, String operationType, String module, Object beforeData, Object afterData, String ipAddress, String reason);
}
