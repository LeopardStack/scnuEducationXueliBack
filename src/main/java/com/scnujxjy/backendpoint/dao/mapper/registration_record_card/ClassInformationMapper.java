package com.scnujxjy.backendpoint.dao.mapper.registration_record_card;

import com.scnujxjy.backendpoint.dao.entity.registration_record_card.ClassInformationPO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.StudentStatusPO;
import com.scnujxjy.backendpoint.model.ro.core_data.PaymentInfoFilterRO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.ClassInformationFilterRO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.ClassInformationRO;
import com.scnujxjy.backendpoint.model.vo.core_data.PaymentInfoVO;
import com.scnujxjy.backendpoint.model.vo.course_learning.CourseClassInfoVO;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.ClassInformationVO;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.StudentStatusChangeClassInfoVO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.ClassInformationDownloadVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 班级信息表 Mapper 接口
 * </p>
 *
 * @author leopard
 * @since 2023-08-14
 */
public interface ClassInformationMapper extends BaseMapper<ClassInformationPO> {
    /**
     * 根据 班级标识获得整个班级信息
     * @param classIdentifier 班级标识
     * @return 班级信息集合，类型为 ClassInformationPO
     */
    @Select("SELECT * FROM class_information WHERE class_identifier = #{classIdentifier}")
    List<ClassInformationPO> selectClassByclassIdentifier(String classIdentifier);


    /**
     * 根据 年级 专业名称 层次 学习形式 获取所有班级信息
     * @param grade 年级
     * @param major_name 专业名称
     * @param level 层次
     * @param study_form 学习形式
     * @return ClassInformationPO 集合
     */
    @Select("SELECT * FROM class_information WHERE grade = #{grade} AND major_name = #{major_name} AND level = #{level}" +
            " AND study_form = #{study_form}")
    List<ClassInformationPO> selectClassByCondition1(String grade, String major_name, String level, String study_form);

    /**
     * 获取所有不重复的专业名称
     * @return 专业名称列表
     */
    @Select("SELECT DISTINCT major_name FROM class_information")
    List<String> selectDistinctMajorNames();

    /**
     * 获取所有不重复的年级
     * @return 年级列表
     */
    @Select("SELECT DISTINCT grade FROM class_information")
    List<String> selectDistinctGrades();

    /**
     * 获取所有不重复的层次信息
     * @return 层次列表
     */
    @Select("SELECT DISTINCT level FROM class_information")
    List<String> selectDistinctLevels();

    /**
     * 获取所有不重复的学习形式信息
     * @return 学习形式列表
     */
    @Select("SELECT DISTINCT study_form FROM class_information")
    List<String> selectDistinctStudyforms();

    /**
     * 根据年级、层次、学习形式、专业名称、班级名称来获取其学院信息
     * @param grade 年级
     * @param majorName 专业名称
     * @param level 层次
     * @param studyForm 学习形式
     * @param className 班级名称（行政班别）
     * @return
     */
    @Select("SELECT college FROM class_information " +
            "WHERE grade = #{grade} " +
            "AND major_name = #{majorName} " +
            "AND level = #{level} " +
            "AND study_form = #{studyForm} " +
            "AND class_name = #{className}")
    String selectCollegeByMultipleConditions(String grade, String majorName, String level, String studyForm, String className);

    /**
     * 根据筛选条件获取班级信息
     * @param entity 筛选条件
     * @param pageSize 筛选条件
     * @param l 筛选条件
     * @return
     */
    List<ClassInformationVO> getClassInfoByFilter(@Param("entity") ClassInformationFilterRO entity,
                                                  @Param("pageSize") Long pageSize, @Param("l") long l);

    /**
     * 根据筛选条件获取班级统计数据
     * @param entity 筛选条件
     * @return
     */
    long getCountClassInfoByFilter(@Param("entity") ClassInformationFilterRO entity);

    /**
     * 获取班级的年级筛选参数
     * @param entity 筛选条件
     * @return
     */
    List<String> getDistinctGrades(@Param("entity") ClassInformationFilterRO entity);

    /**
     * 获取班级的层次筛选参数
     * @param entity 筛选条件
     * @return
     */
    List<String> getDistinctLevels(@Param("entity") ClassInformationFilterRO entity);


    /**
     * 获取班级的年级筛选参数
     * @param entity 筛选条件
     * @return
     */
    List<String> getDistinctStudyForms(@Param("entity") ClassInformationFilterRO entity);

    /**
     * 获取班级的年级筛选参数
     * @param entity 筛选条件
     * @return
     */
    List<String> getDistinctClassNames(@Param("entity") ClassInformationFilterRO entity);

    /**
     * 获取班级的学制筛选参数
     * @param entity 筛选条件
     * @return
     */
    List<String> getDistinctStudyPeriods(@Param("entity") ClassInformationFilterRO entity);


    /**
     * 获取班级的学院筛选参数
     * @param entity 筛选条件
     * @return
     */
    List<String> getDistinctCollegeNames(@Param("entity") ClassInformationFilterRO entity);

    /**
     * 获取班级的专业名称筛选参数
     * @param entity 筛选条件
     * @return
     */
    List<String> getDistinctMajorNames(@Param("entity") ClassInformationFilterRO entity);


    /**
     * 获取班级的教学点筛选参数
     * @param entity 筛选条件
     * @return
     */
    List<String> getDistinctTeachingPoints(@Param("entity") ClassInformationFilterRO entity);

    List<ClassInformationDownloadVO> downloadClassInformationDataByManager0(@Param("entity") ClassInformationFilterRO entity);

    List<StudentStatusChangeClassInfoVO> getStudentStatusChangeClassInfoByFilter(@Param("entity") ClassInformationRO classInformationRO);

    List<ClassInformationVO> selectClassInfoData(@Param("entity") ClassInformationRO classInformationRO);
}
