package com.scnujxjy.backendpoint.dao.mapper.courses_learning;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scnujxjy.backendpoint.dao.entity.courses_learning.CoursesLearningPO;
import com.scnujxjy.backendpoint.model.bo.course_learning.CourseRecordBO;
import com.scnujxjy.backendpoint.model.ro.courses_learning.CourseScheduleSearchRO;
import com.scnujxjy.backendpoint.model.ro.courses_learning.CourseStudentSearchRO;
import com.scnujxjy.backendpoint.model.ro.courses_learning.CoursesLearningRO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.StudentStatusFilterRO;
import com.scnujxjy.backendpoint.model.vo.course_learning.CourseLearningStudentInfoVO;
import com.scnujxjy.backendpoint.model.vo.course_learning.CourseLearningVO;
import com.scnujxjy.backendpoint.model.vo.course_learning.CourseScheduleVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author 谢辉龙
 * @since 2024-03-05
 */
public interface CoursesLearningMapper extends BaseMapper<CoursesLearningPO> {
    // 使用 TRUNCATE 清除表中的数据并重置自增主键
    @Update("TRUNCATE TABLE courses_learning")
    void truncateTable();

    /**
     * 获取不同角色输入下 的 课程信息
     *
     * @param coursesLearningRO
     * @return
     * @Param("entity")AdmissionInformationRO entity,
     * @Param("pageNumber")Long pageNumber, @Param("pageSize")Long pageSize
     */
    List<CourseLearningVO> selectCourseLearningData(@Param("entity") CoursesLearningRO coursesLearningRO,
                                                    @Param("pageNumber") Long pageNumber, @Param("pageSize") Long pageSize);

    Long selectCourseLearningDataCount(@Param("entity") CoursesLearningRO coursesLearningRO);

    List<CourseLearningVO> selectCourseLearningDataWithoutPaging(@Param("entity")CoursesLearningRO coursesLearningRO);

    List<CourseRecordBO> getCourseSectionsData();

    List<CourseLearningStudentInfoVO> selectCourseStudentsInfo(@Param("entity") CourseStudentSearchRO entity);

    Long selectCountCourseStudentsInfo(@Param("entity") CourseStudentSearchRO entity);

    List<CourseLearningStudentInfoVO> selectCourseRetakeStudentsInfo(@Param("entity") CourseStudentSearchRO entity);

    List<CourseScheduleVO> selectCoursesScheduleInfo(@Param("entity") CourseScheduleSearchRO entity,
                                                     @Param("pageNumber") Long pageNumber, @Param("pageSize") Long pageSize,
                                                     @Param("specialNodeType") String sectionContentType);

    Long selectCoursesScheduleInfoCount(@Param("entity") CourseScheduleSearchRO entity,
                                        @Param("specialNodeType") String sectionContentType);

    List<String> selectCourseStudentsInfoSelectParamsGrades(@Param("entity") CourseStudentSearchRO entity);

    List<String> selectCourseStudentsInfoSelectParamsColleges(@Param("entity") CourseStudentSearchRO entity);

    List<String> selectCourseStudentsInfoSelectParamsMajorNames(@Param("entity") CourseStudentSearchRO entity);

    List<String> selectCourseStudentsInfoSelectParamsLevels(@Param("entity") CourseStudentSearchRO entity);

    List<String> selectCourseStudentsInfoSelectParamsStudyForms(@Param("entity") CourseStudentSearchRO entity);

    List<String> selectCourseStudentsInfoSelectParamsClassNames(@Param("entity") CourseStudentSearchRO entity);

    List<String> selectCourseStudentsInfoSelectParamsTeachingPointNames(@Param("entity") CourseStudentSearchRO entity);

    List<String> selectCourseStudentsInfoSelectParamsGradesForRetake(@Param("entity") CourseStudentSearchRO entity);

    List<String> selectCourseStudentsInfoSelectParamsCollegesForRetake(@Param("entity") CourseStudentSearchRO entity);

    List<String> selectCourseStudentsInfoSelectParamsMajorNamesForRetake(@Param("entity") CourseStudentSearchRO entity);

    List<String> selectCourseStudentsInfoSelectParamsLevelsForRetake(@Param("entity") CourseStudentSearchRO entity);

    List<String> selectCourseStudentsInfoSelectParamsStudyFormsForRetake(@Param("entity") CourseStudentSearchRO entity);

    List<String> selectCourseStudentsInfoSelectParamsClassNamesForRetake(@Param("entity") CourseStudentSearchRO entity);

    List<String> selectCourseStudentsInfoSelectParamsTeachingPointNamesForRetake(@Param("entity") CourseStudentSearchRO entity);

    List<String> selectCourseLearningDataSelectParamsGrades(@Param("entity") CoursesLearningRO entity);

    List<String> selectCourseLearningDataSelectParamsColleges(@Param("entity") CoursesLearningRO entity);

    List<String> selectCourseLearningDataSelectParamsMajorNames(@Param("entity") CoursesLearningRO entity);

    List<String> selectCourseLearningDataSelectParamsStudyForms(@Param("entity") CoursesLearningRO entity);

    List<String> selectCourseLearningDataSelectParamsLevels(@Param("entity") CoursesLearningRO entity);

    List<String> selectCourseLearningDataSelectParamsTeachingPointNames(@Param("entity") CoursesLearningRO entity);

    List<String> selectCourseLearningDataSelectParamsClassNames(@Param("entity") CoursesLearningRO entity);

    List<String> selectCourseLearningDataSelectParamsCourseNames(@Param("entity") CoursesLearningRO entity);
}
