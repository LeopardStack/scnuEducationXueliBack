package com.scnujxjy.backendpoint.controller.admission_information;


import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scnujxjy.backendpoint.constant.enums.SystemEnum;
import com.scnujxjy.backendpoint.dao.entity.admission_information.AdmissionInformationPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.PersonalInfoPO;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.admission_information.AdmissionInformationRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.admission_information.AdmissionInformationVO;
import com.scnujxjy.backendpoint.model.vo.admission_information.AdmissionSelectArgs;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.StudentStatusSelectArgs;
import com.scnujxjy.backendpoint.service.admission_information.AdmissionInformationService;
import com.scnujxjy.backendpoint.service.registration_record_card.PersonalInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.scnujxjy.backendpoint.constant.enums.RoleEnum.*;
import static com.scnujxjy.backendpoint.constant.enums.RoleEnum.TEACHING_POINT_ADMIN;
import static com.scnujxjy.backendpoint.exception.DataException.*;

/**
 * 录取学生信息表
 *
 * @author leopard
 * @since 2023-08-02
 */
@RestController
@RequestMapping("/admission-information")
@Slf4j
public class AdmissionInformationController {
    @Resource
    private AdmissionInformationService admissionInformationService;

    @Resource
    private PersonalInfoService personalInfoService;


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
        PageVO<AdmissionInformationVO> admissionInformationVOPageVO = admissionInformationService.getAdmissionInformationByAllRoles(admissionInformationROPageRO);
//        PageVO<AdmissionInformationVO> admissionInformationVOPageVO = admissionInformationService.pageQueryAdmissionInformation(admissionInformationROPageRO);
        // 校验数据
        if (Objects.isNull(admissionInformationVOPageVO)) {
            throw dataNotFoundError();
        }
        // 返回数据
        return SaResult.data(admissionInformationVOPageVO);
    }

    /**
     * 学生查询自己的录取信息
     * @return 录取学生分页信息
     */
    @GetMapping("/get_admission_info")
    public SaResult getAdmission_info() {

        // 查询数据
        AdmissionInformationVO admissionInformationVO = admissionInformationService.getAdmission_info();

        // 返回数据
        return SaResult.data(admissionInformationVO);
    }

    /**
     * 学生确认自己的录取信息
     * @return 录取学生分页信息
     */
    @GetMapping("/confirm_admission_info")
    public SaResult confirmAdmission_info() {
        String systemArg = SystemEnum.NOW_NEW_STUDENT_GRADE.getSystemArg();
        String loginIdAsString = StpUtil.getLoginIdAsString();
        // 查询数据
        AdmissionInformationPO admissionInformationPO = admissionInformationService.getBaseMapper().selectOne(new LambdaQueryWrapper<AdmissionInformationPO>()
                .eq(AdmissionInformationPO::getIdCardNumber, loginIdAsString)
                .eq(AdmissionInformationPO::getGrade, systemArg)
        );
        if(admissionInformationPO.getIsConfirmed().equals(1)){
            return SaResult.ok("已确认成功");
        }
        admissionInformationPO.setIsConfirmed(1);
        int i = admissionInformationService.getBaseMapper().updateById(admissionInformationPO);
        // 返回数据
        if(i > 0){
            return SaResult.ok("确认成功");
        }else{
            return SaResult.error("新生录取信息确认失败").setCode(2001);
        }
    }

    /**
     * 学生修改自己自己的联系电话和通讯地址
     * @return 录取学生分页信息
     */
    @GetMapping("/change_personal_info")
    public SaResult getAdmission_info(@RequestParam("studentAddress")String studentAddress, @RequestParam("phoneNumber")String phoneNumber) {
        String systemArg = SystemEnum.NOW_NEW_STUDENT_GRADE.getSystemArg();
        String loginIdAsString = StpUtil.getLoginIdAsString();
        // 查询数据
        PersonalInfoPO personalInfoPO = personalInfoService.getBaseMapper().selectOne(new LambdaQueryWrapper<PersonalInfoPO>()
                .eq(PersonalInfoPO::getGrade, systemArg)
                .eq(PersonalInfoPO::getIdNumber, loginIdAsString)
        );
        personalInfoPO.setPhoneNumber(phoneNumber);
        personalInfoPO.setAddress(studentAddress);
        int i = personalInfoService.getBaseMapper().updateById(personalInfoPO);
        if(i > 0){
            return SaResult.ok("更新成功");
        }

        // 返回数据
        return SaResult.error("未更新").setCode(2001);
    }

    @PostMapping("/get_admission_select_args")
    public SaResult getAdmissionArgsByAllRoles(@RequestBody AdmissionInformationRO admissionInformationRO) {

        AdmissionSelectArgs admissionSelectArgs =  admissionInformationService.getAdmissionArgsByAllRoles(admissionInformationRO);


        return SaResult.data(admissionSelectArgs);
    }

    /**
     * 批量导出新生录取信息
     * @param admissionInformationRO
     * @return
     */
    @PostMapping("/batch_export_admission_information")
    public SaResult batchExportAdmissionInformationByAllRoles(@RequestBody AdmissionInformationRO admissionInformationRO) {
        log.info("批量导出筛选参数 " + admissionInformationRO);
        Boolean ident =  admissionInformationService.batchExportAdmissionInformationByAllRoles(admissionInformationRO);

        if(ident){
            return SaResult.ok("导出新生录取数据成功");
        }

        return SaResult.error("导出新生录取数据失败").setCode(2001);
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

