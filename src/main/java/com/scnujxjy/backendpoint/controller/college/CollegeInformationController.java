package com.scnujxjy.backendpoint.controller.college;


import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.util.StrUtil;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.college.CollegeInformationRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.college.CollegeInformationVO;
import com.scnujxjy.backendpoint.service.college.CollegeInformationService;
import com.scnujxjy.backendpoint.util.ResultCode;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Objects;

import static com.scnujxjy.backendpoint.exception.DataException.*;

/**
 * 学院基础信息表
 *
 * @author leopard
 * @since 2023-08-02
 */
@RestController
@RequestMapping("/college_information")
public class CollegeInformationController {

    @Resource
    private CollegeInformationService collegeInformationService;

    /**
     * 根据collegeId查询学院信息
     *
     * @param collegeId 学院id
     * @return 学院信息
     */
    @GetMapping("/detail")
    public SaResult detailById(String collegeId) {
        // 参数校验
        if (StrUtil.isBlank(collegeId)) {
            throw dataMissError();
        }
        // 查询数据
        CollegeInformationVO collegeInformationVO = collegeInformationService.detailById(collegeId);
        // 数据校验
        if (Objects.isNull(collegeInformationVO)) {
            throw dataNotFoundError();
        }
        // 返回数据
        return SaResult.data(collegeInformationVO);
    }

    /**
     * 分页查询学院信息
     *
     * @param collegeInformationROPageRO 分页查询信息
     * @return 学院分页查询信息
     */
    @PostMapping("/page")
    public SaResult pageQueryCollegeInformation(@RequestBody PageRO<CollegeInformationRO> collegeInformationROPageRO) {
        // 参数校验
        if (Objects.isNull(collegeInformationROPageRO)) {
            throw dataMissError();
        }
        if (Objects.isNull(collegeInformationROPageRO.getEntity())) {
            collegeInformationROPageRO.setEntity(new CollegeInformationRO());
        }
        // 查询数据
        PageVO<CollegeInformationVO> collegeInformationVOPageVO = collegeInformationService.pageQueryCollegeInformation(collegeInformationROPageRO);
        // 数据校验
        if (Objects.isNull(collegeInformationVOPageVO)) {
            throw dataNotFoundError();
        }
        // 数据返回
        return SaResult.data(collegeInformationVOPageVO);
    }

    /**
     * 查询二级学院的管理员信息
     *
     * @param collegeInformationRO 查询二级学院管理员的参数信息
     * @return 二级学院管理员信息
     */
    @PostMapping("/query_college_admin_info")
    public SaResult queryCollegeAdminInfo(@RequestBody CollegeInformationRO collegeInformationRO) {
        // 参数校验
        if (Objects.isNull(collegeInformationRO)) {
            return ResultCode.PARAM_IS_NULL.generateErrorResultInfo();
        }


        // 数据返回
        return collegeInformationService.queryCollegeAdminInfo(collegeInformationRO);
    }

    /**
     * 添加单个二级学院信息
     *
     * @param collegeInformationRO 添加学院参数
     * @return 添加结果
     */
    @PostMapping("/add")
    public SaResult addCollegeInfo(@RequestBody CollegeInformationRO collegeInformationRO) {
        // 参数校验
        if (Objects.isNull(collegeInformationRO)) {
            return ResultCode.PARAM_IS_NULL.generateErrorResultInfo();
        }


        // 数据返回
        return collegeInformationService.addCollegeInfo(collegeInformationRO);
    }

    /**
     * 根据collegeId更新学院信息
     *
     * @param collegeInformationRO 更新的学院信息
     * @return 更新后的学院信息
     */
    @PutMapping("/edit")
    public SaResult editById(@RequestBody CollegeInformationRO collegeInformationRO) {
        // 参数校验
        if (Objects.isNull(collegeInformationRO) || StrUtil.isBlank(collegeInformationRO.getCollegeId())) {
            return ResultCode.PARAM_IS_NULL.generateErrorResultInfo();
        }
        // 更新数据
        CollegeInformationVO collegeInformationVO = collegeInformationService.editById(collegeInformationRO);
        // 校验更新结果
        if (Objects.isNull(collegeInformationVO)) {
            throw dataUpdateError();
        }
        // 返回数据
        return SaResult.data(collegeInformationVO);
    }

    /**
     * 根据collegeId删除学院信息
     *
     * @param collegeId 学院id
     * @return 删除的数量
     */
    @DeleteMapping("/delete")
    public SaResult deleteById(String collegeId) {
        // 参数校验
        if (StrUtil.isBlank(collegeId)) {
            return ResultCode.PARAM_IS_NULL.generateErrorResultInfo();
        }
        // 删除数据
        Integer count = collegeInformationService.deleteById(collegeId);
        if(count  == 0){
            return ResultCode.DATABASE_DELETE_ERROR.generateErrorResultInfo();
        }else if(count < 0){
            return ResultCode.DATABASE_DELETE_ERROR2.generateErrorResultInfo();
        }else{
            return SaResult.ok("删除成功");
        }
    }

}

