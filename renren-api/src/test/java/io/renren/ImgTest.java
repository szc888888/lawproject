package io.renren;

import cn.hutool.core.codec.Base64;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.unfbx.chatgpt.entity.billing.Subscription;
import com.unfbx.chatgpt.entity.chat.ChatCompletion;
import com.unfbx.chatgpt.entity.images.ImageResponse;
import io.renren.common.utils.*;
import io.renren.oss.cloud.OSSFactory;
import io.renren.utils.BASE64DecodedMultipartFile;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Slf4j
public class ImgTest {
    @Test
    public void testA() throws Exception {
//        InputStream in1 = this.getClass().getResourceAsStream("/fonts/PingFang SC Regular.ttf");
//        Font regularFont = Font.createFont(Font.TRUETYPE_FONT, in1);
//        Font regularFont = FontUtils.loadLocalFont("fonts/PingFang SC Regular.ttf", Font.TRUETYPE_FONT, 400);
//        Font mediumFont = FontUtils.loadLocalFont("fonts/PingFang Medium.ttf", Font.TRUETYPE_FONT, 400);
        //昵称显示的字体（此字体库是从网上下载的，非windows自带），使用时引用FontName字体名称即可（字体名称：双击字体库可以看到字体名称）
//        InputStream in2 = this.getClass().getResourceAsStream("/fonts/PingFang Medium.ttf");
//        Font mediumFont = Font.createFont(Font.TRUETYPE_FONT, in2);
//        BufferedImage image = PosterUtils.createImage(regularFont, mediumFont);
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        ImageIO.write(image, "png", stream);
//        String asBase64 = "data:image/png;base64,"+ Base64.encode(stream.toByteArray());
//        System.out.println(asBase64);
//        ImageIO.write(image, "png", new File("/Users/shican/logs/demo1011.png"));
        //Basic aGFuZnU6aGFuZnU=  对应汉服   账号hanfu  模型3Guofeng3_v32Light
        //Basic bW9tb3l1Om1vbW95dQ==  对应momoyu账号  模型sd-v1-5-inpainting

//        JSONObject params = new JSONObject();
//        params.put("prompt", "best quality, masterpiece, highres, 1girl,blush,(seductive smile:0.8),star-shaped pupils,china hanfu,hair ornament,necklace, jewelry,Beautiful face,upon_body, tyndall effect,photorealistic, dark studio, rim lighting, two tone lighting,(high detailed skin:1.2), 8k uhd, dslr, soft lighting, high quality, volumetric lighting, candid, Photograph, high resolution, 4k, 8k, Bokeh");
//        params.put("negative_prompt","(((simple background))),monochrome ,lowres, bad anatomy, bad hands, text, error, missing fingers, extra digit, fewer digits, cropped, worst quality, low quality, normal quality, jpeg artifacts, signature, watermark, username, blurry, lowres, bad anatomy, bad hands, text, error, extra digit, fewer digits, cropped, worst quality, low quality, normal quality, jpeg artifacts, signature, watermark, username, blurry, ugly,pregnant,vore,duplicate,morbid,mut ilated,tran nsexual, hermaphrodite,long neck,mutated hands,poorly drawn hands,poorly drawn face,mutation,deformed,blurry,bad anatomy,bad proportions,malformed limbs,extra limbs,cloned face,disfigured,gross proportions, (((missing arms))),(((missing legs))), (((extra arms))),(((extra legs))),pubic hair, plump,bad legs,error legs,username,blurry,bad feet");
//        params.put("width", "512");
//        params.put("height", "512");
//        params.put("steps", "30");
//        params.put("batch_size", 3);
//        params.put("cfg_scale", 8);
//        params.put("seed", -1);
//        params.put("sampler_index", "Euler a");
//        String s = HttpUtil.sendPost("http://106.53.68.80:7860/sdapi/v1/txt2img", params, "Basic bW9tb3l1Om1vbW95dQ==");
//        JSONObject jsonObject = JSONObject.parseObject(s);
//        JSONArray jsonArray = jsonObject.getJSONArray("images");
//        for (Object o : jsonArray) {
//            String s1 = o.toString();
//            //上传到阿里云OSS
//            MultipartFile file = BASE64DecodedMultipartFile.base64ToMultipartFile("data:image/png,"+s1);
//            //上传文件
//            String extension = FilenameUtils.getExtension(file.getOriginalFilename());
//            //拿到文件存储地址
//            String url = OSSFactory.build().uploadSuffix(file.getBytes(), extension);
////            Base64ImgUtil.convertBase64ToImage(s1,"国风"+new Date().getTime()+"");
//        }

//        String s1 = HttpUtil.sendPost("http://106.53.68.80:7860/sdapi/v1/txt2img", params, "Basic aGFuZnU6aGFuZnU=");
//        JSONObject jsonObject1 = JSONObject.parseObject(s1);
//        JSONArray jsonArray1 = jsonObject1.getJSONArray("images");
//        for (Object o : jsonArray1) {
//            String s2 = o.toString();
//            Base64ImgUtil.convertBase64ToImage(s2,"国风模型账号"+new Date().getTime()+"");
//        }

    }

    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.add("0");
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");
        list.add("5");
        list.add("6");
        list.add("7");
        list.add("8");
        list.add("9");
        list.add("10");
        list.add("11");
        list.add("12");
        list.add("13");
        list.add("14");
        list.add("15");
        list.add("16");
        list.add("17");
        list.add("18");
        list.add("19");
        list.add("20");
        for ( int i = 0; i <10; i++) {
            System.out.println(list.get(0));
            list.remove(0);
        }
        System.out.println(list);
//        String str = "MybatisPlus分表查询可以通过使用分页插件和分页拦截器来实现。具体步骤如下：1. 使用分页插件在mybatis-plus的配置文件中，需要配置分页插件，例如：```<plugins>    <plugin interceptor=\\\"com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor\"/></plugins>```2. 在Mapper接口中使用Page对象在Mapper接口中定义一个查询方法，方法参数中需要使用Page对象，例如：```public interface UserMapper extends BaseMapper<User> {    List<User> selectUserList(Page<User> page);}```3. 在XML文件中编写分表查询SQL在XML文件中编写查询SQL时，需要使用分表的表名，例如：```<select id=\\\"selectUserList\\\" resultType=\\\"User\\\">    select * from user_${page.current} ${ew.customSqlSegment}</select>```其中，`${page.current}`表示当前页数，`${ew.customSqlSegment}`是MybatisPlus提供的动态SQL语句。4. 调用Mapper接口方法进行查询最后，在业务逻辑代码中调用Mapper接口方法进行查询，并将分页查询结果返回给前端展示，例如：```@Servicepublic class UserServiceImpl implements UserService {    @Autowired    private UserMapper userMapper;    @Override    public IPage<User> selectUserList(int page, int size) {        Page<User> userPage = new Page<>(page, size);        return userMapper.selectUserList(userPage);    }}```";
//        for (String s : str.split("```")) {
//            System.out.println(s);
//        }
//
//        String[] values1= StringUtils.substringsBetween(str,"```","```");
//        for (String s : StringUtils.substringsBetween(str, "```", "```")) {
//            System.out.println(s);
//        }


//     String s  = "";
//        for (int i = 0; i < values1.length; i++) {
//            //替换指定的字符串
//            str = str.replaceAll(values1[i], "替换");
////      System.err.print(s);
//            if(i == values1.length - 1) {
//                System.err.print(str);
//            }
//
//
//        }


    }
}
