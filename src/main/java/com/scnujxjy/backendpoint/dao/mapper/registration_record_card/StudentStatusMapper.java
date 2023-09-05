package com.scnujxjy.backendpoint.dao.mapper.registration_record_card;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scnujxjy.backendpoint.dao.entity.core_data.TeacherInformationPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.StudentStatusPO;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 学籍信息表 Mapper 接口
 * </p>
 *
 * @author leopard
 * @since 2023-08-04
 */
public interface StudentStatusMapper extends BaseMapper<StudentStatusPO> {
    /**
     * 根据 学生的身份证号码查找其学籍信息
     * @param idNumber 身份证号码
     * @return 教师信息集合，类型为 TeacherInformationPO
     */
    @Select("SELECT * FROM student_status WHERE id_number = #{idNumber}")
    List<StudentStatusPO> selectStudentByidNumber(String idNumber);


    /**
     * 根据 学生身份证号码获取其全部的教学计划
     * @param grade 年级
     * @param college 学院
     * @return 教师信息集合，类型为 TeacherInformationPO
     */
    @Select("SELECT * FROM student_status WHERE grade = #{grade} AND college = #{college}")
    List<StudentStatusPO> selectStudentsByGradeCollege(String grade, String college);
}
