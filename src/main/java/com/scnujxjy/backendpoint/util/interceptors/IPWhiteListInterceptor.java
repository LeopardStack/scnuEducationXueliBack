package com.scnujxjy.backendpoint.util.interceptors;

import cn.dev33.satoken.util.SaResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scnujxjy.backendpoint.util.annotations.CheckIPWhiteList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class IPWhiteListInterceptor implements HandlerInterceptor {

    private Set<String> whiteListIPs = new HashSet<>();

    public IPWhiteListInterceptor() {
        // 初始化你的IP白名单
        whiteListIPs.add("192.168.91.1");
        // 添加其他白名单IP地址...
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String handlerClassName = handler.getClass().getName();
        if ("org.springframework.web.method.HandlerMethod".equals(handlerClassName)) {
            try {
                // 使用反射获取getMethodAnnotation方法
                Method getMethodAnnotation = handler.getClass().getMethod("getMethodAnnotation", Class.class);
                // 调用getMethodAnnotation方法
                Object result = getMethodAnnotation.invoke(handler, CheckIPWhiteList.class);
                if (result != null) {
                    String clientIP = request.getRemoteAddr();
                    if (!whiteListIPs.contains(clientIP)) {
                        // 构造SaResult对象
                        SaResult saResult = SaResult.error("Access Denied for IP: " + clientIP).setCode(403);

                        // 将SaResult对象转换为JSON字符串
                        ObjectMapper objectMapper = new ObjectMapper();
                        String json = objectMapper.writeValueAsString(saResult);

                        // 设置响应类型和编码
                        response.setContentType("application/json;charset=UTF-8");
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

                        // 将SaResult JSON字符串写入响应
                        response.getWriter().write(json);
                        return false;
                    }
                }
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                // 处理异常情况
                e.printStackTrace();
            }
        }
        return true;
    }

}

