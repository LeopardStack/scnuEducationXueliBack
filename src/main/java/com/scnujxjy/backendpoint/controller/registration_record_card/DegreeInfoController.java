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
 * <p>
 * 学位信息表 前端控制器
 * </p>
 *
 * @author leopard
 * @since 2023-08-04
 */
@RestController
@RequestMapping("/degree-info")
public class DegreeInfoController {

    @Resource
    private DegreeInfoService degreeInfoService;

    @GetMapping("/detail")
    public SaResult detailById(Long id) {
        if (Objects.isNull(id)) {
            throw dataMissError();
        }
        DegreeInfoVO degreeInfoVO = degreeInfoService.detailById(id);
        if (Objects.isNull(degreeInfoVO)) {
            throw dataNotFoundError();
        }
        return SaResult.data(degreeInfoVO);
    }

    @PostMapping("/page")
    public SaResult pageQueryDegreeInfo(@RequestBody PageRO<DegreeInfoRO> degreeInfoROPageRO) {
        if (Objects.isNull(degreeInfoROPageRO)) {
            throw dataMissError();
        }
        if (Objects.isNull(degreeInfoROPageRO.getEntity())) {
            degreeInfoROPageRO.setEntity(new DegreeInfoRO());
        }
        PageVO<DegreeInfoVO> degreeInfoVOPageVO = degreeInfoService.pageQueryDegreeInfo(degreeInfoROPageRO);
        if (Objects.isNull(degreeInfoVOPageVO)) {
            throw dataNotFoundError();
        }
        return SaResult.data(degreeInfoVOPageVO);
    }

    @PutMapping("/edit")
    public SaResult editById(DegreeInfoRO degreeInfoRO) {
        if (Objects.isNull(degreeInfoRO) || Objects.isNull(degreeInfoRO.getId())) {
            throw dataMissError();
        }
        DegreeInfoVO degreeInfoVO = degreeInfoService.editById(degreeInfoRO);
        if (Objects.isNull(degreeInfoVO)) {
            throw dataUpdateError();
        }
        return SaResult.data(degreeInfoVO);
    }

    @DeleteMapping("/delete")
    public SaResult deleteById(Long id) {
        if (Objects.isNull(id)) {
            throw dataMissError();
        }
        Integer count = degreeInfoService.deleteById(id);
        if (count <= 0) {
            throw dataDeleteError();
        }
        return SaResult.data(count);
    }

}

