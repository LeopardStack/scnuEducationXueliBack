package com.scnujxjy.backendpoint.controller.teaching_point;


import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.util.StrUtil;
import com.scnujxjy.backendpoint.dao.entity.teaching_point.TeachingPointAdminInformationPO;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.teaching_point.TeachingPointAdminInformationRO;
import com.scnujxjy.backendpoint.model.ro.teaching_point.TeachingPointInformationRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.teaching_point.TeachingPointInformationQueryArgsVO;
import com.scnujxjy.backendpoint.model.vo.teaching_point.TeachingPointInformationVO;
import com.scnujxjy.backendpoint.service.teaching_point.TeachingPointAdminInformationService;
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
@SaCheckPermission("平台基础信息.查询信息")
public class TeachingPointInformationController {

    @Resource
    private TeachingPointInformationService teachingPointInformationService;

    @Resource
    private TeachingPointAdminInformationService pointAdminInformationService;

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
            return ResultCode.PARAM_IS_NULL.generateErrorResultInfo();
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
     * 分页查询教学点基础信息的筛选项参数
     *
     * @param teachingPointInformationRO 分页查询教学点基础信息参数
     * @return 分页查询教学点基础信息数据
     */
    @PostMapping("/get_args")
    public SaResult getQueryTeachingPointInformationArgs(@RequestBody TeachingPointInformationRO teachingPointInformationRO) {
        // 数据校验
        if (Objects.isNull(teachingPointInformationRO)) {
            return ResultCode.PARAM_IS_NULL.generateErrorResultInfo();
        }
        if (Objects.isNull(teachingPointInformationRO)) {
            teachingPointInformationRO = new TeachingPointInformationRO();
        }
        // 列表查询 或 分页查询
        TeachingPointInformationQueryArgsVO teachingPointInformationQueryArgsVO = teachingPointInformationService
                .getQueryTeachingPointInformationArgs(teachingPointInformationRO);

        // 返回数据
        return SaResult.ok().setData(teachingPointInformationQueryArgsVO);
    }

    /**
     * 添加教学点信息
     *
     * @param teachingPointInformationRO 更新的教学点基本信息
     * @return 更新后的教学点基本信息
     */
    @PostMapping("/add")
    @SaCheckPermission("平台基础信息.编辑信息")
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
    @SaCheckPermission("平台基础信息.编辑信息")
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
    @SaCheckPermission("平台基础信息.编辑信息")
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


    /**
     * 根据teachingPointId添加教学点管理人员
     *
     * @param teachingPointAdminInformationRO 教学点id
     * @return 删除的数量
     */
    @PostMapping("/add_manager")
    @SaCheckPermission("平台基础信息.编辑信息")
    public SaResult addManager(@RequestBody TeachingPointAdminInformationRO teachingPointAdminInformationRO) {
        // 参数校验
        if (Objects.isNull(teachingPointAdminInformationRO)) {
            return ResultCode.PARAM_IS_NULL.generateErrorResultInfo();
        }
        // 返回数据
        return teachingPointInformationService.addManager(teachingPointAdminInformationRO);
    }


    /**
     * 根据teachingPointId userId 修改教学点管理人员
     *
     * @param teachingPointAdminInformationRO 教学点id userId
     * @return 删除的数量
     */
    @PostMapping("/update_manager")
    @SaCheckPermission("平台基础信息.编辑信息")
    public SaResult updateManager(@RequestBody TeachingPointAdminInformationRO teachingPointAdminInformationRO) {
        // 参数校验
        if (Objects.isNull(teachingPointAdminInformationRO)) {
            return ResultCode.PARAM_IS_NULL.generateErrorResultInfo();
        }
        // 返回数据
        return teachingPointInformationService.updateManager(teachingPointAdminInformationRO);
    }


    /**
     * 根据teachingPointId userId 修改教学点管理人员
     *
     * @param teachingPointAdminInformationRO 教学点id userId
     * @return 删除的数量
     */
    @DeleteMapping("/delete_manager")
    @SaCheckPermission("平台基础信息.编辑信息")
    public SaResult deleteManager(@RequestBody TeachingPointAdminInformationRO teachingPointAdminInformationRO) {
        // 参数校验
        if (Objects.isNull(teachingPointAdminInformationRO)) {
            return ResultCode.PARAM_IS_NULL.generateErrorResultInfo();
        }
        // 返回数据
        return teachingPointInformationService.deleteManager(teachingPointAdminInformationRO);
    }

}

