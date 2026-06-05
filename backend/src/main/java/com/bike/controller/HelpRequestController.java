package com.bike.controller;

import com.bike.common.PageQuery;
import com.bike.common.Result;
import com.bike.entity.HelpRequest;
import com.bike.entity.dto.HandleHelpDTO;
import com.bike.service.HelpRequestService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "求助登记管理")
@RestController
@RequestMapping("/api/help-requests")
public class HelpRequestController {

    @Autowired
    private HelpRequestService helpRequestService;

    @ApiOperation("分页查询求助登记")
    @GetMapping
    public Result<PageQuery.PageResult<HelpRequest>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String requesterName,
            @RequestParam(required = false) String helpType,
            @RequestParam(required = false) String urgency,
            @RequestParam(required = false) String status) {

        Map<String, Object> params = new HashMap<>();
        params.put("requesterName", requesterName);
        params.put("helpType", helpType);
        params.put("urgency", urgency);
        params.put("status", status);

        return Result.success(helpRequestService.pageByCondition(pageNum, pageSize, params));
    }

    @ApiOperation("获取待处理求助列表")
    @GetMapping("/pending")
    public Result<List<HelpRequest>> getPendingList() {
        return Result.success(helpRequestService.getPendingList());
    }

    @ApiOperation("获取求助详情")
    @GetMapping("/{id}")
    public Result<HelpRequest> getById(@PathVariable Long id) {
        return Result.success(helpRequestService.getById(id));
    }

    @ApiOperation("新增求助登记")
    @PostMapping
    public Result<Void> save(@RequestBody HelpRequest helpRequest) {
        helpRequestService.save(helpRequest);
        return Result.success();
    }

    @ApiOperation("更新求助状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam String status) {
        helpRequestService.updateStatus(id, status);
        return Result.success();
    }

    @ApiOperation("处理求助")
    @PutMapping("/{id}/handle")
    public Result<Void> handleHelp(@PathVariable Long id, @RequestBody HandleHelpDTO dto) {
        helpRequestService.handleHelp(id, dto);
        return Result.success();
    }

    @ApiOperation("删除求助登记")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        helpRequestService.removeById(id);
        return Result.success();
    }
}
