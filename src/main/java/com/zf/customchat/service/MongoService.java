package com.zf.customchat.service;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.InsertOneResult;
import com.zf.customchat.pojo.bo.Message;
import com.zf.customchat.pojo.bo.User;
import com.zf.customchat.pojo.dbo.MessageDO;
import com.zf.customchat.utils.MessageUtils;
import com.zf.customchat.utils.MongoClientSingleton;
import com.zf.customchat.utils.UserUtils;
import org.bson.Document;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

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

    public List<Message> getHistory(String fromName, String toName, Date date) {
        MongoClient client = MongoClientSingleton.getInstance();
        MongoDatabase chatDB = client.getDatabase("chatDB");
        MongoCollection<Document> chat = chatDB.getCollection("chat");

        FindIterable<Document> documents = chat.find(and(or(
                and(eq("fromName", fromName), eq("toName", toName)),
                and(eq("fromName", toName), eq("toName", fromName))),
                lt("sendTime", date)))
                .sort(descending("sendTime"))
                .limit(10);
        List<Message> messages = MessageUtils.convertToMessageList(documents);
        Collections.reverse(messages);
        return messages;
    }

    public User queryUser(String username) {
        MongoClient client = MongoClientSingleton.getInstance();
        MongoDatabase chatDB = client.getDatabase("chatDB");
        MongoCollection<Document> usersCollection = chatDB.getCollection("users");
        Document userDocument = usersCollection.find(eq("username", username)).first();

        return UserUtils.convertToUser(userDocument);
    }

    public void insertUser(User user) {
        MongoClient client = MongoClientSingleton.getInstance();
        MongoDatabase chatDB = client.getDatabase("chatDB");
        MongoCollection<Document> users = chatDB.getCollection("users");
        Document document = UserUtils.convertToDocument(user);
        InsertOneResult result = users.insertOne(document);
    }

    public List<String> getAllUsers() {
        MongoClient client = MongoClientSingleton.getInstance();
        MongoDatabase chatDB = client.getDatabase("chatDB");
        MongoCollection<Document> users = chatDB.getCollection("users");
        FindIterable<Document> documents = users.find();
        return UserUtils.convertToUserList(documents);
    }
}
