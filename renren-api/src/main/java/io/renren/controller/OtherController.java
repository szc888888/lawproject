package io.renren.controller;

import io.renren.annotation.Login;
import io.renren.annotation.LoginUser;
import io.renren.commom.ChatService;
import io.renren.commom.CommonService;
import io.renren.common.entity.ShortVideoEntity;
import io.renren.common.redis.RedisService;
import io.renren.common.service.ShortVideoService;
import io.renren.common.utils.Result;
import io.renren.common.validator.ValidatorUtils;
import io.renren.dto.SendSmsDTO;
import io.renren.dto.TmplQsDTO;
import io.renren.entity.UserEntity;
import io.renren.milvus.MilvusService;
import io.renren.milvus.PushMaterielsConfig;
import io.renren.service.AiModelService;
import io.renren.service.HotQsService;
import io.renren.service.TmplCategoryService;
import io.renren.service.TmplQsService;
import io.renren.utils.PdfUtils;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * 模型类型 热门问题等等各种接口
 *
 * @author Mark sunlightcs@gmail.com
 */

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/other")
public class OtherController {

    @Autowired
    private AiModelService aiModelService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private TmplCategoryService tmplCategoryService;
    @Autowired
    private TmplQsService tmplQsService;
    @Autowired
    private HotQsService hotQsService;
    @Autowired
    private CommonService commonService;
    @Autowired
    private ChatService chatService;
    @Autowired
    private ShortVideoService shortVideoService;
    @GetMapping("queryAiModel")
    @ApiOperation("查询所有打开的模型列表")
    public Result queryAiModel(){
        return aiModelService.queryAiModel();
    }
    @GetMapping("queryTmplCate")
    @ApiOperation("查询所有打开的问题分类")
    public Result queryTmplCate(){
        return tmplCategoryService.queryTmplCate();
    }

    @GetMapping("queryQsByCateId")
    @ApiOperation("根据分类ID查询模板问题列表")
    public Result queryQsByCateId(TmplQsDTO dto){
        //表单校验
        ValidatorUtils.validateEntity(dto);
        return tmplQsService.queryQsByCateId(dto.getTid());
    }
    @GetMapping("queryAllQs")
    @ApiOperation("查询所有打开的模板问题")
    public Result queryAllQs(){
        return tmplQsService.queryAllQs();
    }
    @GetMapping("queryHotQs")
    @ApiOperation("查询所有设置的热门问题")
    public Result queryHotQs(){
        return hotQsService.queryHotQs();
    }

    @PostMapping("cleanHistory")
    @ApiOperation("切换模型,清空redis上下文记录")
    @Login
    public Result cleanHistory(@LoginUser UserEntity user, HttpServletRequest request){
        //从header中获取token
        String token = request.getHeader("token");
        //如果header中不存在token，则从参数中获取token
        if(StringUtils.isBlank(token)){
            token = request.getParameter("token");
        }
        redisService.deleteKey(token);
        return new Result();
    }

    @GetMapping("sendSms")
    @ApiOperation("发送短信接口")
    public Result sendSms(SendSmsDTO dto){
        ValidatorUtils.validateEntity(dto);
        return commonService.sendSms(dto);
    }

    @GetMapping("shortVideoList")
    @ApiOperation("获取我拉取的短视频列表")
    @Login
    public Result shortVideoList(@LoginUser UserEntity user, HttpServletRequest request){
        List<ShortVideoEntity> shortVideoEntities = shortVideoService.getShortVideoListByUserId(user.getId());
        return new Result().ok(shortVideoEntities);
    }
}
