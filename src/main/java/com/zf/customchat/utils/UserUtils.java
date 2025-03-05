package com.zf.customchat.utils;

import com.alibaba.fastjson.JSON;
import com.mongodb.client.FindIterable;
import com.zf.customchat.enums.MessageEnum;
import com.zf.customchat.pojo.bo.Message;
import com.zf.customchat.pojo.bo.Result;
import com.zf.customchat.pojo.bo.User;
import com.zf.customchat.pojo.dbo.MessageDO;
import com.zf.customchat.pojo.dto.MessageDTO;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class UserUtils {

    public static User convertToUser(Document userDocument ){
        if (userDocument == null)
            return null;
        User user = new User();
        ObjectId id = userDocument.getObjectId("_id");
        String username = userDocument.getString("username");
        String password = userDocument.getString("password");
        user.setUserId(id.toHexString());
        user.setUserId(username);
        user.setPassword(password);
        return user;
    }

    public static Document convertToDocument(User user) {
        Document document = new Document();
        document.append("username", user.getUsername())
                .append("password", user.getPassword());
        return document;
    }
}
