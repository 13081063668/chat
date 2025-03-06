package com.zf.customchat.service;

import com.alibaba.fastjson.JSON;
import com.zf.customchat.enums.MessageEnum;
import com.zf.customchat.enums.UserLoginStatusEnum;
import com.zf.customchat.kafka.KafkaProducerService;
import com.zf.customchat.pojo.bo.Message;
import com.zf.customchat.pojo.dto.MessageDTO;
import com.zf.customchat.utils.MessageUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ChatService {
    // 使用static静态变量，使对象绑定类
    private static final Map<String, Session> users = new ConcurrentHashMap<>();
    private HttpSession httpSession;
    @Resource
    private RedisService redisService;
    @Resource
    private MongoService mongoService;
    @Resource
    private KafkaProducerService kafkaProducerService;

    @Value("${server.name}")
    private String serverName;

    @Value("${server2.name}")
    private String server2Name;


    public void onOpen(Session session, EndpointConfig endpointConfig) throws IOException {
        // 1. 获取userName
        this.httpSession = (HttpSession) endpointConfig.getUserProperties().get(HttpSession.class.getName());
        String username = this.httpSession.getAttribute("user").toString();

        // 2. 上线
        users.put(username, session);
        redisService.setStatus(username, UserLoginStatusEnum.Online);
        // 3. 存储session所在服务器位置
        redisService.setLocation(username);
        // 4. 广播消息，将所有用户推送给所有的用户
        List<String> allUsers = mongoService.getAllUsers();
        Message message = new Message();
        message.setMessage(allUsers.toString());
        message.setRead(false);
        message.setMessageType(MessageEnum.SystemMessage);
        String msg = MessageUtils.getMessage(message);
        broadcastAllUsers(msg);

        // 3. 推送历史十条消息
//        List<Message> history = mongoService.getHistory(username, new Date());
//        Message historyMessage = new Message();
//        historyMessage.setMessageList(history);
//        historyMessage.setRead(false);
//        historyMessage.setMessageType(MessageEnum.LoginHistoryMessage);
//        String historyMsg = MessageUtils.getMessage(historyMessage);
//        session.getBasicRemote().sendText(historyMsg);
    }

    private void broadcastAllUsers(String msg){
        try{
            // 遍历在线用户
            for (Map.Entry<String, Session> entry : users.entrySet()) {
                String key = entry.getKey();
                UserLoginStatusEnum status = redisService.getStatus(key);
                if (UserLoginStatusEnum.Online.equals(status)){
                    Session session = entry.getValue();
                    session.getBasicRemote().sendText(msg);
                }
            }
        }catch (IOException e){
            // 记录或处理异常
        }catch (Exception e){
            // 记录或处理异常
        }
    }

    public void onMessage(String msg) throws IOException {
        MessageDTO messageDTO = JSON.parseObject(msg, MessageDTO.class);
        Message message = MessageUtils.dtoConvertToMessage(messageDTO);
        if (MessageEnum.CommonMessage.equals(message.getMessageType())){
            handleCommonMessage(message);
        }else if(MessageEnum.GetHistoryMessage.equals(message.getMessageType())){
            handleGetHistoryMessage(message);
        }else if (MessageEnum.HeartBeat.equals(message.getMessageType())){
            handleHeartBeatMessage(message);
        }
    }

    public void handleHeartBeatMessage(Message message) {
        String fromName = message.getFromName();
        redisService.setStatus(fromName, UserLoginStatusEnum.Online);
        redisService.setLocation(fromName);
    }
    public void handleGetHistoryMessage(Message message) throws IOException {

        String fromName = (String) httpSession.getAttribute("user");
        String toName = message.getToName();
        System.out.println(fromName + " " + toName);
        UserLoginStatusEnum statusEnum = redisService.getStatus(fromName);
        String location = redisService.getLocation(fromName);
        if (UserLoginStatusEnum.Offline.equals(statusEnum) || location == null){
            return;
        }
        if (!location.equals(serverName)){
            kafkaProducerService.sendMessage(location, JSON.toJSONString(MessageUtils.convertToMessageDTO(message)));
        }else {
            Date lastTime = message.getSendTime();
            if (lastTime == null){
                lastTime = new Date();
            }
            List<Message> history = mongoService.getHistory(fromName, toName, lastTime);
            Message historyMessage = new Message();
            historyMessage.setMessageList(history);
            historyMessage.setRead(false);
            historyMessage.setMessageType(MessageEnum.GetHistoryMessage);
            String historyMsg = MessageUtils.getMessage(historyMessage);
            Session session = users.get(fromName);
            session.getBasicRemote().sendText(historyMsg);
        }
    }

    public void handleCommonMessage(Message message) throws IOException {
        String toName = message.getToName();
        // 获取在线状态
        UserLoginStatusEnum statusEnum = redisService.getStatus(toName);
        String location = redisService.getLocation(toName);
        System.out.println(1);
        // 离线
        if (UserLoginStatusEnum.Offline.equals(statusEnum) || location == null){
            System.out.println(2);
            // 存储消息记录
            mongoService.insertChat(MessageUtils.convertToMongoDO(message));
            return;
        }
        if (location.equals(serverName)) {
            System.out.println(3);
            Session session = users.get(toName);
            message.setSendTime(new Date());
            message.setRead(false);
            message.setMessageType(MessageEnum.CommonMessage);
            String finalMsg = MessageUtils.getMessage(message);
            session.getBasicRemote().sendText(finalMsg);
            // 存储消息记录
            mongoService.insertChat(MessageUtils.convertToMongoDO(message));
        }else {
            System.out.println(4);
            kafkaProducerService.sendMessage(location, JSON.toJSONString(MessageUtils.convertToMessageDTO(message)));
        }
    }

    public void onClose(Session session) throws IOException {
        String username = (String) httpSession.getAttribute("user");
        // 下线
        users.remove(username);
        redisService.setStatus(username, UserLoginStatusEnum.Offline);
        redisService.deleteLocation(username);
        System.out.println("username:" + username + " is logout!");
        session.close();
    }
}
