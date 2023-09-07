package com.scnujxjy.backendpoint.controller.core_data;


import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.core_data.TeacherInformationRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.core_data.TeacherInformationVO;
import com.scnujxjy.backendpoint.service.core_data.TeacherInformationService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

import static com.scnujxjy.backendpoint.exception.DataException.*;

/**
 * 教师信息表
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
    public SaResult detailById(int userId) {
        // 参数校验
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
        if (Objects.isNull(teacherInformationRO)) {
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

    @PostMapping("/excel/import")
    public SaResult ExcelImportTeacherInformation(MultipartFile file) {
        if (Objects.isNull(file) || file.isEmpty()) {
            throw dataMissError();
        }
        List<TeacherInformationVO> teacherInformationVOS = teacherInformationService.excelImportTeacherInformation(file);
        if (CollUtil.isEmpty(teacherInformationVOS)) {
            return SaResult.error("错误解析");
        }
        return SaResult.data(teacherInformationVOS);
    }
}

