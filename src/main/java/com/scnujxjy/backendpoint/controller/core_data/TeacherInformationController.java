package com.scnujxjy.backendpoint.controller.core_data;


import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.util.StrUtil;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.core_data.TeacherInformationRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.core_data.TeacherInformationVO;
import com.scnujxjy.backendpoint.service.core_data.TeacherInformationService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Objects;

import static com.scnujxjy.backendpoint.exception.DataException.*;

/**
 * <p>
 * 教师信息表 前端控制器
 * </p>
 *
 * @author leopard
 * @since 2023-08-02
 */
@RestController
@RequestMapping("/teacher-information")
public class TeacherInformationController {
    @Resource
    private TeacherInformationService teacherInformationService;

    @GetMapping("/detail")
    public SaResult detailById(String userId) {
        // 参数校验
        if (StrUtil.isBlank(userId)) {
            throw dataMissError();
        }
        // 数据查询
        TeacherInformationVO teacherInformationVO = teacherInformationService.detailById(userId);
        if (Objects.isNull(teacherInformationVO)) {
            throw dataNotFoundError();
        }
        return SaResult.data(teacherInformationVO);
    }

    @PostMapping("/page")
    public SaResult pageQueryTeacherInformation(@RequestBody PageRO<TeacherInformationRO> teacherInformationROPageRO) {
        // 参数校验
        if (Objects.isNull(teacherInformationROPageRO)) {
            throw dataMissError();
        }
        if (Objects.isNull(teacherInformationROPageRO.getEntity())) {
            teacherInformationROPageRO.setEntity(new TeacherInformationRO());
        }
        // 数据查询
        PageVO<TeacherInformationVO> teacherInformationVOPageVO = teacherInformationService.pageQueryTeacherInformation(teacherInformationROPageRO);
        if (Objects.isNull(teacherInformationVOPageVO)) {
            throw dataNotFoundError();
        }
        return SaResult.data(teacherInformationVOPageVO);
    }

    @PutMapping("/edit")
    public SaResult editById(@RequestBody TeacherInformationRO teacherInformationRO) {
        // 参数校验
        if (Objects.isNull(teacherInformationRO) || StrUtil.isBlank(teacherInformationRO.getUserId())) {
            throw dataMissError();
        }
        // 数据更新
        TeacherInformationVO teacherInformationVO = teacherInformationService.editById(teacherInformationRO);
        if (Objects.isNull(teacherInformationVO)) {
            throw dataUpdateError();
        }
        return SaResult.data(teacherInformationVO);
    }

    @DeleteMapping("/delete")
    public SaResult deleteById(String userId) {
        // 参数校验
        if (StrUtil.isBlank(userId)) {
            throw dataMissError();
        }
        // 数据删除
        Integer count = teacherInformationService.deleteById(userId);
        if (count <= 0) {
            throw dataDeleteError();
        }
        return SaResult.data(count);
    }
}

