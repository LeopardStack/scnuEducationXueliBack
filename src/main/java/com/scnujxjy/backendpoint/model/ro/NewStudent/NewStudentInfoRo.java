package com.scnujxjy.backendpoint.model.ro.NewStudent;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewStudentInfoRo {
    private String grade; //年级
    private  String studentNumber;//学号

    private String idCardNumber;//身份证号

    private String college;//学院

    private String teachingPoint;//教学点

    private  String admissionCollege; //录取学院

}
