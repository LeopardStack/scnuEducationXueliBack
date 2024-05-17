package com.scnujxjy.backendpoint.model.vo.platform_message;

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
public class AnnouncementMsgFilterArgsVO {
    /**
     * 继续教育学院所有的部门
     */
    List<String> departmentList;

    /**
     * 继续教育学院所有合作的二级学院
     */
    List<String> collegeNameList;

    /**
     * 继续教育学院所有合作的教学点
     */
    List<String> teachingPointNameList;

    /**
     * 新旧生的年级筛选项
     */
    List<String> gradeList;

    /**
     * 新生所涉及的所有学院
     */
    List<String> newStudentCollegeList;

    /**
     * 新生所涉及的所有专业名称
     */
    List<String> newStudentMajorNameList;

    /**
     * 新生所涉及的层次信息
     */
    List<String> newStudentLevelList;

    /**
     * 新生所涉及的学习形式信息
     */
    List<String> newStudentStudyFormList;

    /**
     * 新生所涉及的教学点信息
     */
    List<String> newStudentTeachingPointList;

    /**
     * 旧生所涉及的所有学院信息
     */
    List<String> oldStudentCollegeList;

    /**
     * 旧生所涉及的所有专业信息
     */
    List<String> oldStudentMajorNameList;

    /**
     * 旧生所涉及的层次信息
     */
    List<String> oldStudentLevelList;

    /**
     * 旧生所涉及的学制信息
     */
    List<String> oldStudentStudyDurationList;

    /**
     * 旧生所涉及的学习形式信息
     */
    List<String> oldStudentStudyFormList;

    /**
     * 旧生所涉及的教学点信息
     */
    List<String> oldStudentTeachingPointList;

    /**
     * 旧生所涉及的学籍状态信息
     */
    List<String> oldStudentAcademicStatusList;
}
