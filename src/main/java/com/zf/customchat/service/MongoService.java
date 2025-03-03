package com.zf.customchat.service;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.zf.customchat.pojo.bo.Message;
import com.zf.customchat.pojo.dbo.MessageDO;
import com.zf.customchat.utils.MessageUtils;
import com.zf.customchat.utils.MongoClientSingleton;
import org.bson.Document;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Sorts.descending;

@Service
public class MongoService {
    public void insertChat(MessageDO messageDO) {
        MongoClient client = MongoClientSingleton.getInstance();
        MongoDatabase chatDB = client.getDatabase("chatDB");
        MongoCollection<Document> chat = chatDB.getCollection("chat");
        Document document = convertToDocument(messageDO);
        chat.insertOne(document);
    }
    private Document convertToDocument(MessageDO messageDO) {
        Document document = new Document();
        document.append("fromName", messageDO.getFromName())
                .append("toName", messageDO.getToName())
                .append("sendTime", messageDO.getSendTime())
                .append("message", messageDO.getMessage());
        return document;
    }

    public List<Message> getHistory(String username, Date date) {
        MongoClient client = MongoClientSingleton.getInstance();
        MongoDatabase chatDB = client.getDatabase("chatDB");
        MongoCollection<Document> chat = chatDB.getCollection("chat");
        FindIterable<Document> documents = chat.find(and(or(eq("fromName", username), eq("toName", username)), lt("sendTime", date)))
                .sort(descending("sendTime"))
                .limit(10);
        List<Message> messages = MessageUtils.convertToMessageList(documents);
        Collections.reverse(messages);
        return messages;
    }
}
