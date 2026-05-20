package com.travel.websocket;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.travel.entity.ChatMessage;
import com.travel.entity.User;
import com.travel.mapper.ChatMessageMapper;
import com.travel.mapper.ChatSessionMapper;
import com.travel.mapper.UserMapper;
import com.travel.entity.ChatSession;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

//WebSocket 聊天实现
@ServerEndpoint("/ws/chat/{userId}")
@Component
public class ChatWebSocket {

    // 在线用户连接池（线程安全）
    private static ConcurrentHashMap<Long, ChatWebSocket> webSocketMap = new ConcurrentHashMap<>();
    private static ChatMessageMapper chatMessageMapper;
    private static ChatSessionMapper chatSessionMapper;
    private static UserMapper userMapper;
    
    private Session session;
    private Long userId;

    @Autowired
    public void setChatMessageMapper(ChatMessageMapper mapper) {
        ChatWebSocket.chatMessageMapper = mapper;
    }

    @Autowired
    public void setChatSessionMapper(ChatSessionMapper mapper) {
        ChatWebSocket.chatSessionMapper = mapper;
    }
    
    @Autowired
    public void setUserMapper(UserMapper mapper) {
        ChatWebSocket.userMapper = mapper;
    }

    // 连接建立
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") Long userId) {
        this.session = session;
        this.userId = userId;
        webSocketMap.put(userId, this);
        System.out.println("用户 " + userId + " 连接成功，当前在线人数：" + webSocketMap.size());
        
        JSONObject msg = new JSONObject();
        msg.set("type", "connect");
        msg.set("message", "连接成功");
        msg.set("onlineCount", webSocketMap.size());
        sendMessage(msg.toString());
    }

    // 连接关闭
    @OnClose
    public void onClose() {
        if (userId != null) {
            webSocketMap.remove(userId);
            System.out.println("用户 " + userId + " 断开连接，当前在线人数：" + webSocketMap.size());
        }
    }

    // 接收消息
    @OnMessage
    public void onMessage(String message, Session session) {
        try {
            JSONObject json = JSONUtil.parseObj(message);
            String type = json.getStr("type");
            
            if ("chat".equals(type)) {
                Long receiverId = json.getLong("receiverId");
                String content = json.getStr("content");
                
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setSenderId(userId);
                chatMessage.setReceiverId(receiverId);
                chatMessage.setContent(content);
                chatMessage.setType(0);
                chatMessage.setStatus(0);
                chatMessage.setCreateTime(LocalDateTime.now());
                chatMessageMapper.insert(chatMessage);
                
                updateSession(userId, receiverId, content);
                updateSession(receiverId, userId, content);
                
                // 获取发送者信息
                User sender = userMapper.selectById(userId);
                String senderName = sender != null ? (sender.getNickname() != null ? sender.getNickname() : sender.getUsername()) : "用户" + userId;
                String senderAvatar = sender != null ? sender.getAvatar() : null;
                
                JSONObject response = new JSONObject();
                response.set("type", "chat");
                response.set("messageId", chatMessage.getId());
                response.set("senderId", userId);
                response.set("senderName", senderName);
                response.set("senderAvatar", senderAvatar);
                response.set("receiverId", receiverId);
                response.set("content", content);
                response.set("createTime", chatMessage.getCreateTime().toString());

                // 发送给自己（回执）
                sendMessage(response.toString());

                // 转发给接收者
                ChatWebSocket receiverSocket = webSocketMap.get(receiverId);
                if (receiverSocket != null) {
                    receiverSocket.sendMessage(response.toString());
                }
                // 处理已读回执
            } else if ("read".equals(type)) {
                Long senderId = json.getLong("senderId");
                chatMessageMapper.markAsRead(senderId, userId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 发生错误
    @OnError
    public void onError(Session session, Throwable error) {
        System.err.println("WebSocket错误：" + error.getMessage());
    }

    // 发送消息
    private void sendMessage(String message) {
        try {
            this.session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 更新会话（最后一条消息和未读数）
    private void updateSession(Long userId, Long targetId, String lastMessage) {
        try {
            ChatSession session = chatSessionMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ChatSession>()
                    .eq(ChatSession::getUserId, userId)
                    .eq(ChatSession::getTargetId, targetId)
            );
            
            if (session == null) {
                session = new ChatSession();
                session.setUserId(userId);
                session.setTargetId(targetId);
                session.setLastMessage(lastMessage);
                session.setUnreadCount(0);
                session.setCreateTime(LocalDateTime.now());
                session.setUpdateTime(LocalDateTime.now());
                chatSessionMapper.insert(session);
            } else {
                session.setLastMessage(lastMessage);
                session.setUpdateTime(LocalDateTime.now());
                if (!userId.equals(this.userId)) {
                    session.setUnreadCount(session.getUnreadCount() + 1);
                }
                chatSessionMapper.updateById(session);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 判断用户是否在线
    public static boolean isOnline(Long userId) {
        return webSocketMap.containsKey(userId);
    }

    // 获取在线人数
    public static int getOnlineCount() {
        return webSocketMap.size();
    }
}
