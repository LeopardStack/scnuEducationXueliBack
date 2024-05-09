package com.scnujxjy.backendpoint.constant.enums.announceMsg;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author leopard
 */

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum AnnounceMsgUserTypeEnum {
    MANAGER("管理员"),

    NEW_STUDENT("新生"),
    OLD_STUDENT("在籍生");

    String userType;
}
