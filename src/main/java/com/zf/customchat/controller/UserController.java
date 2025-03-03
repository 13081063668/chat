package com.zf.customchat.controller;



import com.zf.customchat.pojo.bo.Result;
import com.zf.customchat.pojo.bo.User;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;


@RestController
@RequestMapping("/user")
public class UserController {

    /**
     * 登陆
     * @param user 提交的用户数据，包含用户名和密码
     * @param session
     * @return
     */
    @PostMapping("/login")
    public Result login(@RequestBody User user, HttpSession session) {
        Result result = new Result();
        if(!StringUtils.isEmpty(user.getUsername()) && "123".equals(user.getPassword())) {
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
