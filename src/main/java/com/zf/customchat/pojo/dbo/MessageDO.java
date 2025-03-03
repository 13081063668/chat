package com.zf.customchat.pojo.dbo;

import lombok.Data;

import java.util.Date;

@Data
public class MessageDO {
    private String fromName;
    private String toName;
    private String message;
    private Date sendTime;
    private Integer MessageType;
    private boolean read;
}
