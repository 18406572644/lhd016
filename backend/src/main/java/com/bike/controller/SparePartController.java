package com.bike.controller;

import com.bike.common.OperationLog;
import com.bike.common.PageQuery;
import com.bike.common.Result;
import com.bike.entity.SparePart;
import com.bike.entity.StockRecord;
import com.bike.entity.dto.SensitiveOperationDTO;
import com.bike.entity.dto.StockInOutDTO;
import com.bike.service.SparePartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "配件库存管理")
@RestController
@RequestMapping("/api/spare-parts")
public class SparePartController {

    @Autowired
    private SparePartService sparePartService;

    @ApiOperation("分页查询配件库存")
    @GetMapping
    public Result<PageQuery.PageResult<SparePart>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String partName,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Long supplyPointId,
            @RequestParam(required = false) Boolean lowStock) {

        Map<String, Object> params = new HashMap<>();
        params.put("partName", partName);
        params.put("category", category);
        params.put("supplyPointId", supplyPointId);
        params.put("lowStock", lowStock);

        return Result.success(sparePartService.pageByCondition(pageNum, pageSize, params));
    }

    @ApiOperation("获取低库存预警列表")
    @GetMapping("/warning")
    public Result<List<SparePart>> getWarningList() {
        return Result.success(sparePartService.getLowStockList());
    }

    @ApiOperation("根据站点获取配件列表")
    @GetMapping("/by-supply-point/{supplyPointId}")
    public Result<List<SparePart>> getBySupplyPointId(@PathVariable Long supplyPointId) {
        return Result.success(sparePartService.getBySupplyPointId(supplyPointId));
    }

    @ApiOperation("获取配件详情")
    @GetMapping("/{id}")
    public Result<SparePart> getById(@PathVariable Long id) {
        return Result.success(sparePartService.getById(id));
    }

    @ApiOperation("新增配件")
    @OperationLog(module = "配件库存", operationType = "新增")
    @PostMapping
    public Result<Void> save(@RequestBody SparePart sparePart) {
        sparePartService.save(sparePart);
        return Result.success();
    }

    @ApiOperation("更新配件")
    @OperationLog(module = "配件库存", operationType = "修改")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody SparePart sparePart) {
        sparePart.setId(id);
        sparePartService.updateById(sparePart);
        return Result.success();
    }

    @ApiOperation("删除配件")
    @OperationLog(module = "配件库存", operationType = "删除", sensitive = true)
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, @RequestBody SensitiveOperationDTO dto, @RequestParam String reason) {
        sparePartService.removeById(id);
        return Result.success();
    }

    @ApiOperation("入库")
    @OperationLog(module = "配件库存", operationType = "入库", sensitive = true)
    @PostMapping("/{id}/stock-in")
    public Result<Void> stockIn(@PathVariable Long id, @RequestBody StockInOutDTO dto, @RequestParam(required = false) String reason) {
        sparePartService.stockIn(id, dto);
        return Result.successMsg("入库成功");
    }

    @ApiOperation("出库")
    @OperationLog(module = "配件库存", operationType = "出库", sensitive = true)
    @PostMapping("/{id}/stock-out")
    public Result<Void> stockOut(@PathVariable Long id, @RequestBody StockInOutDTO dto, @RequestParam(required = false) String reason) {
        sparePartService.stockOut(id, dto);
        return Result.successMsg("出库成功");
    }

    @ApiOperation("获取库存变动记录")
    @GetMapping("/{id}/records")
    public Result<PageQuery.PageResult<StockRecord>> getStockRecords(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @PathVariable Long id) {
        return Result.success(sparePartService.getStockRecords(pageNum, pageSize, id));
    }
}
