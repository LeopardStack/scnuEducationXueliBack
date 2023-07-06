package com.scnujxjy.backendpoint.dto;

import lombok.Data;

import java.util.List;

@Data
public class RolePermissions {
    private Long roleID;
    private String roleName;
    private String roleDescription;
    private List<String> permissions;
}
