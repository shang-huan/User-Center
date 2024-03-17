package com.usercenter.exception;

import com.usercenter.common.BaseResponse;
import com.usercenter.common.ErrorCode;
import com.usercenter.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 *
 * 1、捕获代码中所有异常，内部消化，集中处理，让前端得到更详细业务报错
 * 2、同时屏蔽掉项目框架本身异常（不暴露服务器内部状态）
 * 3、集中处理、例如记录日志
 * 基于Spring AOP：在调用方法前后进行额外处理
 *
 * @author ou
 */

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 改方法仅捕获BusinessException该异常
     * @return
     */
    @ExceptionHandler(BusinessException.class)
    public BaseResponse businessExceptionHandler(BusinessException businessException){
        log.error("BusinessException:" + businessException.getMessage(),businessException);
        return ResultUtils.error(businessException.getCode(),businessException.getMessage(),businessException.getDescription());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse runtimeExceptionHandler(RuntimeException runtimeException){
        log.error("RuntimeException:" + runtimeException.getMessage(),runtimeException);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR,runtimeException.getMessage(),"");
    }
}
