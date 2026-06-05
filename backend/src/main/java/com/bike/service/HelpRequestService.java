package com.bike.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bike.common.PageQuery;
import com.bike.entity.HelpRequest;
import com.bike.entity.dto.HandleHelpDTO;

import java.util.List;
import java.util.Map;

public interface HelpRequestService extends IService<HelpRequest> {

    PageQuery.PageResult<HelpRequest> pageByCondition(Integer pageNum, Integer pageSize, Map<String, Object> params);

    List<HelpRequest> getPendingList();

    void updateStatus(Long id, String status);

    void handleHelp(Long id, HandleHelpDTO dto);
}
