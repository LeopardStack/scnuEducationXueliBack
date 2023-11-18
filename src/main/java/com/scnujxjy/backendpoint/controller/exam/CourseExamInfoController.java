package com.scnujxjy.backendpoint.controller.exam;


import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.exam.ExamFilterRO;
import com.scnujxjy.backendpoint.model.ro.exam.SingleSetTeachersInfoRO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.StudentStatusFilterRO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.CourseInformationSelectArgs;
import com.scnujxjy.backendpoint.service.exam.CourseExamInfoService;
import com.scnujxjy.backendpoint.service.teaching_process.CourseScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

import static com.scnujxjy.backendpoint.constant.enums.RoleEnum.*;
import static com.scnujxjy.backendpoint.exception.DataException.dataNotFoundError;

/**
 * <p>
 * 存储考试信息 前端控制器
 * </p>
 *
 * @author 谢辉龙
 * @since 2023-11-15
 */
@RestController
@RequestMapping("/course-exam-info")
@Slf4j
public class CourseExamInfoController {

    @Resource
    private CourseExamInfoService courseExamInfoService;

    /**
     * 单个课程设置为机考
     *
     * @param id 年级
     * @return
     */
    @GetMapping("/singleSetExamType/{id}")
    public SaResult getImportPhoto(@PathVariable Long id) {
        boolean b = courseExamInfoService.singleSetExamType(id);
        if(b){
            return SaResult.ok("更新考试方式成功!");
        }
        return SaResult.error("更新考试方式失败!").setCode(2001);
    }


    /**
     * 根据参数批量设置机考
     * @param examFilterROPageRO
     * @return
     */
    @PostMapping("/batch_set_exam_type")
    public SaResult batchSetExamType(@RequestBody PageRO<ExamFilterRO> examFilterROPageRO) {

        boolean b = courseExamInfoService.batchSetExamType(examFilterROPageRO.getEntity());
        return SaResult.ok("批量设置机考结果为  " + b);
    }

    /**
     * 根据参数批量取消机考
     * @param examFilterROPageRO
     * @return
     */
    @PostMapping("/batch_unset_exam_type")
    public SaResult batchUnSetExamType(@RequestBody PageRO<ExamFilterRO> examFilterROPageRO) {

        boolean b = courseExamInfoService.batchUnSetExamType(examFilterROPageRO.getEntity());
        return SaResult.ok("批量设置机考结果为  " + b);
    }

    /**
     * 单个设置命题教师和阅卷助教
     * @param singleSetTeachersInfoRO
     * @return
     */
    @PostMapping("/single_set_exam_teachers")
    public SaResult singleSetExamTeachers(@RequestBody SingleSetTeachersInfoRO singleSetTeachersInfoRO) {

        try {
            boolean b = courseExamInfoService.singleSetTeachers(singleSetTeachersInfoRO);
            if(b){
                return SaResult.ok("成功更新老师信息结果为  " + b);
            }else{
                return SaResult.error("更新教师信息失败 " + b).setCode(2001);
            }

        }catch (Exception e){
            log.error("单个更新教师信息失败 " + singleSetTeachersInfoRO + "\n" + e.toString());
            return SaResult.error("更新教师信息失败").setCode(2001);
        }
    }

    /**
     * 单个清除命题教师和阅卷助教
     * @param singleSetTeachersInfoRO
     * @return
     */
    @PostMapping("/single_delete_exam_teachers")
    public SaResult singleDeleteExamTeachers(@RequestBody SingleSetTeachersInfoRO singleSetTeachersInfoRO) {

        try {
            int b = courseExamInfoService.singleDeleteTeachers(singleSetTeachersInfoRO);
            if(b > 0){
                return SaResult.ok("成功删除老师 " + b);
            }else{
                return SaResult.ok("老师信息已删除 " + b);
            }

        }catch (Exception e){
            log.error("单个删除教师信息失败 " + singleSetTeachersInfoRO + "\n" + e.toString());
            return SaResult.error("删除教师信息失败").setCode(2001);
        }
    }
}

