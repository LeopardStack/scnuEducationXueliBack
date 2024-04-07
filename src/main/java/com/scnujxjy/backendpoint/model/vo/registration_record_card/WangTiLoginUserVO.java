package com.scnujxjy.backendpoint.model.vo.registration_record_card;

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
public class WangTiLoginUserVO {
    /**
     * 网梯系统账号
     */
    private String username;

    /**
     * 网梯系统密码
     */
    private String password;
}
