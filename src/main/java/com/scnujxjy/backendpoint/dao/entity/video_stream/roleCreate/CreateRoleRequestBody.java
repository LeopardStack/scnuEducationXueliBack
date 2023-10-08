package com.scnujxjy.backendpoint.dao.entity.video_stream.roleCreate;

import lombok.Data;
import java.util.List;

@Data
public class CreateRoleRequestBody {

    /**
     * 角色.
     * Assistant: 助教
     * Guest: 嘉宾
     */
    private String role;

    /**
     * 头衔. 长度1~10位，默认值为"助教".
     */
    private String actor;

    /**
     * 昵称. 长度1~15位，默认随机生成昵称.
     */
    private String nickName;

    /**
     * 头像. 默认使用初始头像，支持JPG、PNG格式图片.
     */
    private String avatar;

    /**
     * 角色密码. 长度6~16位，默认随机生成密码.
     */
    private String passwd;

    /**
     * 权限列表.
     */
    private List<PurviewList> purviewList;
}