package com.bike.controller;

import com.bike.common.OperationLog;
import com.bike.common.PageQuery;
import com.bike.common.Result;
import com.bike.entity.HelpRequest;
import com.bike.entity.dto.DispatchDTO;
import com.bike.entity.dto.HandleHelpDTO;
import com.bike.entity.dto.HelpRequestVO;
import com.bike.entity.dto.RepairShopRecommendation;
import com.bike.service.HelpRequestService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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

    @ApiOperation("分页查询求助登记（包含维修点信息）")
    @GetMapping("/with-shop")
    public Result<PageQuery.PageResult<HelpRequestVO>> pageWithShop(
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

        return Result.success(helpRequestService.pageWithRepairShop(pageNum, pageSize, params));
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

    @ApiOperation("获取求助详情（包含维修点信息）")
    @GetMapping("/{id}/with-shop")
    public Result<HelpRequestVO> getDetailWithShop(@PathVariable Long id) {
        return Result.success(helpRequestService.getDetailWithRepairShop(id));
    }

    @ApiOperation("获取推荐维修点列表")
    @GetMapping("/recommend-shops")
    public Result<List<RepairShopRecommendation>> getRecommendedShops(
            @RequestParam BigDecimal longitude,
            @RequestParam BigDecimal latitude,
            @RequestParam(required = false) String helpType) {
        return Result.success(helpRequestService.getRecommendedShops(longitude, latitude, helpType));
    }

    @ApiOperation("新增求助登记")
    @OperationLog(module = "求助登记", operationType = "新增")
    @PostMapping
    public Result<Void> save(@RequestBody HelpRequest helpRequest) {
        helpRequestService.save(helpRequest);
        return Result.success();
    }

    @ApiOperation("分配求助到维修点")
    @OperationLog(module = "求助登记", operationType = "状态变更")
    @PostMapping("/dispatch")
    public Result<Void> dispatch(@RequestBody DispatchDTO dto) {
        helpRequestService.dispatchHelpRequest(dto);
        return Result.success();
    }

    @ApiOperation("手动调整分配结果")
    @OperationLog(module = "求助登记", operationType = "状态变更")
    @PutMapping("/{id}/adjust-dispatch")
    public Result<Void> adjustDispatch(
            @PathVariable Long id,
            @RequestParam Long repairShopId,
            @RequestParam(defaultValue = "true") Boolean notify) {
        helpRequestService.adjustDispatch(id, repairShopId, notify);
        return Result.success();
    }

    @ApiOperation("更新求助状态")
    @OperationLog(module = "求助登记", operationType = "状态变更")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam String status) {
        helpRequestService.updateStatus(id, status);
        return Result.success();
    }

    @ApiOperation("处理求助")
    @OperationLog(module = "求助登记", operationType = "状态变更")
    @PutMapping("/{id}/handle")
    public Result<Void> handleHelp(@PathVariable Long id, @RequestBody HandleHelpDTO dto) {
        helpRequestService.handleHelp(id, dto);
        return Result.success();
    }

    @ApiOperation("删除求助登记")
    @OperationLog(module = "求助登记", operationType = "删除", sensitive = true)
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, @RequestParam String reason) {
        helpRequestService.removeById(id);
        return Result.success();
    }
}
