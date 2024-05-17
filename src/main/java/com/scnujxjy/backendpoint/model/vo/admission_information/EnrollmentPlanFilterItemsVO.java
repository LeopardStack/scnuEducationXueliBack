package com.scnujxjy.backendpoint.model.vo.admission_information;

import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = false)
@Builder
public class EnrollmentPlanFilterItemsVO {
    List<String> yearList;
    List<String> majorNameList;
    List<String> studyFormList;
    List<String> trainingLevelList;
    List<String> collegeList;
    List<String> teachingPointNameList;
}
