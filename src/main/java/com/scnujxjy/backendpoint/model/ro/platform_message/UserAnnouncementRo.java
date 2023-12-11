package com.scnujxjy.backendpoint.model.ro.platform_message;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class UserAnnouncementRo {
    private long id;
    private  String title;
    private  String content;
    private  long attachmentId;
    private  String userId;
    private  String messageType;
    private  long relatedMessageId;
    private Date createdAt;
    private  Boolean isRead;
}
