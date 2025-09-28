/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.controller;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.unfbx.chatgpt.OpenAiStreamClient;
import com.unfbx.chatgpt.function.KeyRandomStrategy;
import com.unfbx.chatgpt.interceptor.OpenAILogger;
import io.renren.annotation.Login;
import io.renren.annotation.LoginUser;
import io.renren.common.exception.RenException;
import io.renren.common.redis.RedisKeys;
import io.renren.common.utils.DateUtils;
import io.renren.common.utils.FontUtils;
import io.renren.common.utils.PosterUtils;
import io.renren.common.utils.Result;
import io.renren.common.validator.ValidatorUtils;
import io.renren.dto.MobileLoginDTO;
import io.renren.dto.UpdateUserDTO;
import io.renren.entity.PosterEntity;
import io.renren.entity.QuestionAnswerEntity;
import io.renren.entity.SpeciesListEntity;
import io.renren.entity.UserEntity;
import io.renren.service.*;
import io.renren.vo.InviteVo;
import io.renren.vo.TodayQsCount;
import io.renren.websocket.WebSocketServer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 测试接口
 *
 * @author Mark sunlightcs@gmail.com
 */

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/user")
@Api(tags="用户信息等各种")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private SpeciesListService speciesListService;
    @Autowired
    private QuestionAnswerService questionAnswerService;
    @Autowired
    private SysParamsService sysParamsService;

    @Autowired
    private PosterService posterService;
    private Font regularFont;
    private Font mediumFont;
    @PostConstruct
    public void init() {
         regularFont = FontUtils.loadLocalFont("fonts/PingFang SC Regular.ttf", Font.TRUETYPE_FONT, 400);
         mediumFont = FontUtils.loadLocalFont("fonts/PingFang Medium.ttf", Font.TRUETYPE_FONT, 400);
    }
    @Login
    @GetMapping("userInfo")
    @ApiOperation(value="获取用户信息", response=UserEntity.class)
    public Result<UserEntity> userInfo(@ApiIgnore @LoginUser UserEntity user){
        String value = sysParamsService.getValue("vip_gift_number");
        String msg_img_price = sysParamsService.getValue("msg_img_price");
        user.setVipCount(value);
        user.setImgCount(msg_img_price);
        if(user.isVip()){
            int days = DateUtils.differNaturalDays(new Date(), user.getVipDate());
            user.setRemark("VIP剩余:"+days+"天");
        }else{
            user.setRemark("普通用户");
        }
        String countVip3 = sysParamsService.getValue("COUNT_VIP_3");
        String countVip4 = sysParamsService.getValue("COUNT_VIP_4");
        String countUser3 = sysParamsService.getValue("COUNT_USER_3");
        String countUser4 = sysParamsService.getValue("COUNT_USER_4");
        String money3 = sysParamsService.getValue("MONEY3");
        String money4 = sysParamsService.getValue("MONEY4");
        String fsImgPrice = sysParamsService.getValue("fs_img_price");//SD绘画价钱
        String sdImgPrice = sysParamsService.getValue("sd_img_price");//SD绘画价钱
        //查询今日问答了3.5和4.0的次数
        TodayQsCount todayQsCount = questionAnswerService.count3And4TodayByUser(user.getId());
        int threeCuont = todayQsCount.getThreeCuont();
        int fourCuont = todayQsCount.getFourCuont();
        int todayTreeCount = 0;
        int todayFourCount = 0;
        if(user.isVip()){
            todayTreeCount = Integer.parseInt(countVip3) - threeCuont;
            todayFourCount = Integer.parseInt(countVip4) - fourCuont;
        }else{
            todayTreeCount = Integer.parseInt(countUser3) - threeCuont;
            todayFourCount = Integer.parseInt(countUser4) - fourCuont;
        }
        user.setTodayTreeCount(todayTreeCount < 0 ? 0 : todayTreeCount);
        user.setTodayFourCount(todayFourCount < 0 ? 0 : todayFourCount);
        user.setCountVip3(countVip3);
        user.setCountVip4(countVip4);
        user.setCountUser3(countUser3);
        user.setCountUser4(countUser4);
        user.setMoney3(money3);
        user.setMoney4(money4);
        user.setFsImgPrice(fsImgPrice);
        user.setSdImgPrice(sdImgPrice);
        return new Result<UserEntity>().ok(user);
    }


    @Login
    @PostMapping("update")
    @ApiOperation(value="修改用户信息", response=UserEntity.class)
    public Result<UserEntity> update(@ApiIgnore @LoginUser UserEntity user,@RequestBody UpdateUserDTO dto){
        ValidatorUtils.validateEntity(dto);
        UserEntity updateUser = new UserEntity();
        updateUser.setId(user.getId());
        updateUser.setNickName(dto.getNickName());
        updateUser.setAvatar(dto.getAvatar());
        userService.updateById(updateUser);
        return new Result<UserEntity>().ok(user);
    }

    @Login
    @GetMapping("querySpeciesList")
    @ApiOperation(value="查询问答次数流水", response= SpeciesListEntity.class)
    public Result<List<SpeciesListEntity>> querySpeciesList(@LoginUser UserEntity user){

        List<SpeciesListEntity> list = speciesListService.querySpeciesListByUserId(user);
        return new Result<List<SpeciesListEntity>>().ok(list);
    }
    @Login
    @GetMapping("queryQuestionAnswerList")
    @ApiOperation(value="查询问答记录,只返回最新500条", response= QuestionAnswerEntity.class)
    public Result<List<QuestionAnswerEntity>> queryQuestionAnswerList(String pageNum,@LoginUser UserEntity user){
        List<QuestionAnswerEntity> list = new ArrayList<>();
        if(StrUtil.isBlank(pageNum)){
            list = questionAnswerService.queryQuestionAnswerList(user);
        }else{
            list = questionAnswerService.queryQuestionAnswerListPage(user,pageNum);
        }

        return new Result<List<QuestionAnswerEntity>>().ok(list);
    }

    @Login
    @GetMapping("getInviteUrl")
    @ApiOperation(value="获取邀请链接", response= String.class)
    public Result<InviteVo> getInviteUrl(@LoginUser UserEntity user){
        String h5_url = sysParamsService.getValue("H5_URL");//H5域名
        String invite_title = sysParamsService.getValue("invite_title");//微信公众号/小程序分享的标题
        String invite_img_url = sysParamsService.getValue("invite_img_url");//微信公众号/小程序分享的图片
        String invite_summary = sysParamsService.getValue("invite_summary");//微信公众号分享的文字内容
        String invite_poster_url = sysParamsService.getValue("invite_poster_url");//海报的图片URL
        InviteVo inviteVo = new InviteVo();
        inviteVo.setH5Url(h5_url + "?pid=" + user.getId());
        inviteVo.setInviteTitle(invite_title);
        inviteVo.setInviteImgUrl(invite_img_url);
        inviteVo.setInviteSummary(invite_summary);
        inviteVo.setPid(user.getId());
        inviteVo.setInvitePosterUrl(invite_poster_url);
        return new Result<InviteVo>().ok(inviteVo);
    }
    @Login
    @GetMapping("getPoster")
    @ApiOperation(value="获取海报图片", response= String.class)
    public Result<String> getPoster(@LoginUser UserEntity user) throws Exception {
        String avatar = user.getAvatar();
        String nickName = user.getNickName();
        String h5_url = sysParamsService.getValue("H5_URL");//H5域名
        String invUrl = h5_url + "?pid=" + user.getId();
        PosterEntity poster = posterService.getOne();
        if(poster == null){
            return new Result<String>().error("暂未配置海报信息");
        }
        if(StrUtil.isBlank(avatar)){
            avatar = poster.getLogo();
        }
        BufferedImage image = PosterUtils.createImage(regularFont, mediumFont,poster.getPosterImg(),avatar,invUrl,poster.getLogo(),nickName,poster.getTitle(),poster.getContent());
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", stream);
        String asBase64 = Base64.encode(stream.toByteArray());
        return new Result<String>().ok(asBase64);
    }
}