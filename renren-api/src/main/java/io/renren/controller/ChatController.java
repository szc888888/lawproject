/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.controller;


import io.renren.annotation.Login;
import io.renren.annotation.LoginUser;
import io.renren.commom.ChatService;
import io.renren.common.entity.ShortVideoEntity;
import io.renren.common.service.ShortVideoService;
import io.renren.common.utils.Result;
import io.renren.common.validator.ValidatorUtils;
import io.renren.dto.*;
import io.renren.entity.QuestionAnswerEntity;
import io.renren.entity.UserEntity;
import io.renren.oss.cloud.OSSFactory;
import io.renren.service.QuestionAnswerService;
import io.renren.service.TokenService;
import io.renren.service.UserService;
//import io.renren.utils.ExtractVideoFirstFrameUtil;
import io.renren.utils.HttpDownloadUtil;
import io.renren.utils.MyFileConvertUtil;
import io.renren.vo.DouYinVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * chatgpt各种接口
 *
 * @author Mark sunlightcs@gmail.com
 */

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/chat")
@Api(tags="chatgpt各种接口")
public class ChatController {
   @Autowired
   private ChatService chatService;
   @Autowired
   private ShortVideoService shortVideoService;
    @Autowired
    QuestionAnswerService questionAnswerService;
    @Value("${filePath}")
    private String filePath;
    @Login
    @PostMapping("draw")
    @ApiOperation("根据描述生成图片接口")
    public Result chatDraw(@LoginUser UserEntity user,@RequestBody ChatMsgDTO dto){
        //表单校验
        ValidatorUtils.validateEntity(dto);

        return chatService.chatDraw(user,dto);
    }

//    @Login
    @PostMapping("douyin")
    @ApiOperation("根据分享链接拉取抖音视频数据")
    @Login
    public Result douyin(@LoginUser UserEntity user,@RequestBody ChatMsgDTO dto) throws Exception {
        //表单校验
        ValidatorUtils.validateEntity(dto);
        DouYinVo douyin = chatService.douyin(dto.getMsg(),filePath);
        if(douyin != null){
            ShortVideoEntity shortVideoEntity = new ShortVideoEntity(user.getId(),douyin.getVideoUrl(),douyin.getMusicUrl(),null,douyin.getDesc(),douyin.getContent());
            String download = HttpDownloadUtil.download(shortVideoEntity.getVideoUrl(), filePath);
            shortVideoEntity.setVideoUrl(download);
            shortVideoService.insert(shortVideoEntity);
            return new Result();
        }
        return new Result().error("解析错误");
    }


    @PostMapping("test")
    @ApiOperation("测试下载")
    public Result test(@RequestBody TestDTO dto) throws Exception {
        String download = HttpDownloadUtil.download(dto.getUrl(), dto.getFileUrl());
        return new Result().ok(download);
    }

    @GetMapping("history")
    @ApiOperation("多个对话列表")
    public Result chatlist(@LoginUser UserEntity user) throws Exception {
        System.out.println("chatlist:user.getId():"+user.getId());
        List<ConversationDTO> conversationDTOs=  questionAnswerService.getFirstQuestions(user.getId());
        return new Result().ok(conversationDTOs);
    }

    @GetMapping("listByConversationId")
    @ApiOperation("单个对话列表")
    public Result chatlist(String conversationId) throws Exception {
        List<QuestionAnswerEntity> questionAnswerEntitys=  questionAnswerService.listByConversationId(conversationId);
        return new Result().ok(questionAnswerEntitys);
    }
}