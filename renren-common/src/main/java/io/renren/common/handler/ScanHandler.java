/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.yixiang.co

 */
package io.renren.common.handler;

import cn.hutool.core.util.StrUtil;
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
public class ScanHandler extends AbstractHandler {

    @Autowired
    private RedisService redisService;
    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMpXmlMessage, Map<String, Object> map,
                                    WxMpService wxMpService, WxSessionManager wxSessionManager) throws WxErrorException {
        // 扫码事件处理
        String event = wxMpXmlMessage.getEventKey();
        String openid = wxMpXmlMessage.getFromUser();//openid
        if(StrUtil.isNotBlank(event)){
            String scene_str = (String)redisService.get(RedisKeys.GZH_TICKET, event);
            if(StrUtil.isNotBlank(scene_str)){//说明是web扫码登录进来的这里
                redisService.set(RedisKeys.GZH_TICKET,event,openid,5*60);
                return new TextBuilder().build("登录成功", wxMpXmlMessage, wxMpService);
            }
        }
        return null;
    }
}
