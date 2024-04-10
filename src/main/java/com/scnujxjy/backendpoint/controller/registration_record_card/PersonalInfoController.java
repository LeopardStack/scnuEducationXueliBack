package com.scnujxjy.backendpoint.controller.registration_record_card;


import cn.dev33.satoken.util.SaResult;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.PersonalInfoRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.PersonalInfoVO;
import com.scnujxjy.backendpoint.service.registration_record_card.PersonalInfoService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Objects;

import static com.scnujxjy.backendpoint.exception.DataException.*;

/**
 * 个人基本信息表
 *
 * @author leopard
 * @since 2023-08-04
 */
@RestController
@RequestMapping("/personal-info")
public class PersonalInfoController {

    @Resource
    private PersonalInfoService personalInfoService;

    /**
     * 根据id查询个人基本信息
     *
     * @param id 个人基本信息id
     * @return 个人基本信息
     */
    @GetMapping("/detail")
    public SaResult detailById(Long id) {
        // 校验参数
        if (Objects.isNull(id)) {
            throw dataMissError();
        }
        // 查询数据
        PersonalInfoVO personalInfoVO = personalInfoService.detailById(id);

        return SaResult.data(personalInfoVO);
    }

    /**
     * 分页查询个人基本信息
     *
     * @param personalInfoROPageRO 分页查询参数
     * @return 分页查询结果
     */
    @PostMapping("/page")
    public SaResult pageQueryPersonalInfo(@RequestBody PageRO<PersonalInfoRO> personalInfoROPageRO) {
        // 校验参数
        if (Objects.isNull(personalInfoROPageRO)) {
            throw dataMissError();
        }
        if (Objects.isNull(personalInfoROPageRO.getEntity())) {
            personalInfoROPageRO.setEntity(new PersonalInfoRO());
        }
        // 查询数据
        PageVO<PersonalInfoVO> personalInfoVOPageVO = personalInfoService.pageQueryPersonalInfo(personalInfoROPageRO);

        return SaResult.data(personalInfoROPageRO);
    }

    /**
     * 根据id更新个人基本信息
     *
     * @param personalInfoRO 个人基本信息
     * @return 更新后的个人基本信息
     */
    @PutMapping("/edit")
    public SaResult editById(@RequestBody PersonalInfoRO personalInfoRO) {
        // 校验参数
        if (Objects.isNull(personalInfoRO) || Objects.isNull(personalInfoRO.getId())) {
            throw dataMissError();
        }
        // 更新数据
        PersonalInfoVO personalInfoVO = personalInfoService.editById(personalInfoRO);
        if (Objects.isNull(personalInfoVO)) {
            throw dataUpdateError();
        }
        return SaResult.data(personalInfoVO);
    }

    /**
     * 根据id删除数据
     *
     * @param id 个人基本信息id
     * @return 删除的数据条数
     */
    @DeleteMapping("/delete")
    public SaResult deleteById(Long id) {
        // 校验参数
        if (Objects.isNull(id)) {
            throw dataMissError();
        }
        // 删除数据
        int count = personalInfoService.deleteById(id);
        if (count <= 0) {
            throw dataDeleteError();
        }
        return SaResult.data(count);
    }
}

