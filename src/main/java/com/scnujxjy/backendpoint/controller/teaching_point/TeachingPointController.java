package com.scnujxjy.backendpoint.controller.teaching_point;

import cn.dev33.satoken.util.SaResult;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.StudentStatusRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
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
}
