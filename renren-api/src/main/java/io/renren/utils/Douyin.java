package io.renren.utils;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;

import io.renren.common.bean.ApiResult;
import io.renren.vo.DouYinVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 抖音视频去除水印工具.
 *
 * @author xtea
 * @date 2020-06-28 16:33
 */
public class Douyin {


    public static final String UA = "Mozilla/5.0 (iPhone; CPU iPhone OS 12_1_4 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/16D57 Version/12.0 Safari/604.1";
    public static final String API = "https://www.douyin.com/aweme/v1/web/aweme/detail/?aweme_id=";
    public static final String BOGUS_API = "https://tiktok.iculture.cc/X-Bogus";



    public static void main(String[] args) throws IOException {
        String msgFromDouyin = "3.56 AtR:/ 复制打开抖音，看看【丑得像是有背景的作品】视频内容纯属娱乐，无不良引导。# 跨境电商 # 独... https://v.douyin.com/iBQAWNL/";
//        downloadVideo(msgFromDouyin);

//        ApiResult apiResult = Douyin.fetchVideoScheme(msgFromDouyin);
    }


    /**
     * 远程获取无水印视频地址.
     *
     * @param shareInfo
     * @return
     */
    public static ApiResult fetchVideoScheme(String shareInfo) {
        try {
            String shortUrl = extractUrl(shareInfo);
            String originUrl = convertUrl(shortUrl);
            String itemId = parseItemIdFromUrl(originUrl);
            ApiResult apiBean = requestToAPI(itemId);
            return apiBean;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static DouYinVo getInfO(String shareInfo) {
        try {
            String shortUrl = extractUrl(shareInfo);
            String originUrl = convertUrl(shortUrl);
            String itemId = parseItemIdFromUrl(originUrl);//得到了ID
            Map<String, Object> jsonObject = new HashMap<>();
            jsonObject.put("url",API+itemId);
            jsonObject.put("user_agent",UA);
            JSONObject jsonObject1 = SmsPost(BOGUS_API, jsonObject);
            String param = jsonObject1.getString("param");
            String msToken = getRandomString(107);
            //这个可以就这样写死应该
            String odin_tt = "odin_tt=324fb4ea4a89c0c05827e18a1ed9cf9bf8a17f7705fcc793fec935b637867e2a5a9b8168c885554d029919117a18ba69; ttwid=1%7CWBuxH_bhbuTENNtACXoesI5QHV2Dt9-vkMGVHSRRbgY%7C1677118712%7C1d87ba1ea2cdf05d80204aea2e1036451dae638e7765b8a4d59d87fa05dd39ff";
            //这个可以就这样写死应该
            String bd_ticket_guard_client_data = "eyJiZC10aWNrZXQtZ3VhcmQtdmVyc2lvbiI6MiwiYmQtdGlja2V0LWd1YXJkLWNsaWVudC1jc3IiOiItLS0tLUJFR0lOIENFUlRJRklDQVRFIFJFUVVFU1QtLS0tLVxyXG5NSUlCRFRDQnRRSUJBREFuTVFzd0NRWURWUVFHRXdKRFRqRVlNQllHQTFVRUF3d1BZbVJmZEdsamEyVjBYMmQxXHJcbllYSmtNRmt3RXdZSEtvWkl6ajBDQVFZSUtvWkl6ajBEQVFjRFFnQUVKUDZzbjNLRlFBNUROSEcyK2F4bXAwNG5cclxud1hBSTZDU1IyZW1sVUE5QTZ4aGQzbVlPUlI4NVRLZ2tXd1FJSmp3Nyszdnc0Z2NNRG5iOTRoS3MvSjFJc3FBc1xyXG5NQ29HQ1NxR1NJYjNEUUVKRGpFZE1Cc3dHUVlEVlIwUkJCSXdFSUlPZDNkM0xtUnZkWGxwYmk1amIyMHdDZ1lJXHJcbktvWkl6ajBFQXdJRFJ3QXdSQUlnVmJkWTI0c0RYS0c0S2h3WlBmOHpxVDRBU0ROamNUb2FFRi9MQnd2QS8xSUNcclxuSURiVmZCUk1PQVB5cWJkcytld1QwSDZqdDg1czZZTVNVZEo5Z2dmOWlmeTBcclxuLS0tLS1FTkQgQ0VSVElGSUNBVEUgUkVRVUVTVC0tLS0tXHJcbiJ9";
            String s = httpGet(param, msToken, odin_tt, bd_ticket_guard_client_data);
            JSONObject jsonObject2 = JSONObject.parseObject(s);
            String awemeDetail = jsonObject2.getString("aweme_detail");
            ApiResult apiBean = new Gson().fromJson(awemeDetail, ApiResult.class);
            DouYinVo douYinVo = new DouYinVo();
            douYinVo.setDesc(apiBean.getDesc());
            douYinVo.setMusicUrl(apiBean.getMusic().getPlayUrl().getUri());
            douYinVo.setVideoUrl(apiBean.getVideo().getPlayAddr().getUrlList().get(0));
            return douYinVo;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    /**
     * 从路径中提取itemId
     *
     * @param url
     * @return
     */
    public static String parseItemIdFromUrl(String url) {
        // https://www.iesdouyin.com/share/video/6519691519585160455/?region=CN&mid=6519692104368098051&u_code=36fi3lehcdfb&titleType=title
        String ans = "";
        String[] strings = url.split("/");
        // after video.
        for (String string : strings) {
            if (StringUtils.isNumeric(string)) {
                return string;
            }
        }
        return ans;
    }

    /**
     * 短连接转换成长地址
     *
     * @param shortURL
     * @return
     * @throws IOException
     */
    public static String convertUrl(String shortURL) throws IOException {
        URL inputURL = new URL(shortURL);
        URLConnection urlConn = inputURL.openConnection();
        System.out.println("Short URL: " + shortURL);
        urlConn.getHeaderFields();
        String ans = urlConn.getURL().toString();
        System.out.println("Original URL: " + ans);
        return ans;
    }

    /**
     * 抽取URL
     *
     * @param rawInfo
     * @return
     */
    public static String extractUrl(String rawInfo) {
        if (StringUtils.isBlank(rawInfo)) {
            return StringUtils.EMPTY;
        }
        for (String string : rawInfo.split(" ")) {
            if (string.startsWith("http")) {
                return string;
            }
        }
        return StringUtils.EMPTY;
    }

    /**
     * 解析抖音API获取视频结果.
     *
     * @param itemId
     * @return
     * @throws Exception
     */
    public static ApiResult requestToAPI(String itemId) throws Exception {
        String url = API + itemId;
        HttpURLConnection httpClient =
                (HttpURLConnection) new URL(url).openConnection();
        // optional default is GET
        httpClient.setRequestMethod("GET");

        //add request header
        httpClient.setRequestProperty("User-Agent", UA);

        int responseCode = httpClient.getResponseCode();
        System.out.println("Response Code : " + responseCode);

        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(httpClient.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            //print result
            ApiResult apiBean = new Gson().fromJson(response.toString(), ApiResult.class);
            return apiBean;
        }
    }
    public static JSONObject SmsPost(String url, Map<String, Object> map) {
        try {
            //创建一个获取连接客户端的工具
            CloseableHttpClient httpClient = HttpClients.createDefault();
            //创建Post请求
            HttpPost httpPost = new HttpPost(url);
            //添加请求头
            httpPost.addHeader("Content-Type", "application/json;charset=utf-8");
            //封装请求参数，将map集合转换成json格式
            JSONObject jsonString = new JSONObject(map);
            //使用StringEntity转换成实体类型
            StringEntity entity = new StringEntity(jsonString.toString());
            System.out.println(jsonString.toJSONString());
//            entity.setContentEncoding("UTF-8");
//            entity.setContentType("application/json");//发送json数据需要设置contentType
            //将封装的参数添加到Post请求中
            httpPost.setEntity(entity);
            //执行请求
            CloseableHttpResponse response = httpClient.execute(httpPost);
            //获取响应的实体
            HttpEntity responseEntity = response.getEntity();
            //转化成字符串
            String entityString = EntityUtils.toString(responseEntity);
            //转换成JSON格式输出
            JSONObject result = JSONObject.parseObject(entityString);
            response.close();
            httpClient.close();
            return result;
        } catch (Exception e) {
            return null;
        }
    }
    //生成指定length的随机字符串（A-Z，a-z，0-9）
    public static String getRandomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {int number = random.nextInt(str.length());sb.append(str.charAt(number));}
        return sb.toString();
    }
    public static String httpGet(String url,String msToken,String ttwid,String bd_ticket_guard_client_data){
        // 获取连接客户端工具
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse httpResponse=null;
        String finalString = null;
        HttpGet httpGet = new HttpGet(url);
        /**公共参数添加至httpGet*/

        /**header中通用属性*/
        httpGet.setHeader("User-Agent", UA);
        httpGet.setHeader("Referer", "https://www.douyin.com/");
        httpGet.setHeader("Cookie", "msToken="+msToken+"; ttwid="+ttwid+"; bd_ticket_guard_client_data="+bd_ticket_guard_client_data);
        httpGet.setHeader("Accept", "*/*");
        httpGet.setHeader("Host", "www.douyin.com");
        httpGet.setHeader("Connection", "keep-alive");

        try {
            httpResponse = httpClient.execute(httpGet);
            HttpEntity entity = httpResponse.getEntity();
            finalString= EntityUtils.toString(entity, "UTF-8");
            try {
                httpResponse.close();
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return finalString;
    }
}
