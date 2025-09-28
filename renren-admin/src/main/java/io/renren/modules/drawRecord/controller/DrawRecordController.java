package io.renren.modules.drawRecord.controller;

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
import io.renren.modules.drawRecord.dto.DrawRecordDTO;
import io.renren.modules.drawRecord.excel.DrawRecordExcel;
import io.renren.modules.drawRecord.service.DrawRecordService;
import io.renren.modules.questionAnswer.dto.QuestionAnswerDTO;
import io.renren.modules.user.dto.UserDTO;
import io.renren.modules.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;


/**
 * 画图记录
 *
 * @author shican 1208296327@qq.com
 * @since 1.0.0 2023-05-19
 */
@RestController
@RequestMapping("drawRecord/drawrecord")
@Api(tags="画图记录")
public class DrawRecordController {
    @Autowired
    private DrawRecordService drawRecordService;
    @Autowired
    private UserService userService;
    @GetMapping("page")
    @ApiOperation("分页")
    @ApiImplicitParams({
        @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
        @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String")
    })
    @RequiresPermissions("drawRecord:drawrecord:page")
    public Result<PageData<DrawRecordDTO>> page(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("orderField","create_time");
        params.put("order","desc");
        PageData<DrawRecordDTO> page = drawRecordService.page(params);
        List<DrawRecordDTO> list = page.getList();
        if(CollUtil.isNotEmpty(list)){
            for(DrawRecordDTO dto : list){
                UserDTO userDTO = userService.get(dto.getUserId());
                dto.setNickName(userDTO.getNickName());
            }
        }
        return new Result<PageData<DrawRecordDTO>>().ok(page);
    }

    @GetMapping("{id}")
    @ApiOperation("信息")
    @RequiresPermissions("drawRecord:drawrecord:info")
    public Result<DrawRecordDTO> get(@PathVariable("id") Long id){
        DrawRecordDTO data = drawRecordService.get(id);

        return new Result<DrawRecordDTO>().ok(data);
    }

    @PostMapping
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("drawRecord:drawrecord:save")
    public Result save(@RequestBody DrawRecordDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);

        drawRecordService.save(dto);

        return new Result();
    }

    @PutMapping
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("drawRecord:drawrecord:update")
    public Result update(@RequestBody DrawRecordDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);

        drawRecordService.update(dto);

        return new Result();
    }

    @DeleteMapping
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("drawRecord:drawrecord:delete")
    public Result delete(@RequestBody Long[] ids){
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");

        drawRecordService.delete(ids);

        return new Result();
    }

    @GetMapping("export")
    @ApiOperation("导出")
    @LogOperation("导出")
    @RequiresPermissions("drawRecord:drawrecord:export")
    public void export(@ApiIgnore @RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        List<DrawRecordDTO> list = drawRecordService.list(params);

        ExcelUtils.exportExcelToTarget(response, null, list, DrawRecordExcel.class);
    }

}