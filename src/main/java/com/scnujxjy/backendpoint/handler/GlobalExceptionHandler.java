package com.scnujxjy.backendpoint.handler;

import cn.dev33.satoken.util.SaResult;
import com.scnujxjy.backendpoint.exception.DataException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 数据错误拦截
     *
     * @param e 错误
     * @return 响应信息
     */
    @ExceptionHandler(DataException.class)
    public SaResult RuntimeExceptionHandler(DataException e) {
        log.error("数据出现错误：", e);
        return new SaResult(e.getCode(), e.getMessage(), null);
    }

    /**
     * 全局异常拦截
     *
     * @param e
     * @return
     */
    @ExceptionHandler
    public SaResult handlerException(Exception e) {
        log.error("出现异常：", e);
        return SaResult.error(e.getMessage());
    }
}
