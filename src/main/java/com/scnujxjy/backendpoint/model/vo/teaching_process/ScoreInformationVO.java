package com.scnujxjy.backendpoint.model.vo.teaching_process;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Accessors(chain = true)
public class ScoreInformationVO implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long id;

    private String studentId;

    private String classIdentifier;

    private String grade;

    private String college;

    private String majorName;

    private String semester;

    private String courseName;

    private String courseCode;

    private String courseType;

    private String assessmentType;

    private String finalScore;

    private String makeupExam1Score;

    private String makeupExam2Score;

    private String postGraduationScore;

    private String remarks;

    private String status;

    /**
     * 班级名称
     */
    private String className;

    /**
     * 学习形式
     */
    private String studyForm;

    /**
     * 层次
     */
    private String level;

    /**
     * 姓名
     */
    private String name;
}
