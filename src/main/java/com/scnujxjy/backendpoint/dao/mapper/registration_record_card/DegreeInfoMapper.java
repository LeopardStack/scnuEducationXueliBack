package com.scnujxjy.backendpoint.dao.mapper.registration_record_card;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.DegreeInfoPO;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.DegreeInfoVO;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 学位信息表 Mapper 接口
 * </p>
 *
 * @author leopard
 * @since 2023-08-04
 */
public interface DegreeInfoMapper extends BaseMapper<DegreeInfoPO> {

    /**
     * 根据年级和身份证号码来查询学生的毕业信息（查询学历教育的学生的学位信息 而不是自考的）
     * @param grade 年级
     * @param studentId 证件号码
     */
    @Select("SELECT * FROM degree_info WHERE student_number = #{studentId}")
    List<DegreeInfoVO> selectInfoByGradeAndIdNumber(String studentId);
}
