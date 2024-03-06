package com.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.usercenter.constant.UserContent;
import com.usercenter.model.domain.User;
import com.usercenter.model.request.DeleteUsersRequest;
import com.usercenter.model.request.SearchUsersRequest;
import com.usercenter.model.request.UserLoginRequest;
import com.usercenter.model.request.UserRegisterRequest;
import com.usercenter.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController //适用于编写restful风格api，返回值默认为json类型
@RequestMapping("/user")
public class userController {
    @Resource
    private UserService userService;

    //必须鉴权

    @PostMapping("/register")
    public Long userRegister(@RequestBody UserRegisterRequest userRegisterRequest){
        if (userRegisterRequest == null) {
            return null;
        }
        /* controller层倾向于对请求参数本身做校验，不涉及业务逻辑本身（越少越好）
            service层是对业务逻辑做校验（可能被controller以外类调用）
         */
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if(StringUtils.isAnyEmpty(userAccount,userPassword,checkPassword)){
            return null;
        }
        return userService.userRegister(userAccount,userPassword,checkPassword);
    }

    @PostMapping("/login")
    public User userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request){
        if (userLoginRequest == null) {
            return null;
        }
        /* controller层倾向于对请求参数本身做校验，不涉及业务逻辑本身（越少越好）
            service层是对业务逻辑做校验（可能被controller以外类调用）
         */
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        System.out.println(userAccount + " " + userPassword);
        if(StringUtils.isAnyEmpty(userAccount,userPassword)){
            return null;
        }
        return userService.userLogin(userAccount, userPassword, request);
    }

    /**
     * 根据用户名查找用户，支持模糊查找
     * @param searchUsersRequest
     * @param request
     * @return User查找结果列表
     */
    @PostMapping("/search")
    public List<User> searchUsers(@RequestBody SearchUsersRequest searchUsersRequest, HttpServletRequest request){
        // 必须鉴权
        String userAccount = searchUsersRequest.getUserAccount();
        if(!isAdmin(request)){
            return new ArrayList<>();
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper();
        if(StringUtils.isNotBlank(userAccount)){
            queryWrapper.like("user_account",userAccount);//username为空就是模糊查询： % + str + %
        }
        List<User> userList = userService.list(queryWrapper);
        userList = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return userList;
    }

    /**
     * 根据id删除用户
     * @param deleteUsersRequest
     * @param request
     * @return true 删除成功，false 删除失败
     */
    @PostMapping("/delete")
    public boolean deleteUsers(@RequestBody DeleteUsersRequest deleteUsersRequest, HttpServletRequest request){
        // 必须鉴权
        long id = deleteUsersRequest.getId();
        if(!isAdmin(request)){
            return false;
        }
        if (id <= 0){
            return false;
        }
        return userService.removeById(id);
    }

    /**
     * 验证用户是否为管理员
     * @param request
     * @return 用户为admin ，返回true，否则返回false
     */
    private boolean isAdmin(HttpServletRequest request){
        Object userObj = request.getSession().getAttribute(UserContent.USER_LOGIN_STATE);
        User user = (User) userObj;
        return user != null && user.getUserRole() == UserContent.ADMIN_ROLE;
    }
}
