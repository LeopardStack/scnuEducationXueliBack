package com.scnujxjy.backendpoint.model.ro.platform_message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class AnnouncementMessageRO {

    /**
     * 自增主键
     */
    private Long id;

    /**
     * 公告标题
     */
    private String title;

    /**
     * 公告内容
     */
    private String content;

    /**
     * 附件ID
     */
    private Long attachmentId;

    /**
     * 生成时间
     */
    private Date createdAt;

    /**
     * 发布者的用户 id
     */
    private Long userId;

    /**
     * 发布者姓名
     */
    private String name;

    /**
     * 状态：1-草稿，2-发布，3-撤销，4-删除
     */
    private Integer status;
    /**
     * 年级 集合
     */
    private Set<Long> gradeSet;
    /**
     * 专业 id 集合
     */
    private Set<Long> majorIdSet;
    /**
     * 教学点 id 集合
     */
    private Set<String> teachingPointIdSet;

}
