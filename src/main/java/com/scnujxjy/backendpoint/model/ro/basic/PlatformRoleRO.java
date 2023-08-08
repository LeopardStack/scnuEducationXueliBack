package com.scnujxjy.backendpoint.model.ro.basic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class PlatformRoleRO {
    /**
     * 角色id
     */
    private Long roleId;

    /**
     * 角色名
     */
    private String roleName;
}
