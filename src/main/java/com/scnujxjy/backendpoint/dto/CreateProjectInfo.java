package com.scnujxjy.backendpoint.dto;

import lombok.Data;

import java.util.Date;

/**
 * @author leopard
 */
@Data
public class CreateProjectInfo {
    /**
     * 培训项目名称
     */
    private String name;
    /**
     * 培训项目描述
     */
    private String description;
    /**
     * 培训项目创建时间 由系统生成
     */
    private Date createdAt;
    /**
     * 培训项目截止时间
     */
    private Date deadline;
    /**
     * 培训项目学时
     */
    private String creditHours;
    /**
     * 培训项目讨论区是否打开
     */
    private boolean discussionClosed;
    /**
     * 培训项目讨论区点赞是否打开
     */
    private boolean likesEnabled;
    /**
     * 培训项目讨论区评论是否打开
     */
    private boolean commentsClosed;
    /**
     * 培训项目成果区是否打开
     */
    private boolean achievementsClosed;
    /**
     * 培训项目直播权限是否打开
     */
    private boolean livePermissionsClosed;

    /**
     * 培训项目点播权限是否打开
     */
    private boolean vodPermissionsClosed;
}
