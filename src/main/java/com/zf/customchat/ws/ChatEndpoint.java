package com.zf.customchat.ws;


import com.alibaba.fastjson.JSON;
import com.zf.customchat.config.GetHttpSessionConfig;
import com.zf.customchat.enums.MessageEnum;
import com.zf.customchat.pojo.bo.Message;

import com.zf.customchat.pojo.dto.MessageDTO;
import com.zf.customchat.service.MongoService;
import com.zf.customchat.utils.MessageUtils;
import com.zf.customchat.utils.SpringContextHolder;
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

    private MongoService mongoService;

    // 使用static静态变量，使对象绑定类
    private static final Map<String, Session> users = new ConcurrentHashMap<>();
    private HttpSession httpSession;
    /**
     * 建立websocket连接后调用
     * @param session
     * @param endpointConfig
     */
    @OnOpen
    public void onOpen(Session session, EndpointConfig endpointConfig) throws IOException {

        // 通过 SpringContextHolder 获取服务实例
        mongoService = SpringContextHolder.getBean(MongoService.class);

        // 1. 将session保存
        this.httpSession = (HttpSession) endpointConfig.getUserProperties().get(HttpSession.class.getName());

        String username = this.httpSession.getAttribute("user").toString();
        System.out.println("username: " + username);
        // 上线
        users.put(username, session);

        // 2. 广播消息，将所有用户推送给所有的用户
        Set<String> allUsers = getAllUsers();
        Message message = new Message();
        message.setMessage(allUsers.toString());
        message.setRead(false);
        message.setMessageType(MessageEnum.SystemMessage);
        String msg = MessageUtils.getMessage(message);
        broadcastAllUsers(msg);
        // 3. 推送历史十条消息
        List<Message> history = mongoService.getHistory(username, new Date());
        Message historyMessage = new Message();
        historyMessage.setMessageList(history);
        historyMessage.setRead(false);
        historyMessage.setMessageType(MessageEnum.LoginHistoryMessage);
        String historyMsg = MessageUtils.getMessage(historyMessage);
        session.getBasicRemote().sendText(historyMsg);
    }

    private Set<String> getAllUsers() {
        // 创建一个新的 HashSet 来存储所有用户
        return users.keySet();
    }

    private void broadcastAllUsers(String msg){
        try{
            // 遍历在线用户
            for (Map.Entry<String, Session> entry : users.entrySet()) {
                Session session = entry.getValue();
                session.getBasicRemote().sendText(msg);
            }
        }catch (IOException e){
            // 记录或处理异常
        }catch (Exception e){
            // 记录或处理异常
        }
    }
    @OnMessage
    public void onMessage(String msg) throws IOException {
        // 消息推送
        MessageDTO messageDTO = JSON.parseObject(msg, MessageDTO.class);
        Message message = MessageUtils.dtoConvertToMessage(messageDTO);
        if (MessageEnum.CommonMessage.equals(message.getMessageType())){
            handleCommonMessage(message);
        }else if(MessageEnum.GetHistoryMessage.equals(message.getMessageType())){
            handleGetHistoryMessage(message);
        }
    }

    private void handleGetHistoryMessage(Message message) throws IOException {
        // 3. 推送历史十条消息
        String toName = message.getToName();
        String fromName = (String) httpSession.getAttribute("user");
        Date lastTime = message.getSendTime();
        List<Message> history = mongoService.getHistory(fromName, lastTime);
        Message historyMessage = new Message();
        historyMessage.setMessageList(history);
        historyMessage.setRead(false);
        historyMessage.setMessageType(MessageEnum.GetHistoryMessage);
        String historyMsg = MessageUtils.getMessage(historyMessage);
        Session session = users.get(fromName);
        if(session != null){
            session.getBasicRemote().sendText(historyMsg);
        }
    }

    private void handleCommonMessage(Message message) throws IOException {
        String toName = message.getToName();
        String fromName = (String) httpSession.getAttribute("user");

        message.setFromName(fromName);
        message.setSendTime(new Date());
        message.setRead(false);
        message.setMessageType(MessageEnum.CommonMessage);

        // 获取session
        Session session = users.get(toName);
        if (session != null){
            String finalMsg = MessageUtils.getMessage(message);
            session.getBasicRemote().sendText(finalMsg);
        }
        // 存储消息记录
        mongoService.insertChat(MessageUtils.convertToMongoDO(message));
    }

    /**
     * 断开Socket连接时调用
     * @param session
     */
    @OnClose
    public void onClose(Session session) throws IOException {
        String username = (String) httpSession.getAttribute("user");
        users.remove(username);
        System.out.println("username:" + username + " is logout!");
        session.close();

    }

}
