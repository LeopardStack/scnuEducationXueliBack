package com.scnujxjy.backendpoint.model.vo.course_learning;

import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CoursesInfoByClassMappingVO {
    private Long id;

    private String grade;

    private String courseName;

    private String courseType;

    private String courseDescription;

    private String courseCoverUrl;

    private String defaultMainTeacherUsername;

    /**
     * 课程标识符 用来在未来建立好 全局统一的课程标识符后
     * 直接通过课程标识符来匹配 某个年级教学计划中这门课的所有班
     */
    private String courseIdentifier;

    private String valid;

    private Date createdTime;

    private Date updatedTime;

    private Long courseId;

    private String classIdentifier;
}
