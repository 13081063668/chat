package com.zf.customchat.pojo.dto;

import lombok.Data;

import java.util.Date;

@Data
public class MessageDTO {
    private String fromName;
    private String toName;
    private String message;
    private Date sendTime;
    private Integer messageType;
    private boolean read;
}
