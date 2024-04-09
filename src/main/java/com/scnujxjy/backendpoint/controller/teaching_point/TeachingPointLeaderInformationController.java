package com.scnujxjy.backendpoint.controller.teaching_point;


import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.util.StrUtil;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.teaching_point.TeachingPointLeaderInformationRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.teaching_point.TeachingPointLeaderInformationVO;
import com.scnujxjy.backendpoint.service.teaching_point.TeachingPointLeaderInformationService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Objects;

import static com.scnujxjy.backendpoint.exception.DataException.*;

/**
 * 教学点负责人信息表
 *
 * @author leopard
 * @since 2023-08-02
 */
@RestController
@RequestMapping("/teaching-point-leader-information")
@SaCheckPermission("平台基础信息.查询信息")
public class TeachingPointLeaderInformationController {

    @Resource
    private TeachingPointLeaderInformationService teachingPointLeaderInformationService;

    /**
     * 根据userId查询教学点负责人信息
     *
     * @param userId 用户id
     * @return 教学点负责人信息
     */
    @GetMapping("/detail")
    public SaResult detailById(String userId) {
        // 数据校验
        if (StrUtil.isBlank(userId)) {
            throw dataMissError();
        }
        // 查询数据
        TeachingPointLeaderInformationVO teachingPointLeaderInformationVO = teachingPointLeaderInformationService.detailById(userId);
        // 数据校验
        if (Objects.isNull(teachingPointLeaderInformationVO)) {
            throw dataNotFoundError();
        }
        //返回数据
        return SaResult.data(teachingPointLeaderInformationVO);
    }

    /**
     * 分页查询教学点负责人信息
     *
     * @param teachingPointLeaderInformationROPageRO 分页参数
     * @return 分页查询后的数据
     */
    @PostMapping("/page")
    public SaResult pageQueryTeachingPointLeaderInformation(@RequestBody PageRO<TeachingPointLeaderInformationRO> teachingPointLeaderInformationROPageRO) {
        // 参数校验
        if (Objects.isNull(teachingPointLeaderInformationROPageRO)) {
            throw dataMissError();
        }
        if (Objects.isNull(teachingPointLeaderInformationROPageRO.getEntity())) {
            teachingPointLeaderInformationROPageRO.setEntity(new TeachingPointLeaderInformationRO());
        }
        // 构建查询参数

        // 列表查询 或 分页查询 并返回结果
        PageVO<TeachingPointLeaderInformationVO> teachingPointLeaderInformationVOPageVO = teachingPointLeaderInformationService.pageQueryTeachingPointLeaderInformation(teachingPointLeaderInformationROPageRO);
        // 数据校验
        if (Objects.isNull(teachingPointLeaderInformationVOPageVO)) {
            throw dataNotFoundError();
        }
        // 返回数据
        return SaResult.data(teachingPointLeaderInformationVOPageVO);
    }

    /**
     * 根据userId更新教学点负责人信息
     *
     * @param teachingPointLeaderInformationRO 更新的教学点负责人信息
     * @return 更新后的教学点负责人信息
     */
    @PutMapping("/edit")
    public SaResult editById(@RequestBody TeachingPointLeaderInformationRO teachingPointLeaderInformationRO) {
        // 参数校验
        if (Objects.isNull(teachingPointLeaderInformationRO) || StrUtil.isBlank(teachingPointLeaderInformationRO.getUserId())) {
            throw dataMissError();
        }
        // 数据更新
        TeachingPointLeaderInformationVO teachingPointLeaderInformationVO = teachingPointLeaderInformationService.editById(teachingPointLeaderInformationRO);
        // 数据校验
        if (Objects.isNull(teachingPointLeaderInformationVO)) {
            throw dataUpdateError();
        }
        // 返回数据
        return SaResult.data(teachingPointLeaderInformationVO);
    }

    /**
     * 根据userId删除教学点负责人信息
     *
     * @param userId 用户id
     * @return 删除数量
     */
    @DeleteMapping("/delete")
    public SaResult deleteById(String userId) {
        // 参数校验
        if (StrUtil.isBlank(userId)) {
            throw dataMissError();
        }
        // 删除数据
        int count = teachingPointLeaderInformationService.deleteById(userId);
        // 数据校验
        if (count <= 0) {
            throw dataDeleteError();
        }
        // 返回结果
        return SaResult.data(count);
    }
}

