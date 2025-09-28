package io.renren.utils;

import com.alipay.api.internal.util.file.IOUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @Author: JCccc
 * @Date: 2022-8-25 02:15
 * @Description:
 */
public class MyFileConvertUtil {

    protected static final Logger logger = LoggerFactory.getLogger(MyFileConvertUtil.class);


    /**
     * url转变为 MultipartFile对象
     * @param url 网络地址链接
     * @param fileName  文件名
     * @return
     * @throws Exception
     */
    public static MultipartFile createMultipartFile(String url, String fileName,String type){
        FileItem item = null;
        CommonsMultipartFile commonsMultipartFile  = null;
        try {
            //HttpURLconnection是基于http协议的，支持get，post，put，delete等各种请求方式，最常用的就是get和post  这里是根据url发起一个HTTP请求
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setReadTimeout(30000); //设置从主机读取数据超时（单位：毫秒）
            conn.setConnectTimeout(30000); //设置连接主机超时（单位：毫秒）
            //设置应用程序要从网络连接读取数据
            conn.setDoInput(true);  //允许读入
            conn.setRequestMethod("GET"); //设置请求方式为GET
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {  //发起请求并获取响应码比对。
                //得到响应流
                InputStream is = conn.getInputStream();
                //采用参数指定临界值和系统临时文件夹构造文件项工厂对象。
                FileItemFactory factory = new DiskFileItemFactory(16, null);
                String textFieldName = "uploadfile";
                //根据DiskFileItemFactory相关配置将每一个请求消息实体项目创建成DiskFileItem 实例
                item = factory.createItem(textFieldName, ContentType.APPLICATION_OCTET_STREAM.toString(), false, fileName);
                OutputStream os = item.getOutputStream();

                int bytesRead = 0;
                byte[] buffer = new byte[8192];
                while ((bytesRead = is.read(buffer, 0, 8192)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }

                // 转换为MultipartFile对象
                commonsMultipartFile = new CommonsMultipartFile(item);
                os.close();
                is.close();
            }
        } catch (Exception e) {
            throw new RuntimeException("文件下载失败", e);
        }

        return commonsMultipartFile ;
    }
    public static MultipartFile getMultipartFile(File file) {
        FileItem item = new DiskFileItemFactory().createItem("file"
                , MediaType.MULTIPART_FORM_DATA_VALUE
                , true
                , file.getName());
        try (InputStream input = new FileInputStream(file);
             OutputStream os = item.getOutputStream()) {
            // 流转移
            IOUtils.copy(input, os);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid file: " + e, e);
        }

        return new CommonsMultipartFile(item);
    }
}
