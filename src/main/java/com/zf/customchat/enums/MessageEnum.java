package com.zf.customchat.enums;


public enum MessageEnum {
    CommonMessage(1),
    SystemMessage(2),
    LoginHistoryMessage(3),
    GetHistoryMessage(4),
    HeartBeat(5)
    ;
    private final Integer type;
    MessageEnum(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }

    public static MessageEnum ofEnum(Integer num){
        for (MessageEnum value : MessageEnum.values()) {
            if (num.equals(value.getType())){
                return value;
            }
        }
        return null;
    }
}
