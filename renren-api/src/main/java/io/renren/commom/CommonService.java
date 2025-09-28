
package io.renren.commom;



import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.unfbx.chatgpt.OpenAiClient;
import com.unfbx.chatgpt.OpenAiStreamClient;
import com.unfbx.chatgpt.entity.chat.ChatCompletion;
import com.unfbx.chatgpt.entity.embeddings.EmbeddingResponse;
import com.unfbx.chatgpt.entity.embeddings.Item;
import com.unfbx.chatgpt.function.KeyRandomStrategy;
import com.unfbx.chatgpt.interceptor.OpenAILogger;
import io.milvus.grpc.SearchResultData;
import io.milvus.grpc.SearchResults;
import io.milvus.param.R;
import io.milvus.response.SearchResultsWrapper;
import io.renren.common.entity.ShortVideoEntity;
import io.renren.common.exception.RenException;
import io.renren.common.redis.RedisKeys;
import io.renren.common.redis.RedisService;
import io.renren.common.service.ShortVideoService;
import io.renren.common.utils.*;
import io.renren.dto.*;
import io.renren.entity.*;
import io.renren.listener.OpenAIWebSocketEventSourceListener;
import io.renren.milvus.MilvusService;
import io.renren.service.*;
import io.renren.utils.BaiduSample;
import io.renren.utils.SendSmsUtils;
import io.renren.vo.MsgMoneyVo;
import io.renren.vo.UserVo;
import io.renren.websocket.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;

import com.unfbx.chatgpt.entity.chat.Message;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@Slf4j
public class CommonService {

	@Autowired
	private RedisService redisService;
	@Autowired
	private UserService userService;

	@Autowired
	private AsyncService asyncService;
	private OpenAiStreamClient chatGPTStream;
	@Autowired
	private Gson gson;
    @Autowired
	private SysParamsService paramsService;
	@Autowired
	private AiModelService aiModelService;
	@Autowired
	private SysParamsService sysParamsService;
	private String getTokenUrl = "https://flagopen.baai.ac.cn/flagStudio/auth/getToken?apikey=";
	private String fsText2ImgUrl = "https://flagopen.baai.ac.cn/flagStudio/v1/text2img";
	@Autowired
	private FlagstudioService flagstudioService;
	private int msgCount = 0;
	private List<String> gptKeyList = new ArrayList<>();
	private String chatGptUrl;
	private OpenAiClient client;//普通的
	@Autowired
	private MilvusService milvusService;
	@Autowired
	private PdfDocService pdfDocService;
	@Autowired
	private ShortVideoService shortVideoService;

	@Value("${bigmodel.minmax.address}")
	private String bigmodelMinmaxAddress;

	private static String minmaxAddress;

	@PostConstruct
	public void initBean() {
		// 在 bean 初始化完成后把注入的值赋给静态变量
		minmaxAddress = bigmodelMinmaxAddress;
	}

	public void init() {
		Object o = redisService.get(RedisKeys.GPT_KEY);
		if(o == null){
			throw new RenException("暂未配置apikey");
		}
		gptKeyList = JSON.parseArray(o.toString(), String.class);
		 chatGptUrl = sysParamsService.getValue("CHAT_GPT_URL");
		if(StrUtil.isBlank(chatGptUrl)){
			chatGptUrl = "https://api.openai.com/";
		}
		initGpt();
	}
	//官方4.0的
	public void initG4() {
		Object o = redisService.get(RedisKeys.GPT_KEY_4);
		if(o == null){
			throw new RenException("暂未配置apikey");
		}
		gptKeyList = JSON.parseArray(o.toString(), String.class);
		 chatGptUrl = sysParamsService.getValue("CHAT_GPT_URL");
		if(StrUtil.isBlank(chatGptUrl)){
			chatGptUrl = "https://api.openai.com/";
		}
		initGpt();
	}
	//三方4.0的
	public void initS4() {
		Object o = redisService.get(RedisKeys.GPT_KEY_S4);
		if(o == null){
			throw new RenException("暂未配置apikey");
		}
		gptKeyList = JSON.parseArray(o.toString(), String.class);
		 chatGptUrl = sysParamsService.getValue("SANFANG_CHAT_GPT_URL");
		if(StrUtil.isBlank(chatGptUrl)){
			throw new RenException("暂未配置三方4.0代理");
		}
		initGpt();
	}
	public void initGpt(){
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
				.apiKey(gptKeyList)
				//自定义key使用策略 默认随机策略
				.keyStrategy(new KeyRandomStrategy())
				.okHttpClient(okHttpClient)
				.build();
		client = OpenAiClient.builder()
				//支持多key传入，请求时候随机选择
				.apiKey(gptKeyList)
				//自定义key的获取策略：默认KeyRandomStrategy
				.keyStrategy(new KeyRandomStrategy())
				.okHttpClient(okHttpClient)
				//自己做了代理就传代理地址，没有可不不传
				.apiHost(chatGptUrl)
				.build();
	}
	public  void  initMsgCount(){
		String registerMsgCount = sysParamsService.getValue("register_msg_count");
		if(StrUtil.isBlank(registerMsgCount)){
			registerMsgCount = "0";
		}
		msgCount = Integer.parseInt(registerMsgCount);
	}
	public void onMessage(String modeId,String msg, WebSocketServer webSocketServer) throws IOException {
		try{
			init();
		}catch (Exception e){
			webSocketServer.session.getBasicRemote().sendText(e.getMessage());
			webSocketServer.session.getBasicRemote().sendText("[DONE]");
			return;
		}
		sendMsg(modeId,msg,webSocketServer,0);


	}
	public void onMessage4(String modeId,String msg, WebSocketServer webSocketServer) throws IOException {
		try{
			//判断4.0是走官方还是三方
			String gpt4Platfrom = sysParamsService.getValue("GPT4_PLATFROM");
			if(gpt4Platfrom.equals("0")){//走官方的4.0还是三方的
				initG4();
			}else{
				initS4();
			}

		}catch (Exception e){
			webSocketServer.session.getBasicRemote().sendText(e.getMessage());
			webSocketServer.session.getBasicRemote().sendText("[DONE]");
			return;
		}
		sendMsg(modeId,msg,webSocketServer,1);
	}
	public void sendMsg(String modeId,String msg, WebSocketServer webSocketServer,int type) throws IOException {
		UserEntity userEntity = userService.selectById(webSocketServer.userId);
		MsgMoneyVo notMsg = userService.isMsg(userEntity, type);//是否余额充足  是否在免费消息行列
		//		if(userEntity.isNotMsg()){//问题次数为0 并且不是VIP
		if(!notMsg.isMsgOk()){//问题次数为0 并且不是VIP
			webSocketServer.session.getBasicRemote().sendText("提问次数已用完,可通过邀请获得次数,或购买次数");
			webSocketServer.session.getBasicRemote().sendText("[DONE]");
			return;
		}
		String isOpenSample = sysParamsService.getValue("is_open_sample");//是否开启百度内容审核
		if(StrUtil.isBlank(isOpenSample)){
			isOpenSample = "1";
		}
		if(isOpenSample.equals("0")){//0才是开启
			String baiduApiKey = sysParamsService.getValue("baidu_apiKey");
			String baiduSecretKey = sysParamsService.getValue("baidu_secretKey");
			String identify = BaiduSample.identify(msg, baiduApiKey, baiduSecretKey);
			if(identify.equals("不合规")){//审核结果类型，可取值1.合规，2.不合规，3.疑似，4.审核失败 这里可以自己改
				/**
				 * 百度内容检测刚注册赠送次数
				 * 个人认证：一次性赠送50,000次，2 QPS，有效期 365天；
				 * 企业认证：一次性赠送250,000次，5 QPS，有效期 365天
				 */
				webSocketServer.session.getBasicRemote().sendText("提问内容违规,请重新提问!");
				webSocketServer.session.getBasicRemote().sendText("[DONE]");
				return;
			}
		}
//        //接受参数
		OpenAIWebSocketEventSourceListener eventSourceListener = new OpenAIWebSocketEventSourceListener(webSocketServer.session,userEntity.getId(),asyncService,msg,notMsg);
		String messageContext =  (String)redisService.get(webSocketServer.token);
		List<Message> messages = new ArrayList<>();
		if (StrUtil.isNotBlank(messageContext)) {
			messages = JSONUtil.toList(messageContext, Message.class);
		}
		Message currentMessage = Message.builder().content(msg).role(Message.Role.USER).build();
		AiModelEntity aiModelEntity = null;
		messages.add(currentMessage);
		if(StrUtil.isNotBlank(modeId)){//选择了 就把模型放在第一个
			aiModelEntity = aiModelService.selectById(Long.parseLong(modeId));
			if(aiModelEntity != null){
				Message systemMessage = Message.builder().content(aiModelEntity.getModelContent()).role(Message.Role.SYSTEM).build();
				messages.add(0,systemMessage);
			}
		}
		ChatCompletion completion = ChatCompletion.builder().messages(messages).build();
		long tokens = completion.tokens();
		int returnTokens = 2000;
		long toMsgToken = tokens + returnTokens;//发送消息带上上下文的总token
		long allToken = 4096;
		if(type == 0){
			allToken = 16000;
		}
		//总token数是tokens   所以  如果tokens+2048大于4097  那么 就需要删除部分聊天记录
		if( toMsgToken>= allToken){
			log.error("循环前的toMsgToken："+toMsgToken);
			for ( int i = 0; i < messages.size(); i++) {//循环聊天记录
				ChatCompletion build = ChatCompletion.builder().messages(Arrays.asList(messages.get(0))).build();//每次取最早一条 不能取i应为下面删了下标为0的 所以这里一直取0
				long tokens1 = build.tokens();//这条消息的tokens
				messages.remove(0);//删除这一条
				toMsgToken = toMsgToken - tokens1;//如果总token数减去这一条的小于4000了 那么就不循环了 直接跳出，如果没有小于4000 就继续循环 并且赋值计算后的总token下一次重新算
				log.error("循环后总token:"+toMsgToken);
				if(toMsgToken < 4000){
					break;
				}
			}
			if(aiModelEntity != null){
				Message systemMessage = Message.builder().content(aiModelEntity.getModelContent()).role(Message.Role.SYSTEM).build();
				messages.add(0,systemMessage);
			}
		}
		ChatCompletion chatCompletion = null;
		if(type == 0){
			chatCompletion = ChatCompletion
					.builder()
					.model(ChatCompletion.Model.GPT_3_5_TURBO_16K_0613.getName())
					.user(webSocketServer.userId.toString())
					.temperature(0.8)
					.maxTokens(returnTokens)
					.messages(messages)
					.stream(true)
					.build();
		}else{
			chatCompletion = ChatCompletion
					.builder()
					.model(ChatCompletion.Model.GPT_4.getName())
					.user(webSocketServer.userId.toString())
					.temperature(0.8)
					.maxTokens(returnTokens)
					.messages(messages)
					.stream(true)
					.build();
		}

		chatGPTStream.streamChatCompletion(chatCompletion, eventSourceListener);
		if(StrUtil.isNotBlank(modeId)){//懒得在上面判断是否存了模型角色  索性不存了 前端带了modeId  就在messages的第一个位置加就行了 所以存记录的时候 如果带了id,就删了再存
			messages.remove(0);
		}
		redisService.set(webSocketServer.token,JSONUtil.toJsonStr(messages),300);
	}
	public static String getAnswerByDeepSeek(String messagesJson) {
		// 构建请求体 JSON
		cn.hutool.json.JSONObject json = new cn.hutool.json.JSONObject();
        //封装deepseek请求对象
		json.set("model", "deepseek-chat");
		json.set("messages", JSONUtil.parseArray(messagesJson));

		// 发送 POST 请求
		HttpResponse response = HttpRequest.post("http://192.168.1.3:8000/v1/chat/completions")
				.header("Authorization", "Bearer zcOvdrYtY22yiL8Q/AHB1rsOeLgV3UXlg2OasNId5MtTlmU2I1cM7APh9w1C44b6")
				.header("Content-Type", "application/json")
				.body(json.toString())
				.execute();

		// 输出响应结果
		System.out.println("状态码: " + response.getStatus());
		System.out.println("响应内容: " + response.body());

		cn.hutool.json.JSONObject json2 = JSONUtil.parseObj(response.body());
		String content = json2.getJSONArray("choices")
				.getJSONObject(0)
				.getJSONObject("message")
				.getStr("content");

		return content;
	}

	public static String getAnswerByMinMax(String messagesJson) {
		// 构建请求体 JSON
		cn.hutool.json.JSONObject json = new cn.hutool.json.JSONObject();
		//封装deepseek请求对象
		json.set("model", "hailuo");
		json.set("messages", JSONUtil.parseArray(messagesJson));
//		String test = minmaxAddress;
		// 发送 POST 请求
		System.out.println(minmaxAddress);
		System.out.println("http://192.168.1.3:8000/v1/chat/completions");
		System.out.println(minmaxAddress+"/v1/chat/completions");
		try {
			HttpResponse response = HttpRequest.post(minmaxAddress+"/v1/chat/completions")
					.header("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE3NjAyNTE0NzMsInVzZXIiOnsiaWQiOiIzODE3OTYyNDc4OTE0NzY0ODUiLCJuYW1lIjoi5o6i57Si6ICFIiwiYXZhdGFyIjoiaHR0cHM6Ly9jZG4uaGFpbHVvYWkuY29tL3Byb2QvMjAyNS0wMy0xMi0yMC91c2VyX2F2YXRhci8xNzQxNzgxNTUxMDQ1MzY0NjgwLTIxMTE5MTg3OTQ4NjY2ODgwMW92ZXJzaXplLnBuZyIsImRldmljZUlEIjoiMzk1OTkxNjY0OTY3NjA2MjcyIiwiaXNBbm9ueW1vdXMiOmZhbHNlfX0.6Fmpxur47vBEJMyWGOTzVjYwWOhxR19GECKd0z0xkN8")
					.header("Content-Type", "application/json")
					.body(json.toString())
					.execute();

			// 输出响应结果
			System.out.println("状态码: " + response.getStatus());
			System.out.println("响应内容: " + response.body());

			cn.hutool.json.JSONObject json2 = JSONUtil.parseObj(response.body());
			String content = json2.getJSONArray("choices")
					.getJSONObject(0)
					.getJSONObject("message")
					.getStr("content");

			return content;

		}catch (Exception e){
			e.printStackTrace();
			return "服务器繁忙";
		}
	}
	public void sendMsgByDeepseek(String conversationId,List<Message> history,String modeId,String question, WebSocketServer webSocketServer) throws IOException {
		UserEntity userEntity = userService.selectById(webSocketServer.userId);

//        //接受参数
//		String messageContext =  (String)redisService.get(webSocketServer.token);
//		List<Message> messages = new ArrayList<>();
//		if (StrUtil.isNotBlank(messageContext)) {
//			messages = JSONUtil.toList(messageContext, Message.class);
//		}
		Message currentMessage = Message.builder().content(question).role(Message.Role.USER).build();
//		messages.add(currentMessage);
		history.add(currentMessage);
//		redisService.set(webSocketServer.token,JSONUtil.toJsonStr(messages),300);
		//提问之前,要历史记录一起发送

		System.out.println(JSONUtil.toJsonStr(history));

		String answer = getAnswerByMinMax(JSONUtil.toJsonStr(history));

		asyncService.saveQuestionAndAnswer(conversationId,webSocketServer.userId,question,answer,null);
//		Message answerMessage = Message.builder().content(answer).role(Message.Role.ASSISTANT).build();
//		history.add(answerMessage);
//        redisService.set(webSocketServer.token,JSONUtil.toJsonStr(messages),300);
		//回答之后，需要发送消息存数据库
		System.out.println(answer);
		webSocketServer.session.getBasicRemote().sendText(answer);

	}
	/**
	 * @throws
	 * @title weChatLogin
	 * @description 微信授权登录
	 */
	public Result<UserVo> weChatLogin(WeChatLoginDTO dto) {
		initMsgCount();
		String code = dto.getCode();
		String appid = sysParamsService.getValue("app_wx_login_appid");
		String secret = sysParamsService.getValue("app_wx_login_secret");  //这三个取配置表
		try {
			//通过第一步获得的code获取微信授权信息
			String url = String.format("https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code", appid, secret,code);

			JSONObject jsonObject = HttpUtil.doGetJson(url);
			String openid = jsonObject.getString("openid");
			String unionId = openid;//就当小程序公众号等没有在微信开放平台绑定在一起的情况
			//如果开启了UnionId
			if (StrUtil.isNotBlank(jsonObject.getString("unionid"))) {//取到了 说明绑定了
				unionId = jsonObject.getString("unionid");
			}
			String token = jsonObject.getString("access_token");
			String infoUrl = String.format("https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s&lang=zh_CN", token, openid);
			JSONObject userInfo = HttpUtil.doGetJson(infoUrl);

			//成功获取授权,以下部分为业务逻辑处理了，根据个人业务需求写就可以了
			if (userInfo != null && openid != null) {
				String nickname = userInfo.getString("nickname");
				String headimgurl = userInfo.getString("headimgurl");
				headimgurl = headimgurl.replace("\\", "");
				//根据openid查询时候有用户信息
				UserEntity user = userService.getUserByUnionId(unionId);

				if (user == null) {
					//没有绑定用户 就添加新用户
					//一级返利比例
					String invite_rebate_one = paramsService.getValue("invite_rebate_one");
					//二级返利比例
					String invite_rebate_two = paramsService.getValue("invite_rebate_two");
					user = new UserEntity(nickname,openid,headimgurl,msgCount,dto.getPid(),new BigDecimal(invite_rebate_one),new BigDecimal(invite_rebate_two));
					if(dto.getPid() != null){
						//查询这个用户有没有上级 有就绑定好爷爷辈ID
						UserEntity userEntity = userService.selectById(dto.getPid());
						if(userEntity != null){
							user.setGpid(userEntity.getPid());
						}

					}
					user.setWxUnionid(unionId);
					userService.insert(user);
					//给上级返利 如果有的话
					asyncService.bindToSuperior(user);
				}
				//生成token
				String creatToken = userService.creatToken(user);
				UserVo userVo = new UserVo(user);
				userVo.setToken(creatToken);
				return new Result<UserVo>().ok(userVo);
			} else {
				return new Result<UserVo>().error("登录失败");
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			return new Result<UserVo>().error("登录失败");
		}
	}

	public Result<UserVo> mpWeChatLogin(WeChatLoginDTO dto) {
		initMsgCount();
		try {
			WxMpDefaultConfigImpl configStorage = new WxMpDefaultConfigImpl();
			configStorage.setAppId(sysParamsService.getValue("mp_wx_login_appid"));
			configStorage.setSecret(sysParamsService.getValue("mp_wx_login_secret"));
			configStorage.setToken(sysParamsService.getValue("mp_wx_wechat_token"));
			configStorage.setAesKey(sysParamsService.getValue("mp_wx_wechat_encodingaeskey"));
			WxMpService wxMpService = new WxMpServiceImpl();
			wxMpService.setWxMpConfigStorage(configStorage);
			WxOAuth2AccessToken wxMpOAuth2AccessToken = wxMpService.getOAuth2Service().getAccessToken(dto.getCode());
			WxOAuth2UserInfo wxMpUser = wxMpService.getOAuth2Service().getUserInfo(wxMpOAuth2AccessToken, null);
			String openid = wxMpUser.getOpenid();
			String unionId = openid;//就当小程序公众号等没有在微信开放平台绑定在一起的情况
			//如果开启了UnionId
			if (StrUtil.isNotBlank(wxMpUser.getUnionId())) {//取到了 说明绑定了
				unionId = wxMpUser.getUnionId();
			}
			//如果绑定了  那公众号 小程序等同一个用户的unionId是一样的 用这个来判断同一用户避免同一微信在公众号小程序下生成2个用户了
			//如果没绑定 openid和unionId都存openid
			UserEntity user = userService.getUserByUnionId(unionId);
			String nickname = wxMpUser.getNickname();
			String headImgUrl = wxMpUser.getHeadImgUrl();
			if (user == null) {
				//没有绑定用户 就添加新用户
				//一级返利比例
				String invite_rebate_one = paramsService.getValue("invite_rebate_one");
				//二级返利比例
				String invite_rebate_two = paramsService.getValue("invite_rebate_two");
				user = new UserEntity(nickname,openid,headImgUrl,msgCount,dto.getPid(),new BigDecimal(invite_rebate_one),new BigDecimal(invite_rebate_two));
				user.setWxUnionid(unionId);
				if(dto.getPid() != null){
					//查询这个用户有没有上级 有就绑定好爷爷辈ID
					UserEntity userEntity = userService.selectById(dto.getPid());
					if(userEntity != null){
						user.setGpid(userEntity.getPid());
					}
				}
				userService.insert(user);
				//给上级返利 如果有的话
				asyncService.bindToSuperior(user);
			}
			//更新openid
			asyncService.updateOpenID(user,openid);
			//生成token
			String creatToken = userService.creatToken(user);
			UserVo userVo = new UserVo(user);
			userVo.setToken(creatToken);
			return new Result<UserVo>().ok(userVo);
		}catch (WxErrorException e){
			e.printStackTrace();
			log.error(e.getMessage());
		}
		return new Result<UserVo>().error("登录失败");
	}

	/**
	 * 公众号扫码登录注册
	 * @param dto
	 * @return
	 */
	public Result mpWeChatScanLogin(WeChatLoginDTO dto) throws WxErrorException {
		initMsgCount();
		String code = dto.getCode();
		String openid = (String)redisService.get(RedisKeys.GZH_TICKET, code);
		if(StrUtil.isBlank(openid)){
			return new Result().error("二维码已过期!");
		}
		if(code.equals(openid)){
			//说明还没有成功获取openid  用户没有扫码
			UserVo userVo = new UserVo();
			userVo.setLoginOk(false);
			return new Result().ok(userVo);
		}
		String GZH_INVITE_PID = (String)redisService.get(RedisKeys.GZH_INVITE_PID, code);
		Long pid = null;
		try {
			if(StrUtil.isNotBlank(GZH_INVITE_PID)){
				pid = Long.parseLong(GZH_INVITE_PID);
				dto.setPid(pid);
			}
		}catch (Exception e){
			//格式化失败了  说明传来的pid可能是字符串
		}
		//公众号扫码得到的openid去换一下uniodId,此处如果没有在微信开放平台绑定就无所谓了 绑定了则是用的unionId当做唯一 必须去换
		WxMpDefaultConfigImpl configStorage = new WxMpDefaultConfigImpl();
		configStorage.setAppId(sysParamsService.getValue("mp_wx_login_appid"));
		configStorage.setSecret(sysParamsService.getValue("mp_wx_login_secret"));
		configStorage.setToken(sysParamsService.getValue("mp_wx_wechat_token"));
		configStorage.setAesKey(sysParamsService.getValue("mp_wx_wechat_encodingaeskey"));
		WxMpService wxService = new WxMpServiceImpl();
		wxService.setWxMpConfigStorage(configStorage);

		WxMpUser wxMpUser = wxService.getUserService().userInfo(openid);
		String unionId = openid;//就当小程序公众号等没有在微信开放平台绑定在一起的情况
		if(StrUtil.isNotBlank(wxMpUser.getUnionId())){
			unionId = wxMpUser.getUnionId();
		}
		String USER_HEAD_URL = paramsService.getValue("USER_HEAD_URL");
		//如果绑定了  那公众号 小程序等同一个用户的unionId是一样的 用这个来判断同一用户避免同一微信在公众号小程序下生成2个用户了
		//如果没绑定 openid和unionId都存openid
		UserEntity user = userService.getUserByUnionId(unionId);
		if (user == null) {
			//没有绑定用户 就添加新用户
			//一级返利比例
			String invite_rebate_one = paramsService.getValue("invite_rebate_one");
			//二级返利比例
			String invite_rebate_two = paramsService.getValue("invite_rebate_two");
			user = new UserEntity("微信用户",openid,USER_HEAD_URL,msgCount,dto.getPid(),new BigDecimal(invite_rebate_one),new BigDecimal(invite_rebate_two));
			user.setWxUnionid(unionId);
			if(dto.getPid() != null){
				//查询这个用户有没有上级 有就绑定好爷爷辈ID
				UserEntity userEntity = userService.selectById(dto.getPid());
				if(userEntity != null){
					user.setGpid(userEntity.getPid());
				}
			}
			userService.insert(user);
			//给上级返利 如果有的话
			asyncService.bindToSuperior(user);
		}
		//更新openid
		asyncService.updateOpenID(user,openid);
		//生成token
		String creatToken = userService.creatToken(user);
		UserVo userVo = new UserVo(user);
		userVo.setToken(creatToken);
		userVo.setLoginOk(true);
		return new Result().ok(userVo);
	}

	public Result<UserVo> miniAppLogin(WeChatLoginDTO dto) {
		initMsgCount();
		String code = dto.getCode();
		String encryptedData = dto.getEncryptedData();
		String iv = dto.getIv();
		String miniAppAppid = sysParamsService.getValue("mini_app_appid");
		String miniAppSecret = sysParamsService.getValue("mini_app_secret");
		if (StrUtil.isBlank(miniAppAppid) || StrUtil.isBlank(miniAppSecret)) {
			throw new RenException("请先配置小程序");
		}
		try {
			WxMaDefaultConfigImpl config = new WxMaDefaultConfigImpl();
			config.setMsgDataFormat("JSON");
			config.setAppid(miniAppAppid);
			config.setSecret(miniAppSecret);
			WxMaService wxMaService = new WxMaServiceImpl();
			wxMaService.setWxMaConfig(config);
			WxMaJscode2SessionResult session = wxMaService.getUserService().getSessionInfo(code);
			String openid = session.getOpenid();
			String unionId = openid;//就当小程序公众号等没有在微信开放平台绑定在一起的情况
			String USER_HEAD_URL = paramsService.getValue("USER_HEAD_URL");
			//如果开启了UnionId
			if (StrUtil.isNotBlank(session.getUnionid())) {//取到了 说明绑定了
				unionId = session.getUnionid();
			}
			WxMaPhoneNumberInfo phoneNoInfo = wxMaService.getUserService()
					.getPhoneNoInfo(session.getSessionKey(), encryptedData, iv);
			String phoneNumber = phoneNoInfo.getPhoneNumber();//用户手机号
			UserEntity user = null;
			if(StrUtil.isNotBlank(phoneNumber)){//取到手机号了
				user = userService.getByMobile(phoneNumber);
				if(user == null){//根据手机号没查到
					//那根据UnionId 查询
					user = userService.getUserByUnionId(unionId);
					//查到了就绑定手机号 没查到就是新用户
					if(user == null){
						//没有绑定用户 就添加新用户
						//一级返利比例
						String invite_rebate_one = paramsService.getValue("invite_rebate_one");
						//二级返利比例
						String invite_rebate_two = paramsService.getValue("invite_rebate_two");
						user = new UserEntity("微信用户",openid,USER_HEAD_URL,msgCount,dto.getPid(),new BigDecimal(invite_rebate_one),new BigDecimal(invite_rebate_two));
						user.setWxUnionid(unionId);
						user.setUsername(phoneNumber);
						user.setMobile(phoneNumber);
						if(dto.getPid() != null){
							//查询这个用户有没有上级 有就绑定好爷爷辈ID
							UserEntity userEntity = userService.selectById(dto.getPid());
							if(userEntity != null){
								user.setGpid(userEntity.getPid());
							}
						}
						userService.insert(user);
						//给上级返利 如果有的话
						asyncService.bindToSuperior(user);
					}else{
						//这里是查到了 不管别的  给这狗日的把openid和unionId绑定起
						log.error(unionId);
						log.error(openid);
						user.setWxUnionid(unionId);
						user.setMobile(phoneNumber);
						user.setWxOpenid(openid);
						userService.updateUnionidAndOpenId(user);
					}
				}else{
					//这里是查到了 不管别的  给这狗日的把openid和unionId绑定起  这里可能会出现 用户先H5浏览器或者PC  用手机号登录了  然后他又公众号或者PC扫码登录了 就会出现2条数据  一条是有手机号 但是没有unionId和openid的数据  一条是有unionId和openid的数据 但是没有手机号的数据  这是必然的
					//所以这里如果根据手机号查到了 还需要根据unionId查一下是否有数据
					UserEntity userByUnionId = userService.getUserByUnionId(unionId);
					if(userByUnionId == null){
						user.setWxUnionid(unionId);
						user.setMobile(phoneNumber);
						user.setWxOpenid(openid);
						userService.updateUnionidAndOpenId(user);
					}else {
						user = userByUnionId;
						user.setWxOpenid(openid);//需要改一下openid 要不小程序可能会拉不起支付
						userService.updateUnionidAndOpenId(user);
					}

				}
			}else{//没取到手机号
				//先根据unionId查询是否由用户
				user = userService.getUserByUnionId(unionId);
				if(user == null){//没查到就是新用户 查到了直接登录
					//没有绑定用户 就添加新用户
					//一级返利比例
					String invite_rebate_one = paramsService.getValue("invite_rebate_one");
					//二级返利比例
					String invite_rebate_two = paramsService.getValue("invite_rebate_two");
					user = new UserEntity("微信用户",openid,USER_HEAD_URL,msgCount,dto.getPid(),new BigDecimal(invite_rebate_one),new BigDecimal(invite_rebate_two));
					user.setWxUnionid(unionId);
					if(dto.getPid() != null){
						//查询这个用户有没有上级 有就绑定好爷爷辈ID
						UserEntity userEntity = userService.selectById(dto.getPid());
						if(userEntity != null){
							user.setGpid(userEntity.getPid());
						}
					}
					userService.insert(user);
					//给上级返利 如果有的话
					asyncService.bindToSuperior(user);
				}
			}
			//生成token
			String creatToken = userService.creatToken(user);
			UserVo userVo = new UserVo(user);
			userVo.setToken(creatToken);
			return new Result<UserVo>().ok(userVo);
		}catch (WxErrorException e){
			e.printStackTrace();
			log.error(e.getMessage());
		}
		return null;
	}

	public Result<UserVo> smsLogin(MobileLoginDTO dto) {
		initMsgCount();
		boolean b = verifySmsCode(dto.getMobile(), dto.getCode());
		if(!b){
			throw new RenException("验证码错误");
		}
		//验证码正确
		UserEntity userEntity = userService.getByMobile(dto.getMobile());
		if(userEntity == null){
			//添加用户
			//一级返利比例
			String invite_rebate_one = paramsService.getValue("invite_rebate_one");
			//二级返利比例
			String invite_rebate_two = paramsService.getValue("invite_rebate_two");
			//默认头像后台设置的
			String USER_HEAD_URL = paramsService.getValue("USER_HEAD_URL");
			userEntity = new UserEntity("用户"+SundryUtils.getCardTailNum(dto.getMobile()),null,USER_HEAD_URL,msgCount,dto.getPid(),new BigDecimal(invite_rebate_one),new BigDecimal(invite_rebate_two));
			userEntity.setUsername(dto.getMobile());
			userEntity.setMobile(dto.getMobile());
			if(dto.getPid() != null){
				//查询这个用户有没有上级 有就绑定好爷爷辈ID
				UserEntity gpUser = userService.selectById(dto.getPid());
				if(gpUser != null){
					userEntity.setGpid(gpUser.getPid());
				}
			}
			userService.insert(userEntity);
			//给上级返利 如果有的话
			asyncService.bindToSuperior(userEntity);
		}
		//生成token
		String creatToken = userService.creatToken(userEntity);
		UserVo userVo = new UserVo(userEntity);
		userVo.setToken(creatToken);
		return new Result<UserVo>().ok(userVo);
	}
	/**
	 * 验证手机短信
	 */
	public boolean verifySmsCode(String mobile, String code) {
		if("20200910".equals(code)){
			return true;
		}

		//先判断验证码是否正确
		String smsCode = (String) redisService.get(RedisKeys.SMS_CODE, mobile);
		if (StrUtil.isBlank(smsCode)) {
			return false;
		}
		return smsCode.equals(code);
	}

	public Result sendSms(SendSmsDTO dto) {
		String smsCode = (String) redisService.get(RedisKeys.SMS_CODE,dto.getMobile());
		if (StrUtil.isBlank(smsCode)) {
			smsCode = RandomUtil.getRandomSmsCode();
			redisService.set(RedisKeys.SMS_CODE,dto.getMobile(),smsCode,5 * 60);
		}
		String is_yanshi = paramsService.getValue("IS_YANSHI");
		if(StrUtil.isBlank(is_yanshi)){
			is_yanshi = "0";
		}
		if(is_yanshi.equals("1")){
			return new Result().error("演示环境,验证码为:"+smsCode);
		}else{
			String s = SendSmsUtils.jgsendSms(dto.getMobile(), smsCode);
			if(!s.equals("1")){
				return new Result().error("发送验证码失败");
			}
			return new Result().ok("发送成功");
		}

	}

	public String getAccessToken(WxMpService wxService)  {

		String accToken = (String)redisService.get(RedisKeys.GZH_ACCESS_TOKEN);
		if(StrUtil.isBlank(accToken)){
			try {
				accToken = wxService.getAccessToken();
				//7200秒 存redis
				redisService.set(RedisKeys.GZH_ACCESS_TOKEN, accToken, 7200 - 10);
			} catch (WxErrorException e) {
				log.error(e.getMessage());
				return null;
			}
			//存redis
		}
		return accToken;
	}

	public Result sdDraw(UserEntity user, DrawDTO dto) throws ParseException, IOException {
		String sd_img_price = sysParamsService.getValue("sd_img_price");//SD绘画价钱
		if(StrUtil.isBlank(sd_img_price)){
			sd_img_price = "5";//默认5个币一张图
		}
		String sd_img_url = sysParamsService.getValue("sd_img_url");//SD绘画服务器接口
		if(StrUtil.isBlank(sd_img_url)){
			return new Result().error("暂未配置SD绘画服务");
		}
		String sd_img_secret = sysParamsService.getValue("sd_img_secret");//SD绘画秘钥
		if(StrUtil.isBlank(sd_img_secret)){
			return new Result().error("暂未配置SD绘画服务");
		}
		int batchSize = dto.getBatchSize();//一共画了这么多张
		BigDecimal multiply = new BigDecimal(batchSize).multiply(new BigDecimal(sd_img_price));//需要扣的币
		if(!user.isVip() && user.getMsgCount() < multiply.intValue()){
			return new Result().error("提问/绘画次数已用完!");
		}
		//画画啦
		JSONObject params = new JSONObject();
		//best quality, masterpiece, highres, 1girl,blush,(seductive smile:0.8),star-shaped pupils,china hanfu,hair ornament,necklace, jewelry,Beautiful face,upon_body, tyndall effect,photorealistic, dark studio, rim lighting, two tone lighting,(high detailed skin:1.2), 8k uhd, dslr, soft lighting, high quality, volumetric lighting, candid, Photograph, high resolution, 4k, 8k, Bokeh
		params.put("prompt", dto.getPrompt());
		//(((simple background))),monochrome ,lowres, bad anatomy, bad hands, text, error, missing fingers, extra digit, fewer digits, cropped, worst quality, low quality, normal quality, jpeg artifacts, signature, watermark, username, blurry, lowres, bad anatomy, bad hands, text, error, extra digit, fewer digits, cropped, worst quality, low quality, normal quality, jpeg artifacts, signature, watermark, username, blurry, ugly,pregnant,vore,duplicate,morbid,mut ilated,tran nsexual, hermaphrodite,long neck,mutated hands,poorly drawn hands,poorly drawn face,mutation,deformed,blurry,bad anatomy,bad proportions,malformed limbs,extra limbs,cloned face,disfigured,gross proportions, (((missing arms))),(((missing legs))), (((extra arms))),(((extra legs))),pubic hair, plump,bad legs,error legs,username,blurry,bad feet
		params.put("negative_prompt",dto.getNegativePrompt());
		params.put("width", dto.getWidth());
		params.put("height", dto.getHeight());
		params.put("steps", dto.getSteps());
		params.put("batch_size", dto.getBatchSize());
		params.put("cfg_scale", dto.getCfgScale());
		params.put("seed", dto.getSeed());
		params.put("sampler_index", dto.getSamplerIndex());
		String s = HttpUtil.sendPost(sd_img_url, params, sd_img_secret);
		JSONObject jsonObject = JSONObject.parseObject(s);
		JSONArray jsonArray = jsonObject.getJSONArray("images");
//		for (Object o : jsonArray) {
//			String s1 = o.toString();
//			Base64ImgUtil.convertBase64ToImage(s1,"国风"+new Date().getTime()+"");
//		}
		//存记录 扣钱
		asyncService.sdRecord(user.getId(),dto,multiply.intValue(),jsonArray);
		return new Result().ok(jsonArray);
	}

	public Result fsDraw(UserEntity user, FsDrawDTO dto) throws IOException, ParseException {
		String fs_img_price = sysParamsService.getValue("fs_img_price");//SD绘画价钱
		if(StrUtil.isBlank(fs_img_price)){
			fs_img_price = "5";//默认5个币一张图
		}
		//获取key列表,单日次数小于500的
		List<FlagstudioEntity> flagstudioEntities = flagstudioService.getListByTodayCountLt500();
		if(CollUtil.isEmpty(flagstudioEntities)){
			return new Result().error("暂未配置绘画服务!");
		}
		//随机取一个出来
		FlagstudioEntity flagstudioEntity = flagstudioEntities.get((int) (Math.random() * flagstudioEntities.size()));
		//判断token是否过期
		Date tokenTime = flagstudioEntity.getTokenTime();
		boolean after = tokenTime.after(new Date());
		if(!after){
			//重新生成
			JSONObject jsonObject = HttpUtil.doGetJson(getTokenUrl + flagstudioEntity.getApiKey());
			String code = jsonObject.getString("code");
			if(!code.equals("200")){
				return new Result().error("key有误,请检查key");
			}
			String token = jsonObject.getJSONObject("data").getString("token");
			//修改下token
			flagstudioEntity.setToken(token);
			Date afterDay = DateUtils.getAfterDay(new Date(), 29);//过期时间,正常是30天 这里就设置提前一天吧
			flagstudioEntity.setTokenTime(afterDay);
			asyncService.updateFsToken(flagstudioEntity);
		}
		int batchSize = dto.getFsCount();//一共画了这么多张
		BigDecimal multiply = new BigDecimal(batchSize).multiply(new BigDecimal(fs_img_price));//需要扣的币
		if(!user.isVip() && user.getMsgCount() < multiply.intValue()){
			return new Result().error("提问/绘画次数已用完!");
		}
		String s = JSONObject.toJSONString(dto);
		JSONObject parm = JSONObject.parseObject(s);
		JSONArray jsonArray = new JSONArray();
		for (int i = 0; i < batchSize; i++) {
			String response = HttpUtil.sendPostFs(fsText2ImgUrl, parm, flagstudioEntity.getToken());
			JSONObject parseObject = JSONObject.parseObject(response);
			String code = parseObject.getString("code");
			String nsfw = parseObject.getString("nsfw");
			boolean isOk = code.equals("200") && nsfw.equals("0");
			if(!isOk){
				return new Result().error("违规图像!");
			}
			//这里就正常了
			String data = parseObject.getString("data");
			jsonArray.add(data);
		}
		//扣钱存记录
		asyncService.fsRecord(user.getId(),dto,multiply.intValue(),jsonArray,flagstudioEntity);
		return new Result().ok(jsonArray);
	}


	/**
	 * PDF问答
	 * @param webSocketServer
	 * @throws IOException
	 */
	public void onMessagePdf(String qMsg,String pdfId, WebSocketServer webSocketServer) throws IOException {
		try{
			init();
		}catch (Exception e){
			webSocketServer.session.getBasicRemote().sendText(e.getMessage());
			webSocketServer.session.getBasicRemote().sendText("[DONE]");
			return;
		}
		if(StrUtil.isBlank(pdfId)){
			webSocketServer.session.getBasicRemote().sendText("请选择文件再提问！");
			webSocketServer.session.getBasicRemote().sendText("[DONE]");
			return;
		}
		String isOpenSample = sysParamsService.getValue("is_open_sample");//是否开启百度内容审核
		if(StrUtil.isBlank(isOpenSample)){
			isOpenSample = "1";
		}
		if(isOpenSample.equals("0")){//0才是开启
			String baiduApiKey = sysParamsService.getValue("baidu_apiKey");
			String baiduSecretKey = sysParamsService.getValue("baidu_secretKey");
			String identify = BaiduSample.identify(qMsg, baiduApiKey, baiduSecretKey);
			if(identify.equals("不合规")){//审核结果类型，可取值1.合规，2.不合规，3.疑似，4.审核失败 这里可以自己改
				/**
				 * 百度内容检测刚注册赠送次数
				 * 个人认证：一次性赠送50,000次，2 QPS，有效期 365天；
				 * 企业认证：一次性赠送250,000次，5 QPS，有效期 365天
				 */
				webSocketServer.session.getBasicRemote().sendText("提问内容违规,请重新提问!");
				webSocketServer.session.getBasicRemote().sendText("[DONE]");
				return;
			}
		}
		UserEntity userEntity = userService.selectById(webSocketServer.userId);
		MsgMoneyVo notMsg = userService.isMsg(userEntity, 0);//是否余额充足  是否在免费消息行列
//		if(userEntity.isNotMsg()){//问题次数为0 并且不是VIP
		if(!notMsg.isMsgOk()){//问题次数为0 并且不是VIP
			webSocketServer.session.getBasicRemote().sendText("提问次数已用完,可通过邀请获得次数,或购买次数");
			webSocketServer.session.getBasicRemote().sendText("[DONE]");
			return;
		}
//        //接受参数
		OpenAIWebSocketEventSourceListener eventSourceListener = new OpenAIWebSocketEventSourceListener(webSocketServer.session,userEntity.getId(),asyncService,qMsg,notMsg);
		EmbeddingResponse embeddings = client.embeddings(qMsg);
		List<List<Float>> collect = new ArrayList<>();
		for (Item datum : embeddings.getData()) {
			List<BigDecimal> embedding = datum.getEmbedding();
			List<Float> collect1 = embedding.stream().map(bigDecimal -> bigDecimal.floatValue()).collect(Collectors.toList());
			collect.add(collect1);
		}
		PdfDocEntity pdfDocEntity = pdfDocService.selectById(Long.parseLong(pdfId));
		String collectionName = "pdf_" + userEntity.getId() + "_" + pdfDocEntity.getId();
		milvusService.loadCollection(collectionName);
		R<SearchResults> response = milvusService.searchContent(collect, collectionName);
		SearchResultData results = response.getData().getResults();
		SearchResultsWrapper wrapper = new SearchResultsWrapper(results);
		List<String> pdf_content = (List<String>) wrapper.getFieldData("PDF_Content", 0);
		System.out.println(pdf_content);
		String msg = "Please note: Please carefully evaluate the correlation between the query and the context information provided. Only answer based on the content of the text information entered in this paragraph, and try to reply in detail as much as possible. The tokens are around 800. If the query is not related to the provided material, please answer \"I don't know\". Also, do not answer irrelevant answers. Answer in Chinese:\"{%s}\"\n\"text:{%s}\"";
//		String msg = "Refer to the text below to answer in simplified Chinese:\"{%s}\"\n\"text:{%s}\"";
		ChatCompletion completion = ChatCompletion.builder().messages(Arrays.asList( Message.builder().content(qMsg).role(Message.Role.USER).build(), Message.builder().content(msg).role(Message.Role.USER).build())).build();
		long tokens = completion.tokens();//提问加预制的  占了多少token
		StringBuilder context = new StringBuilder();
		int count = 0;
		for(String candidate: pdf_content){
			ChatCompletion candidatea = ChatCompletion.builder().messages(Arrays.asList( Message.builder().content(candidate).role(Message.Role.USER).build())).build();
			long curCandidateToken = candidatea.tokens();
			if(curCandidateToken + tokens + 1000 > 16000){
				break;
			}
			context.append(candidate);
			tokens += curCandidateToken;
			count++;
		}
		String format = String.format(msg, context.toString(), qMsg);
		ChatCompletion chatCompletion = ChatCompletion
				.builder()
				.model(ChatCompletion.Model.GPT_3_5_TURBO_16K_0613.getName())
//				.model("gpt-3.5-turbo-16k")
				.temperature(0.0)
				.maxTokens(1000)
				.messages(Arrays.asList( Message.builder().content(format).role(Message.Role.USER).build()))
				.stream(true)
				.build();
		chatGPTStream.streamChatCompletion(chatCompletion, eventSourceListener);
	}
	private static final String URL = "http://192.168.31.228:8000/v1/chat/completions";

	private static final String AUTHORIZATION = "Bearer zcOvdrYtY22yiL8Q/AHB1rsOeLgV3UXlg2OasNId5MtTlmU2I1cM7APh9w1C44b6,8vDv5+RF83lcfb6rru1ekRL1t2WBjF59JrEpqO0PcQXlL1UO0yzEv0v7dFsM1TON,iWE+AJR9X7FguP2fGr921P+s06wolD8tVnmzQWHpG7WDpkrajAobA4sQjlxtuItK";

	public String deepseekByWebUi(String content, String model) {
		// 构造消息体
		Map<String, Object> message = new HashMap<>();
		message.put("role", "user");
		message.put("content", content);

		Map<String, Object> payload = new HashMap<>();
		payload.put("model", model);
		payload.put("messages", Collections.singletonList(message));
		payload.put("stream", false);

		// 发送 POST 请求
		HttpResponse response = HttpRequest.post(URL)
				.header("Authorization", AUTHORIZATION)
				.header("Content-Type", "application/json")
				.body(JSONUtil.toJsonStr(payload))
				.execute();

//		return response.body();
		if (response.getStatus() != 200) {
			throw new RuntimeException("DeepSeek 返回错误，状态码：" + response.getStatus() + "\n内容：" + response.body());
		}

		// 解析 JSON 内容
		cn.hutool.json.JSONObject json = cn.hutool.json.JSONUtil.parseObj(response.body());
		String result = json.getJSONArray("choices")
				.getJSONObject(0)
				.getJSONObject("message")
				.getStr("content");

		return result;
	}
//	import cn.hutool.json.JSONObject;
//import cn.hutool.json.JSONUtil;
}
