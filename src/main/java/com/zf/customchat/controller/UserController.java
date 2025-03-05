package com.zf.customchat.controller;



import com.zf.customchat.pojo.bo.Result;
import com.zf.customchat.pojo.bo.User;
import com.zf.customchat.service.MongoService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;


@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private MongoService mongoService;
    /**
     * 登陆
     * @param user 提交的用户数据，包含用户名和密码
     * @param session
     * @return
     */
    @PostMapping("/login")
    public Result login(@RequestBody User user, HttpSession session) {
        Result result = new Result();
        String username = user.getUsername();
        User userFromDb = mongoService.queryUser(username);
        if(userFromDb == null){
            // 用户未注册，直接为用户注册
            mongoService.insertUser(user);
            // 登入成功
            //将数据存储到session对象中
            result.setFlag(true);
            System.out.println(user.getUsername() + " is login!");
            session.setAttribute("user", user.getUsername());
        }
        else if(userFromDb.getUsername().equals(user.getUsername()) && userFromDb.getPassword().equals(user.getPassword())) {
            //将数据存储到session对象中
            result.setFlag(true);
            System.out.println(user.getUsername() + " is login!");
            session.setAttribute("user", user.getUsername());
        } else {
            result.setFlag(false);
            result.setMessage("登陆失败");
        }
        return result;
    }

    /**
     * 获取用户名
     * @param session
     * @return
     */
    @GetMapping("/getUsername")
    public String getUsername(HttpSession session) {

        String username = (String) session.getAttribute("user");
        return username;
    }
}
