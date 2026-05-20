package com.travel.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.travel.common.Result;
import com.travel.entity.ChatMessage;
import com.travel.entity.ChatSession;
import com.travel.entity.User;
import com.travel.mapper.ChatMessageMapper;
import com.travel.mapper.ChatSessionMapper;
import com.travel.service.UserService;
import com.travel.utils.JwtUtils;
import com.travel.websocket.ChatWebSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin
public class ChatController {

    @Autowired
    private ChatMessageMapper chatMessageMapper;

    @Autowired
    private ChatSessionMapper chatSessionMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    // 获取当前用户的会话列表
    @GetMapping("/sessions")
    public Result<List<ChatSession>> getSessions(@RequestHeader("Authorization") String token) {
        try {
            Long userId = jwtUtils.getUserId(token.replace("Bearer ", ""));
            List<ChatSession> sessions = chatSessionMapper.getSessionsByUserId(userId);
            return Result.success(sessions);
        } catch (Exception e) {
            return Result.error("获取会话列表失败");
        }
    }

    // 获取与指定用户的聊天记录
    @GetMapping("/history/{targetId}")
    public Result<List<ChatMessage>> getHistory(
            @RequestHeader("Authorization") String token,
            @PathVariable Long targetId,
            @RequestParam(defaultValue = "50") int limit) {
        try {
            Long userId = jwtUtils.getUserId(token.replace("Bearer ", ""));
            List<ChatMessage> messages = chatMessageMapper.getChatHistory(userId, targetId, limit);
            chatMessageMapper.markAsRead(targetId, userId);
            return Result.success(messages);
        } catch (Exception e) {
            return Result.error("获取聊天记录失败");
        }
    }

    // 获取联系人列表（排除自己，只显示状态正常的用户）
    @GetMapping("/contacts")
    public Result<List<User>> getContacts(@RequestHeader("Authorization") String token) {
        try {
            Long userId = jwtUtils.getUserId(token.replace("Bearer ", ""));
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.ne(User::getId, userId);
            wrapper.eq(User::getStatus, 1);
            wrapper.select(User::getId, User::getUsername, User::getNickname, User::getAvatar);
            wrapper.last("LIMIT 50");
            List<User> users = userService.list(wrapper);
            return Result.success(users);
        } catch (Exception e) {
            return Result.error("获取联系人列表失败");
        }
    }

    // 检查用户是否在线
    @GetMapping("/online/{userId}")
    public Result<Map<String, Object>> checkOnline(@PathVariable Long userId) {
        Map<String, Object> data = new HashMap<>();
        data.put("online", ChatWebSocket.isOnline(userId));
        data.put("onlineCount", ChatWebSocket.getOnlineCount());
        return Result.success(data);
    }

    // 删除与指定用户的会话
    @DeleteMapping("/session/{targetId}")
    public Result<String> deleteSession(
            @RequestHeader("Authorization") String token,
            @PathVariable Long targetId) {
        try {
            Long userId = jwtUtils.getUserId(token.replace("Bearer ", ""));
            LambdaQueryWrapper<ChatSession> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ChatSession::getUserId, userId);
            wrapper.eq(ChatSession::getTargetId, targetId);
            chatSessionMapper.delete(wrapper);
            return Result.success("删除成功");
        } catch (Exception e) {
            return Result.error("删除失败");
        }
    }
}
