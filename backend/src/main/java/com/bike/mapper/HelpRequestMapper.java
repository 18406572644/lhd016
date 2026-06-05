package com.bike.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bike.entity.HelpRequest;
import com.bike.entity.dto.HelpRequestVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface HelpRequestMapper extends BaseMapper<HelpRequest> {

    IPage<HelpRequest> selectPageByCondition(Page<HelpRequest> page, @Param("params") Map<String, Object> params);

    List<HelpRequest> selectPendingList();

    List<Map<String, Object>> selectWorkloadByShop();

    IPage<HelpRequestVO> selectPageWithRepairShop(Page<HelpRequestVO> page, @Param("params") Map<String, Object> params);

    HelpRequestVO selectDetailWithRepairShop(@Param("id") Long id);
}
