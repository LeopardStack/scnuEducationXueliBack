package com.scnujxjy.backendpoint.dao.entity.basic;

import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户操作行为日志表
 * </p>
 *
 * @author 谢辉龙
 * @since 2023-12-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("user_action_log")
public class UserActionLogPO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户姓名
     */
    private String name;

    /**
     * 用户角色
     */
    private String roleName;

    /**
     * 操作类型，如登录、退出、修改等
     */
    private String actionType;

    /**
     * 操作详细描述
     */
    private String actionDescription;

    /**
     * 操作时间戳
     */
    private Date actionTimestamp;

    /**
     * 用户操作时的IP地址
     */
    private String ipAddress;

    /**
     * 设备信息，如浏览器、操作系统等
     */
    private String deviceInfo;


}
