package com.scnujxjy.backendpoint.dao.mapper.registration_record_card;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.PersonalInfoPO;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.PersonalInfoVO;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 * 个人基本信息表 Mapper 接口
 * </p>
 *
 * @author leopard
 * @since 2023-08-04
 */
public interface PersonalInfoMapper extends BaseMapper<PersonalInfoPO> {

    /**
     * 根据年级和身份证号码来查询学生的个人信息
     * @param grade 年级
     * @param studentId 证件号码
     */
    @Select("SELECT * FROM personal_info WHERE grade = #{grade} And id_number = #{studentId}")
    List<PersonalInfoVO> selectInfoByGradeAndIdNumber(String grade, String studentId);

    /**
     * 根据年级和身份证号码来查询学生的个人信息
     * @param grade 年级
     * @param studentId 证件号码
     */
    @Select("SELECT * FROM personal_info WHERE grade = #{grade} And id_number = #{studentId}")
    PersonalInfoVO selectInfoByGradeAndIdNumberOne(String grade, String studentId);


    /**
     * 根据年级和身份证号码来更新学生的所有个人信息
     * @param personalInfoPO 学生的个人信息
     */
    @Update("UPDATE personal_info SET " +
            "gender = #{gender}, " +
            "birth_date = #{birthDate}, " +
            "political_status = #{politicalStatus}, " +
            "ethnicity = #{ethnicity}, " +
            "native_place = #{nativePlace}, " +
            "id_type = #{idType}, " +
            "postal_code = #{postalCode}, " +
            "phone_number = #{phoneNumber}, " +
            "email = #{email}, " +
            "address = #{address}, " +
            "entrance_photo = #{entrancePhoto}, " +
            "is_disabled = #{isDisabled}, " +
            "grade = #{grade}, " +
            "name = #{name} " +
            "WHERE grade = #{grade} AND id_number = #{idNumber}")
    void updateAllInfoByGradeAndIdNumber(PersonalInfoPO personalInfoPO);

}
