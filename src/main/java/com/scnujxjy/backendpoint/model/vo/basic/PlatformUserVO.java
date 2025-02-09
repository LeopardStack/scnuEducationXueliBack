package com.scnujxjy.backendpoint.model.vo.basic;

import com.scnujxjy.backendpoint.model.vo.platform_message.PlatformPopupMsgVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class PlatformUserVO implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 用户id，自增
     */
    private Long userId;
    /**
     * 角色id
     */
    private Long roleId;

    /**
     * 用户头像地址
     */
    private String avatarImagePath;

    /**
     * 密码
     */
    private String password;

    /**
     * 用户名，用于登录
     */
    private String username;

    /**
     * 用户姓名
     */
    private String name;


    /**
     * 微信 openId
     */
    private String wechatOpenId;
    /**
     * 补充角色 id集合
     */
    private List<Long> supplementaryRoleIdSet;

    /**
     * 是否是新生
     */
    private Boolean isNewStudent;

    /**
     * 新生录取公告
     */
    private String newStudentAnnouncement;

    /**
     * 系统消息
     */
    private List<PlatformPopupMsgVO> platformPopupMsgVOList;

    /**
     * 未读消息总条数
     */
    private Integer unReadMsgCount;

}
