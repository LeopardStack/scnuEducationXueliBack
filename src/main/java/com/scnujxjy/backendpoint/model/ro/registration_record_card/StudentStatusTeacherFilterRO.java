package com.scnujxjy.backendpoint.model.ro.registration_record_card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class StudentStatusTeacherFilterRO {
    /**
     * 学号
     */
    private String studentNumber;

    /**
     * 身份证号码
     */
    private String idNumber;


    /**
     * 姓名
     */
    private String name;

    /**
     * 班级名称
     */
    private String className;

    /**
     * 班级序号
     */
    private List<String> classIdentifiers;
}
