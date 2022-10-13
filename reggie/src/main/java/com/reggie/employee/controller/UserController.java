package com.reggie.employee.controller;


import com.alibaba.druid.sql.visitor.functions.If;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.reggie.employee.common.Result;
import com.reggie.employee.entity.User;
import com.reggie.employee.service.UserService;
import com.reggie.employee.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 发送手机验证码
     * @param user
     * @param session
     * @return
     */
    @PostMapping("/sendMsg")
    public Result<String> sendMsg(@RequestBody User user, HttpSession session){
        String phone = user.getPhone();

        if(StringUtils.isNotEmpty(phone)){
            String code = ValidateCodeUtils.generateValidateCode(6).toString();
            log.info("code={}",code);
            //调用阿里云提供的短信服务API完成发送短信
            //SMSUtils.sendMessage("瑞吉外卖","",phone,code);
            //session.setAttribute(phone,code);
            //利用redis缓存验证码
            redisTemplate.opsForValue().set(phone,code,3, TimeUnit.MINUTES);
            return Result.success("手机验证码发送成功");
        }
        return Result.error("短信验证发送失败");
    }

    /**
     * 移动端用户登录
     * @param map
     * @return
     */
    @PostMapping("/login")
    public Result<User> login(@RequestBody Map map, HttpSession session){
        log.info("登录信息 {}",map);
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();
        //Object codeInSession = session.getAttribute(phone);
        Object codeInSession = redisTemplate.opsForValue().get(phone);
        if(codeInSession!=null && codeInSession.equals(code)){
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq((User::getPhone),phone);
            User user = userService.getOne(queryWrapper);
            if (user == null){
                user =new User();
                user.setPhone(phone);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());
            redisTemplate.delete(phone);
            return Result.success(user);
        }
        return Result.error("用户登录失败");
    }
}
