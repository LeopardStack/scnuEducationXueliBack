package com.scnujxjy.backendpoint.controller.college;


import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.util.StrUtil;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.college.CollegeAdminInformationRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.college.CollegeAdminInformationVO;
import com.scnujxjy.backendpoint.service.college.CollegeAdminInformationService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Objects;

import static com.scnujxjy.backendpoint.exception.DataException.*;

/**
 * <p>
 * 教务员信息表 前端控制器
 * </p>
 *
 * @author leopard
 * @since 2023-08-02
 */
@RestController
@RequestMapping("/college-admin-information")
public class CollegeAdminInformationController {

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
        // 校验数据
        if (Objects.isNull(collegeAdminInformationVO)) {
            throw dataNotFoundError();
        }
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
        // 数据校验
        if (Objects.isNull(collegeAdminInformationROPageRO)) {
            throw dataNotFoundError();
        }
        // 返回数据
        return SaResult.data(collegeAdminInformationVOPageVO);
    }

    /**
     * 根据userId更新学院教务员信息
     *
     * @param collegeAdminInformationRO
     * @return
     */
    @PutMapping("/edit")
    public SaResult editById(CollegeAdminInformationRO collegeAdminInformationRO) {
        // 参数校验
        if (Objects.isNull(collegeAdminInformationRO) || StrUtil.isBlank(collegeAdminInformationRO.getUserId())) {
            throw dataMissError();
        }
        // 更新数据
        CollegeAdminInformationVO collegeAdminInformationVO = collegeAdminInformationService.editById(collegeAdminInformationRO);
        // 更新校验
        if (Objects.isNull(collegeAdminInformationVO)) {
            throw dataUpdateError();
        }
        // 返回数据
        return SaResult.data(collegeAdminInformationVO);
    }

    /**
     * 根据userId删除学院教务员信息
     *
     * @param userId
     * @return
     */
    @DeleteMapping("/delete")
    public SaResult deleteById(String userId) {
        // 参数校验
        if (StrUtil.isBlank(userId)) {
            throw dataMissError();
        }
        // 删除数据
        int count = collegeAdminInformationService.deleteById(userId);
        // 删除校验
        if (count <= 0) {
            throw dataDeleteError();
        }
        return SaResult.data(count);
    }

}

