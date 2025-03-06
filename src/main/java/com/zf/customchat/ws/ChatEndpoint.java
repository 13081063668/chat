package com.zf.customchat.ws;


import com.alibaba.fastjson.JSON;
import com.zf.customchat.config.GetHttpSessionConfig;
import com.zf.customchat.enums.MessageEnum;
import com.zf.customchat.enums.UserLoginStatusEnum;
import com.zf.customchat.pojo.bo.Message;

import com.zf.customchat.pojo.dto.MessageDTO;
import com.zf.customchat.service.ChatService;
import com.zf.customchat.service.MongoService;
import com.zf.customchat.service.RedisService;
import com.zf.customchat.utils.MessageUtils;
import com.zf.customchat.utils.SpringContextHolder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint(value = "/chat", configurator = GetHttpSessionConfig.class)
@Component
public class ChatEndpoint {

    private ChatService chatService;
    // 使用static静态变量，使对象绑定类
    private static final Map<String, Session> users = new ConcurrentHashMap<>();
    /**
     * 建立websocket连接后调用
     * @param session
     * @param endpointConfig
     */
    @OnOpen
    public void onOpen(Session session, EndpointConfig endpointConfig) throws IOException {
        // 通过 SpringContextHolder 获取服务实例
        chatService = SpringContextHolder.getBean(ChatService.class);

        chatService.onOpen(session, endpointConfig);
    }

    @OnMessage
    public void onMessage(String msg) throws IOException {
        // 消息推送
        chatService.onMessage(msg);
    }

    /**
     * 断开Socket连接时调用
     * @param session
     */
    @OnClose
    public void onClose(Session session) throws IOException {
        chatService.onClose(session);
    }
}
