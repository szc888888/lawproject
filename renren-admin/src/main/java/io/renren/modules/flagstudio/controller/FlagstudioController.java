package io.renren.modules.flagstudio.controller;

import com.alibaba.fastjson.JSONObject;
import io.renren.common.annotation.LogOperation;
import io.renren.common.constant.Constant;
import io.renren.common.page.PageData;
import io.renren.common.utils.DateUtils;
import io.renren.common.utils.ExcelUtils;
import io.renren.common.utils.HttpUtil;
import io.renren.common.utils.Result;
import io.renren.common.validator.AssertUtils;
import io.renren.common.validator.ValidatorUtils;
import io.renren.common.validator.group.AddGroup;
import io.renren.common.validator.group.DefaultGroup;
import io.renren.common.validator.group.UpdateGroup;
import io.renren.modules.flagstudio.dto.FlagstudioDTO;
import io.renren.modules.flagstudio.excel.FlagstudioExcel;
import io.renren.modules.flagstudio.service.FlagstudioService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * FS绘画配置
 *
 * @author shican 1208296327@qq.com
 * @since 1.0.0 2023-05-31
 */
@RestController
@RequestMapping("flagstudio/flagstudio")
@Api(tags="FS绘画配置")
public class FlagstudioController {
    @Autowired
    private FlagstudioService flagstudioService;

    private String getTokenUrl = "https://flagopen.baai.ac.cn/flagStudio/auth/getToken?apikey=";
    @GetMapping("page")
    @ApiOperation("分页")
    @ApiImplicitParams({
        @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
        @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String")
    })
    @RequiresPermissions("flagstudio:flagstudio:page")
    public Result<PageData<FlagstudioDTO>> page(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("orderField","create_time");
        params.put("order","desc");
        PageData<FlagstudioDTO> page = flagstudioService.page(params);

        return new Result<PageData<FlagstudioDTO>>().ok(page);
    }

    @GetMapping("{id}")
    @ApiOperation("信息")
    @RequiresPermissions("flagstudio:flagstudio:info")
    public Result<FlagstudioDTO> get(@PathVariable("id") Long id){
        FlagstudioDTO data = flagstudioService.get(id);

        return new Result<FlagstudioDTO>().ok(data);
    }

    @PostMapping
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("flagstudio:flagstudio:save")
    public Result save(@RequestBody FlagstudioDTO dto) throws IOException {
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);
        //先生成token 从而能判断这个key是否有用

        JSONObject jsonObject = HttpUtil.doGetJson(getTokenUrl + dto.getApiKey());
        String code = jsonObject.getString("code");
        if(!code.equals("200")){
            return new Result().error("key有误,请检查key");
        }
        String token = jsonObject.getJSONObject("data").getString("token");
        dto.setToken(token);
        dto.setDayCount(0);
        Date afterDay = DateUtils.getAfterDay(new Date(), 29);//过期时间,正常是30天 这里就设置提前一天吧
        dto.setTokenTime(afterDay);
        dto.setCreateTime(new Date());
        flagstudioService.save(dto);

        return new Result();
    }

    @PutMapping
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("flagstudio:flagstudio:update")
    public Result update(@RequestBody FlagstudioDTO dto) throws IOException {
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);
        JSONObject jsonObject = HttpUtil.doGetJson(getTokenUrl + dto.getApiKey());
        String code = jsonObject.getString("code");
        if(!code.equals("200")){
            return new Result().error("key有误,请检查key");
        }
        String token = jsonObject.getJSONObject("data").getString("token");
        dto.setToken(token);
        Date afterDay = DateUtils.getAfterDay(new Date(), 29);//过期时间,正常是30天 这里就设置提前一天吧
        dto.setTokenTime(afterDay);
        flagstudioService.update(dto);

        return new Result();
    }

    @DeleteMapping
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("flagstudio:flagstudio:delete")
    public Result delete(@RequestBody Long[] ids){
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");

        flagstudioService.delete(ids);

        return new Result();
    }

    @GetMapping("export")
    @ApiOperation("导出")
    @LogOperation("导出")
    @RequiresPermissions("flagstudio:flagstudio:export")
    public void export(@ApiIgnore @RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        List<FlagstudioDTO> list = flagstudioService.list(params);

        ExcelUtils.exportExcelToTarget(response, null, list, FlagstudioExcel.class);
    }

}