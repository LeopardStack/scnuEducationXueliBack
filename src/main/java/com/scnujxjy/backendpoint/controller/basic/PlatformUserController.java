package com.scnujxjy.backendpoint.controller.basic;


import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.util.StrUtil;
import com.scnujxjy.backendpoint.model.ro.basic.PlatformUserRO;
import com.scnujxjy.backendpoint.service.basic.PlatformUserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author leopard
 * @since 2023-08-02
 */
@RestController
@RequestMapping("/platform-user")
public class PlatformUserController {

    @Resource
    private PlatformUserService platformUserService;

    @PostMapping("/login")
    public SaResult userLogin(@RequestBody PlatformUserRO platformUserRO) {

        if (Objects.isNull(platformUserRO) || StrUtil.isBlank(platformUserRO.getUsername()) || StrUtil.isBlank(platformUserRO.getPassword())) {
            return SaResult.data(false);
        }

        Boolean isLogin = platformUserService.userLogin(platformUserRO);

        return SaResult.data(isLogin);
    }
}

