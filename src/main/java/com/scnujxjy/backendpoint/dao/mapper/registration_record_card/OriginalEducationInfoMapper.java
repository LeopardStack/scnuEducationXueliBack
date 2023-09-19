package com.scnujxjy.backendpoint.dao.mapper.registration_record_card;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.OriginalEducationInfoPO;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.OriginalEducationInfoVO;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 原学历信息表 Mapper 接口
 * </p>
 *
 * @author leopard
 * @since 2023-08-02
 */
public interface OriginalEducationInfoMapper extends BaseMapper<OriginalEducationInfoPO> {

    /**
     * 根据年级和身份证号码来查询学生的原学历信息
     * @param grade 年级
     * @param studentId 证件号码
     */
    @Select("SELECT * FROM original_education_info WHERE grade = #{grade} And id_number = #{studentId}")
    List<OriginalEducationInfoVO> selectInfoByGradeAndIdNumber(String grade, String studentId);
}
