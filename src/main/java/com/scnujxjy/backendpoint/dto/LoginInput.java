package com.scnujxjy.backendpoint.dto;

import lombok.Data;

/**
 * @author leopard
 */
@Data
public class LoginInput {
    private String username;
    private String password;
    private String roleName;
}
