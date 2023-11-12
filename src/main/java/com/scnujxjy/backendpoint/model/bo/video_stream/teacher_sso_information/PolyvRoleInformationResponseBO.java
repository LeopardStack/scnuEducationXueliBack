package com.scnujxjy.backendpoint.model.bo.video_stream.teacher_sso_information;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class PolyvRoleInformationResponseBO implements Serializable {

    /**
     * 账户 id
     */
    private String account;

    /**
     * 频道 id
     */
    private String channelId;

    /**
     * 角色密码
     */
    private String passwd;

    /**
     * 角色昵称
     */
    private String nickname;
    /**
     * 角色状态
     */
    private String status;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 角色：Assistant-助教，Guest-嘉宾
     */
    private String role;
}
