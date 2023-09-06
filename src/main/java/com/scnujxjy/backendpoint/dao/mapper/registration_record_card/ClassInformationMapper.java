package com.scnujxjy.backendpoint.dao.mapper.registration_record_card;

import com.scnujxjy.backendpoint.dao.entity.registration_record_card.ClassInformationPO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.StudentStatusPO;
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
}
