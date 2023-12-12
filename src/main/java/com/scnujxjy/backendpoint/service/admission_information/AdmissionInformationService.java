package com.scnujxjy.backendpoint.service.admission_information;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.dao.entity.admission_information.AdmissionInformationPO;
import com.scnujxjy.backendpoint.dao.mapper.admission_information.AdmissionInformationMapper;
import com.scnujxjy.backendpoint.inverter.admission_information.AdmissionInformationInverter;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.admission_information.AdmissionInformationRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.admission_information.AdmissionInformationVO;
import com.scnujxjy.backendpoint.model.vo.admission_information.AdmissionSelectArgs;
import com.scnujxjy.backendpoint.model.vo.teaching_process.FilterDataVO;
import com.scnujxjy.backendpoint.util.filter.CollegeAdminFilter;
import com.scnujxjy.backendpoint.util.filter.ManagerFilter;
import com.scnujxjy.backendpoint.util.filter.TeacherFilter;
import com.scnujxjy.backendpoint.util.filter.TeachingPointFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

import static com.scnujxjy.backendpoint.constant.enums.RoleEnum.*;
import static com.scnujxjy.backendpoint.constant.enums.RoleEnum.TEACHING_POINT_ADMIN;
import static com.scnujxjy.backendpoint.exception.DataException.dataNotFoundError;

/**
 * <p>
 * 录取学生信息表 服务实现类
 * </p>
 *
 * @author leopard
 * @since 2023-08-02
 */
@Service
@Slf4j
public class AdmissionInformationService extends ServiceImpl<AdmissionInformationMapper, AdmissionInformationPO> implements IService<AdmissionInformationPO> {

    @Resource
    private AdmissionInformationInverter admissionInformationInverter;

    @Resource
    private CollegeAdminFilter collegeAdminFilter;

    @Resource
    private TeacherFilter teacherFilter;

    @Resource
    private ManagerFilter managerFilter;

    @Resource
    private TeachingPointFilter teachingPointFilter;

    /**
    * @Version：1.0.0
    * @Description：添加录取学生信息
    * @Author：3304393868@qq.com
    * @Date：2023/12/7-16:00
    */
    public Integer insterAdmissionInformation(AdmissionInformationRO admissionInformationRO){
        AdmissionInformationPO admissionInformationPO =  admissionInformationInverter.ro2PO(admissionInformationRO);
        return   baseMapper.insert(admissionInformationPO);
    }


    /**
     * 根据id查询录取学生信息表
     *
     * @param id 录取学生信息id
     * @return 录取学生信息
     */
    public AdmissionInformationVO detailById(Long id) {
        // 参数校验
        if (Objects.isNull(id)) {
            log.error("参数缺失");
            return null;
        }
        // 查找数据
        AdmissionInformationPO admissionInformationPO = baseMapper.selectById(id);
        // 返回结果
        return admissionInformationInverter.po2VO(admissionInformationPO);
    }
    public PageVO<AdmissionInformationVO> getAdmissionInformationByAllRoles(PageRO<AdmissionInformationRO> admissionInformationROPageRO) {
        List<String> roleList = StpUtil.getRoleList();

        if (roleList.contains(SECOND_COLLEGE_ADMIN.getRoleName())) {
            // 查询继续教育管理员权限范围内的教学计划

        } else if (roleList.contains(XUELIJIAOYUBU_ADMIN.getRoleName()) || roleList.contains(CAIWUBU_ADMIN.getRoleName())) {
            // 查询继续教育管理员权限范围内的教学计划
            return managerFilter.getAdmissionInformationByAllRoles(admissionInformationROPageRO);

        } else if (roleList.contains(TEACHING_POINT_ADMIN.getRoleName())) {

        }
        return null;
    }

    /**
     * 分页查询学生录取信息
     *
     * @param admissionInformationROPageRO 录取学生信息分页查询参数
     * @return 录取学生分页信息
     */
    public PageVO<AdmissionInformationVO> pageQueryAdmissionInformation(PageRO<AdmissionInformationRO> admissionInformationROPageRO) {
        // 参数校验
        if (Objects.isNull(admissionInformationROPageRO)) {
            log.error("参数缺失");
            return null;
        }
        AdmissionInformationRO entity = admissionInformationROPageRO.getEntity();
        if (Objects.isNull(entity)) {
            entity = new AdmissionInformationRO();
        }
        // 构建查询分页查询语句
        // 分页查询 或 列表查询 最后返回结果
        LambdaQueryWrapper<AdmissionInformationPO> wrapper = Wrappers.<AdmissionInformationPO>lambdaQuery()
                .eq(Objects.nonNull(entity.getId()), AdmissionInformationPO::getId, entity.getId())
                .eq(StrUtil.isNotBlank(entity.getName()), AdmissionInformationPO::getName, entity.getName())
                .eq(StrUtil.isNotBlank(entity.getGender()), AdmissionInformationPO::getGender, entity.getGender())
                .eq(Objects.nonNull(entity.getTotalScore()), AdmissionInformationPO::getTotalScore, entity.getTotalScore())
                .eq(StrUtil.isNotBlank(entity.getMajorCode()), AdmissionInformationPO::getMajorCode, entity.getMajorCode())
                .like(StrUtil.isNotBlank(entity.getMajorName()), AdmissionInformationPO::getMajorName, entity.getMajorName())
                .eq(StrUtil.isNotBlank(entity.getLevel()), AdmissionInformationPO::getLevel, entity.getLevel())
                .eq(StrUtil.isNotBlank(entity.getStudyForm()), AdmissionInformationPO::getStudyForm, entity.getStudyForm())
                .eq(StrUtil.isNotBlank(entity.getOriginalEducation()), AdmissionInformationPO::getOriginalEducation, entity.getOriginalEducation())
                .eq(StrUtil.isNotBlank(entity.getGraduationSchool()), AdmissionInformationPO::getGraduationSchool, entity.getGraduationSchool())
                .eq(Objects.nonNull(entity.getGraduationDate()), AdmissionInformationPO::getGraduationDate, entity.getGraduationDate())
                .eq(StrUtil.isNotBlank(entity.getPhoneNumber()), AdmissionInformationPO::getPhoneNumber, entity.getPhoneNumber())
                .eq(StrUtil.isNotBlank(entity.getIdCardNumber()), AdmissionInformationPO::getIdCardNumber, entity.getIdCardNumber())
                .eq(Objects.nonNull(entity.getBirthDate()), AdmissionInformationPO::getBirthDate, entity.getBirthDate())
                .like(StrUtil.isNotBlank(entity.getAddress()), AdmissionInformationPO::getAddress, entity.getAddress())
                .like(StrUtil.isNotBlank(entity.getPostalCode()), AdmissionInformationPO::getPostalCode, entity.getPostalCode())
                .eq(StrUtil.isNotBlank(entity.getEthnicity()), AdmissionInformationPO::getEthnicity, entity.getEthnicity())
                .eq(StrUtil.isNotBlank(entity.getPoliticalStatus()), AdmissionInformationPO::getPoliticalStatus, entity.getPoliticalStatus())
                .eq(StrUtil.isNotBlank(entity.getAdmissionNumber()), AdmissionInformationPO::getAdmissionNumber, entity.getAdmissionNumber())
                .eq(StrUtil.isNotBlank(entity.getShortStudentNumber()), AdmissionInformationPO::getShortStudentNumber, entity.getShortStudentNumber())
                .eq(StrUtil.isNotBlank(entity.getCollege()), AdmissionInformationPO::getCollege, entity.getCollege())
                .eq(StrUtil.isNotBlank(entity.getTeachingPoint()), AdmissionInformationPO::getTeachingPoint, entity.getTeachingPoint())
                .like(StrUtil.isNotBlank(entity.getReportLocation()), AdmissionInformationPO::getReportLocation, entity.getReportLocation())
                .eq(StrUtil.isNotBlank(entity.getEntrancePhotoUrl()), AdmissionInformationPO::getEntrancePhotoUrl, entity.getEntrancePhotoUrl())
                .eq(StrUtil.isNotBlank(entity.getGrade()), AdmissionInformationPO::getGrade, entity.getGrade())
                .last(StrUtil.isNotBlank(admissionInformationROPageRO.getOrderBy()), admissionInformationROPageRO.lastOrderSql());
        // 分页查询 或 列表查询 最后返回结果
        if (Objects.equals(true, admissionInformationROPageRO.getIsAll())) {
            List<AdmissionInformationPO> admissionInformationPOS = baseMapper.selectList(wrapper);
            return new PageVO<>(admissionInformationInverter.po2VO(admissionInformationPOS));
        } else {
            Page<AdmissionInformationPO> admissionInformationPOPage = baseMapper.selectPage(admissionInformationROPageRO.getPage(), wrapper);
            return new PageVO<>(admissionInformationPOPage, admissionInformationInverter.po2VO(admissionInformationPOPage.getRecords()));
        }
    }

    /**
     * 根据id更新录取学生信息
     *
     * @param admissionInformationRO 更新的学生信息
     * @return 更新后的录取学生信息
     */
    public AdmissionInformationVO editById(AdmissionInformationRO admissionInformationRO) {
        // 参数校验
        if (Objects.isNull(admissionInformationRO) || Objects.isNull(admissionInformationRO.getId())) {
            log.error("参数缺失");
            return null;
        }
        // 更新
        AdmissionInformationPO admissionInformationPO = admissionInformationInverter.ro2PO(admissionInformationRO);
        int count = baseMapper.updateById(admissionInformationPO);
        // 更新校验
        if (count <= 0) {
            log.error("更新失败，Admission information：{}", admissionInformationPO);
            return null;
        }
        // 返回数据
        return detailById(admissionInformationRO.getId());
    }

    /**
     * 根据id删除录取学生信息
     *
     * @param id 录取学生信息id
     * @return 删除的数量
     */
    public Integer deleteById(Long id) {
        // 参数校验
        if (Objects.isNull(id)) {
            log.error("参数缺失");
            return null;
        }
        // 删除数据
        int count = baseMapper.deleteById(id);
        // 校验操作
        if (count <= 0) {
            log.error("删除失败，id：{}", id);
            return null;
        }
        // 返回删除数量
        return count;
    }

    /**
     * 获取新生录取信息查询的筛选参数
     * @param admissionInformationRO
     * @return
     */
    public AdmissionSelectArgs getAdmissionArgsByAllRoles(AdmissionInformationRO admissionInformationRO) {
        List<String> roleList = StpUtil.getRoleList();

        if (roleList.contains(SECOND_COLLEGE_ADMIN.getRoleName())) {
            // 查询继续教育管理员权限范围内的教学计划

        } else if (roleList.contains(XUELIJIAOYUBU_ADMIN.getRoleName()) || roleList.contains(CAIWUBU_ADMIN.getRoleName())) {
            // 查询继续教育管理员权限范围内的教学计划
            return managerFilter.getAdmissionArgsByAllRoles(admissionInformationRO);

        } else if (roleList.contains(TEACHING_POINT_ADMIN.getRoleName())) {

        }
        return null;
    }
}
