package com.usercenter.service;
import java.util.Date;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.usercenter.model.domain.User;
import com.usercenter.service.impl.UserServiceImpl;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class UserServiceTest {

    @Resource
    private UserService userService;

    @Test
    public void testAddUser(){
        User user = new User();
        user.setUserPassword("12345678");
        user.setPhone("123");
        user.setEmail("123");
        user.setUserStatus(0);
        user.setIsDelete(0);
        user.setAvatarUrl("https://www1.djicdn.com/cms/uploads/0d51c8c029cc7d68541e091c4e7fe589.png");
        user.setUsername("usercenter");
        user.setUserAccount("56742");
        user.setGender(0);

        Boolean result = userService.save(user);
        assertEquals(true, result);
    }

    @Test
    public void testCount(){
        long count = userService.count();
        System.out.println(userService.getClass());
        System.out.println(count);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account","123456");
        count = userService.count(queryWrapper);
        System.out.println(count);
    }

    @Test
    public void testMd5(){
        String md5Password = DigestUtils.md5Hex("abcd"+"password");
        System.out.println(md5Password);
    }

    @Test
    void userRegister() {
        String userAccount = "useroyt";
        String userPassword = "";
        String checkPassword = "234673825";
        // 存在空
        long result = userService.userRegister(userAccount,userPassword,checkPassword);
        Assertions.assertEquals(result,-1);
        // 密码和校验码不一致
        userAccount = "useroyt";
        userPassword = "73825";
        checkPassword = "234673825";
        result = userService.userRegister(userAccount,userPassword,checkPassword);
        Assertions.assertEquals(result,-1);
        // 名称长度不符合
        userAccount = "oyt";
        userPassword = "234673825";
        checkPassword = "234673825";
        result = userService.userRegister(userAccount,userPassword,checkPassword);
        Assertions.assertEquals(result,-1);
        // 密码长度不符合
        userAccount = "useroyt";
        userPassword = "2346";
        checkPassword = "2346";
        result = userService.userRegister(userAccount,userPassword,checkPassword);
        Assertions.assertEquals(result,-1);
        // 名称特殊字符
        userAccount = "user**@@";
        userPassword = "23462147";
        checkPassword = "23462147";
        result = userService.userRegister(userAccount,userPassword,checkPassword);
        Assertions.assertEquals(result,-1);
        // 密码特殊字符
        userAccount = "useroyt";
        userPassword = "23462147@@**";
        checkPassword = "23462147@@**";
        result = userService.userRegister(userAccount,userPassword,checkPassword);
        Assertions.assertEquals(result,-1);
        // 已存在同名账户
        userAccount = "useroyt";
        userPassword = "23462147";
        checkPassword = "23462147";
        result = userService.userRegister(userAccount,userPassword,checkPassword);
        Assertions.assertEquals(result,-1);
        // 成功插入
        userAccount = "userwang";
        userPassword = "23462147";
        checkPassword = "23462147";
        result = userService.userRegister(userAccount,userPassword,checkPassword);
        System.out.println(result);
        Assertions.assertTrue((result > 0));
    }
}