package com.zf.customchat.pojo.bo;

import lombok.Data;

import java.util.List;

@Data
public class Result {
    private Integer messageType;
    private boolean flag;
    private String fromName;
    private String toName;
    private String message;
    private List<Message> messageList;

}
