package io.renren.controller;

import io.renren.annotation.Login;
import io.renren.annotation.LoginUser;
import io.renren.common.utils.Result;
import io.renren.entity.UserEntity;
import io.renren.entity.WithdrawalEntity;
import io.renren.service.SysParamsService;
import io.renren.service.UserService;
import io.renren.service.WithdrawalService;
import io.renren.vo.WithdrawalVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户提现记录请求接口
 *
 * @author tiechui
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
@Api(tags="用户提现记录请求接口")
public class WithdrawalController {
    
    @Autowired
    private WithdrawalService withdrawalService;
    @Autowired
    private UserService userService;
    @Autowired
    private SysParamsService paramsService;
    
    
    @GetMapping("WithdrawalServiceChargeScale")
    @ApiOperation("查询提现手续费")
    public Result<String> WithdrawalServiceChargeScale(){
    	
    	//查询后台设置的提现手续费比例
    	String serviceChargeScale = paramsService.getValue("service_charge_scale");
        
    	
        return new Result<String>().ok(serviceChargeScale);
    }
    
    @Login
    @GetMapping("WithdrawalList")
    @ApiOperation("根据用户ID查询用户所有的提现记录")
    public Result<List<WithdrawalEntity>> OrderList(@LoginUser UserEntity user){
    	//拿到用户ID
    	Long userId = user.getId();
    	
    	//根据条件查询用户的充值记录
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        
        List<WithdrawalEntity> WithdrawalList = withdrawalService.getWithdrawalList(map);
        
    	
        return new Result<List<WithdrawalEntity>>().ok(WithdrawalList);
    }
    
    
    
    /**
     * 用户提现接口
     * 
     * withdrawalMoney:提现金额(只能为数字并且小数点为两位)
     * makeCollectionsUrl:用户收款二维码链接
     * type:收款类型(1:支付宝  2:微信)
     *
     * @author Mark sunlightcs@gmail.com
     */
    @Login
    @PostMapping("withdrawalflowpath")
    @ApiOperation("用户提现开始")
    public Result<List<WithdrawalEntity>> withdrawalflowpath(@LoginUser UserEntity user,@RequestBody WithdrawalVo withdrawalVo){
    	//拿到用户ID
    	Long userId = user.getId();
    	
    	if(withdrawalVo.getWithdrawalMoney() == null || withdrawalVo.getMakeCollectionsUrl() == null || withdrawalVo.getType() == null) {
    		
    		return new Result<List<WithdrawalEntity>>().error("请填写完整的提现数据!");
    		
    	}
    	
    	//查询后台设置最低提现额度
    	String withdrawalLowestQuot = paramsService.getValue("withdrawal_lowest_quota");
    	
    	//拿到用户的提现金额
    	BigDecimal withdrawalMoney = withdrawalVo.getWithdrawalMoney();
    	
    	//判断提现金额是否高于后台设置的最低提现金额
    	if(withdrawalMoney.compareTo(new BigDecimal(withdrawalLowestQuot)) == -1) {
    		
    		return new Result<List<WithdrawalEntity>>().error("提现最低金额为:"+withdrawalLowestQuot+"元!");
    		
    	}
    	
//    	//查询用户数据
//    	UserEntity userByUserId = userService.getUserByUserId(userId);
    	//拿到用户可提现余额
    	BigDecimal money = user.getMoney();
    	
    	//判断用户的可提现金额是否足够
    	if(money.compareTo(withdrawalMoney) == -1) {
    		
    		return new Result<List<WithdrawalEntity>>().error("您本次最高提现金额为:"+money+"元!");
    		
    	}
    	
    	/****************开始提现流程*************************************************************************/
    	//拿到后台设置的提现手续费比例
    	String serviceChargeScale = paramsService.getValue("service_charge_scale");
    	BigDecimal serviceChargeScaledivide = new BigDecimal(serviceChargeScale).divide(new BigDecimal(100));
    	
    	//计算提现手续费  保留两位小数
    	BigDecimal withdrawalMoneysetScale = withdrawalMoney.multiply(serviceChargeScaledivide).setScale(2, RoundingMode.HALF_UP);
    	
    	//计算用户实际得到金额
    	BigDecimal withdrawalMoneysubtract = withdrawalMoney.subtract(withdrawalMoneysetScale);
    	
    	//计算用户提现后还剩余多少金额
    	BigDecimal moneysubtract = money.subtract(withdrawalMoney);
    	
    	//添加一天用户提现记录
    	WithdrawalEntity withdrawalEntity = new WithdrawalEntity();
    	withdrawalEntity.setUserId(userId);//用户id
    	withdrawalEntity.setUserName(user.getNickName());//用户昵称
    	withdrawalEntity.setWithdrawalMoney(withdrawalMoney);//提现金额
    	withdrawalEntity.setRealityMoney(withdrawalMoneysubtract);//实到金额(扣除手续费后金额)
    	withdrawalEntity.setFrontVacancies(money);//提现前余额
    	withdrawalEntity.setBehindVacancies(moneysubtract);//提现后余额(当前余额)
    	withdrawalEntity.setServiceCharge(withdrawalMoneysetScale);//提现手续费
    	withdrawalEntity.setServiceChargeScale(new BigDecimal(serviceChargeScale));//提现手续费比例
    	withdrawalEntity.setMakeCollectionsUrl(withdrawalVo.getMakeCollectionsUrl());//用户收款二维码链接
    	withdrawalEntity.setType(withdrawalVo.getType());//收款类型(1:支付宝  2:微信)
    	withdrawalEntity.setStatus(0);//审核状态(0:审核中  1:审核通过  2:审核拒绝)
    	withdrawalEntity.setTime(new Date());//创建时间
    	withdrawalEntity.setContent("等待审核!");//审核内容
    	
    	withdrawalService.insert(withdrawalEntity);
    	
    	//修改用户的提现余额
    	UserEntity userEntity = new UserEntity();
    	userEntity.setMoney(moneysubtract);
    	userEntity.setId(userId);
    	userService.updateById(userEntity);
    	
        return new Result<List<WithdrawalEntity>>().ok(null);
    }
    
    

}