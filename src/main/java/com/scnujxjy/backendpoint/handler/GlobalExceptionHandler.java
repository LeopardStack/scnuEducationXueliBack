package com.scnujxjy.backendpoint.handler;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.util.SaResult;
import com.scnujxjy.backendpoint.exception.DataException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<SaResult> handlerException(Exception e) {
        log.info("出现服务端获取数据异常 " + e);
        if(e instanceof cn.dev33.satoken.exception.NotLoginException){
            // Token 无效的情况
            log.info("token 无效 重新登录 " + e);
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED) // 401 Unauthorized
                    .body(SaResult.error("Token 无效").setCode(401));
        }else{
            log.info("识别不到异常 " + e);
        }

        if (e instanceof NotPermissionException) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN) // 403 Forbidden
                    .body(SaResult.error(e.getMessage()).setCode(2001));
        } else if (e instanceof NotLoginException) {
            NotLoginException notLoginEx = (NotLoginException) e;
            if (notLoginEx.getType().equals(NotLoginException.INVALID_TOKEN)) {
                // Token 无效的情况
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED) // 401 Unauthorized
                        .body(SaResult.error("Token 无效").setCode(401));
            } else {
                // 其他登录异常
                return ResponseEntity
                        .badRequest()
                        .body(SaResult.error(e.getMessage()).setCode(2003));
            }
        } else {
            log.error("出现异常：", e);
            return ResponseEntity
                    .badRequest()
                    .body(SaResult.error(e.getMessage()));
        }
    }
}
