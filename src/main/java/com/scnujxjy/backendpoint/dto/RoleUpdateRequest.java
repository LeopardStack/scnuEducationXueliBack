package com.scnujxjy.backendpoint.dto;

import lombok.Data;

import java.util.List;

@Data
public class RoleUpdateRequest {
    private String roleID;
    private String roleName;
    private String roleDescription;
    private List<String> permissions;
}
