package com.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.usercenter.common.BaseResponse;
import com.usercenter.common.ErrorCode;
import com.usercenter.common.ResultUtils;
import com.usercenter.constant.UserContent;
import com.usercenter.exception.BusinessException;
import com.usercenter.model.domain.User;
import com.usercenter.model.request.*;
import com.usercenter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.usercenter.constant.UserContent.USER_LOGIN_STATE;

@RestController //适用于编写restful风格api，返回值默认为json类型
@RequestMapping("/user")
@Slf4j
@CrossOrigin(origins = {"http://localhost:8080","http://121.40.253.205:8080"}, maxAge = 86400, allowCredentials = "true")
public class userController {
    @Resource
    private UserService userService;

    //必须鉴权

    /**
     * 用户注册
     * @param userRegisterRequest userAccount，userPassword，checkPassword
     * @return userId
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest){
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        /* controller层倾向于对请求参数本身做校验，不涉及业务逻辑本身（越少越好）
            service层是对业务逻辑做校验（可能被controller以外类调用）
         */
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if(StringUtils.isAnyEmpty(userAccount,userPassword,checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        return userService.userRegister(userAccount,userPassword,checkPassword);
    }

    /**
     * 用户登录
     * @param userLoginRequest  userAccount，userPassword
     * @param request
     * @return User信息
     */
    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request){
        log.info("userLogin request:"+request.toString());
        log.info("userLogin session:"+request.getSession().getId());
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        /* controller层倾向于对请求参数本身做校验，不涉及业务逻辑本身（越少越好）
            service层是对业务逻辑做校验（可能被controller以外类调用）
         */
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if(StringUtils.isAnyEmpty(userAccount,userPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        return userService.userLogin(userAccount, userPassword, request);
    }

    /**
     * 用户退出登录
     * @param request
     */
    @PostMapping("/outLogin")
    public BaseResponse<Void> userOutLogin(HttpServletRequest request){
        return userService.userOutLogin(request);
    }

    @PostMapping("/modify")
    public BaseResponse<Boolean> modifyUser(@RequestBody ModifyUserRequest modifyUserRequest,HttpServletRequest request){
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if(currentUser == null){
            throw new BusinessException(ErrorCode.NO_LOGIN_ERROR,"当前未登录");
        }
        Long id = currentUser.getId();
        String userAccount = modifyUserRequest.getUserAccount();
        String username = modifyUserRequest.getUsername();
        String phone = modifyUserRequest.getPhone();
        String email = modifyUserRequest.getEmail();
        String avatarUrl = modifyUserRequest.getAvatarUrl();
        Integer gender = modifyUserRequest.getGender();
        return userService.modifyUser(id,userAccount,username,phone,email,avatarUrl,gender);
    }

    @PostMapping("/currentUser")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request){
//        if(!isAdmin(request)){
//            return null;
//        }
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "空会话");
        }
        User currentUser = (User) session.getAttribute(USER_LOGIN_STATE);
        if(currentUser == null){
            throw new BusinessException(ErrorCode.NO_LOGIN_ERROR, "当前未登录");
        }
        // 若用户数据长期不变，可直接返回缓存数据
        //否则查询数据库返回用户数据
        long userId = currentUser.getId();
        User user = userService.getById(userId);
        // todo：校验用户是否合法
        return ResultUtils.success(userService.getSafetyUser(user));
    }

    /**
     * 根据用户名查找用户，支持模糊查找
     * @param searchUsersRequest
     * @param request
     * @return User查找结果列表
     */
    @PostMapping("/search")
    public BaseResponse<List<User>> searchUsers(@RequestBody SearchUsersRequest searchUsersRequest, HttpServletRequest request){
        // 必须鉴权
        if(!isAdmin(request)){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR,"当前用户非管理员");
        }
        String userAccount = searchUsersRequest.getUserAccount();
        QueryWrapper<User> queryWrapper = new QueryWrapper();
        if(StringUtils.isNotBlank(userAccount)){
            queryWrapper.like("user_account",userAccount);//username为空就是模糊查询： % + str + %
        }
        List<User> userList = userService.list(queryWrapper);
        userList = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return ResultUtils.success(userList);
    }

    /**
     * 根据id删除用户
     * @param deleteUsersRequest
     * @param request
     * @return true 删除成功，false 删除失败
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUsers(@RequestBody DeleteUsersRequest deleteUsersRequest, HttpServletRequest request){
        // 必须鉴权
        if(!isAdmin(request)){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR,"当前用户非管理员");
        }
        long id = deleteUsersRequest.getId();
        if (id <= 0){
            throw new BusinessException(ErrorCode.NULL_ERROR,"删除数据失败或用户不存在");
        }
        return ResultUtils.success(userService.removeById(id));
    }

    /**
     * 验证用户是否为管理员
     * @param request
     * @return 用户为admin ，返回true，否则返回false
     */
    private boolean isAdmin(HttpServletRequest request){
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return user != null && user.getUserRole() == UserContent.ADMIN_ROLE;
    }
}
