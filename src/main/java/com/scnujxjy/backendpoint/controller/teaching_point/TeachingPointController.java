package com.scnujxjy.backendpoint.controller.teaching_point;

import cn.dev33.satoken.util.SaResult;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.core_data.PaymentInfoFilterRO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.StudentStatusRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseInformationRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseScheduleFilterRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.core_data.PaymentInfoVO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.CourseInformationVO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.SchedulesVO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.ScoreInformationVO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.StudentStatusAllVO;
import com.scnujxjy.backendpoint.util.filter.TeachingPointFilter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Objects;

import static com.scnujxjy.backendpoint.exception.DataException.dataMissError;
import static com.scnujxjy.backendpoint.exception.DataException.dataNotFoundError;

/**
 * 教学点教务员信息表
 *
 * @author liweitang
 * @since 2023-11-08
 */
@RestController
@RequestMapping("/teaching-point")
public class TeachingPointController {
    @Resource
    private TeachingPointFilter teachingPointFilter;

    /**
     * 根据教学点条件分页查询学生成绩
     *
     * @param studentStatusROPageRO 分页条件查询参数
     * @return 查询结果
     */
    @PostMapping("/detail-student-score-data")
    public SaResult selectTeachingPointStudentScore(@RequestBody PageRO<StudentStatusRO> studentStatusROPageRO) {
        if (Objects.isNull(studentStatusROPageRO)) {
            throw dataMissError();
        }
        PageVO<ScoreInformationVO> scoreInformationVOPageVO = teachingPointFilter.selectTeachingPointStudentScoreInformation(studentStatusROPageRO);
        if (Objects.isNull(scoreInformationVOPageVO)) {
            throw dataNotFoundError();
        }
        return SaResult.data(scoreInformationVOPageVO);
    }

    /**
     * 根据教学点查询学生的学籍信息；
     * 可以进行条件分页查询；
     *
     * @param studentStatusROPageRO 条件分页查询条件参数
     * @return 教学点的学生学籍信息
     */
    @PostMapping("/detail-student-all-status")
    public SaResult selectTeachingPointStudentAllStatus(@RequestBody PageRO<StudentStatusRO> studentStatusROPageRO) {
        if (Objects.isNull(studentStatusROPageRO)) {
            throw dataMissError();
        }
        PageVO<StudentStatusAllVO> studentStatusAllVOPageVO = teachingPointFilter.selectTeachingPointStudentAllStatus(studentStatusROPageRO);
        if (Objects.isNull(studentStatusAllVOPageVO)) {
            throw dataNotFoundError();
        }
        return SaResult.data(studentStatusAllVOPageVO);
    }

    /**
     * 分页条件查询教学点学生的缴费信息
     *
     * @param paymentInfoROPageRO 分页条件查询参数
     * @return 分页条件查询教学点缴费信息结果
     */
    @PostMapping("/detail-student-pay-information")
    public SaResult selectTeachingPointStudentPayInformation(@RequestBody PageRO<PaymentInfoFilterRO> paymentInfoROPageRO) {
        if (Objects.isNull(paymentInfoROPageRO)) {
            throw dataMissError();
        }
        PageVO<PaymentInfoVO> paymentInfoVOPageVO = teachingPointFilter.selectTeachingPointPaymentInformation(paymentInfoROPageRO);
        if (Objects.isNull(paymentInfoVOPageVO)) {
            throw dataNotFoundError();
        }
        return SaResult.data(paymentInfoVOPageVO);
    }

    /**
     * 分页条件查询教学点课程信息
     *
     * @param courseInformationROPageRO 分页条件查询参数
     * @return 分页条件查询教学点课程信息结果
     */
    @PostMapping("/detail-course-information")
    public SaResult selectTeachingPointCourseInformation(@RequestBody PageRO<CourseInformationRO> courseInformationROPageRO) {
        if (Objects.isNull(courseInformationROPageRO)) {
            throw dataMissError();
        }
        PageVO<CourseInformationVO> courseInformationVOPageVO = teachingPointFilter.selectTeachingPointCourseInformation(courseInformationROPageRO);
        if (Objects.isNull(courseInformationVOPageVO)) {
            throw dataNotFoundError();
        }
        return SaResult.data(courseInformationVOPageVO);
    }

    /**
     * 分页条件查询教学点课程表
     *
     * @param courseScheduleFilterROPageRO 分页条件查询参数
     * @return 分页条件查询教学点课程表结果
     */
    @PostMapping("/detail-course-schedule")
    public SaResult selectTeachingPointCourseSchedule(@RequestBody PageRO<CourseScheduleFilterRO> courseScheduleFilterROPageRO) {
        if (Objects.isNull(courseScheduleFilterROPageRO)) {
            throw dataMissError();
        }
        PageVO<SchedulesVO> schedulesVOPageVO = teachingPointFilter.selectTeachingPointCourseSchedule(courseScheduleFilterROPageRO);
        if (Objects.isNull(schedulesVOPageVO)) {
            throw dataNotFoundError();
        }
        return SaResult.data(schedulesVOPageVO);
    }
}
