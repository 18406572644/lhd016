package com.bike.controller;

import com.bike.common.OperationLog;
import com.bike.common.PageQuery;
import com.bike.common.Result;
import com.bike.entity.InventoryCheck;
import com.bike.service.InventoryCheckService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Api(tags = "物资盘点管理")
@RestController
@RequestMapping("/api/inventory-checks")
public class InventoryCheckController {

    @Autowired
    private InventoryCheckService inventoryCheckService;

    @ApiOperation("分页查询盘点单")
    @GetMapping
    public Result<PageQuery.PageResult<InventoryCheck>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String checkNo,
            @RequestParam(required = false) Long supplyPointId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(required = false) String status) {

        Map<String, Object> params = new HashMap<>();
        params.put("checkNo", checkNo);
        params.put("supplyPointId", supplyPointId);
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        params.put("status", status);

        return Result.success(inventoryCheckService.pageByCondition(pageNum, pageSize, params));
    }

    @ApiOperation("获取盘点单详情（含明细）")
    @GetMapping("/{id}")
    public Result<InventoryCheck> getDetailById(@PathVariable Long id) {
        return Result.success(inventoryCheckService.getDetailById(id));
    }

    @ApiOperation("创建盘点单")
    @OperationLog(module = "物资盘点", operationType = "新增")
    @PostMapping
    public Result<Void> create(@RequestBody InventoryCheck check) {
        inventoryCheckService.createCheck(check);
        return Result.successMsg("盘点单创建成功");
    }

    @ApiOperation("完成盘点")
    @OperationLog(module = "物资盘点", operationType = "盘点完成")
    @PostMapping("/{id}/complete")
    public Result<Void> complete(@PathVariable Long id, @RequestBody InventoryCheck check) {
        inventoryCheckService.completeCheck(id, check);
        return Result.successMsg("盘点完成");
    }

    @ApiOperation("删除盘点单")
    @OperationLog(module = "物资盘点", operationType = "删除", sensitive = true)
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, @RequestParam String reason) {
        inventoryCheckService.removeById(id);
        return Result.success();
    }
}
