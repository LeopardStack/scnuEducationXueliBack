package com.scnujxjy.backendpoint.model.bo;

import com.scnujxjy.backendpoint.model.vo.basic.PermissionVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class UserRolePermissionBO {
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 用户角色id
     */
    private Long roleId;

    /**
     * 用户权限列表
     */
    private List<PermissionVO> permissionVOS;

    /**
     * 用户资源列表
     */
    private List<String> resources;

}
