package com.scnujxjy.backendpoint.controller.registration_record_card;


import cn.dev33.satoken.util.SaResult;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.StudentStatusRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.StudentStatusVO;
import com.scnujxjy.backendpoint.service.registration_record_card.StudentStatusService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Objects;

import static com.scnujxjy.backendpoint.exception.DataException.*;

/**
 * <p>
 * 学籍信息表 前端控制器
 * </p>
 *
 * @author leopard
 * @since 2023-08-04
 */
@RestController
@RequestMapping("/student-status")
public class StudentStatusController {

    @Resource
    private StudentStatusService studentStatusService;

    /**
     * 根据id查询学籍信息
     *
     * @param id 学籍信息id
     * @return 学籍信息
     */
    @GetMapping("/detail")
    public SaResult detailById(Long id) {
        // 校验参数
        if (Objects.isNull(id)) {
            throw dataMissError();
        }
        // 查询
        StudentStatusVO studentStatusVO = studentStatusService.detailById(id);
        if (Objects.isNull(studentStatusVO)) {
            throw dataNotFoundError();
        }
        return SaResult.data(studentStatusVO);
    }

    /**
     * 分页查询学籍信息
     *
     * @param studentStatusROPageRO 分页参数
     * @return 学籍信息列表
     */
    @PostMapping("/page")
    public SaResult pageQueryStudentStatus(@RequestBody PageRO<StudentStatusRO> studentStatusROPageRO) {
        // 校验参数
        if (Objects.isNull(studentStatusROPageRO)) {
            throw dataMissError();
        }
        if (Objects.isNull(studentStatusROPageRO.getEntity())) {
            studentStatusROPageRO.setEntity(new StudentStatusRO());
        }
        // 查询
        PageVO<StudentStatusVO> studentStatusVOPageVO = studentStatusService.pageQueryStudentStatus(studentStatusROPageRO);
        if (Objects.isNull(studentStatusVOPageVO)) {
            throw dataNotFoundError();
        }
        return SaResult.data(studentStatusVOPageVO);
    }

    /**
     * 更新学籍信息
     *
     * @param studentStatusRO 学籍信息
     * @return 更新后的学籍信息
     */
    @PutMapping("/edit")
    public SaResult editById(StudentStatusRO studentStatusRO) {
        // 校验参数
        if (Objects.isNull(studentStatusRO) || Objects.isNull(studentStatusRO.getId())) {
            throw dataMissError();
        }
        // 更新学籍信息
        StudentStatusVO studentStatusVO = studentStatusService.editById(studentStatusRO);
        if (Objects.isNull(studentStatusVO)) {
            throw dataUpdateError();
        }
        return SaResult.data(studentStatusVO);
    }

    /**
     * 删除学籍信息
     *
     * @param id 学籍信息id
     * @return 删除学籍信息的数量
     */
    @DeleteMapping("/delete")
    public SaResult deleteById(Long id) {
        // 校验参数
        if (Objects.isNull(id)) {
            throw dataDeleteError();
        }
        // 删除学籍信息
        int count = studentStatusService.deleteById(id);
        if (count <= 0) {
            throw dataDeleteError();
        }
        return SaResult.data(count);
    }
}

