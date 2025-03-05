package com.zf.customchat.service;

import com.zf.customchat.enums.UserLoginStatusEnum;
import com.zf.customchat.pojo.bo.User;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class RedisService {
    @Resource
    private RedisTemplate<String, Integer> redisTemplate;
    private static final String LOGIN_STATUS_KEY = "user:login:";

    public void setStatus(String username, UserLoginStatusEnum online) {
        Integer status = online.getStatus();
        redisTemplate.opsForValue().set(LOGIN_STATUS_KEY + username, status);
    }

    public UserLoginStatusEnum getStatus(String username) {
        Integer status = redisTemplate.opsForValue().get(LOGIN_STATUS_KEY + username);
        UserLoginStatusEnum userLoginStatusEnum = UserLoginStatusEnum.ofEnum(status);
        return  userLoginStatusEnum;
    }
}
