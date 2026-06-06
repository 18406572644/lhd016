package com.bike.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bike.entity.HelpStatusFlow;

import java.util.List;

public interface HelpStatusFlowService extends IService<HelpStatusFlow> {

    List<HelpStatusFlow> getByHelpRequestId(Long helpRequestId);

    void recordFlow(Long helpRequestId, String fromStatus, String toStatus, String operator, String remark);
}
