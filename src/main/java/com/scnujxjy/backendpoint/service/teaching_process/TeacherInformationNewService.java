package com.scnujxjy.backendpoint.service.teaching_process;

import cn.dev33.satoken.util.SaResult;
import com.scnujxjy.backendpoint.dao.entity.core_data.TeacherInformationPO;
import com.scnujxjy.backendpoint.model.ro.core_data.TeacherInformationRequest;

public interface TeacherInformationNewService {
    SaResult queryTeacherInformation(TeacherInformationRequest teacherInformationRequest);

    SaResult updateTeacherInformation(TeacherInformationPO teacherInformationPO);

    SaResult addTeacherInformation(TeacherInformationPO teacherInformationPO);

    SaResult delteteTeacherInformation(TeacherInformationRequest teacherInformationRequest);
}
