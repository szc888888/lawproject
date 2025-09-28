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
import io.renren.modules.sys.dto.CamilleDTO;
import io.renren.modules.sys.excel.CamilleExcel;
import io.renren.modules.sys.service.CamilleService;
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
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 卡密表
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0 2023-04-26
 */
@RestController
@RequestMapping("/sys/camille")
@Api(tags="卡密表")
public class CamilleController {
    @Autowired
    private CamilleService camilleService;

    @GetMapping("page")
    @ApiOperation("分页")
    @ApiImplicitParams({
        @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
        @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String")
    })
    @RequiresPermissions("sys:camille:page")
    public Result<PageData<CamilleDTO>> page(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<CamilleDTO> page = camilleService.page(params);

        return new Result<PageData<CamilleDTO>>().ok(page);
    }

    @GetMapping("{id}")
    @ApiOperation("信息")
    @RequiresPermissions("sys:camille:info")
    public Result<CamilleDTO> get(@PathVariable("id") Long id){
        CamilleDTO data = camilleService.get(id);

        return new Result<CamilleDTO>().ok(data);
    }

    @PostMapping
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("sys:camille:save")
    public Result save(@RequestBody CamilleDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);
        
        //拿到生成数量
        Integer number = dto.getNumber();
        for (int i = 0; i < number; i++) {
        	
        	//随机生成卡密
        	String str = getRandomCharacterAndNumber(12);
        	dto.setId(null);
        	//保存卡密号
        	dto.setCamilleNumber(str);
        	//创建时间
        	dto.setTime(new Date());
            camilleService.save(dto);
		}



        return new Result();
    }

    @PutMapping
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("sys:camille:update")
    public Result update(@RequestBody CamilleDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);

        camilleService.update(dto);

        return new Result();
    }

    @DeleteMapping
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("sys:camille:delete")
    public Result delete(@RequestBody Long[] ids){
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");

        camilleService.delete(ids);

        return new Result();
    }

    @GetMapping("export")
    @ApiOperation("导出")
    @LogOperation("导出")
    @RequiresPermissions("sys:camille:export")
    public void export(@ApiIgnore @RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        List<CamilleDTO> list = camilleService.list(params);

        ExcelUtils.exportExcelToTarget(response, null, list, CamilleExcel.class);
    }
    
    
    public static String getRandomCharacterAndNumber(int length) {

    	String val = "";

    	Random random = new Random();

    	for (int i = 0; i < length; i++) {

    	String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num"; // 输出字母还是数字

    	if ("char".equalsIgnoreCase(charOrNum)) // 字符串

    	{

    	int choice = random.nextInt(2) % 2 == 0 ? 65 : 97; // 取得大写字母还是小写字母

    	val += (char) (choice + random.nextInt(26));

    	// int choice = 97; // 指定字符串为小写字母

    	val += (char) (choice + random.nextInt(26));

    	} else if ("num".equalsIgnoreCase(charOrNum)) // 数字

    	{

    	val += String.valueOf(random.nextInt(10));

    	}

    	}

    	return val;

    	}

    

}