package com.bike.controller;

import com.bike.common.OperationLog;
import com.bike.common.PageQuery;
import com.bike.common.Result;
import com.bike.entity.SupplyPoint;
import com.bike.service.SupplyPointService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "补给点管理")
@RestController
@RequestMapping("/api/supply-points")
public class SupplyPointController {

    @Autowired
    private SupplyPointService supplyPointService;

    @ApiOperation("分页查询补给点")
    @GetMapping
    public Result<PageQuery.PageResult<SupplyPoint>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String area,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status) {

        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("area", area);
        params.put("type", type);
        params.put("status", status);

        return Result.success(supplyPointService.pageByCondition(pageNum, pageSize, params));
    }

    @ApiOperation("获取所有补给点列表")
    @GetMapping("/list")
    public Result<List<SupplyPoint>> list() {
        return Result.success(supplyPointService.listAll());
    }

    @ApiOperation("获取补给点详情")
    @GetMapping("/{id}")
    public Result<SupplyPoint> getById(@PathVariable Long id) {
        return Result.success(supplyPointService.getById(id));
    }

    @ApiOperation("新增补给点")
    @PostMapping
    @OperationLog(module = "补给点管理", operationType = "新增")
    public Result<Void> save(@RequestBody SupplyPoint supplyPoint) {
        supplyPointService.save(supplyPoint);
        return Result.success();
    }

    @ApiOperation("更新补给点")
    @PutMapping("/{id}")
    @OperationLog(module = "补给点管理", operationType = "修改", sensitive = true)
    public Result<Void> update(@PathVariable Long id, @RequestBody SupplyPoint supplyPoint) {
        supplyPoint.setId(id);
        supplyPointService.updateById(supplyPoint);
        return Result.success();
    }

    @ApiOperation("删除补给点")
    @DeleteMapping("/{id}")
    @OperationLog(module = "补给点管理", operationType = "删除", sensitive = true)
    public Result<Void> delete(@PathVariable Long id, @RequestParam String reason) {
        supplyPointService.removeById(id);
        return Result.success();
    }
}
