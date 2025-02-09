package com.scnujxjy.backendpoint.controller.courses_learning;


import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.util.SaResult;
import com.scnujxjy.backendpoint.model.ro.courses_learning.CourseRetakeRO;
import com.scnujxjy.backendpoint.model.ro.courses_learning.CourseSectionRO;
import com.scnujxjy.backendpoint.model.vo.course_learning.CourseSectionVO;
import com.scnujxjy.backendpoint.service.courses_learning.RetakeStudentsService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 重修学生的管理
 *
 * @author 谢辉龙
 * @since 2024-03-05
 */
@RestController
@RequestMapping("/retake-students")
public class RetakeStudentsController {

    @Resource
    private RetakeStudentsService retakeStudentsService;

    /**
     *  添加重修学生
     *
     * @return 课程节点信息
     */
    @PostMapping("/add_retake_students")
    @SaCheckPermission("课程学习.删除课程")
    public SaResult addRetakeStudents(@RequestBody CourseRetakeRO courseRetakeRO) {

        return retakeStudentsService.addRetakeStudents(courseRetakeRO);
    }

    /**
     *  删除重修学生
     *
     * @return 课程节点信息
     */
    @PostMapping("/delete_retake_students")
    @SaCheckPermission("课程学习.删除课程")
    public SaResult deleteRetakeStudents(@RequestBody CourseRetakeRO courseRetakeRO) {

        return retakeStudentsService.deleteRetakeStudents(courseRetakeRO);
    }
}

