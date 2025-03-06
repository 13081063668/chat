package com.zf.customchat.kafka;

import com.alibaba.fastjson.JSON;
import com.zf.customchat.enums.MessageEnum;
import com.zf.customchat.pojo.bo.Message;
import com.zf.customchat.pojo.dto.MessageDTO;
import com.zf.customchat.service.ChatService;
import com.zf.customchat.utils.MessageUtils;
import lombok.SneakyThrows;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

@Component
public class KafkaMessageListener implements MessageListener<String, String> {


    @Resource
    private ChatService chatService;

    @Override
    public void onMessage(ConsumerRecord<String, String> record) {
        String msg = record.value();
        MessageDTO messageDTO = JSON.parseObject(msg, MessageDTO.class);
        System.out.println(messageDTO);
        Message message = MessageUtils.dtoConvertToMessage(messageDTO);
        System.out.println(message);
        try {
            if (MessageEnum.CommonMessage.equals(message.getMessageType())){
                chatService.handleCommonMessage(message);
            }else if(MessageEnum.GetHistoryMessage.equals(message.getMessageType())){
                chatService.handleGetHistoryMessage(message);
            }else if (MessageEnum.HeartBeat.equals(message.getMessageType())){
                chatService.handleHeartBeatMessage(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Received message: " + record.value());
    }
}