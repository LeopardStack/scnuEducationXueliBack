package com.scnujxjy.backendpoint.controller.registration_record_card;


import cn.dev33.satoken.util.SaResult;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.OriginalEducationInfoRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.OriginalEducationInfoVO;
import com.scnujxjy.backendpoint.service.registration_record_card.OriginalEducationInfoService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Objects;

import static com.scnujxjy.backendpoint.exception.DataException.*;

/**
 * 原学历信息表
 *
 * @author leopard
 * @since 2023-08-02
 */
@RestController
@RequestMapping("/original-education-info")
public class OriginalEducationInfoController {
    @Resource
    private OriginalEducationInfoService originalEducationInfoService;

    /**
     * 根据id查询原学历信息
     *
     * @param id 原学历信息id
     * @return 原学历信息
     */
    @GetMapping("/detail")
    public SaResult detailById(Long id) {
        // 参数校验
        if (Objects.isNull(id)) {
            throw dataMissError();
        }
        // 查询数据
        OriginalEducationInfoVO originalEducationInfoVO = originalEducationInfoService.detailById(id);

        return SaResult.data(originalEducationInfoVO);
    }

    /**
     * 分页查询原学历信息
     *
     * @param originalEducationInfoROPageRO 查询原学历信息分页参数
     * @return 分页查询结果
     */
    @PostMapping("/page")
    public SaResult pageQueryOriginalEducationInfo(@RequestBody PageRO<OriginalEducationInfoRO> originalEducationInfoROPageRO) {
        // 参数校验
        if (Objects.isNull(originalEducationInfoROPageRO)) {
            throw dataMissError();
        }
        if (Objects.isNull(originalEducationInfoROPageRO.getEntity())) {
            originalEducationInfoROPageRO.setEntity(new OriginalEducationInfoRO());
        }
        // 查询数据
        PageVO<OriginalEducationInfoVO> originalEducationInfoVOPageVO = originalEducationInfoService.pageQueryOriginalEducationInfo(originalEducationInfoROPageRO);

        return SaResult.data(originalEducationInfoVOPageVO);

    }

    /**
     * 更新原学历信息
     *
     * @param originalEducationInfoRO 原学历信息
     * @return 更新后的原学历信息
     */
    @PutMapping("/edit")
    public SaResult editById(@RequestBody OriginalEducationInfoRO originalEducationInfoRO) {
        // 参数校验
        if (Objects.isNull(originalEducationInfoRO) || Objects.isNull(originalEducationInfoRO.getId())) {
            throw dataMissError();
        }
        // 更新数据
        OriginalEducationInfoVO originalEducationInfoVO = originalEducationInfoService.editById(originalEducationInfoRO);
        if (Objects.isNull(originalEducationInfoVO)) {
            throw dataUpdateError();
        }
        // 返回更新后的数据
        return SaResult.data(originalEducationInfoVO);
    }

    /**
     * 根据id删除原学历信息
     *
     * @param id 原学历信息id
     * @return 删除结果
     */
    @DeleteMapping("/delete")
    public SaResult deleteById(Long id) {
        // 参数校验
        if (Objects.isNull(id)) {
            throw dataMissError();
        }
        // 删除数据
        int count = originalEducationInfoService.deleteById(id);
        if (count <= 0) {
            throw dataDeleteError();
        }
        return SaResult.data(count);
    }
}

