package io.renren;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.unfbx.chatgpt.OpenAiClient;
import com.unfbx.chatgpt.OpenAiStreamClient;
import com.unfbx.chatgpt.entity.embeddings.EmbeddingResponse;
import com.unfbx.chatgpt.entity.embeddings.Item;
import com.unfbx.chatgpt.function.KeyRandomStrategy;
import com.unfbx.chatgpt.interceptor.OpenAILogger;
import io.renren.common.exception.RenException;
import io.renren.common.redis.RedisKeys;
import io.renren.common.redis.RedisService;
import io.renren.milvus.MilvusService;
import io.renren.service.SysParamsService;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ApiApplication.class)
public class TestPdf {
    private OpenAiStreamClient chatGPTStream;//流式的
    private OpenAiClient client;//普通的
    @Autowired
    private RedisService redisService;
    @Autowired
    private MilvusService milvusService;

    @Autowired
    private SysParamsService sysParamsService;
    public void init() {
        Object o = redisService.get(RedisKeys.GPT_KEY);
        if(o == null){
            throw new RenException("暂未配置apikey");
        }
        List<String> list = JSON.parseArray(o.toString(), String.class);
        String chatGptUrl = sysParamsService.getValue("CHAT_GPT_URL");
        if(StrUtil.isBlank(chatGptUrl)){
            chatGptUrl = "https://api.openai.com/";
        }
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new OpenAILogger());
        //!!!!!!测试或者发布到服务器千万不要配置Level == BODY!!!!
        //!!!!!!测试或者发布到服务器千万不要配置Level == BODY!!!!
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        OkHttpClient okHttpClient = new OkHttpClient
                .Builder()
//                .proxy(proxy)
                .addInterceptor(httpLoggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(600, TimeUnit.SECONDS)
                .build();
        chatGPTStream = OpenAiStreamClient
                .builder()
                .apiHost(chatGptUrl)
                .apiKey(list)
                //自定义key使用策略 默认随机策略
                .keyStrategy(new KeyRandomStrategy())
                .okHttpClient(okHttpClient)
                .build();
        client = OpenAiClient.builder()
                //支持多key传入，请求时候随机选择
                .apiKey(list)
                //自定义key的获取策略：默认KeyRandomStrategy
                .keyStrategy(new KeyRandomStrategy())
                .okHttpClient(okHttpClient)
                //自己做了代理就传代理地址，没有可不不传
                .apiHost(chatGptUrl)
                .build();
    }

    @Test
    public void testEmbed(){
        init();
        EmbeddingResponse embeddings = client.embeddings("这篇文章说了什么？");
        List<List<Float>> collect = new ArrayList<>();
        for (Item datum : embeddings.getData()) {
            List<BigDecimal> embedding = datum.getEmbedding();
            List<Float> collect1 = embedding.stream().map(bigDecimal -> bigDecimal.floatValue()).collect(Collectors.toList());
            collect.add(collect1);
        }
        System.out.println(collect);

    }
}
