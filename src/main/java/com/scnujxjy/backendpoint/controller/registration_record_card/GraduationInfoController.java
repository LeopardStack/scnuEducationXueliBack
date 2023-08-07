package com.scnujxjy.backendpoint.controller.registration_record_card;


import cn.dev33.satoken.util.SaResult;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.GraduationInfoRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.GraduationInfoVO;
import com.scnujxjy.backendpoint.service.registration_record_card.GraduationInfoService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Objects;

import static com.scnujxjy.backendpoint.exception.DataException.*;

/**
 * <p>
 * 毕业信息表 前端控制器
 * </p>
 *
 * @author leopard
 * @since 2023-08-04
 */
@RestController
@RequestMapping("/graduation-info")
public class GraduationInfoController {

    @Resource
    private GraduationInfoService graduationInfoService;

    /**
     * 根据id查询毕业信息
     *
     * @param id 毕业信息id
     * @return 毕业信息
     */
    @GetMapping("/detail")
    public SaResult detailById(Long id) {
        // 参数校验
        if (Objects.isNull(id)) {
            throw dataMissError();
        }
        // 查询数据
        GraduationInfoVO graduationInfoVO = graduationInfoService.detailById(id);
        if (Objects.isNull(graduationInfoVO)) {
            throw dataNotFoundError();
        }
        return SaResult.data(graduationInfoVO);
    }

    /**
     * 分页查询毕业信息
     *
     * @param graduationInfoROPageRO 分页参数
     * @return 分页结果
     */
    @PostMapping("/page")
    public SaResult pageQueryGraduationInfo(@RequestBody PageRO<GraduationInfoRO> graduationInfoROPageRO) {
        // 参数校验
        if (Objects.isNull(graduationInfoROPageRO)) {
            throw dataMissError();
        }
        if (Objects.isNull(graduationInfoROPageRO.getEntity())) {
            graduationInfoROPageRO.setEntity(new GraduationInfoRO());
        }
        // 数据查询
        PageVO<GraduationInfoVO> graduationInfoVOPageVO = graduationInfoService.pageQueryGraduationInfo(graduationInfoROPageRO);
        if (Objects.isNull(graduationInfoVOPageVO)) {
            throw dataNotFoundError();
        }
        return SaResult.data(graduationInfoVOPageVO);
    }

    /**
     * 更新毕业信息
     *
     * @param graduationInfoRO 毕业信息
     * @return 更新后的毕业信息
     */
    @PutMapping("/edit")
    public SaResult editById(@RequestBody GraduationInfoRO graduationInfoRO) {
        // 参数校验
        if (Objects.isNull(graduationInfoRO)) {
            throw dataMissError();
        }
        // 数据更新
        GraduationInfoVO graduationInfoVO = graduationInfoService.editById(graduationInfoRO);
        if (Objects.isNull(graduationInfoVO)) {
            throw dataUpdateError();
        }
        return SaResult.data(graduationInfoVO);
    }

    /**
     * 数据删除
     *
     * @param id 毕业信息主键id
     * @return 删除数量
     */
    @DeleteMapping("/delete")
    public SaResult deleteById(Long id) {
        // 参数校验
        if (Objects.isNull(id)) {
            throw dataMissError();
        }
        // 数据删除
        Integer count = graduationInfoService.deleteById(id);
        if (count <= 0) {
            throw dataDeleteError();
        }
        return SaResult.data(count);
    }

}

