package com.scnujxjy.backendpoint.dto;

import cn.dev33.satoken.stp.SaTokenInfo;
import com.scnujxjy.backendpoint.entity.basic.Permission;
import lombok.Data;

import java.util.List;

@Data
public class UserReturn {
    private SaTokenInfo saTokenInfo;
    private List<String> PermissionList;
    private String roleName;
}
