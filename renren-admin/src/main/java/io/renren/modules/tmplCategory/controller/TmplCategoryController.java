package io.renren.modules.tmplCategory.controller;

import io.renren.common.annotation.LogOperation;
import io.renren.common.constant.Constant;
import io.renren.common.page.PageData;
import io.renren.common.utils.ExcelUtils;
import io.renren.common.utils.Result;
import io.renren.common.validator.AssertUtils;
import io.renren.common.validator.ValidatorUtils;
import io.renren.common.validator.group.AddGroup;
import io.renren.common.validator.group.DefaultGroup;
import io.renren.common.validator.group.UpdateGroup;
import io.renren.modules.tmplCategory.dto.TmplCategoryDTO;
import io.renren.modules.tmplCategory.excel.TmplCategoryExcel;
import io.renren.modules.tmplCategory.service.TmplCategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 提问模板分类
 *
 * @author shican 1208296327@qq.com
 * @since 1.0.0 2023-04-10
 */
@RestController
@RequestMapping("tmplCategory/tmplcategory")
@Api(tags="提问模板分类")
public class TmplCategoryController {
    @Autowired
    private TmplCategoryService tmplCategoryService;

    @GetMapping("page")
    @ApiOperation("分页")
    @ApiImplicitParams({
        @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
        @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String")
    })
    @RequiresPermissions("tmplCategory:tmplcategory:page")
    public Result<PageData<TmplCategoryDTO>> page(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<TmplCategoryDTO> page = tmplCategoryService.page(params);

        return new Result<PageData<TmplCategoryDTO>>().ok(page);
    }

    @GetMapping("{id}")
    @ApiOperation("信息")
    @RequiresPermissions("tmplCategory:tmplcategory:info")
    public Result<TmplCategoryDTO> get(@PathVariable("id") Long id){
        TmplCategoryDTO data = tmplCategoryService.get(id);

        return new Result<TmplCategoryDTO>().ok(data);
    }

    @PostMapping
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("tmplCategory:tmplcategory:save")
    public Result save(@RequestBody TmplCategoryDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);
        dto.setCreateTime(new Date());
        tmplCategoryService.save(dto);

        return new Result();
    }

    @PutMapping
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("tmplCategory:tmplcategory:update")
    public Result update(@RequestBody TmplCategoryDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);

        tmplCategoryService.update(dto);

        return new Result();
    }

    @DeleteMapping
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("tmplCategory:tmplcategory:delete")
    public Result delete(@RequestBody Long[] ids){
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");

        tmplCategoryService.delete(ids);

        return new Result();
    }

    @GetMapping("export")
    @ApiOperation("导出")
    @LogOperation("导出")
    @RequiresPermissions("tmplCategory:tmplcategory:export")
    public void export(@ApiIgnore @RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        List<TmplCategoryDTO> list = tmplCategoryService.list(params);

        ExcelUtils.exportExcelToTarget(response, null, list, TmplCategoryExcel.class);
    }
    @GetMapping("all")
    @ApiOperation("查询所有开启模板")
    public Result<List<TmplCategoryDTO>> getAll(){
        List<TmplCategoryDTO> data = tmplCategoryService.getAll();

        return new Result<List<TmplCategoryDTO>>().ok(data);
    }
}