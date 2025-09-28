package test;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

public class DeepSeekExample {
    public static void main(String[] args) {
        getAnswerByDeepSeek();
    }

    private static void getAnswerByDeepSeek() {
        // 构建请求体 JSON
        JSONObject json = new JSONObject();
        json.set("model", "deepseek-chat");
        json.set("messages", JSONUtil.parseArray(
//                "[{\"role\": \"system\", \"content\": \"You are a helpful assistant.\"}," +
                        "[{\"role\": \"user\", \"content\": \"你是谁!\"}]"
        ));

        // 发送 POST 请求
        HttpResponse response = HttpRequest.post("http://192.168.31.228:8000/v1/chat/completions")
                .header("Authorization", "Bearer zcOvdrYtY22yiL8Q/AHB1rsOeLgV3UXlg2OasNId5MtTlmU2I1cM7APh9w1C44b6")
                .header("Content-Type", "application/json")
                .body(json.toString())
                .execute();

        // 输出响应结果
        System.out.println("状态码: " + response.getStatus());
        System.out.println("响应内容: " + response.body());

        JSONObject json2 = JSONUtil.parseObj(response.body());
        String content = json2.getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getStr("content");

        System.out.println("助手回复内容：\n" + content);
    }
}
