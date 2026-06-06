package com.bike.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bike.entity.OperationLog;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

public interface OperationLogMapper extends BaseMapper<OperationLog> {

    IPage<OperationLog> selectPageByCondition(Page<OperationLog> page, @Param("params") Map<String, Object> params);
}
