package com.usercenter.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.usercenter.model.domain.User;
import com.usercenter.service.UserService;
import com.usercenter.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import static com.usercenter.constant.UserContent.USER_LOGIN_STATE;

/**
* 用户服务
* @author ou
*/
@Service
@Slf4j //可以在lombok中使用日志
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {
    @Resource
    private UserMapper userMapper;
    /**
     * 盐值：混淆密码
     */
    private static final String SALT = "abcd";


    @Override
    public long userRegister(String userAccount,String userPassword,String checkPassword){
        // 校验空
        if(StringUtils.isAnyEmpty(userAccount,userPassword,checkPassword)){
            return -1;
        }
        // 密码不相同
        if(!userPassword.equals(checkPassword)){
            return -1;
        }
        // 用户账户: 4-10位字母、数字、下划线，不要求全包含
        String regExpUserAccount = "^[^0-9][\\w_]{4,10}$";
        if(!userAccount.matches(regExpUserAccount)){
            return -1;
        }
        // 用户密码：6-20位的字母、数字、下划线，不要求全包含
        String regExpUserPassword = "^[\\w_]{6,20}$";
        if(!userPassword.matches(regExpUserPassword)){
            return -1;
        }
        // 用户账户不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account",userAccount);
        long count = this.count(queryWrapper);
        if(count > 0){
            return -1;
        }
        // 密码加密
        String item = SALT + userPassword;
        String md5Password = DigestUtils.md5Hex(item);
        // 插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(md5Password);
        boolean result = this.save(user);
        if(!result){
            return -1; // 防止保存失败返回null
        }
        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // todo: null 修改为自定义异常状态类
        if(StringUtils.isAnyEmpty(userAccount,userPassword)){
            return null;
        }
        // 用户账户: 4-10位字母、数字、下划线，不要求全包含
        String regExpUserAccount = "^[^0-9][\\w_]{4,10}$";
        if(!userAccount.matches(regExpUserAccount)){
            return null;
        }
        // 用户密码：6-20位的字母、数字、下划线，不要求全包含
        String regExpUserPassword = "^[\\w_]{6,20}$";
        if(!userPassword.matches(regExpUserPassword)){
            return null;
        }
        String item = SALT + userPassword;
        String md5Password = DigestUtils.md5Hex(item);
        // 用户账户不存在或者密码错误
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account",userAccount);
        queryWrapper.eq("user_password",md5Password);
        // 检测用户是否被删除
//        queryWrapper.ne("is_delete",1);
//        通过mybatis puls框架实现逻辑删除校验
        //
        Long count = userMapper.selectCount(queryWrapper);
        User user = userMapper.selectOne(queryWrapper);
        if(count == 0){
            log.info("user login failed,userAccount can't match userPassword");
            return null;
        }
        // 限流，防止单ip单用户短时间多次访问
        ;
        //用户脱敏
        User safetyUser = getSafetyUser(user);
        // 记录用户登录态
        /*
        * 连接服务器后，得到一个session状态(匿名会话)，返回给前端；
        * 当用户登录成功后，得到登录成功的session，返回给前端一个设置cookie的命令；
        * 前端收到后端命令后，返回cookie，保存在浏览器中
        * 前端再次请求后端的时候（相同域名），在请求头中带上cookie去请求
        * 后端拿到前端传来的cookie,找到对应的seesion
        * 后端从session中可以找到取出基于该seesion存储的变量（用户登录信息等）
        * */
        request.getSession().setAttribute(USER_LOGIN_STATE,user);
//        System.out.println(request.getSession().getMaxInactiveInterval());

        // 返回用户脱敏信息
        return safetyUser;
    }

    /**
     * 用户数据脱敏
     * @param originUser 用户原始数据
     * @return 脱敏后User数据
     */
    @Override
    public User getSafetyUser(User originUser) {
        // 脱敏再次填写，清晰，防漏写
        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setUsername(originUser.getUsername());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setEmail(originUser.getEmail());
        return safetyUser;
    }
}




