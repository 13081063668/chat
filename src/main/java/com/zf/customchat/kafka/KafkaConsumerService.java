package com.zf.customchat.kafka;

import com.zf.customchat.service.ChatService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.stereotype.Service;

import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Service
public class KafkaConsumerService {

    @Resource
    private ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory;

    @Resource
    private KafkaMessageListener messageListener;


    @Value("${server.name}")
    private String serverName;

    @PostConstruct
    public void startListening() {
        ConcurrentMessageListenerContainer<String, String> container =
                kafkaListenerContainerFactory.createContainer(serverName);
        container.getContainerProperties().setMessageListener(messageListener);
        container.start();
    }
}