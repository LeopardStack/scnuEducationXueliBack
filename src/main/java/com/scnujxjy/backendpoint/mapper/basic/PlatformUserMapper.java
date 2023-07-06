package com.scnujxjy.backendpoint.mapper.basic;

import com.scnujxjy.backendpoint.dto.UserPermissions;
import com.scnujxjy.backendpoint.entity.basic.PlatformUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author leopard
 * @since 2023-07-02
 */
public interface PlatformUserMapper extends BaseMapper<PlatformUser> {
    /**
     *  MySQL 心跳检测 检测数据库是否还在线
     * @return
     */
    @Select("SELECT 1")
    Integer healthCheck();

    /**
     * 获取指定用户的所有权限信息
     * @param userID 用户唯一 ID
     * @return
     */
    @Select("SELECT " +
            "u.UserID, " +
            "u.Username, " +
            "p.Resource " +
            "FROM " +
            "PlatformUser u " +
            "LEFT JOIN RolePermission rp ON u.RoleID = rp.RoleID " +
            "LEFT JOIN Permission p ON rp.PermissionID = p.PermissionID " +
            "WHERE " +
            "  u.UserID = #{userID};")
    List<UserPermissions> getUserPermissions(long userID);

    /**
     * 获取指定用户的角色名
     * @param userID 指定用户 ID
     * @return
     */
    @Select("SELECT " +
            "rp.RoleName " +
            "FROM " +
            "PlatformUser u " +
            "LEFT JOIN PlatformRole rp ON u.RoleID = rp.RoleID " +
            "WHERE " +
            "  u.UserID = #{userID};")
    String getUserRoleName(long userID);
}
