package com.zf.customchat.pojo.bo;

import com.zf.customchat.enums.MessageEnum;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class Message {
    private MessageEnum messageType;
    private boolean read;
    private String fromName;
    private String toName;
    private String message;
    private Date sendTime;
    private List<Message> messageList;

    public MessageEnum getMessageType() {
        return messageType;
    }

    public boolean getRead() {
        return read;
    }

}
