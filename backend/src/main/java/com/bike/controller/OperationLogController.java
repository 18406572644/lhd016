package com.bike.controller;

import com.bike.common.PageQuery;
import com.bike.common.Result;
import com.bike.entity.OperationLog;
import com.bike.service.OperationLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Api(tags = "操作日志管理")
@RestController
@RequestMapping("/api/operation-logs")
public class OperationLogController {

    @Autowired
    private OperationLogService operationLogService;

    @ApiOperation("分页查询操作日志")
    @GetMapping
    public Result<PageQuery.PageResult<OperationLog>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String operator,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String operationType,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {

        Map<String, Object> params = new HashMap<>();
        params.put("operator", operator);
        params.put("module", module);
        params.put("operationType", operationType);
        params.put("startTime", startTime);
        params.put("endTime", endTime);

        return Result.success(operationLogService.pageByCondition(pageNum, pageSize, params));
    }

    @ApiOperation("获取操作日志详情")
    @GetMapping("/{id}")
    public Result<OperationLog> getById(@PathVariable Long id) {
        return Result.success(operationLogService.getById(id));
    }

    @ApiOperation("获取模块列表")
    @GetMapping("/modules")
    public Result<Map<String, String>[]> getModules() {
        Map<String, String>[] modules = new Map[]{
                createOption("配件库存", "配件库存"),
                createOption("补给点管理", "补给点管理"),
                createOption("维修点管理", "维修点管理"),
                createOption("求助登记", "求助登记"),
                createOption("物资盘点", "物资盘点")
        };
        return Result.success(modules);
    }

    @ApiOperation("获取操作类型列表")
    @GetMapping("/operation-types")
    public Result<Map<String, String>[]> getOperationTypes() {
        Map<String, String>[] types = new Map[]{
                createOption("新增", "新增"),
                createOption("修改", "修改"),
                createOption("删除", "删除"),
                createOption("入库", "入库"),
                createOption("出库", "出库"),
                createOption("状态变更", "状态变更"),
                createOption("盘点完成", "盘点完成")
        };
        return Result.success(types);
    }

    private Map<String, String> createOption(String value, String label) {
        Map<String, String> option = new HashMap<>();
        option.put("value", value);
        option.put("label", label);
        return option;
    }
}
