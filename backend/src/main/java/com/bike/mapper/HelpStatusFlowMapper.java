package com.bike.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bike.entity.HelpStatusFlow;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HelpStatusFlowMapper extends BaseMapper<HelpStatusFlow> {

    List<HelpStatusFlow> selectByHelpRequestId(@Param("helpRequestId") Long helpRequestId);
}
