
package io.renren.commom;


import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.unfbx.chatgpt.OpenAiClient;
import com.unfbx.chatgpt.OpenAiStreamClient;
import com.unfbx.chatgpt.entity.chat.ChatCompletion;
import com.unfbx.chatgpt.entity.chat.Message;
import com.unfbx.chatgpt.entity.embeddings.EmbeddingResponse;
import com.unfbx.chatgpt.entity.embeddings.Item;
import com.unfbx.chatgpt.entity.images.Image;
import com.unfbx.chatgpt.entity.images.ImageResponse;
import com.unfbx.chatgpt.entity.images.ResponseFormat;
import com.unfbx.chatgpt.entity.whisper.Transcriptions;
import com.unfbx.chatgpt.entity.whisper.Whisper;
import com.unfbx.chatgpt.entity.whisper.WhisperResponse;
import com.unfbx.chatgpt.function.KeyRandomStrategy;
import com.unfbx.chatgpt.interceptor.OpenAILogger;
import io.milvus.grpc.SearchResultData;
import io.milvus.grpc.SearchResults;
import io.milvus.param.R;
import io.milvus.response.SearchResultsWrapper;
import io.renren.common.exception.RenException;
import io.renren.common.redis.RedisKeys;
import io.renren.common.redis.RedisService;
import io.renren.common.utils.Result;
import io.renren.dto.ChatMsgDTO;
import io.renren.entity.PdfDocEntity;
import io.renren.entity.UserEntity;
import io.renren.listener.OpenAIWebSocketEventSourceListener;
import io.renren.milvus.MilvusService;
import io.renren.oss.cloud.OSSFactory;
import io.renren.service.PdfDocService;
import io.renren.service.SysParamsService;
import io.renren.service.UserService;
import io.renren.utils.BASE64DecodedMultipartFile;
import io.renren.utils.Douyin;
import io.renren.utils.HttpDownloadUtil;
import io.renren.utils.PdfUtils;
import io.renren.vo.DouYinVo;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ChatService {

	@Autowired
	private RedisService redisService;
	@Autowired
	private UserService userService;
	@Autowired
	private AsyncService asyncService;
	private OpenAiStreamClient chatGPTStream;//流式的
	private OpenAiClient client;//普通的
	@Autowired
	private MilvusService milvusService;

	@Autowired
	private SysParamsService sysParamsService;
	@Autowired
	private PdfDocService pdfDocService;

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

	/**
	 * 根据描述是生成图片
	 * @param dto
	 * @return
	 */
	public Result chatDraw(UserEntity user,ChatMsgDTO dto) {
		init();
		String msg_img_price = sysParamsService.getValue("msg_img_price");//图片扣除的次数
		if(StrUtil.isBlank(msg_img_price)){
			msg_img_price = "3";
		}
		if(!user.isVip() && user.getMsgCount() < Integer.parseInt(msg_img_price)){
			return new Result().error("提问次数已用完!");
		}
		try {
//			ImageResponse imageResponse = client.genImages(dto.getMsg());
			Image image = Image.builder().prompt(dto.getMsg()).responseFormat(ResponseFormat.B64_JSON.getName()).build();
			ImageResponse imageResponse = client.genImages(image);
			String b64Json = imageResponse.getData().get(0).getB64Json();//base64图片 存阿里云去
			String osSurl = getOSSurl(b64Json);
			imageResponse.getData().get(0).setUrl(osSurl);
			//扣次数 加流水
			asyncService.updateUserMsgCountAndLog(user.getId(),dto.getMsg(),-Integer.parseInt(msg_img_price),1);
			asyncService.saveDrawRecord(user.getId(),dto.getMsg(),osSurl,null,0);
			return new Result().ok(imageResponse.getData().get(0));
		}catch (Exception e){
			Map<String,String> map = new HashMap<>();
			map.put("errorMsg","存在不合适关键词,AI拒绝绘画");
			return new Result().ok(map);
		}

	}

	public String getOSSurl(String b64Json) throws IOException {
		//上传到阿里云OSS
		MultipartFile file = BASE64DecodedMultipartFile.base64ToMultipartFile("data:image/png,"+b64Json);
		//上传文件
		String extension = FilenameUtils.getExtension(file.getOriginalFilename());
		//拿到文件存储地址
		String url = OSSFactory.build().uploadSuffix(file.getBytes(), extension);
		return url;
	}

	public Result gptEmbeddings(MultipartFile file,UserEntity user) throws IOException {
		init();
		PdfDocEntity pdfDocEntity = new PdfDocEntity(user.getId(),file.getOriginalFilename(),0);
		pdfDocService.insert(pdfDocEntity);
		List<String> strings = PdfUtils.parsePdf(file.getInputStream());
		EmbeddingResponse embeddings = client.embeddings(strings);

		List<List<Float>> collect = new ArrayList<>();
		for (Item datum : embeddings.getData()) {
			List<BigDecimal> embedding = datum.getEmbedding();
			List<Float> collect1 = embedding.stream().map(bigDecimal -> bigDecimal.floatValue()).collect(Collectors.toList());
			collect.add(collect1);
		}

		String collectionName = "pdf_" + user.getId() + "_" + pdfDocEntity.getId();
		//建集合
		boolean b = milvusService.creatCollection(collectionName);
		//加数据
		boolean insert = milvusService.insert(collectionName, strings, collect);
		//创建索引
		milvusService.createIndex(collectionName);
		return new Result();
	}


	public Result test(String qMsg ) {
		init();

		OpenAIWebSocketEventSourceListener eventSourceListener = new OpenAIWebSocketEventSourceListener(null,null,asyncService,qMsg,null);
		EmbeddingResponse embeddings = client.embeddings(qMsg);
		List<List<Float>> collect = new ArrayList<>();
		for (Item datum : embeddings.getData()) {
			List<BigDecimal> embedding = datum.getEmbedding();
			List<Float> collect1 = embedding.stream().map(bigDecimal -> bigDecimal.floatValue()).collect(Collectors.toList());
			collect.add(collect1);
		}
		boolean aac = milvusService.loadCollection("pdf_1650836654262345729_1668291425938059266");
		R<SearchResults> response = milvusService.searchContent(collect, "pdf_1650836654262345729_1668291425938059266");
		SearchResultData results = response.getData().getResults();
		SearchResultsWrapper wrapper = new SearchResultsWrapper(results);
		List<String> pdf_content = (List<String>) wrapper.getFieldData("PDF_Content", 0);
		System.out.println(pdf_content);
		String msg = "Refer to the text below to answer in simplified Chinese:\"{%s}\"\n\"text:{%s}\"";
		ChatCompletion completion = ChatCompletion.builder().messages(Arrays.asList( Message.builder().content(qMsg).role(Message.Role.USER).build(), Message.builder().content(msg).role(Message.Role.USER).build())).build();
		long tokens = completion.tokens();//提问加预制的  占了多少token
		StringBuilder context = new StringBuilder();
		int count = 0;
		for(String candidate: pdf_content){
			ChatCompletion candidatea = ChatCompletion.builder().messages(Arrays.asList( Message.builder().content(candidate).role(Message.Role.USER).build())).build();
			long curCandidateToken = candidatea.tokens();
			if(curCandidateToken + tokens + 1000 > 4000){
				break;
			}
			context.append(candidate);
			tokens += curCandidateToken;
			count++;
		}
		String format = String.format(msg, context.toString(), qMsg);
		ChatCompletion chatCompletion = ChatCompletion
				.builder()
				.model(ChatCompletion.Model.GPT_3_5_TURBO.getName())
				.temperature(0.0)
				.maxTokens(1000)
				.messages(Arrays.asList( Message.builder().content(format).role(Message.Role.USER).build()))
				.stream(true)
				.build();
		chatGPTStream.streamChatCompletion(chatCompletion, eventSourceListener);
		return null;
	}

	public DouYinVo douyin(String msg,String filePath) throws IOException {
		init();
		DouYinVo mp3 = Douyin.getInfO(msg);
		if(mp3 == null){
			return null;
		}
		String mp3Url = mp3.getMusicUrl();
		if(!mp3Url.contains("mp3")){
			return null;
		}
		File fileDir = new File(filePath);
		if(!fileDir.getParentFile().exists()){   //这里就判断了文件夹是否存在
			fileDir.getParentFile().mkdirs();	//如果不存在就创建文件夹
		}
		if(!fileDir.exists()){
			fileDir.createNewFile();		//这里就是安心的创建文件了
		}
		String download = HttpDownloadUtil.download(mp3Url, filePath);
//		String map3Name = IdUtil.simpleUUID();
//		String mp3Path = filePath + "/" + map3Name + ".mp3";
//		HttpUtil.downloadFile(mp3Url, mp3Path);
		Transcriptions transcriptions = Transcriptions.builder()
				.model(Whisper.Model.WHISPER_1.getName())
				.prompt("中文输出")
				.language("zh")
				.temperature(0.2)
				.responseFormat(Whisper.ResponseFormat.VTT.getName())
				.build();

		WhisperResponse whisperResponse =
				client.speechToTextTranscriptions(new File(filePath+"/"+download), transcriptions);
		String text = whisperResponse.getText();
		mp3.setContent(text);
		//删除文件
		try {
			Path path = Paths.get(filePath+"/"+download);
			Files.delete(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mp3;
	}
}
