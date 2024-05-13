package com.scnujxjy.backendpoint.model.vo.platform_message;

import com.scnujxjy.backendpoint.model.ro.platform_message.AnnouncementMsgUserFilterRO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class AnnouncementMessageVO {

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
     * 浏览量
     */
    private Long pageViews;

    /**
     * 附件集合（顺序）
     */
    private List<AttachmentVO> attachmentVOS;

    /**
     * 是否弹框
     */
    private boolean isPopUp;

    /**
     * 用户集合
     */
    private String userType;

    /**
     * 公告截止时间
     */
    private Date dueDate;

    /**
     * 筛选参数
     */
    private AnnouncementMsgUserFilterRO parsedAnnouncementMsgUserFilterRO;

}
