package com.scnujxjy.backendpoint.dao.mapper.registration_record_card;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.GraduationInfoPO;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.GraduationInfoVO;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 毕业信息表 Mapper 接口
 * </p>
 *
 * @author leopard
 * @since 2023-08-04
 */
public interface GraduationInfoMapper extends BaseMapper<GraduationInfoPO> {

    /**
     * 根据年级和身份证号码来查询学生的毕业信息
     * @param grade 年级
     * @param studentId 证件号码
     */
    @Select("SELECT * FROM graduation_info WHERE grade = #{grade} And id_number = #{studentId}")
    List<GraduationInfoVO> selectInfoByGradeAndIdNumber(String grade, String studentId);

    /**
     * 根据年级和身份证号码来查询学生的毕业信息
     * @param grade 年级
     * @param studentId 证件号码
     */
    @Select("SELECT * FROM graduation_info WHERE grade = #{grade} And id_number = #{studentId}")
    GraduationInfoVO selectInfoByGradeAndIdNumberOne(String grade, String studentId);
}
