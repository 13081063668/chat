package com.zf.customchat.service;

import com.zf.customchat.enums.UserLoginStatusEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService {
    @Resource
    private RedisTemplate<String, Integer> redisTemplateInteger;
    @Resource
    private RedisTemplate<String, String> redisTemplateString;

    @Value("${server.name}")
    private String serverName;

    private static final String LOGIN_STATUS_KEY = "user:login:";
    private static final String SESSION_LOCATION_KEY = "session:location:";
    public void setStatus(String username, UserLoginStatusEnum online) {
        Integer status = online.getStatus();
        redisTemplateInteger.opsForValue().set(LOGIN_STATUS_KEY + username, status, 10, TimeUnit.MINUTES);
    }

    public UserLoginStatusEnum getStatus(String username) {
        Integer status = redisTemplateInteger.opsForValue().get(LOGIN_STATUS_KEY + username);
        UserLoginStatusEnum userLoginStatusEnum = UserLoginStatusEnum.ofEnum(status);
        return  userLoginStatusEnum;
    }

    public void setLocation(String username) {
        redisTemplateString.opsForValue().set(SESSION_LOCATION_KEY + username, serverName, 10, TimeUnit.MINUTES);
    }

    public String getLocation(String username) {
        return redisTemplateString.opsForValue().get(SESSION_LOCATION_KEY + username);
    }

    public void deleteLocation(String username) {
        redisTemplateString.opsForValue().getAndDelete(username);
    }
}
