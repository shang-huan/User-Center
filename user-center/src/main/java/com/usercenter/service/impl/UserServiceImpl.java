package com.usercenter.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.usercenter.common.ErrorCode;
import com.usercenter.common.ResultUtils;
import com.usercenter.exception.BusinessException;
import com.usercenter.model.domain.User;
import com.usercenter.service.UserService;
import com.usercenter.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static com.usercenter.constant.UserContent.USER_LOGIN_STATE;
import com.usercenter.common.BaseResponse;
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
    public BaseResponse<Long> userRegister(String userAccount,String userPassword,String checkPassword){
        // 校验空
        if(StringUtils.isAnyEmpty(userAccount,userPassword,checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        // 密码不相同
        if(!userPassword.equals(checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码不相同");
        }
        // 用户账号: 4-10位字母、数字、下划线，不要求全包含
        String regExpUserAccount = "^[^0-9][\\w_]{4,10}$";
        if(!userAccount.matches(regExpUserAccount)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户账号格式不匹配");
        }
        // 用户密码：6-20位的字母、数字、下划线，不要求全包含
        String regExpUserPassword = "^[\\w_]{6,20}$";
        if(!userPassword.matches(regExpUserPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户密码格式不匹配");
        }
        // 用户账号不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account",userAccount);
        long count = this.count(queryWrapper);
        if(count > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户账号重复");
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
            throw new BusinessException(ErrorCode.NULL_ERROR,"数据保存失败");
        }
        return ResultUtils.success(user.getId());
    }

    @Override
    public BaseResponse<User> userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // todo: null 修改为自定义异常状态类
        if(StringUtils.isAnyEmpty(userAccount,userPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        // 用户账号: 4-10位字母、数字、下划线，不要求全包含
        String regExpUserAccount = "^[^0-9][\\w_]{4,10}$";
        if(!userAccount.matches(regExpUserAccount)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号格式不匹配");
        }
        // 用户密码：6-20位的字母、数字、下划线，不要求全包含
        String regExpUserPassword = "^[\\w_]{6,20}$";
        if(!userPassword.matches(regExpUserPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码格式不匹配");
        }
        String item = SALT + userPassword;
        String md5Password = DigestUtils.md5Hex(item);
        // 用户账号不存在或者密码错误
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
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号或密码不正确");
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
        HttpSession session = request.getSession();
        session.setAttribute(USER_LOGIN_STATE, safetyUser);

        // 返回用户脱敏信息
        return ResultUtils.success(safetyUser);
    }

    @Override
    public BaseResponse<Void> userOutLogin(HttpServletRequest request){
        request.getSession().invalidate();
        return ResultUtils.success(null);
    }

    @Override
    public BaseResponse<Boolean> modifyUser(Long id, String userAccount, String username, String phone, String email, String avatarUrl, Integer gender){
        if(id == null || userAccount == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        // 用户账号: 4-10位字母、数字、下划线，不要求全包含
        String regExpUserAccount = "^[^0-9][\\w_]{4,10}$";
        if(!userAccount.matches(regExpUserAccount)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号格式不匹配");
        }
        // 手机号: 1开头，11位数字
        String regExpPhone = "1[0-9]{10}$";
        if(phone != null && !phone.isEmpty() && !phone.matches(regExpPhone)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"手机号格式不匹配");
        }
        // 邮箱：XXX@XXX.XXX
        String regExpEmail = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        if(email != null && !email.isEmpty() && !email.matches(regExpEmail)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"邮件格式不匹配");
        }
        // 头像
        String regExpAvatarUrl = "^https?://[\\w.-]+(:\\d+)?(/[\\w./_-]+)*\\.(png|jpg|jpeg|gif|bmp|webp|svg)$";
        if(avatarUrl != null && !avatarUrl.isEmpty() && !avatarUrl.matches(regExpAvatarUrl)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"头像网址格式不匹配");
        }

        Object userObj = userMapper.selectById(id);
        User user = (User)userObj;
        if(user == null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"用户ID不存在");
        }
        user.setUserAccount(userAccount);
        user.setUsername(username);
        user.setPhone(phone);
        user.setEmail(email);
        user.setAvatarUrl(avatarUrl);
        user.setGender(gender);

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",user.getId());
        int result = userMapper.update(user,queryWrapper);
        return ResultUtils.success(result == 1);
    }

    /**
     * 用户数据脱敏
     * @param originUser 用户原始数据
     * @return 脱敏后User数据
     */
    @Override
    public User getSafetyUser(User originUser) {
        if(originUser == null){
            return null;
        }
        // 脱敏再次填写，清晰，防漏写
        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setUsername(originUser.getUsername());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setUserRole(originUser.getUserRole());
        return safetyUser;
    }
}




