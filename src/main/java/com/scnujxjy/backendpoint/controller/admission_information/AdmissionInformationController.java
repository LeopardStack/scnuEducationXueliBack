package com.scnujxjy.backendpoint.controller.admission_information;


import cn.dev33.satoken.util.SaResult;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.admission_information.AdmissionInformationRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.admission_information.AdmissionInformationVO;
import com.scnujxjy.backendpoint.service.admission_information.AdmissionInformationService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Objects;

import static com.scnujxjy.backendpoint.exception.DataException.*;

/**
 * <p>
 * 录取学生信息表 前端控制器
 * </p>
 *
 * @author leopard
 * @since 2023-08-02
 */
@RestController
@RequestMapping("/admission-information")
public class AdmissionInformationController {
    @Resource
    private AdmissionInformationService admissionInformationService;


    /**
     * 根据id查询录取学生信息表
     *
     * @param id 录取学生信息id
     * @return 录取学生信息
     */
    @GetMapping("/detail")
    public SaResult detailById(Long id) {
        // 参数校验
        if (Objects.isNull(id)) {
            throw dataMissError();
        }
        // 查找数据
        AdmissionInformationVO admissionInformationVO = admissionInformationService.detailById(id);

        // 校验结果
        if (Objects.isNull(admissionInformationVO)) {
            throw dataNotFoundError();
        }

        // 返回结果
        return SaResult.data(admissionInformationVO);
    }

    /**
     * 分页查询学生录取信息
     *
     * @param admissionInformationROPageRO 录取学生信息分页查询参数
     * @return 录取学生分页信息
     */
    @PostMapping("/page")
    public SaResult pageQueryAdmissionInformation(@RequestBody PageRO<AdmissionInformationRO> admissionInformationROPageRO) {
        // 参数校验
        if (Objects.isNull(admissionInformationROPageRO)) {
            throw dataMissError();
        }
        if (Objects.isNull(admissionInformationROPageRO.getEntity())) {
            admissionInformationROPageRO.setEntity(new AdmissionInformationRO());
        }
        // 查询数据
        PageVO<AdmissionInformationVO> admissionInformationVOPageVO = admissionInformationService.pageQueryAdmissionInformation(admissionInformationROPageRO);
        // 校验数据
        if (Objects.isNull(admissionInformationVOPageVO)) {
            throw dataNotFoundError();
        }
        // 返回数据
        return SaResult.data(admissionInformationVOPageVO);
    }

    /**
     * 根据id更新录取学生信息
     *
     * @param admissionInformationRO 更新的学生信息
     * @return 更新后的录取学生信息
     */
    @PutMapping("/edit")
    public SaResult editById(@RequestBody AdmissionInformationRO admissionInformationRO) {
        // 参数校验
        if (Objects.isNull(admissionInformationRO) || Objects.isNull(admissionInformationRO.getId())) {
            throw dataMissError();
        }
        // 更新
        AdmissionInformationVO admissionInformationVO = admissionInformationService.editById(admissionInformationRO);
        // 更新校验
        if (Objects.isNull(admissionInformationVO)) {
            throw dataUpdateError();
        }
        // 返回数据
        return SaResult.data(admissionInformationVO);
    }

    /**
     * 根据id删除录取学生信息
     *
     * @param id 录取学生信息id
     * @return 删除的数量
     */
    @DeleteMapping("/delete")
    public SaResult deleteById(Long id) {
        // 参数校验
        if (Objects.isNull(id)) {
            throw dataMissError();
        }
        // 删除数据
        int count = admissionInformationService.deleteById(id);
        // 校验操作
        if (count <= 0) {
            throw dataDeleteError();
        }
        // 返回删除数量
        return SaResult.data(count);
    }
}

