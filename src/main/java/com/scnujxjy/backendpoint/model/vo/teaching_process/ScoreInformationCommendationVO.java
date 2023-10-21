package com.scnujxjy.backendpoint.model.vo.teaching_process;

import lombok.Data;

import java.util.List;

@Data
public class ScoreInformationCommendationVO {
    @Data
    public class ScoreInfo{
        /**
         * 学期
         */
        private String semester;


        /**
         * 课程名称
         */
        private String courseName;

        /**
         * 考核类型
         */
        private String assessmentType;

        /**
         * 总评
         */
        private String finalScore;

        /**
         * 第一次补考成绩
         */
        private String makeupExam1Score;

        /**
         * 第二次补考成绩
         */
        private String makeupExam2Score;

        /**
         * 结业后补考成绩
         */
        private String postGraduationScore;

        /**
         * 备注
         */
        private String remarks;

        /**
         * 状态
         */
        private String status;
    }

    /**
     * 自增主键
     */
    private Long id;

    /**
     * 学号
     */
    private String studentId;

    /**
     * 姓名
     */
    private String name;

    /**
     * 年级
     */
    private String grade;

    /**
     * 学院
     */
    private String college;

    /**
     * 专业名称
     */
    private String majorName;

    /**
     * 学习形式
     */
    private String studyForm;

    /**
     * 层次
     */
    private String level;

    /**
     * 学制
     */
    private String studyDuration;

    /**
     * 班级名称
     */
    private String className;

    /**
     * 成绩集合
     */
    List<ScoreInfo> scoreInfoList;
}
