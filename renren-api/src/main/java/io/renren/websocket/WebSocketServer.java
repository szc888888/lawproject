package io.renren.websocket;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.unfbx.chatgpt.entity.chat.Message;
import io.renren.commom.AsyncService;
import io.renren.commom.CommonService;
import io.renren.common.exception.RenException;
import io.renren.entity.TokenEntity;
import io.renren.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import java.util.List;
@Slf4j
@Component
@ServerEndpoint("/api/websocket/{token}")
public class WebSocketServer {
    //在线总数
    private static int onlineCount;
    //当前会话
    public Session session;
    //用户token
    public String token;
    public Long userId;

    private static TokenService tokenService;
    private static CommonService commonService;
    private static AsyncService asyncService;

    @Autowired
    public void setTokenService(TokenService tokenService) {
        this.tokenService = tokenService;
    }
    @Autowired
    public void setAsyncService(AsyncService asyncService) {
        this.asyncService = asyncService;
    }
    @Autowired
    public void setCommonService(CommonService commonService) {
        this.commonService = commonService;
    }
    private static CopyOnWriteArraySet<WebSocketServer> webSocketSet = new CopyOnWriteArraySet<>();
    /**
     * 用来存放每个客户端对应的WebSocketServer对象
     */
    private static ConcurrentHashMap<String, WebSocketServer> webSocketMap = new ConcurrentHashMap();


    /**
     * 建立连接
     * @param session
     * @param token
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token)  {
        //查询token信息
        try {
            TokenEntity tokenEntity = tokenService.getByToken(token);
            if(tokenEntity == null ){//|| tokenEntity.getExpireDate().getTime() < System.currentTimeMillis()

                // 立即关闭连接，并确保线程不再操作 session
                CloseReason reason = new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, "token失效，请重新登录");

                if (session.isOpen()) { // 确保连接未提前关闭
                    session.close(reason);
                    log.error("连接已关闭，状态码: {}, 原因: {}", reason.getCloseCode().getCode(), reason.getReasonPhrase()+  session.isOpen());
                } else {
                    log.error("连接已处于关闭状态，无法发送关闭帧");
                }
                return;
//                session.close(reason);
//
//                log.error("连接已关闭，原因: {}", reason.getReasonPhrase()+  session.isOpen());
//                return; // 确保后续代码不执行
//                RenException rrException = new RenException("token失效，请重新登录");
//                rrException.setCode(8000);
//                throw rrException;

                //
            }
            this.userId = tokenEntity.getUserId();
            this.session = session;
            this.token = token;
            webSocketSet.add(this);
            if (webSocketMap.containsKey(token)) {
                webSocketMap.remove(token);
                webSocketMap.put(token, this);
            } else {
                webSocketMap.put(token, this);
                addOnlineCount();
            }
            log.info("[连接ID:{}] 建立连接, 当前连接数:{}", this.token, getOnlineCount());
        } catch (IOException e) {
            log.error("关闭连接失败", e);
        }
    }

    /**
     * 断开连接
     */
    @OnClose
    public void onClose() {
        try{
            webSocketSet.remove(this);
            if (webSocketMap.containsKey(token)) {
                webSocketMap.remove(token);
                subOnlineCount();
            }
            if(StrUtil.isNotBlank(token)){
                asyncService.releaseCollection(token);
            }
            log.info("[连接ID:{}] 断开连接, 当前连接数:{}", token, getOnlineCount());
        }catch (Exception e){
            log.error("进了error");
        }
    }
    /**
     * 发送错误
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.info("[连接ID:{}] 错误原因:{}", this.token, error.getMessage());
        error.printStackTrace();
    }
    /**
     * 接收到客户端消息
     * @param msg
     */
    @OnMessage
    public void onMessage(String message) throws IOException {
        if ("heartbeat".equals(message)) {
            log.info("[连接ID:{}] 收到心跳消息", this.token);
            this.session.getBasicRemote().sendText("heartbeat"); // 回复客户端
            return;
        }

        log.info("[连接ID:{}] 收到消息:{}", this.token, message);

        // 原来的处理逻辑...
        JSONObject jsonObject = JSONObject.parseObject(message);
        String modelId = jsonObject.getString("modelId");
        String msg = jsonObject.getString("msg");
        String conversationId = jsonObject.getString("conversationId");

        Integer type = jsonObject.getInteger("type");
        List<Message> history = jsonObject.getObject("history", new TypeReference<List<Message>>() {});

        Boolean gptType = jsonObject.getObject("gptType", boolean.class);
        if (gptType == null) {
            gptType = false;
        }
//        if (StringUtils.isBlank(type)) {
//            type = "0";
//        }
        if (type==2){

        }else {
            commonService.sendMsgByDeepseek(conversationId, history, modelId, msg, this);
        }
    }

    //        if(type.equals("0")){
////            commonService.deepseekByWebUi(msg,"deepseek");
////            commonService.sendMsg(modeId,msg,webSocketServer,0);
//            //判断用户是否为VIP或者是否有充足次数
////            if(gptType){
////                commonService.onMessage4(modelId,msg,this);
////            }else{
//               commonService.onMessage(modelId,msg,this);
////            }
//
//        }else{
//            String pdfId = jsonObject.getString("pdfId");
//            commonService.onMessagePdf(msg,pdfId,this);
//        }
    /**
     * 获取当前连接数
     *
     * @return
     */
    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    /**
     * 当前连接数加一
     */
    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    /**
     * 当前连接数减一
     */
    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }
}
