package com.scnujxjy.backendpoint.constant.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum AttachmentType {
    ANNOUNCEMENT("公告附件", "announcement", "公告附件");
    String name;

    String type;

    String directory;
}
