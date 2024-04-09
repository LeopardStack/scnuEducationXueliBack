package com.scnujxjy.backendpoint.model.vo.course_learning;

import com.scnujxjy.backendpoint.model.bo.course_learning.TeacherInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Builder
public class PageQueryCoursesInfoParamsVO {
    List<String> grades;

    List<String> years;

    List<String> colleges;

    List<String> majorNames;

    List<String> studyForms;

    List<String> levels;

    List<String> teachingPointNames;

    List<String> classNames;

    List<String> courseNames;

    List<TeacherInfoVO> teacherInfos;

    List<String> courseTypeList;

}
