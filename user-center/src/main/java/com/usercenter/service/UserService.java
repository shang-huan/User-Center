package com.usercenter.service;

import com.usercenter.common.BaseResponse;
import com.usercenter.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
* @author ou
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2024-02-27 21:56:38
*/
public interface UserService extends IService<User> {
    /**
     * 用户注册
     * @param userAccount：用户账号
     * @param userPassword：用户密码
     * @param checkPassword：校验码
     * @return 成功返回注册用户id，否则-1
     */
    public BaseResponse<Long> userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     * @param userAccount：用户账号
     * @param userPassword：用户密码
     * @return 返回脱敏后用户数据
     */
    public BaseResponse<User> userLogin(String userAccount, String userPassword, HttpServletRequest request);

    public BaseResponse<Void> userOutLogin(HttpServletRequest request);

    /**
     * 修改用户个人信息
     * @param id
     * @param userAccount
     * @param username
     * @param phone
     * @param email
     * @param avatarUrl
     * @param gender
     * @return boolean
     */
    public BaseResponse<Boolean> modifyUser(Long id, String userAccount, String username, String phone, String email, String avatarUrl, Integer gender);

    /**
     * 用户数据脱敏
     * @param originUser 用户原始数据
     * @return 脱敏后User数据
     */
    User getSafetyUser(User originUser);
}