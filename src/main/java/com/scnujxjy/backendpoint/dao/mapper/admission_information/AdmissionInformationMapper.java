package com.scnujxjy.backendpoint.dao.mapper.admission_information;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scnujxjy.backendpoint.dao.entity.admission_information.AdmissionInformationPO;
import com.scnujxjy.backendpoint.model.ro.admission_information.AdmissionInformationRO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.StudentStatusFilterRO;
import com.scnujxjy.backendpoint.model.vo.admission_information.AdmissionInformationVO;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.PersonalInfoVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 录取学生信息表 Mapper 接口
 * </p>
 *
 * @author leopard
 * @since 2023-08-02
 */
public interface AdmissionInformationMapper extends BaseMapper<AdmissionInformationPO> {
    /**
     * 根据年级和身份证号码来查询学生的个人信息
     * @param grade 年级
     * @param studentId 证件号码
     */
    @Select("SELECT * FROM admission_information WHERE grade = #{grade} And id_card_number = #{studentId}")
    List<AdmissionInformationPO> selectInfoByGradeAndIdNumber(String grade, String studentId);

    List<AdmissionInformationVO> getAdmissionInformationByAllRoles(@Param("entity")AdmissionInformationRO entity,
                                                                   @Param("pageNumber")Long pageNumber, @Param("pageSize")Long pageSize);

    long getAdmissionInformationByAllRolesCount(@Param("entity")AdmissionInformationRO entity);

    List<String> getDistinctGrades(@Param("entity") AdmissionInformationRO entity);

    List<String> getDistinctCollegeNames(@Param("entity")AdmissionInformationRO admissionInformationRO);

    List<String> getDistinctMajorNames(@Param("entity")AdmissionInformationRO admissionInformationRO);

    List<String> getDistinctLevels(@Param("entity")AdmissionInformationRO admissionInformationRO);

    List<String> getDistinctStudyForms(@Param("entity")AdmissionInformationRO admissionInformationRO);

    List<String> getDistinctTeachingPoints(@Param("entity")AdmissionInformationRO admissionInformationRO);

    List<AdmissionInformationVO> batchSelectData(@Param("entity")AdmissionInformationRO entity);

    AdmissionInformationVO selectSingleAdmissionInfo(@Param("idNumber")String idNumber, @Param("admissionYear")String admissionYear);
}
