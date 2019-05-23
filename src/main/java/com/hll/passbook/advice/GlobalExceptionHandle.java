package com.hll.passbook.advice;

import com.hll.passbook.vo.ErrorInfo;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * <h1>全局异常处理器</h1>
 */
@ControllerAdvice
public class GlobalExceptionHandle {
    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public ErrorInfo<String> errorHandle(HttpServletRequest request, Exception exception) {
        ErrorInfo<String> errorInfo = new ErrorInfo<>();
        errorInfo.setCode(ErrorInfo.ERROR);
        errorInfo.setMessage(exception.getMessage());
        errorInfo.setData("Do Not Have Return Data");
        errorInfo.setUrl(request.getRequestURL().toString());
        return errorInfo;
    }
}
