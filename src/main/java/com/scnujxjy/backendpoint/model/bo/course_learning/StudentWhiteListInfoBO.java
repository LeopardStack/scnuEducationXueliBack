package com.scnujxjy.backendpoint.model.bo.course_learning;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class StudentWhiteListInfoBO {
    // 身份证号码
    private String idNumber;

    // 姓名
    private String name;

    // 学号
    private String studentNumber;
}
