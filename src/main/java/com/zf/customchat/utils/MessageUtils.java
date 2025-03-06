package com.zf.customchat.utils;

import com.alibaba.fastjson.JSON;

import com.alibaba.fastjson.JSONObject;
import com.mongodb.client.FindIterable;
import com.zf.customchat.enums.MessageEnum;
import com.zf.customchat.pojo.bo.Message;
import com.zf.customchat.pojo.bo.Result;
import com.zf.customchat.pojo.dbo.MessageDO;
import com.zf.customchat.pojo.dto.MessageDTO;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;


public class MessageUtils {

    /**
     * @param message 消息的具体内容
     * @return
     */
    public static String getMessage(Message message) {

        Result result = new Result();
        result.setMessageType(message.getMessageType().getType());
        result.setMessage(message.getMessage());
        result.setMessageList(message.getMessageList());
        if(message.getFromName() != null) {
            result.setFromName(message.getFromName());
        }
        return JSON.toJSONString(result);
    }
    public static MessageDO convertToMongoDO(Message message){
        MessageDO messageDO = new MessageDO();
        messageDO.setFromName(message.getFromName());
        messageDO.setToName(message.getToName());
        messageDO.setSendTime(message.getSendTime());
        messageDO.setMessageType(message.getMessageType().getType());
        messageDO.setRead(message.getRead());
        messageDO.setMessage(message.getMessage());
        return messageDO;
    }

    public static List<Message> convertToMessageList(FindIterable<Document> documents){
        ArrayList<Message> messageList = new ArrayList<>();
        for (Document document : documents) {
            Message message = documentConvertToMessage(document);
            messageList.add(message);
        }
        return messageList;
    }
    public static Message documentConvertToMessage(Document document){
        if (document == null){
            return null;
        }
        Message message = new Message();
        message.setMessage(document.getString("message"));
        message.setMessageType(MessageEnum.CommonMessage);
        message.setFromName(document.getString("fromName"));
        message.setToName(document.getString("toName"));
        message.setSendTime(document.getDate("sendTime"));
        return message;
    }

    public static Message dtoConvertToMessage(MessageDTO messageDTO) {
        if(messageDTO == null){
            return null;
        }
        Message message = new Message();
        message.setMessage(messageDTO.getMessage());
        message.setMessageType(MessageEnum.ofEnum(messageDTO.getMessageType()));
        message.setSendTime(messageDTO.getSendTime());
        message.setFromName(messageDTO.getFromName());
        message.setToName(messageDTO.getToName());
        return message;
    }

    public static MessageDTO convertToMessageDTO(Message message) {
        if(message == null){
            return null;
        }
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setMessage(message.getMessage());
        messageDTO.setMessageType(message.getMessageType().getType());
        messageDTO.setSendTime(message.getSendTime());
        messageDTO.setFromName(message.getFromName());
        messageDTO.setToName(message.getToName());

        return messageDTO;
    }
}
