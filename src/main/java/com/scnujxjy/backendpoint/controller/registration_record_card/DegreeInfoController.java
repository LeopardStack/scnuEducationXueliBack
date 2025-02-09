package com.scnujxjy.backendpoint.controller.registration_record_card;


import cn.dev33.satoken.util.SaResult;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.DegreeInfoRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.DegreeInfoVO;
import com.scnujxjy.backendpoint.service.registration_record_card.DegreeInfoService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Objects;

import static com.scnujxjy.backendpoint.exception.DataException.*;

/**
 * 学位信息表
 *
 * @author leopard
 * @since 2023-08-04
 */
@RestController
@RequestMapping("/degree-info")
public class DegreeInfoController {

    @Resource
    private DegreeInfoService degreeInfoService;

    /**
     * 根据id查询学位信息
     *
     * @param id 学位信息id
     * @return 学位信息
     */
    @GetMapping("/detail")
    public SaResult detailById(Long id) {
        // 参数校验
        if (Objects.isNull(id)) {
            throw dataMissError();
        }
        // 数据查询
        DegreeInfoVO degreeInfoVO = degreeInfoService.detailById(id);

        return SaResult.data(degreeInfoVO);
    }

    /**
     * 分页查询学位信息
     *
     * @param degreeInfoROPageRO 分页参数
     * @return 学位信息列表
     */
    @PostMapping("/page")
    public SaResult pageQueryDegreeInfo(@RequestBody PageRO<DegreeInfoRO> degreeInfoROPageRO) {
        // 参数校验
        if (Objects.isNull(degreeInfoROPageRO)) {
            throw dataMissError();
        }
        if (Objects.isNull(degreeInfoROPageRO.getEntity())) {
            degreeInfoROPageRO.setEntity(new DegreeInfoRO());
        }
        // 数据查询
        PageVO<DegreeInfoVO> degreeInfoVOPageVO = degreeInfoService.pageQueryDegreeInfo(degreeInfoROPageRO);

        return SaResult.data(degreeInfoVOPageVO);
    }

    /**
     * 更新学位信息
     *
     * @param degreeInfoRO 学位信息
     * @return 更新后的学位信息
     */
    @PutMapping("/edit")
    public SaResult editById(DegreeInfoRO degreeInfoRO) {
        // 参数校验
        if (Objects.isNull(degreeInfoRO) || Objects.isNull(degreeInfoRO.getId())) {
            throw dataMissError();
        }
        // 数据查询
        DegreeInfoVO degreeInfoVO = degreeInfoService.editById(degreeInfoRO);
        if (Objects.isNull(degreeInfoVO)) {
            throw dataUpdateError();
        }
        return SaResult.data(degreeInfoVO);
    }

    /**
     * 根据id删除学位信息
     *
     * @param id 学位信息id
     * @return 删除的学位信息数量
     */
    @DeleteMapping("/delete")
    public SaResult deleteById(Long id) {
        // 参数校验
        if (Objects.isNull(id)) {
            throw dataMissError();
        }
        // 删除数据
        Integer count = degreeInfoService.deleteById(id);
        if (count <= 0) {
            throw dataDeleteError();
        }
        return SaResult.data(count);
    }

}

