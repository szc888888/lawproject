package io.renren.modules.sys.controller;


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
import io.renren.modules.sys.dto.WithdrawalDTO;
import io.renren.modules.sys.excel.WithdrawalExcel;
import io.renren.modules.sys.service.WithdrawalService;
import io.renren.modules.user.dto.UserDTO;
import io.renren.modules.user.entity.UserEntity;
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

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 用户提现表
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0 2023-04-13
 */
@RestController
@RequestMapping("sys/withdrawal")
@Api(tags="用户提现表")
public class WithdrawalController {
    @Autowired
    private WithdrawalService withdrawalService;
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
    @RequiresPermissions("sys:withdrawal:page")
    public Result<PageData<WithdrawalDTO>> page(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<WithdrawalDTO> page = withdrawalService.page(params);

        return new Result<PageData<WithdrawalDTO>>().ok(page);
    }

    @GetMapping("{id}")
    @ApiOperation("信息")
    @RequiresPermissions("sys:withdrawal:info")
    public Result<WithdrawalDTO> get(@PathVariable("id") Long id){
        WithdrawalDTO data = withdrawalService.get(id);

        return new Result<WithdrawalDTO>().ok(data);
    }

    @PostMapping
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("sys:withdrawal:save")
    public Result save(@RequestBody WithdrawalDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);

        withdrawalService.save(dto);

        return new Result();
    }

    @PutMapping
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("sys:withdrawal:update")
    public Result update(@RequestBody WithdrawalDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);
        
        //拿到审核状态
        Integer status = dto.getStatus();
        
        //查询提现记录
        WithdrawalDTO withdrawalDTO = withdrawalService.get(dto.getId());
        if(withdrawalDTO.getStatus() != 0) {
        	return new Result().error("提现已审核,请勿重复审核!");
        }
        
        if(status == 1) {
        	dto.setContent("审核通过!");
        }

      //审核状态为拒绝 为用户加上提现余额
        if(status == 2) {
        	//拿到用户ID
        	Long userId = dto.getUserId();
        	//拿到用户的提现金额
        	BigDecimal withdrawalMoney = dto.getWithdrawalMoney();
        	//根据用户ID查询用户数据
        	UserDTO userDTO = userService.get(userId);
        	//拿到用户的可提现余额
        	BigDecimal money = userDTO.getMoney();
        	BigDecimal add = money.add(withdrawalMoney);
        	//为用户加上余额
        	UserEntity newUserEntity = new UserEntity();
        	newUserEntity.setMoney(add);
        	newUserEntity.setId(userId);
        	userService.updateById(newUserEntity);
        }
        
        dto.setExamineTime(new Date());
        withdrawalService.update(dto);

        return new Result();
    }

    @DeleteMapping
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("sys:withdrawal:delete")
    public Result delete(@RequestBody Long[] ids){
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");

        withdrawalService.delete(ids);

        return new Result();
    }

    @GetMapping("export")
    @ApiOperation("导出")
    @LogOperation("导出")
    @RequiresPermissions("sys:withdrawal:export")
    public void export(@ApiIgnore @RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        List<WithdrawalDTO> list = withdrawalService.list(params);

        ExcelUtils.exportExcelToTarget(response, null, list, WithdrawalExcel.class);
    }

}