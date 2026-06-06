package com.bike.controller;

import com.bike.common.Result;
import com.bike.entity.HelpStatusFlow;
import com.bike.service.HelpStatusFlowService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "求助状态流转管理")
@RestController
@RequestMapping("/api/help-status-flows")
public class HelpStatusFlowController {

    @Autowired
    private HelpStatusFlowService helpStatusFlowService;

    @ApiOperation("根据求助ID获取状态流转记录")
    @GetMapping("/by-help-request/{helpRequestId}")
    public Result<List<HelpStatusFlow>> getByHelpRequestId(@PathVariable Long helpRequestId) {
        return Result.success(helpStatusFlowService.getByHelpRequestId(helpRequestId));
    }
}
