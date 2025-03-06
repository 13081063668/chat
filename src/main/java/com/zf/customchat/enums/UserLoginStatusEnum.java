package com.zf.customchat.enums;


public enum UserLoginStatusEnum {
    Online(1),
    Offline(0),
    ;
    private final Integer status;
    UserLoginStatusEnum(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }

    public static UserLoginStatusEnum ofEnum(Integer status){
        for (UserLoginStatusEnum value : UserLoginStatusEnum.values()) {
            if (value.getStatus().equals(status)){
                return value;
            }
        }
        return UserLoginStatusEnum.Offline;
    }
}
