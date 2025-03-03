package com.zf.customchat.enums;


public enum MessageEnum {
    CommonMessage(1),
    SystemMessage(2),
    LoginHistoryMessage(3),
    GetHistoryMessage(4),
    ;
    private final Integer num;
    MessageEnum(Integer num) {
        this.num = num;
    }

    public Integer getNum() {
        return num;
    }

    public static MessageEnum ofEnum(Integer num){
        for (MessageEnum value : MessageEnum.values()) {
            if (num.equals(value.getNum())){
                return value;
            }
        }
        return null;
    }
}
