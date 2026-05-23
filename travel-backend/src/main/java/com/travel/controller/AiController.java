package com.travel.controller;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.travel.common.Result;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin
public class AiController {

    // DeepSeek API 地址和密钥
    private static final String DEEPSEEK_API_URL = "https://api.deepseek.com/chat/completions";
    private static final String API_KEY = "sk-645d72d8c1574fe2b525304bb1d05ac3";

    // AI 智能对话
    @PostMapping("/chat")
    public Result<String> chat(@RequestBody Map<String, Object> request) {
        try {
            String userMessage = (String) request.get("message");
            List<Map<String, String>> history = (List<Map<String, String>>) request.get("history");

            JSONArray messages = new JSONArray();

            // 系统提示词 - AI导游角色
            JSONObject systemMsg = new JSONObject();
            systemMsg.set("role", "system");
            systemMsg.set("content", "你是一个专业的AI旅游导游助手，名叫'小游'。你熟悉中国各地的旅游景点、美食、文化和旅行攻略。" +
                    "你的职责是：1. 回答用户关于旅游目的地的问题；2. 推荐景点、美食和住宿；3. 提供旅行建议和注意事项；" +
                    "4. 帮助用户规划行程。请用友好、专业的语气回答，回答要简洁实用。如果用户问的不是旅游相关问题，" +
                    "请礼貌地引导他们询问旅游相关的内容。");
            messages.add(systemMsg);

            // 添加历史对话
            if (history != null && !history.isEmpty()) {
                for (Map<String, String> msg : history) {
                    JSONObject historyMsg = new JSONObject();
                    historyMsg.set("role", msg.get("role"));
                    historyMsg.set("content", msg.get("content"));
                    messages.add(historyMsg);
                }
            }

            // 添加用户消息
            JSONObject userMsg = new JSONObject();
            userMsg.set("role", "user");
            userMsg.set("content", userMessage);
            messages.add(userMsg);

            // 构建请求体
            JSONObject requestBody = new JSONObject();
            requestBody.set("model", "deepseek-chat");
            requestBody.set("messages", messages);
            requestBody.set("temperature", 0.7);
            requestBody.set("max_tokens", 1024);

            // 调用DeepSeek API
            HttpResponse response = HttpRequest.post(DEEPSEEK_API_URL)
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + API_KEY)
                    .body(requestBody.toString())
                    .timeout(60000)
                    .execute();

            if (response.isOk()) {
                JSONObject result = JSONUtil.parseObj(response.body());
                JSONArray choices = result.getJSONArray("choices");
                if (choices != null && !choices.isEmpty()) {
                    JSONObject choice = choices.getJSONObject(0);
                    JSONObject message = choice.getJSONObject("message");
                    String content = message.getStr("content");
                    return Result.success(content);
                }
            }

            return Result.error("AI服务暂时不可用，请稍后再试");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("请求失败: " + e.getMessage());
        }
    }

}