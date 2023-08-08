package com.scnujxjy.backendpoint.config;

import cn.dev33.satoken.stp.StpInterface;
import com.scnujxjy.backendpoint.service.basic.PermissionService;
import com.scnujxjy.backendpoint.service.basic.PlatformRoleService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 自定义权限加载接口实现类
 */
@Component
public class StpInterfaceConfig implements StpInterface {

    @Resource
    private PlatformRoleService platformRoleService;

    @Resource
    private PermissionService permissionService;

    /**
     * @param loginId   账号id
     * @param loginType 账号类型
     * @return
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return null;
    }

    /**
     * @param loginId   账号id
     * @param loginType 账号类型
     * @return
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        return null;
    }
}
