package io.renren.commom;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONArray;
import com.unfbx.chatgpt.entity.chat.ChatCompletion;
import com.unfbx.chatgpt.entity.chat.Message;
import com.unfbx.chatgpt.entity.images.Item;
import io.renren.common.redis.RedisKeys;
import io.renren.common.redis.RedisService;
import io.renren.common.redis.RedisUtils;
import io.renren.common.utils.Base64ImgUtil;
import io.renren.common.utils.IpUtils;
import io.renren.dto.DrawDTO;
import io.renren.dto.FsDrawDTO;
import io.renren.entity.*;
import io.renren.milvus.MilvusService;
import io.renren.oss.cloud.OSSFactory;
import io.renren.service.*;
import io.renren.utils.BASE64DecodedMultipartFile;
import io.renren.vo.MsgMoneyVo;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

@Component
public class AsyncService {
    @Autowired
    private UserService userService;
    @Autowired
    private SpeciesListService speciesListService;
    @Autowired
    private SysParamsService sysParamsService;
    @Autowired
    private QuestionAnswerService questionAnswerService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private FlagstudioService flagstudioService;

    @Autowired
    private DrawRecordService drawRecordService;
    @Autowired
    private MilvusService milvusService;
    @Autowired
    private PdfDocService pdfDocService;
    /**
     * 修改次数 增加流水
     * @param userId 用户ID
     * @param msg 消息内容
     * @param msgCount 次数
     * @param type 类型
     */
    @Async
    public void updateUserMsgCountAndLog(Long userId, String msg,int msgCount,int type) {
        UserEntity user = userService.selectById(userId);
        if(!user.isVip() || type == 4){
            userService.updateMsgCount(userId,msgCount);

            addSpeciesList(userId,msg,type,msgCount);
        }

    }
    /**
     * 增加了4.0以后 收费模式修改了  这里就问答专用了 目前
     * 修改次数 增加流水
     * @param userId 用户ID
     * @param msg 消息内容
     * @param msgMoneyVo
     * @param type 类型
     */
    @Async
    public void updateUserMsgCountAndLogByQuestionsAndAnswersOnly(Long userId, String msg, MsgMoneyVo msgMoneyVo, int type) {
        if(msgMoneyVo.getType() == 1){
            type = 9;
        }
        if(msgMoneyVo.isCharging()){
            userService.updateMsgCount(userId,-msgMoneyVo.getMoney());
            addSpeciesList(userId,msg,type,-msgMoneyVo.getMoney());
        }

    }
    /**
     * 增加流水
     */
    public void addSpeciesList(Long userId, String msg,int type,int msgCount){
        //增加流水记录
        SpeciesListEntity speciesListEntity = new SpeciesListEntity();
        speciesListEntity.setSpecies(msgCount);
        speciesListEntity.setType(type);
        if(StrUtil.isNotBlank(msg)){
            speciesListEntity.setMsgContent(msg);
        }
        speciesListEntity.setCreateTime(new Date());
        speciesListEntity.setUserid(userId);
        speciesListService.insert(speciesListEntity);
    }

    @Async
    public void bindToSuperior(UserEntity user) {
        String inviteMsgCount = sysParamsService.getValue("invite_msg_count");
        if(StrUtil.isBlank(inviteMsgCount)){
            inviteMsgCount = "1";
        }
        if(user.getPid() != null){
            updateUserMsgCountAndLog(user.getPid(),null,Integer.parseInt(inviteMsgCount),4);
        }
    }

    /**
     * 存用户的问题和答案
     * @param userId 用户ID
     * @param msg 用户提的问题
     * @param lastMessage GPT的回答
     */
    @Async
    public void saveQuestionAndAnswer(String conversationId,Long userId, String msg, String lastMessage,MsgMoneyVo msgMoneyVo) {
        QuestionAnswerEntity questionAnswer = new QuestionAnswerEntity();
        questionAnswer.setQuestion(msg);
        questionAnswer.setAnswer(lastMessage);
        questionAnswer.setUserId(userId);
        questionAnswer.setCreateTime(new Date());
        questionAnswer.setConversationId(conversationId);
//        ChatCompletion quer = ChatCompletion.builder().messages(Arrays.asList( Message.builder().content(msg).role(Message.Role.USER).build())).build();
//        questionAnswer.setQuesTokens(quer.tokens());
//        ChatCompletion anws = ChatCompletion.builder().messages(Arrays.asList( currentMessage)).build();
//        questionAnswer.setAnsTokens(anws.tokens());
//        questionAnswer.setAllTokens(questionAnswer.getQuesTokens()+questionAnswer.getAnsTokens());
//        questionAnswer.setType(msgMoneyVo.getType());
        questionAnswerService.insert(questionAnswer);
        //存回答
//        TokenEntity tokenByUserId = tokenService.getTokenByUserId(userId);
//        String token = tokenByUserId.getToken();
//        String messageContext =  (String)redisService.get(token);//这里是回答  肯定就是有提了问题才会来这里 所以此处不用判断 绝对有
//        List<Message> messages = JSONUtil.toList(messageContext, Message.class);
//
//        messages.add(currentMessage);
//        redisService.set(token,JSONUtil.toJsonStr(messages),300);

    }

    /**
     * 5分钟更新下最后活跃时间和IP
     * @param userId
     * @param request
     */
    @Async
    public void updateLoginTimeAndIp(Long userId, HttpServletRequest request) {
        String s = (String) redisService.get(RedisKeys.LOGIN_IP, userId.toString());
        if(StrUtil.isBlank(s)){
            String ipAddr = IpUtils.getIpAddr(request);
            UserEntity user = new UserEntity();
            user.setIp(ipAddr);
            user.setId(userId);
            user.setLastLogin(new Date());
            userService.updateById(user);
            redisService.set(RedisKeys.LOGIN_IP,userId.toString(),userId.toString(),5 * 60);
        }
    }

    @Async
    public void updateOpenID(UserEntity user, String openid) {
        UserEntity neeUser = new UserEntity();
        neeUser.setId(user.getId());
        neeUser.setWxOpenid(openid);
        userService.updateById(neeUser);
    }

    @Async
    public void saveDrawRecord(Long id, String msg, String url,String negativePrompt,int type) {
        DrawRecordEntity drawRecord = new DrawRecordEntity();
        drawRecord.setUserId(id);
        drawRecord.setPrompt(msg);
        drawRecord.setImgUrl(url);
        drawRecord.setType(type);
        drawRecord.setCreateTime(new Date());
        drawRecord.setNegativePrompt(negativePrompt);
        drawRecordService.insert(drawRecord);
    }

    @Async
    public void sdRecord(Long userId,DrawDTO dto, int intValue, JSONArray jsonArray) throws IOException {
        updateUserMsgCountAndLog(userId,"SD绘画",-intValue,7);
        for (Object o : jsonArray) {
            String s1 = o.toString();
            //上传到阿里云OSS
            MultipartFile file = BASE64DecodedMultipartFile.base64ToMultipartFile("data:image/png,"+s1);
            //上传文件
            String extension = FilenameUtils.getExtension(file.getOriginalFilename());
            //拿到文件存储地址
            String url = OSSFactory.build().uploadSuffix(file.getBytes(), extension);
            saveDrawRecord(userId,dto.getPrompt(),url,dto.getNegativePrompt(),1);
        }

    }

    @Async
    public void updateFsToken(FlagstudioEntity flagstudioEntity) {
        FlagstudioEntity flagstudioEntitynew = new FlagstudioEntity();
        flagstudioEntitynew.setId(flagstudioEntity.getId());
        flagstudioEntitynew.setToken(flagstudioEntitynew.getToken());
        flagstudioEntitynew.setTokenTime(flagstudioEntity.getTokenTime());
        flagstudioService.updateById(flagstudioEntitynew);
    }

    @Async
    public void fsRecord(Long userId, FsDrawDTO dto, int parseInt, JSONArray jsonArray,FlagstudioEntity flagstudioEntity) throws IOException {
        updateUserMsgCountAndLog(userId,"FS绘画",-parseInt,8);
        for (Object o : jsonArray) {
            String s1 = o.toString();
            //上传到阿里云OSS
            MultipartFile file = BASE64DecodedMultipartFile.base64ToMultipartFile("data:image/png,"+s1);
            //上传文件
            String extension = FilenameUtils.getExtension(file.getOriginalFilename());
            //拿到文件存储地址
            String url = OSSFactory.build().uploadSuffix(file.getBytes(), extension);
            saveDrawRecord(userId,dto.getPrompt(),url,dto.getNegative_prompts(),3);
        }
        //增加这个的次数
        FlagstudioEntity flagstudioEntityNew = new FlagstudioEntity();
        flagstudioEntityNew.setId(flagstudioEntity.getId());
        flagstudioEntityNew.setDayCount(flagstudioEntity.getDayCount() + dto.getFsCount());
        flagstudioService.updateById(flagstudioEntityNew);
    }

    @Async
    public void releaseCollection(String token) {
        TokenEntity byToken = tokenService.getByToken(token);
        List<PdfDocEntity> list = pdfDocService.queryPdfListByUserId(byToken.getUserId());
        if(CollUtil.isNotEmpty(list)){
            for (PdfDocEntity pdfDocEntity:list) {
                String collectionName = "pdf_" + byToken.getUserId() + "_" + pdfDocEntity.getId();
                milvusService.releaseCollection(collectionName);
            }
        }
    }
}
