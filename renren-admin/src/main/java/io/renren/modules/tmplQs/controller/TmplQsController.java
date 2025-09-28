package io.renren.modules.tmplQs.controller;

import cn.hutool.core.collection.CollUtil;
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
import io.renren.modules.tmplCategory.entity.TmplCategoryEntity;
import io.renren.modules.tmplCategory.service.TmplCategoryService;
import io.renren.modules.tmplQs.dto.TmplQsDTO;
import io.renren.modules.tmplQs.excel.TmplQsExcel;
import io.renren.modules.tmplQs.service.TmplQsService;
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
 * 提问模板问题
 *
 * @author shican 1208296327@qq.com
 * @since 1.0.0 2023-04-10
 */
@RestController
@RequestMapping("tmplQs/tmplqs")
@Api(tags="提问模板问题")
public class TmplQsController {
    @Autowired
    private TmplQsService tmplQsService;
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
    @RequiresPermissions("tmplQs:tmplqs:page")
    public Result<PageData<TmplQsDTO>> page(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<TmplQsDTO> page = tmplQsService.page(params);
        List<TmplQsDTO> list = page.getList();
        if(CollUtil.isNotEmpty(list)){
            for(TmplQsDTO tmplQsDTO : list){
                TmplCategoryDTO tmplCategoryDTO = tmplCategoryService.get(tmplQsDTO.getTid());
                tmplQsDTO.setTmplName(tmplCategoryDTO.getCategoryName());
            }
        }
        return new Result<PageData<TmplQsDTO>>().ok(page);
    }

    @GetMapping("{id}")
    @ApiOperation("信息")
    @RequiresPermissions("tmplQs:tmplqs:info")
    public Result<TmplQsDTO> get(@PathVariable("id") Long id){
        TmplQsDTO data = tmplQsService.get(id);

        return new Result<TmplQsDTO>().ok(data);
    }

    @PostMapping
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("tmplQs:tmplqs:save")
    public Result save(@RequestBody TmplQsDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);
        dto.setCreateTime(new Date());
        tmplQsService.save(dto);

        return new Result();
    }

    @PutMapping
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("tmplQs:tmplqs:update")
    public Result update(@RequestBody TmplQsDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);

        tmplQsService.update(dto);

        return new Result();
    }

    @DeleteMapping
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("tmplQs:tmplqs:delete")
    public Result delete(@RequestBody Long[] ids){
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");

        tmplQsService.delete(ids);

        return new Result();
    }

    @GetMapping("export")
    @ApiOperation("导出")
    @LogOperation("导出")
    @RequiresPermissions("tmplQs:tmplqs:export")
    public void export(@ApiIgnore @RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        List<TmplQsDTO> list = tmplQsService.list(params);

        ExcelUtils.exportExcelToTarget(response, null, list, TmplQsExcel.class);
    }

}