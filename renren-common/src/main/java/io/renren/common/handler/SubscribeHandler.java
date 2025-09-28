/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.yixiang.co

 */
package io.renren.common.handler;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import io.renren.common.builder.TextBuilder;
import io.renren.common.redis.RedisKeys;
import io.renren.common.redis.RedisService;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
public class SubscribeHandler extends AbstractHandler {

    @Autowired
    private RedisService redisService;


    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage,
                                    Map<String, Object> context, WxMpService weixinService,
                                    WxSessionManager sessionManager) throws WxErrorException {


        String str = "你好，欢迎关注!";


        try {
            String eventKey = wxMessage.getEventKey();
            String openid = wxMessage.getFromUser();//openid
            if(StrUtil.isNotBlank(eventKey)){
                String event = eventKey.startsWith("qrscene_") ? eventKey.replace("qrscene_", "") : eventKey;
                String scene_str = (String)redisService.get(RedisKeys.GZH_TICKET, event);
                if(StrUtil.isNotBlank(scene_str)){//说明是web扫码登录进来的这里
                    redisService.set(RedisKeys.GZH_TICKET,event,openid,5*60);
                }
            }
            WxMpXmlOutMessage msg= WxMpXmlOutMessage.TEXT()
                    .content(str)
                    .fromUser(wxMessage.getToUser())
                    .toUser(wxMessage.getFromUser())
                    .build();
            return msg;
        } catch (Exception e) {
            this.logger.error(e.getMessage(), e);
        }



        return null;
    }



}
