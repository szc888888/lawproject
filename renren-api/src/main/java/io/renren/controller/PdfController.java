package io.renren.controller;


import io.renren.annotation.Login;
import io.renren.annotation.LoginUser;
import io.renren.commom.ChatService;
import io.renren.common.utils.Result;
import io.renren.entity.UserEntity;
import io.renren.service.PdfDocService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/pdf")
public class PdfController {
    @Autowired
    private ChatService chatService;
    @Autowired
    private PdfDocService pdfDocService;


    @PostMapping("uploadPdf")
    @ApiOperation("上传PDF")
    @Login
    public Result test1(@RequestParam("file") MultipartFile file,@LoginUser UserEntity user) throws IOException {
        return chatService.gptEmbeddings(file,user);
    }
    @GetMapping("pdfList")
    @ApiOperation("获取我的PDF列表")
    @Login
    public Result pdfList(@LoginUser UserEntity user) throws IOException {

        return pdfDocService.queryMyPdfList(user);
    }
}
