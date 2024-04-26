package com.scnujxjy.backendpoint.constant.enums.oa;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author lth
 * @version 1.0
 * @description TODO
 * @date 2024/4/26 15:40
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum SystemMessageType2Enum {

    NEW_STUDENT_MAJOR_CHANGE1("新生校内转专业"),
    NEW_STUDENT_MAJOR_CHANGE2("新生校外转专业"),
    NEW_STUDENT_MAJOR_CHANGE3("新生省外转专业"),
    NEW_STUDENT_MAJOR_CHANGE4("在籍生校内转专业");

    String typeName;

}
