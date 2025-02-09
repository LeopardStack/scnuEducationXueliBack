package com.scnujxjy.backendpoint.dao.mapper.admission_information;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scnujxjy.backendpoint.dao.entity.admission_information.AdmissionInformationPO;
import com.scnujxjy.backendpoint.model.ro.admission_information.AdmissionInformationRO;
import com.scnujxjy.backendpoint.model.ro.platform_message.NewStudentRO;
import com.scnujxjy.backendpoint.model.vo.admission_information.AdmissionInformationVO;
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

    /**
     * 获取公告发布的新生群体信息
     * @return
     */
    List<AdmissionInformationPO> getAdmissionInformationByAnnouncementMsg(@Param("entity") NewStudentRO entity,
                                                                          @Param("pageNumber")Long pageNumber, @Param("pageSize")Long pageSize);

    Long getAdmissionInformationByAnnouncementMsgCount(@Param("entity") NewStudentRO newStudentRO);

    @Select("select distinct college from admission_information;")
    List<String> selectDistinctCollegeList();

    @Select("select distinct grade from admission_information;")
    List<String> selectDistinctGradeList();

    @Select("select distinct major_name from admission_information;")
    List<String> selectDistinctMajorNameList();

    @Select("select distinct level from admission_information;")
    List<String> selectDistinctLevelList();

    @Select("select distinct study_form from admission_information;")
    List<String> selectDistinctStudyFormList();

    @Select("select distinct teaching_point from admission_information;")
    List<String> selectDistinctTeachingPointNameList();

    List<AdmissionInformationPO> getAllAdmissionInformationByAnnouncementMsg(@Param("entity") NewStudentRO newStudentRO);

    List<com.scnujxjy.backendpoint.model.vo.platform_message.AdmissionInformationVO > getAllAdmissionInformationByAnnouncementMsgVO(@Param("entity") NewStudentRO newStudentRO);
}
