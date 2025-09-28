/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.controller;


import cn.hutool.core.util.StrUtil;
import io.renren.commom.CommonService;
import io.renren.common.redis.RedisKeys;
import io.renren.common.redis.RedisService;
import io.renren.common.utils.Result;
import io.renren.config.WxMpConfiguration;
import io.renren.dto.WeChatLoginDTO;
import io.renren.service.SysParamsService;
import io.renren.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录接口
 *
 * @author Mark sunlightcs@gmail.com
 */

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/weChatGzh")
@Api(tags="扫码登录接口")
@Slf4j
public class WeChatGzhController {
    @Autowired
    private UserService userService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private CommonService commonService;
    @Autowired
    private SysParamsService sysParamsService;
    private WxMpService wxService = new WxMpServiceImpl();;
    public void init() {
        WxMpDefaultConfigImpl configStorage = new WxMpDefaultConfigImpl();
        configStorage.setAppId(sysParamsService.getValue("mp_wx_login_appid"));
        configStorage.setSecret(sysParamsService.getValue("mp_wx_login_secret"));
        configStorage.setToken(sysParamsService.getValue("mp_wx_wechat_token"));
        configStorage.setAesKey(sysParamsService.getValue("mp_wx_wechat_encodingaeskey"));
        wxService.setWxMpConfigStorage(configStorage);
    }

    @GetMapping("getWechatQrcode")
    @ApiOperation("获取WEB微信扫码登录的二维码")
    public Result getWechatQrcode(String pid) throws WxErrorException {
        init();
        String s = RandomStringUtils.randomAlphabetic(8);
        WxMpQrCodeTicket wxMpQrCodeTicket = wxService.getQrcodeService().qrCodeCreateTmpTicket(s, 5 * 60);
        redisService.set(RedisKeys.GZH_TICKET, s,s, 5 * 60);
        redisService.set(RedisKeys.GZH_INVITE_PID, s,pid, 5 * 60);
//        wxService.getTicket()
        String url = wxMpQrCodeTicket.getUrl();
        Map<String,String> map = new HashMap<>();
        map.put("qrcodeUrl",url);
        map.put("code",s);
        return new Result().ok(map);
    }

    @PostMapping("checkLogin")
    @ApiOperation("定时器查询是否登录成功")
    public Result checkLogin(@RequestBody WeChatLoginDTO dto) throws WxErrorException {

        return commonService.mpWeChatScanLogin(dto);
    }
    /**
     * 微信验证消息
     */
    @GetMapping( value = "serve",produces = "text/plain;charset=utf-8")
    @ApiOperation(value = "微信验证消息",notes = "微信验证消息")
    public String authGet(@RequestParam(name = "signature", required = false) String signature,
                          @RequestParam(name = "timestamp", required = false) String timestamp,
                          @RequestParam(name = "nonce", required = false) String nonce,
                          @RequestParam(name = "echostr", required = false) String echostr) throws WxErrorException {

        init();
        if (wxService == null) {
            throw new IllegalArgumentException("未找到对应配置的服务，请核实！");
        }
        boolean b = wxService.checkSignature(timestamp, nonce, signature);
        if (wxService.checkSignature(timestamp, nonce, signature)) {
            return echostr;
        }

        return "fail";
    }

    /**
     *微信获取消息
     */
    @PostMapping("serve")
    @ApiOperation(value = "微信获取消息",notes = "微信获取消息")
    public void post(@RequestBody String requestBody,
                     @RequestParam("signature") String signature,
                     @RequestParam("timestamp") String timestamp,
                     @RequestParam("nonce") String nonce,
                     @RequestParam("openid") String openid,
                     @RequestParam(name = "encrypt_type", required = false) String encType,
                     @RequestParam(name = "msg_signature", required = false) String msgSignature,
                     HttpServletRequest request,
                     HttpServletResponse response) throws IOException {

        init();

        if (!wxService.checkSignature(timestamp, nonce, signature)) {
            throw new IllegalArgumentException("非法请求，可能属于伪造的请求！");
        }

        String out = null;
        if (encType == null) {
            // 明文传输的消息
            WxMpXmlMessage inMessage = WxMpXmlMessage.fromXml(requestBody);
            WxMpXmlOutMessage outMessage = this.route(inMessage);
            if(outMessage == null) {
                return;
            }
            out = outMessage.toXml();;
        } else if ("aes".equalsIgnoreCase(encType)) {
            // aes加密的消息
            WxMpXmlMessage inMessage = WxMpXmlMessage.fromEncryptedXml(requestBody, wxService.getWxMpConfigStorage(),
                    timestamp, nonce, msgSignature);
            WxMpXmlOutMessage outMessage = this.route(inMessage);
            if(outMessage == null) {
                return;
            }

            out = outMessage.toEncryptedXml(wxService.getWxMpConfigStorage());
        }

        response.setCharacterEncoding("UTF-8");
        PrintWriter writer = response.getWriter();
        writer.print(out);
        writer.close();
    }

    private WxMpXmlOutMessage route(WxMpXmlMessage message) {
        try {
            init();
            return WxMpConfiguration.getWxMpMessageRouter(wxService).route(message);
        } catch (Exception e) {
            log.error("路由消息时出现异常！", e);
        }

        return null;
    }




}