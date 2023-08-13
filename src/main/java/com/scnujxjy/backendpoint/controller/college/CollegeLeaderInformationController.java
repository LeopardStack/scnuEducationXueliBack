package com.scnujxjy.backendpoint.controller.college;


import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.util.StrUtil;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.college.CollegeLeaderInformationRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.college.CollegeLeaderInformationVO;
import com.scnujxjy.backendpoint.service.college.CollegeLeaderInformationService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Objects;

import static com.scnujxjy.backendpoint.exception.DataException.*;

/**
 * 负责人信息表
 *
 * @author leopard
 * @since 2023-08-02
 */
@RestController
@RequestMapping("/college-leader-information")
public class CollegeLeaderInformationController {

    @Resource
    private CollegeLeaderInformationService collegeLeaderInformationService;

    /**
     * 根据userId查询负责人信息
     *
     * @param userId 用户id
     * @return 负责人信息
     */
    @GetMapping("/detail")
    public SaResult detailById(String userId) {
        // 参数校验
        if (StrUtil.isBlank(userId)) {
            throw dataMissError();
        }
        // 查询数据
        CollegeLeaderInformationVO collegeLeaderInformationVO = collegeLeaderInformationService.detailById(userId);
        // 数据校验
        if (Objects.isNull(collegeLeaderInformationVO)) {
            throw dataNotFoundError();
        }
        // 转换数据并返回结果
        return SaResult.data(collegeLeaderInformationVO);
    }

    /**
     * 分页查询负责人信息
     *
     * @param collegeLeaderInformationROPageRO 负责人信息分页查询参数
     * @return 负责人信息分页查询结果
     */
    @PostMapping("/page")
    public SaResult pageQueryCollegeLeaderInformation(@RequestBody PageRO<CollegeLeaderInformationRO> collegeLeaderInformationROPageRO) {
        // 参数校验
        if (Objects.isNull(collegeLeaderInformationROPageRO)) {
            throw dataMissError();
        }
        // 查询数据
        PageVO<CollegeLeaderInformationVO> collegeLeaderInformationVOPageVO = collegeLeaderInformationService.pageQueryCollegeLeaderInformation(collegeLeaderInformationROPageRO);
        // 数据校验
        if (Objects.isNull(collegeLeaderInformationVOPageVO)) {
            throw dataNotFoundError();
        }
        // 返回数据
        return SaResult.data(collegeLeaderInformationVOPageVO);
    }

    /**
     * 根据userId更新负责人信息
     *
     * @param collegeLeaderInformationRO 更新的负责人信息
     * @return 更新后的负责人信息
     */
    @PutMapping("/edit")
    public SaResult editById(@RequestBody CollegeLeaderInformationRO collegeLeaderInformationRO) {
        // 参数校验
        if (Objects.isNull(collegeLeaderInformationRO) || StrUtil.isBlank(collegeLeaderInformationRO.getUserId())) {
            throw dataMissError();
        }
        // 更新数据
        CollegeLeaderInformationVO collegeLeaderInformationVO = collegeLeaderInformationService.editById(collegeLeaderInformationRO);
        // 更新后校验
        if (Objects.isNull(collegeLeaderInformationVO)) {
            throw dataUpdateError();
        }
        // 返回数据
        return SaResult.data(collegeLeaderInformationVO);
    }

    /**
     * 根据userId删除负责人信息
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
        Integer count = collegeLeaderInformationService.deleteById(userId);
        // 删除后校验
        if (count <= 0) {
            throw dataDeleteError();
        }
        // 返回数据
        return SaResult.data(count);
    }
}

