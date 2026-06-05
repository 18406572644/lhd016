package com.bike.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bike.entity.HelpRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface HelpRequestMapper extends BaseMapper<HelpRequest> {

    IPage<HelpRequest> selectPageByCondition(Page<HelpRequest> page, @Param("params") Map<String, Object> params);

    List<HelpRequest> selectPendingList();
}
