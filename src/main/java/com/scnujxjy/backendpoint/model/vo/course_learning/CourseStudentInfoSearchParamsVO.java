package com.scnujxjy.backendpoint.model.vo.course_learning;

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
public class CourseStudentInfoSearchParamsVO {
    List<String> grades;

    List<String> colleges;

    List<String> majorNames;

    List<String> studyForms;

    List<String> levels;

    List<String> teachingPointNames;

    List<String> classNames;

    List<String> isRetakes;

}
