package com.scnujxjy.backendpoint.controller.courses_learning;

import cn.dev33.satoken.util.SaResult;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.scnujxjy.backendpoint.dao.entity.core_data.TeacherInformationPO;
import com.scnujxjy.backendpoint.model.ro.core_data.TeacherInformationRequest;
import com.scnujxjy.backendpoint.service.teaching_process.TeacherInformationNewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Objects;

@RestController
@Slf4j
@RequestMapping("/teacherInformation")
public class TeacherInformationNewController {

    @Resource
    private TeacherInformationNewService teacherInformationNewService;

    @RequestMapping(value = "/query", method = RequestMethod.POST)
    public SaResult query(@RequestBody TeacherInformationRequest teacherInformationRequest) {
        if (Objects.isNull(teacherInformationRequest.getPage()) || Objects.isNull(teacherInformationRequest.getPageSize())) {
            return SaResult.error("必要参数缺失");
        }
        log.info("queryTeacherInformation req param: {}", JSON.toJSONString(teacherInformationRequest));
        return teacherInformationNewService.queryTeacherInformation(teacherInformationRequest);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public SaResult add(@RequestBody TeacherInformationPO teacherInformationPO) {
        if (Objects.isNull(teacherInformationPO) || StringUtils.isBlank(teacherInformationPO.getName())){
            return SaResult.error("必要参数缺失");
        }
        log.info("addTeacherInformation req param: {}", JSON.toJSONString(teacherInformationPO));
        return teacherInformationNewService.addTeacherInformation(teacherInformationPO);
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public SaResult update(@RequestBody TeacherInformationPO teacherInformationPO) {
        if (Objects.isNull(teacherInformationPO) || Objects.isNull(teacherInformationPO.getUserId())){
            return SaResult.error("必要参数缺失");
        }
        log.info("updateTeacherInformation req param: {}", JSON.toJSONString(teacherInformationPO));
        return teacherInformationNewService.updateTeacherInformation(teacherInformationPO);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public SaResult delete(@RequestBody TeacherInformationRequest teacherInformationRequest) {
        if (Objects.isNull(teacherInformationRequest) || Objects.isNull(teacherInformationRequest.getUserId())){
            return SaResult.error("必要参数缺失");
        }
        log.info("deleteTeacherInformation req param: {}", JSON.toJSONString(teacherInformationRequest));
        return teacherInformationNewService.delteteTeacherInformation(teacherInformationRequest);
    }
}
