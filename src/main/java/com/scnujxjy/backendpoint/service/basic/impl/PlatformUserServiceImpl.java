package com.scnujxjy.backendpoint.service.basic.impl;

import com.scnujxjy.backendpoint.controller.basic.PlatformUserController;
import com.scnujxjy.backendpoint.dto.UserPermissions;
import com.scnujxjy.backendpoint.entity.basic.PlatformUser;
import com.scnujxjy.backendpoint.mapper.basic.PlatformUserMapper;
import com.scnujxjy.backendpoint.service.basic.PlatformUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author leopard
 * @since 2023-07-02
 */
@Service
public class PlatformUserServiceImpl extends ServiceImpl<PlatformUserMapper, PlatformUser> implements PlatformUserService {
    private static final Logger logger = LoggerFactory.getLogger(PlatformUserServiceImpl.class);
    public List<UserPermissions> getUserPermissions(long userID){
        try {
            List<UserPermissions> userPermissions = baseMapper.getUserPermissions(userID);
            return userPermissions;
        }catch (Exception e){
            logger.error("获取用户权限失败 userID: " + userID);
        }
        return null;
    }

    public String getUserRoleName(long userID){
        try {
            return baseMapper.getUserRoleName(userID);
        }catch (Exception e){
            logger.error("获取用户角色名 userID: " + userID);
        }
        return  null;
    }

}
