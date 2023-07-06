package com.scnujxjy.backendpoint.service.basic;

import com.scnujxjy.backendpoint.dto.UserPermissions;
import com.scnujxjy.backendpoint.entity.basic.PlatformUser;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author leopard
 * @since 2023-07-02
 */
public interface PlatformUserService extends IService<PlatformUser> {
    /**
     * 获取指定用户权限
     * @param userID
     * @return
     */
    public List<UserPermissions> getUserPermissions(long userID);

    /**
     * 获取指定用户角色名
     * @param userID
     * @return
     */
    public String getUserRoleName(long userID);
}
