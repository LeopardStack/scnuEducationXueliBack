package com.scnujxjy.backendpoint.dto;

import lombok.Data;

/**
 * @author leopard
 */
@Data
public class UserPermissions {
    private long userID;
    private String userName;
    private String resource;
}
