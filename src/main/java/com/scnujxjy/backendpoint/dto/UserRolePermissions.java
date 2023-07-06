package com.scnujxjy.backendpoint.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserRolePermissions {
    private Long userID;
    private String userName;
    private String roleName;
    private List<String> resources;
}
