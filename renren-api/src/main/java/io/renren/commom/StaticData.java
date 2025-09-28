package io.renren.commom;

import com.google.gson.Gson;
import io.renren.common.redis.RedisKeys;
import io.renren.common.redis.RedisService;
import io.renren.service.GptKeyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;

@SuppressWarnings("ALL")
@Slf4j
@Component
public class StaticData {

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private RedisService redisService;

    @Autowired
    private GptKeyService gptKeyService;
    @Autowired
    private Gson gson;

    @PostConstruct
    public void init() {
        //加载昵称字符库
        loadMySqlGptKeyToRedis();


    }


    /**
     * 每次重启都把gptkey有用的加载进redis
     */
    private void loadMySqlGptKeyToRedis(){
        //先删除redis里面的GPTKEY
        redisService.deleteKey(RedisKeys.GPT_KEY);
        redisService.deleteKey(RedisKeys.GPT_KEY_4);
        redisService.deleteKey(RedisKeys.GPT_KEY_S4);
        //查询所有开启的 分类查询
        List<String> g35 = gptKeyService.getAllOpenKeyByType(0);
        List<String> g40 = gptKeyService.getAllOpenKeyByType(1);
        List<String> s40 = gptKeyService.getAllOpenKeyByType(2);
        redisService.set(RedisKeys.GPT_KEY,gson.toJson(g35));
        redisService.set(RedisKeys.GPT_KEY_4,gson.toJson(g40));
        redisService.set(RedisKeys.GPT_KEY_S4,gson.toJson(s40));
    }

}
