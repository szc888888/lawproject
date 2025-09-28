package io.renren.controller;

import io.renren.common.utils.Result;
import io.renren.entity.PayConfigEntity;
import io.renren.service.PayConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * 支付通道请求接口
 *
 * @author tiechui
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
@Api(tags="支付通道请求接口")
public class PayConfigController {
    
    @Autowired
    private PayConfigService payConfigService;
    
	/**
	 * 根据条件查询所有可用的支付接口
	 * 
	 * type:当在微信公众号type传(2)  小程序使用时type传(4) 当在H5使用时 type不用传值   当在pc端使用时type传6(查询type等于6和7)
	 * 
	 * 
	 */
    @GetMapping("PayConfigList")
    @ApiOperation("查询所有可用的支付接口")
    public Result<List<PayConfigEntity>> PayConfigList(Integer type){
    	
        Map<String, Object> map = new HashMap<>();
        map.put("type", type);
        List<PayConfigEntity> payConfigList = payConfigService.getPayConfigList(map);
    	
        return new Result<List<PayConfigEntity>>().ok(payConfigList);
    }

}