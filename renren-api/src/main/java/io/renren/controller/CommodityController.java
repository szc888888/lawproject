package io.renren.controller;

import io.renren.common.utils.Result;
import io.renren.entity.CommodityEntity;
import io.renren.service.CommodityService;
import io.renren.service.SysParamsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商品请求接口
 *
 * @author tiechui
 */

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
@Api(tags="商品请求接口")
public class CommodityController {
    
    @Autowired
    private CommodityService commodityService;
    @Autowired
    private SysParamsService sysParamsService;
    
    @GetMapping("commodityList")
    @ApiOperation("查询商品页面商品")
    public Result<Map<String, Object>> commodityList(){

        Map<String, Object> map = new HashMap<>();
        map.put("status", 1);

        //查询后台设置的条数商品
        map.put("type", 1);
        List<CommodityEntity> commoditystripList = commodityService.getCommodityList(map);

        //查询后台设置的VIP商品
        map.put("type", 2);
        List<CommodityEntity> commodityVIPList = commodityService.getCommodityList(map);
        map.put("type", 3);
        List<CommodityEntity> commodityHhrList = commodityService.getCommodityList(map);
        Map<String, Object> commoditymap = new HashMap<>();
        commoditymap.put("STR", commoditystripList);
        commoditymap.put("VIP", commodityVIPList);
        commoditymap.put("HHR", commodityHhrList);
        //查询高级合伙人的比例
        String invite_partner_one = sysParamsService.getValue("invite_partner_one");
        String invite_partner_two = sysParamsService.getValue("invite_partner_two");
        commoditymap.put("HHRONE", invite_partner_one);
        commoditymap.put("HHRTWO", invite_partner_two);
        return new Result<Map<String, Object>>().ok(commoditymap);
    }

}