package com.scnujxjy.backendpoint.controller.college;


import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.util.StrUtil;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.college.CollegeAdminInformationRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.college.CollegeAdminInformationVO;
import com.scnujxjy.backendpoint.service.college.CollegeAdminInformationService;
import com.scnujxjy.backendpoint.service.college.CollegeInformationService;
import com.scnujxjy.backendpoint.util.ResultCode;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Objects;

import static com.scnujxjy.backendpoint.exception.DataException.*;

/**
 * 教务员信息表
 *
 * @author leopard
 * @since 2023-08-02
 */
@RestController
@RequestMapping("/college_admin_information")
@SaCheckPermission("平台基础信息.查询信息")
public class CollegeAdminInformationController {

    @Resource
    private CollegeInformationService collegeInformationService;

    @Resource
    private CollegeAdminInformationService collegeAdminInformationService;

    /**
     * 通过userId查询教务员信息
     *
     * @param userId 用户id
     * @return 教务员信息
     */
    @GetMapping("/detail")
    public SaResult detailById(String userId) {
        // 参数校验
        if (StrUtil.isBlank(userId)) {
            throw dataMissError();
        }
        // 查询数据
        CollegeAdminInformationVO collegeAdminInformationVO = collegeAdminInformationService.detailById(userId);

        // 返回数据
        return SaResult.data(collegeAdminInformationVO);
    }

    /**
     * 分页查询学院教务员信息
     *
     * @param collegeAdminInformationROPageRO 学院教务员分页查询参数
     * @return 学院教务员信息分页
     */
    @PostMapping("/page")
    public SaResult pageQueryCollegeAdminInformation(@RequestBody PageRO<CollegeAdminInformationRO> collegeAdminInformationROPageRO) {
        // 参数校验
        if (Objects.isNull(collegeAdminInformationROPageRO)) {
            throw dataMissError();
        }
        if (Objects.isNull(collegeAdminInformationROPageRO.getEntity())) {
            collegeAdminInformationROPageRO.setEntity(new CollegeAdminInformationRO());
        }
        // 查询数据
        PageVO<CollegeAdminInformationVO> collegeAdminInformationVOPageVO = collegeAdminInformationService.pageQueryCollegeAdminInformation(collegeAdminInformationROPageRO);

        // 返回数据
        return SaResult.data(collegeAdminInformationVOPageVO);
    }

    /**
     * 为指定学院添加新的教务员
     *
     * @param collegeAdminInformationRO
     * @return
     */
    @PostMapping("/add")
    @SaCheckPermission("平台基础信息.编辑信息")
    public SaResult addNewManager(@RequestBody CollegeAdminInformationRO collegeAdminInformationRO) {
        // 参数校验
        if (Objects.isNull(collegeAdminInformationRO)) {
            return ResultCode.PARAM_IS_NULL.generateErrorResultInfo();
        }
        // 返回数据
        return collegeInformationService.addNewManager(collegeAdminInformationRO);
    }

    /**
     * 根据userId更新学院教务员信息
     *
     * @param collegeAdminInformationRO
     * @return
     */
    @PostMapping("/edit")
    @SaCheckPermission("平台基础信息.编辑信息")
    public SaResult editById(@RequestBody CollegeAdminInformationRO collegeAdminInformationRO) {
        // 参数校验
        if (Objects.isNull(collegeAdminInformationRO)) {
            return ResultCode.PARAM_IS_NULL.generateErrorResultInfo();
        }

        // 返回数据
        return collegeInformationService.editById(collegeAdminInformationRO);
    }

    /**
     * 根据userId删除学院教务员信息
     *
     * @param userId
     * @return
     */
    @DeleteMapping("/delete")
    @SaCheckPermission("平台基础信息.编辑信息")
    public SaResult deleteById(String userId) {
        // 参数校验
        if (StrUtil.isBlank(userId)) {
            return ResultCode.PARAM_IS_NULL.generateErrorResultInfo();
        }
        return collegeInformationService.deleteCollegeAdminInfo(userId);
    }

}

