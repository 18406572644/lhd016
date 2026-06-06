package com.bike.controller;

import com.bike.common.OperationLog;
import com.bike.common.PageQuery;
import com.bike.common.Result;
import com.bike.entity.RepairShop;
import com.bike.service.RepairShopService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "维修点档案")
@RestController
@RequestMapping("/api/repair-shops")
public class RepairShopController {

    @Autowired
    private RepairShopService repairShopService;

    @ApiOperation("分页查询维修点")
    @GetMapping
    public Result<PageQuery.PageResult<RepairShop>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String area,
            @RequestParam(required = false) String level) {

        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("area", area);
        params.put("level", level);

        return Result.success(repairShopService.pageByCondition(pageNum, pageSize, params));
    }

    @ApiOperation("获取所有维修点列表")
    @GetMapping("/list")
    public Result<List<RepairShop>> list() {
        return Result.success(repairShopService.list());
    }

    @ApiOperation("获取维修点详情")
    @GetMapping("/{id}")
    public Result<RepairShop> getById(@PathVariable Long id) {
        return Result.success(repairShopService.getById(id));
    }

    @ApiOperation("新增维修点")
    @OperationLog(module = "维修点管理", operationType = "新增")
    @PostMapping
    public Result<Void> save(@RequestBody RepairShop repairShop) {
        repairShopService.save(repairShop);
        return Result.success();
    }

    @ApiOperation("更新维修点")
    @OperationLog(module = "维修点管理", operationType = "修改", sensitive = true)
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody RepairShop repairShop) {
        repairShop.setId(id);
        repairShopService.updateById(repairShop);
        return Result.success();
    }

    @ApiOperation("删除维修点")
    @OperationLog(module = "维修点管理", operationType = "删除", sensitive = true)
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, @RequestParam String reason) {
        repairShopService.removeById(id);
        return Result.success();
    }
}
