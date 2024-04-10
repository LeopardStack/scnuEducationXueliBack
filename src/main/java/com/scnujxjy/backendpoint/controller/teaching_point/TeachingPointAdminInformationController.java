package com.scnujxjy.backendpoint.controller.teaching_point;


import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.util.StrUtil;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.teaching_point.TeachingPointAdminInformationRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.teaching_point.TeachingPointAdminInformationVO;
import com.scnujxjy.backendpoint.service.teaching_point.TeachingPointAdminInformationService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Objects;

import static com.scnujxjy.backendpoint.exception.DataException.*;

/**
 * 教学点教务员信息表
 *
 * @author leopard
 * @since 2023-08-02
 */
@RestController
@RequestMapping("/teaching-point-admin-information")
@SaCheckPermission("平台基础信息.查询信息")
public class TeachingPointAdminInformationController {

    @Resource
    private TeachingPointAdminInformationService teachingPointAdminInformationService;

    /**
     * 根据userId查询教学点教务员信息
     *
     * @param userId
     * @return
     */
    @GetMapping("/detail")
    public SaResult detailById(String userId) {
        // 参数校验
        if (StrUtil.isBlank(userId)) {
            throw dataMissError();
        }
        // 查询数据
        TeachingPointAdminInformationVO teachingPointAdminInformationVO = teachingPointAdminInformationService.detailById(userId);

        // 返回数据
        return SaResult.data(teachingPointAdminInformationVO);
    }

    /**
     * 分页查询教学点教务员信息
     *
     * @param teachingPointAdminInformationROPageRO 教学点教务员分页查询参数
     * @return 教学点教务员分页信息
     */
    @PostMapping("/page")
    public SaResult pageQueryTeachingPointAdminInformation(@RequestBody PageRO<TeachingPointAdminInformationRO> teachingPointAdminInformationROPageRO) {
        // 参数校验
        if (Objects.isNull(teachingPointAdminInformationROPageRO)) {
            throw dataMissError();
        }
        if (Objects.isNull(teachingPointAdminInformationROPageRO.getEntity())) {
            teachingPointAdminInformationROPageRO.setEntity(new TeachingPointAdminInformationRO());
        }
        // 查询
        PageVO<TeachingPointAdminInformationVO> teachingPointAdminInformationVOPageVO = teachingPointAdminInformationService.pageQueryTeachingPointAdminInformation(teachingPointAdminInformationROPageRO);

        // 返回数据
        return SaResult.data(teachingPointAdminInformationVOPageVO);
    }

    /**
     * 根据userId更新教学点教务员信息
     *
     * @param teachingPointAdminInformationRO 更新的教学点教务员信息
     * @return 更新后的教学点教务员信息
     */
    @PutMapping("/edit")
    public SaResult editById(@RequestBody TeachingPointAdminInformationRO teachingPointAdminInformationRO) {
        // 参数校验
        if (Objects.isNull(teachingPointAdminInformationRO) || StrUtil.isBlank(teachingPointAdminInformationRO.getUserId())) {
            throw dataMissError();
        }

        // 更新数据
        TeachingPointAdminInformationVO teachingPointAdminInformationVO = teachingPointAdminInformationService.editById(teachingPointAdminInformationRO);
        // 更新校验
        if (Objects.isNull(teachingPointAdminInformationVO)) {
            throw dataUpdateError();
        }
        // 返回数据
        return SaResult.data(teachingPointAdminInformationVO);
    }

    /**
     * 根据userId删除教学点教务员信息
     *
     * @param userId 用户id
     * @return 删除的数量
     */
    @DeleteMapping("/delete")
    public SaResult deleteById(String userId) {
        // 参数校验
        if (StrUtil.isBlank(userId)) {
            throw dataMissError();
        }
        // 删除数据
        int count = teachingPointAdminInformationService.deleteById(userId);
        // 删除参数校验
        if (count <= 0) {
            throw dataDeleteError();
        }
        // 返回数据
        return SaResult.data(count);
    }

}

