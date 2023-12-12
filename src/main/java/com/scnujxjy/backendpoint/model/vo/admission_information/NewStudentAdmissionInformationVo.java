package com.scnujxjy.backendpoint.model.vo.admission_information;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewStudentAdmissionInformationVo extends AdmissionInformationVO{
    private String admissionsSubjectCategorie;

    private String contactNumber;

    private Long teachingPointId;

//    private Long majorCode;

    private String admissionCollege;

    @Override
    public String toString() {
        return super.toString()+ "NewStudentAdmissionInformationVo{" +
                "admissionsSubjectCategorie='" + admissionsSubjectCategorie + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                ", teachingPointId=" + teachingPointId +
                ", admissionCollege='" + admissionCollege + '\'' +
                '}';
    }
}
