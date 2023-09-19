package com.scnujxjy.backendpoint.model.vo.registration_record_card;

import com.scnujxjy.backendpoint.model.vo.admission_information.AdmissionInformationVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 学生的学籍全部信息
 * 展示学生各个年级的学籍信息、照片信息、毕业信息、学位信息
 * @author leopard
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class StudentAllStatusInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 学籍信息
     */
    private StudentStatusVO studentStatusVO;
    /**
     * 个人信息
     */
    private PersonalInfoVO personalInfoVO;
    /**
     * 原学历信息
     */
    private OriginalEducationInfoVO originalEducationInfoVO;
    /**
     * 毕业信息
     */
    private GraduationInfoVO graduationInfoVO;

    /**
     * 学位信息
     */
    private DegreeInfoVO degreeInfoVO;

    /**
     * 新生录取信息
     */
    private AdmissionInformationVO admissionInformationVO;

    /**
     * 班级信息
     */
    private ClassInformationVO classInformationVO;
}
