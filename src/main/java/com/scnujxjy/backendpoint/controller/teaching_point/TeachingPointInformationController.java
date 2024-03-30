package com.scnujxjy.backendpoint.controller.teaching_point;


import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.util.StrUtil;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.teaching_point.TeachingPointInformationRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.teaching_point.TeachingPointInformationVO;
import com.scnujxjy.backendpoint.service.teaching_point.TeachingPointInformationService;
import com.scnujxjy.backendpoint.util.ResultCode;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Objects;

import static com.scnujxjy.backendpoint.exception.DataException.*;

/**
 * 教学点基础信息表
 *
 * @author leopard
 * @since 2023-08-02
 */
@RestController
@RequestMapping("/teaching_point_information")
public class TeachingPointInformationController {

    @Resource
    private TeachingPointInformationService teachingPointInformationService;

    /**
     * 根据teachingPointId查询教学点基础信息
     *
     * @param teachingPointId 教学点代码
     * @return 教学点基础信息
     */
    @GetMapping("/detail")
    public SaResult detailById(String teachingPointId) {
        // 参数校验
        if (StrUtil.isBlank(teachingPointId)) {
            throw dataMissError();
        }
        // 查询
        TeachingPointInformationVO teachingPointInformationVO = teachingPointInformationService.detailById(teachingPointId);
        // 数据校验
        if (Objects.isNull(teachingPointInformationVO)) {
            throw dataNotFoundError();
        }
        // 转换类型并返回
        return SaResult.data(teachingPointInformationVO);
    }


    /**
     * 查询教学点的管理人员
     *
     * @param teachingPointId 教学点代码
     * @return 教学点管理人员信息
     */
    @GetMapping("/get_teaching_point_manager_infos")
    public SaResult getTeachingPointMangerInfos(String teachingPointId) {
        // 参数校验
        if (StrUtil.isBlank(teachingPointId)) {
            return ResultCode.DATABASE_INSERT_ERROR.generateErrorResultInfo();
        }

        // 转换类型并返回
        return teachingPointInformationService.getTeachingPointMangerInfos(teachingPointId);
    }

    /**
     * 分页查询教学点基础信息
     *
     * @param teachingPointInformationROPageRO 分页查询教学点基础信息参数
     * @return 分页查询教学点基础信息数据
     */
    @PostMapping("/page")
    public SaResult pageQueryTeachingPointInformation(@RequestBody PageRO<TeachingPointInformationRO> teachingPointInformationROPageRO) {
        // 数据校验
        if (Objects.isNull(teachingPointInformationROPageRO)) {
            throw dataMissError();
        }
        if (Objects.isNull(teachingPointInformationROPageRO.getEntity())) {
            teachingPointInformationROPageRO.setEntity(new TeachingPointInformationRO());
        }
        // 列表查询 或 分页查询
        PageVO<TeachingPointInformationVO> teachingPointInformationVOPageVO = teachingPointInformationService.pageQueryTeachingPointInformation(teachingPointInformationROPageRO);
        // 数据校验
        if (Objects.isNull(teachingPointInformationVOPageVO)) {
            throw dataNotFoundError();
        }
        // 返回数据
        return SaResult.data(teachingPointInformationVOPageVO);
    }

    /**
     * 添加教学点信息
     *
     * @param teachingPointInformationRO 更新的教学点基本信息
     * @return 更新后的教学点基本信息
     */
    @PostMapping("/add")
    public SaResult addTeachingPoint(@RequestBody TeachingPointInformationRO teachingPointInformationRO) {
        // 更新数据
        return teachingPointInformationService.addTeachingPoint(teachingPointInformationRO);
    }

    /**
     * 根绝teachingPointId更新教学点基本信息
     *
     * @param teachingPointInformationRO 更新的教学点基本信息
     * @return 更新后的教学点基本信息
     */
    @PostMapping("/edit")
    public SaResult editById(@RequestBody TeachingPointInformationRO teachingPointInformationRO) {
        // 参数校验
        if (Objects.isNull(teachingPointInformationRO) || StrUtil.isBlank(teachingPointInformationRO.getTeachingPointId())) {
            throw dataMissError();
        }
        // 更新数据
        TeachingPointInformationVO teachingPointInformationVO = teachingPointInformationService.editById(teachingPointInformationRO);
        // 校验更新数据
        if (Objects.isNull(teachingPointInformationVO)) {
            throw dataUpdateError();
        }
        // 返回数据
        return SaResult.data(teachingPointInformationVO);
    }

    /**
     * 根据teachingPointId删除教学点基础信息
     *
     * @param teachingPointId 教学点id
     * @return 删除的数量
     */
    @DeleteMapping("/delete")
    public SaResult deleteById(String teachingPointId) {
        // 参数校验
        if (StrUtil.isBlank(teachingPointId)) {
            throw dataMissError();
        }
        // 删除数据
        Integer count = teachingPointInformationService.deleteById(teachingPointId);
        // 删除校验
        if (count <= 0) {
            throw dataDeleteError();
        }
        // 返回数据
        return SaResult.data(count);
    }

}

