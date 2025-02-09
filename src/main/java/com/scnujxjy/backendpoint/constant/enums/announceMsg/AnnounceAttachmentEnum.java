package com.scnujxjy.backendpoint.constant.enums.announceMsg;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum AnnounceAttachmentEnum {
    NOW_NEW_STUDENT_ADMISSION("2024级新生录取公告"),
    COMMON_ANNOUNCEMENT_MSG_PREFIX("system_announcement_msg")
    ;

    String systemArg;
}
