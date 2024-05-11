package com.scnujxjy.backendpoint.constant.enums.announceMsg;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum AnnouncementMsgStatusEnum {
    PUBLISHED("已发布"),
    DRAFT("草稿"),
    REVOKE("已撤销"),
    DELETED("已删除")
            ;

    String status;

    public static boolean isStatusValid(String status) {
        for (AnnouncementMsgStatusEnum statusEnum : AnnouncementMsgStatusEnum.values()) {
            if (statusEnum.getStatus().equals(status)) {
                return true;
            }
        }
        return false;
    }

    public static List<String> getAllStatus() {
        List<String> statusList = new ArrayList<>();
        for (AnnouncementMsgStatusEnum statusEnum : AnnouncementMsgStatusEnum.values()) {
            statusList.add(statusEnum.getStatus());
        }
        return statusList;
    }
}
