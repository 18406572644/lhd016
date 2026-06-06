package com.bike.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bike.entity.HelpStatusFlow;
import com.bike.mapper.HelpStatusFlowMapper;
import com.bike.service.HelpStatusFlowService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class HelpStatusFlowServiceImpl extends ServiceImpl<HelpStatusFlowMapper, HelpStatusFlow> implements HelpStatusFlowService {

    @Override
    public List<HelpStatusFlow> getByHelpRequestId(Long helpRequestId) {
        return baseMapper.selectByHelpRequestId(helpRequestId);
    }

    @Override
    public void recordFlow(Long helpRequestId, String fromStatus, String toStatus, String operator, String remark) {
        HelpStatusFlow flow = new HelpStatusFlow();
        flow.setHelpRequestId(helpRequestId);
        flow.setFromStatus(fromStatus);
        flow.setToStatus(toStatus);
        flow.setOperator(operator != null ? operator : "system");
        flow.setOperateTime(LocalDateTime.now());
        flow.setRemark(remark);
        flow.setCreateTime(LocalDateTime.now());
        save(flow);
    }
}
