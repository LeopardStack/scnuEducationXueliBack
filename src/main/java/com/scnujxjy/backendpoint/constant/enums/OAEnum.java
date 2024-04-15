package com.scnujxjy.backendpoint.constant.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum OAEnum {
    NEW_STUDENT_MAJOR_CHANGE1("新生转专业-省外"),
    NEW_STUDENT_MAJOR_CHANGE2("新生转专业-校外"),
    NEW_STUDENT_MAJOR_CHANGE3("新生转专业-校内"),
    OLD_STUDENT_MAJOR_CHANGE3("在籍生转专业"),


    APPROVAL_STATUS1("等待审批中"),
    APPROVAL_STATUS2("审批通过"),
    APPROVAL_STATUS3("审批不通过"),
    APPROVAL_STATUS4("打回"),
    APPROVAL_STATUS5("修改成功"),
    APPROVAL_STATUS6("审批异常")
    ;


    String oaType;
}
