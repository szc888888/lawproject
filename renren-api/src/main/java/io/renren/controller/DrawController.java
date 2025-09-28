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
import io.renren.commom.CommonService;
import io.renren.common.utils.Result;
import io.renren.common.validator.ValidatorUtils;
import io.renren.dto.ChatMsgDTO;
import io.renren.dto.DrawDTO;
import io.renren.dto.FsDrawDTO;
import io.renren.entity.DrawRecordEntity;
import io.renren.entity.UserEntity;
import io.renren.service.DrawRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

/**
 * chatgpt各种接口
 *
 * @author Mark sunlightcs@gmail.com
 */

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/draw")
@Api(tags="非GPT得绘画接口")
public class DrawController {
   @Autowired
   private CommonService commonService;
   @Autowired
   private DrawRecordService drawRecordService;

    @Login
    @PostMapping("sd")
    @ApiOperation("SD绘画接口")
    public Result sdDraw(@LoginUser UserEntity user,@RequestBody DrawDTO dto) throws ParseException, IOException {
        //表单校验
        ValidatorUtils.validateEntity(dto);

        return commonService.sdDraw(user,dto);
    }

    @Login
    @PostMapping("getList")
    @ApiOperation("绘画记录列表")
    public Result getList(@LoginUser UserEntity user) throws ParseException, IOException {

        List<DrawRecordEntity> list = drawRecordService.getListByUser(user.getId());
        return new Result().ok(list);
    }


    @Login
    @PostMapping("fs")
    @ApiOperation("fs绘画接口")
    public Result fsDraw(@LoginUser UserEntity user,@RequestBody FsDrawDTO dto) throws ParseException, IOException {
        //表单校验
        ValidatorUtils.validateEntity(dto);

        return commonService.fsDraw(user,dto);
    }
}