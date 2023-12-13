package com.scnujxjy.backendpoint.constant.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum MessageEnum {
    DOWNLOAD_MSG("下载消息"),
    ANNOUNCEMENT_MSG("公告消息"),
    BATCH_SET_Exam_Teachers("批量设置考试命题人和阅卷人"),
    UPLOAD_MSG("上传消息");


    String messageName;

}
