package com.scnujxjy.backendpoint.service.admission_information;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.constant.enums.MajorInformationEnum;
import com.scnujxjy.backendpoint.constant.enums.RoleEnum;
import com.scnujxjy.backendpoint.dao.entity.admission_information.EnrollmentPlanPO;
import com.scnujxjy.backendpoint.dao.entity.basic.PlatformUserPO;
import com.scnujxjy.backendpoint.dao.entity.college.CollegeInformationPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_point.TeachingPointAdminInformationPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_point.TeachingPointInformationPO;
import com.scnujxjy.backendpoint.dao.mapper.admission_information.EnrollmentPlanMapper;
import com.scnujxjy.backendpoint.dao.mapper.basic.PlatformUserMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_point.TeachingPointAdminInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_point.TeachingPointInformationMapper;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.admission_information.EnrollmentPlanApplyRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.admission_information.ApprovalPlanSummaryVO;
import com.scnujxjy.backendpoint.model.vo.admission_information.EnrollmentPlanFilterItemsVO;
import com.scnujxjy.backendpoint.service.college.CollegeInformationService;
import com.scnujxjy.backendpoint.service.teaching_point.TeachingPointInformationService;
import com.scnujxjy.backendpoint.util.ResultCode;
import com.scnujxjy.backendpoint.util.tool.ScnuXueliTools;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.Year;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 招生计划申报表 服务类
 * </p>
 *
 * @author 谢辉龙
 * @since 2024-05-17
 */
@Service
public class EnrollmentPlanService extends ServiceImpl<EnrollmentPlanMapper, EnrollmentPlanPO> {

    @Resource
    private CollegeInformationService collegeInformationService;

    @Resource
    private TeachingPointInformationService teachingPointInformationService;

    @Resource
    private ScnuXueliTools scnuXueliTools;

    @Resource
    private PlatformUserMapper platformUserMapper;

    @Resource
    private TeachingPointAdminInformationMapper teachingPointAdminInformationMapper;

    @Resource
    private TeachingPointInformationMapper teachingPointInformationMapper;

    /**
     * 上报招生计划
     * @param enrollmentPlanApplyRO
     * @return
     */
    public SaResult applyEnrollmentPlan(EnrollmentPlanApplyRO enrollmentPlanApplyRO) {
        EnrollmentPlanPO enrollmentPlanPO = new EnrollmentPlanPO();

        BeanUtils.copyProperties(enrollmentPlanApplyRO, enrollmentPlanPO);

        List<String> roleList = StpUtil.getRoleList();
        if(roleList.contains(RoleEnum.ADMISSIONS_DEPARTMENT_ADMINISTRATOR.getRoleName())){
            // 招生部管理员 插入招生计划时  需要同时选择学院、教学点
            if(enrollmentPlanApplyRO.getCollege() == null){
                return ResultCode.ENROLLMENT_PLAN_FAIL16.generateErrorResultInfo();
            }

            CollegeInformationPO collegeInformationPO = collegeInformationService.getBaseMapper().selectOne(new LambdaQueryWrapper<CollegeInformationPO>()
                    .eq(CollegeInformationPO::getCollegeName, enrollmentPlanApplyRO.getCollege()));
            if(collegeInformationPO == null){
                return ResultCode.ENROLLMENT_PLAN_FAIL16.generateErrorResultInfo();
            }

            if(enrollmentPlanApplyRO.getTeachingPointName() == null){
                return ResultCode.ENROLLMENT_PLAN_FAIL17.generateErrorResultInfo();
            }

            CollegeInformationPO userBelongCollege = scnuXueliTools.getUserBelongCollege();
            TeachingPointInformationPO teachingPointInformationPO = null;
            if("校内".equals(enrollmentPlanApplyRO.getTeachingPointName())){
                teachingPointInformationPO = teachingPointInformationService.getBaseMapper().selectOne(new LambdaQueryWrapper<TeachingPointInformationPO>()
                        .eq(TeachingPointInformationPO::getTeachingPointName, userBelongCollege.getCollegeName()));
            }

            teachingPointInformationPO = teachingPointInformationService.getBaseMapper().selectOne(new LambdaQueryWrapper<TeachingPointInformationPO>()
                    .eq(TeachingPointInformationPO::getTeachingPointName, enrollmentPlanApplyRO.getTeachingPointName()));
            if (teachingPointInformationPO == null) {
                return ResultCode.ENROLLMENT_PLAN_FAIL17.generateErrorResultInfo();
            }

            // 更新学院信息 教学点信息
            enrollmentPlanPO.setCollegeId(collegeInformationPO.getCollegeId());
            enrollmentPlanPO.setTeachingLocation(teachingPointInformationPO.getTeachingPointName());
            enrollmentPlanPO.setTeachingPointId(teachingPointInformationPO.getTeachingPointId());
            // 招生办自己手动添加 那么此时的招生计划的状态就是 招生办管理员
            enrollmentPlanPO.setStatus(RoleEnum.ADMISSIONS_DEPARTMENT_ADMINISTRATOR.getRoleName());
        }else if(roleList.contains(RoleEnum.SECOND_COLLEGE_ADMIN.getRoleName())){
            // 二级学院教务员 需要提供教学点
            if(enrollmentPlanApplyRO.getTeachingPointName() == null){
                return ResultCode.ENROLLMENT_PLAN_FAIL17.generateErrorResultInfo();
            }
            CollegeInformationPO userBelongCollege = scnuXueliTools.getUserBelongCollege();
            TeachingPointInformationPO teachingPointInformationPO = null;
            if("校内".equals(enrollmentPlanApplyRO.getTeachingPointName())){
                teachingPointInformationPO = teachingPointInformationService.getBaseMapper().selectOne(new LambdaQueryWrapper<TeachingPointInformationPO>()
                        .eq(TeachingPointInformationPO::getTeachingPointName, userBelongCollege.getCollegeName()));
            }else {
                teachingPointInformationPO = teachingPointInformationService.getBaseMapper().selectOne(new LambdaQueryWrapper<TeachingPointInformationPO>()
                        .eq(TeachingPointInformationPO::getTeachingPointName, enrollmentPlanApplyRO.getTeachingPointName()));
            }

            if (teachingPointInformationPO == null) {
                return ResultCode.ENROLLMENT_PLAN_FAIL17.generateErrorResultInfo();
            }
            enrollmentPlanPO.setCollegeId(userBelongCollege.getCollegeId());
            enrollmentPlanPO.setTeachingLocation("校内");
            enrollmentPlanPO.setTeachingPointId(teachingPointInformationPO.getTeachingPointId());
            // 二级学院添加完招生计划 此时审核的肯定是 二级学院教务员 因为它需要批量提交
            enrollmentPlanPO.setStatus(RoleEnum.SECOND_COLLEGE_ADMIN.getRoleName());
        }else if(roleList.contains(RoleEnum.TEACHING_POINT_ADMIN.getRoleName())){
            // 教学点教务员 只需要提供二级学院即可
            if(enrollmentPlanApplyRO.getCollege() == null){
                return ResultCode.ENROLLMENT_PLAN_FAIL16.generateErrorResultInfo();
            }

            CollegeInformationPO collegeInformationPO = collegeInformationService.getBaseMapper().selectOne(new LambdaQueryWrapper<CollegeInformationPO>()
                    .eq(CollegeInformationPO::getCollegeName, enrollmentPlanApplyRO.getCollege()));
            if(collegeInformationPO == null){
                return ResultCode.ENROLLMENT_PLAN_FAIL16.generateErrorResultInfo();
            }

            enrollmentPlanPO.setCollegeId(collegeInformationPO.getCollegeId());

            TeachingPointInformationPO teachingPointInformationPO = teachingPointInformationService.getBaseMapper().selectOne(new LambdaQueryWrapper<TeachingPointInformationPO>()
                    .eq(TeachingPointInformationPO::getTeachingPointName, enrollmentPlanApplyRO.getTeachingPointName()));
            if (teachingPointInformationPO == null) {
                return ResultCode.ENROLLMENT_PLAN_FAIL17.generateErrorResultInfo();
            }
            List<TeachingPointInformationPO> userBelongTeachingPoints = scnuXueliTools.getUserBelongTeachingPoints();
            boolean exists = userBelongTeachingPoints.stream()
                    .anyMatch(tp -> tp.getTeachingPointName().equals(teachingPointInformationPO.getTeachingPointName()));

            if (!exists) {
                return ResultCode.ENROLLMENT_PLAN_FAIL50.generateErrorResultInfo();
            }
            enrollmentPlanPO.setTeachingLocation(teachingPointInformationPO.getTeachingPointName());
            enrollmentPlanPO.setTeachingPointId(teachingPointInformationPO.getTeachingPointId());
            // 教学点添加完招生计划 此时审核的肯定是 教学点教务员 因为它需要批量提交
            enrollmentPlanPO.setStatus(RoleEnum.TEACHING_POINT_ADMIN.getRoleName());

        }else{
            return ResultCode.ENROLLMENT_PLAN_FAIL18.generateErrorResultInfo();
        }

        //学费关联
        String majorName = enrollmentPlanApplyRO.getMajorName();
        String trainingLevel = enrollmentPlanApplyRO.getTrainingLevel();
        boolean b = MajorInformationEnum.existsByMajorNameAndLevel(majorName, trainingLevel);
        if (!b){
            return ResultCode.ENROLLMENT_PLAN_FAIL56.generateErrorResultInfo();
        }

        MajorInformationEnum byMajorNameAndLevel = MajorInformationEnum.getByMajorNameAndLevel(majorName, trainingLevel);
        enrollmentPlanPO.setTuition(new BigDecimal(byMajorNameAndLevel.getTuitionFee()));
        enrollmentPlanPO.setEnrollmentSubject(byMajorNameAndLevel.getMajorCategory());

        //如果是申报时，就设为空
        enrollmentPlanPO.setRemarks(null);
        int insert = getBaseMapper().insert(enrollmentPlanPO);
        if(insert <= 0){
            return ResultCode.ENROLLMENT_PLAN_FAIL15.generateErrorResultInfo();
        }
        return SaResult.ok("成功申报");
    }

    /**
     * 不同角色查询自己权限范围内的招生计划
     * @param enrollmentPlanApplyROPageRO
     * @return
     */
    public SaResult queryEnrollmentPlan(PageRO<EnrollmentPlanApplyRO> enrollmentPlanApplyROPageRO) {
        EnrollmentPlanApplyRO entity = enrollmentPlanApplyROPageRO.getEntity();
        Long pageSize = enrollmentPlanApplyROPageRO.getPageSize();
        Long pageNumber = (enrollmentPlanApplyROPageRO.getPageNumber() - 1) * pageSize;
        List<EnrollmentPlanPO> enrollmentPlanPOList = getBaseMapper().queryEnrollmentPlan(entity,
                pageNumber, pageSize);

        Long enrollmentPlanSize = getBaseMapper().queryEnrollmentPlanSize(entity);

        PageVO<EnrollmentPlanPO> pageVO = new PageVO<>();
        pageVO.setRecords(enrollmentPlanPOList);
        pageVO.setTotal(enrollmentPlanSize);
        pageVO.setSize(pageSize);
        pageVO.setCurrent(pageNumber);

        return SaResult.ok("成功获取招生计划数据").setData(pageVO);
    }

    /**
     * 获取 招生计划的筛选项
     * @param enrollmentPlanApplyRO
     * @return
     */
    public SaResult getEnrollmentPlanFilterItems(EnrollmentPlanApplyRO enrollmentPlanApplyRO) {
        EnrollmentPlanFilterItemsVO enrollmentPlanFilterItemsVO = new EnrollmentPlanFilterItemsVO();
        List<String> yearList = getBaseMapper().getDistinctGradeList(enrollmentPlanApplyRO);
        List<String> majorNameList = getBaseMapper().getDistinctMajorNameList(enrollmentPlanApplyRO);
        List<String> studyFormList = getBaseMapper().getDistinctStudyFormList(enrollmentPlanApplyRO);
        List<String> trainingLevelList = getBaseMapper().getDistinctTrainingLevelList(enrollmentPlanApplyRO);
        List<String> collegeList = getBaseMapper().getDistinctTrainingCollegeList(enrollmentPlanApplyRO);
        List<String> teachingPointNameList = getBaseMapper().getDistinctTeachingPointNameList(enrollmentPlanApplyRO);

        enrollmentPlanFilterItemsVO.setYearList(yearList);
        enrollmentPlanFilterItemsVO.setMajorNameList(majorNameList);
        enrollmentPlanFilterItemsVO.setStudyFormList(studyFormList);
        enrollmentPlanFilterItemsVO.setTrainingLevelList(trainingLevelList);
        enrollmentPlanFilterItemsVO.setCollegeList(collegeList);
        enrollmentPlanFilterItemsVO.setTeachingPointNameList(teachingPointNameList);

        return SaResult.ok("成功获取招生计划筛选项").setData(enrollmentPlanFilterItemsVO);
    }

    public SaResult getEnrollmentPlanFilter(EnrollmentPlanApplyRO enrollmentPlanApplyRO,List<String> roleList) {
        EnrollmentPlanFilterItemsVO enrollmentPlanFilterItemsVO = new EnrollmentPlanFilterItemsVO();
        List<String> collegeList=new ArrayList<>();
        List<String> teachingPointNameList=new ArrayList<>();
        List<String> majorNameList = new ArrayList<>();

        if (roleList.contains(RoleEnum.ADMISSIONS_DEPARTMENT_ADMINISTRATOR.getRoleName())) {
            // 招生部管理员 可以获取所有学院，所有教学点,所有专业
             collegeList = getBaseMapper().getDistinctTrainingCollegeList(enrollmentPlanApplyRO);
             teachingPointNameList = getBaseMapper().getDistinctTeachingPointNameList(enrollmentPlanApplyRO);
             majorNameList=getBaseMapper().getDistinctMajorNameList(enrollmentPlanApplyRO);
        } else if (roleList.contains(RoleEnum.SECOND_COLLEGE_ADMIN.getRoleName())) {
            // 二级学院教务员 只能获取本学院，所有教学点,本学院的专业
            CollegeInformationPO userBelongCollege = scnuXueliTools.getUserBelongCollege();
            collegeList.add(userBelongCollege.getCollegeName());
            teachingPointNameList = getBaseMapper().getDistinctTeachingPointNameList(enrollmentPlanApplyRO);

            enrollmentPlanApplyRO.setCollege(userBelongCollege.getCollegeName());
            majorNameList=getBaseMapper().getDistinctMajorNameList(enrollmentPlanApplyRO);

        } else if (roleList.contains(RoleEnum.TEACHING_POINT_ADMIN.getRoleName())) {
            // 教学点教务员获取 获取所有学院，本教学点，所有专业
            majorNameList=getBaseMapper().getDistinctMajorNameList(enrollmentPlanApplyRO);
            collegeList = getBaseMapper().getDistinctTrainingCollegeList(enrollmentPlanApplyRO);
            //如果教学点是这两个天河越平，南方人才，能获取的教学点不止是本身，而是List<String>{天河越平，南方人才}, 学院是获取所有学院
            String loginId = (String) StpUtil.getLoginId();
            PlatformUserPO platformUserPO = platformUserMapper.selectOne(Wrappers.<PlatformUserPO>lambdaQuery().eq(PlatformUserPO::getUsername, loginId));
            LambdaQueryWrapper<TeachingPointAdminInformationPO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TeachingPointAdminInformationPO::getUserId, platformUserPO.getUserId());

            //如果查出来管理的教学点超过了一个，说明是这两个天河越平，南方人才
            List<TeachingPointAdminInformationPO> teachingPointAdminInformationPOS = teachingPointAdminInformationMapper.selectList(queryWrapper);
            if (teachingPointAdminInformationPOS.size()>1){
                teachingPointNameList.add("广州天河越平教学点");
                teachingPointNameList.add("广州南方人才教学点");
            }else {
                //否则则是本身
                TeachingPointInformationPO teachingPointInformationPO = teachingPointInformationMapper.selectById(teachingPointAdminInformationPOS.get(0).getTeachingPointId());
                teachingPointNameList.add(teachingPointInformationPO.getTeachingPointName());
           }

        }
        enrollmentPlanFilterItemsVO.setMajorNameList(majorNameList);
        enrollmentPlanFilterItemsVO.setCollegeList(collegeList);
        enrollmentPlanFilterItemsVO.setTeachingPointNameList(teachingPointNameList);

        return SaResult.ok("成功获取招生计划筛选项").setData(enrollmentPlanFilterItemsVO);
    }

    /**
     * 三个身份 教学点、二级学院都必须限制
     * 教学点、二级学院 都可以把招生计划的状态往前推 只要
     * 招生计划的 status 处于自己的角色下
     * @param enrollmentPlanId
     * @return
     */
    public SaResult approvalEnrollmentPlan(Long enrollmentPlanId) {
        List<String> roleList = StpUtil.getRoleList();
        EnrollmentPlanPO enrollmentPlanPO = getBaseMapper().selectById(enrollmentPlanId);
        if(roleList.contains(RoleEnum.TEACHING_POINT_ADMIN.getRoleName())){
            // 教学点管理员
            if(enrollmentPlanPO.getStatus().equals(RoleEnum.TEACHING_POINT_ADMIN.getRoleName())){
                // 在推之前先判断教学点是否属于它
                TeachingPointInformationPO teachingPointInformationPO = teachingPointInformationService.getBaseMapper().selectOne(new LambdaQueryWrapper<TeachingPointInformationPO>()
                        .eq(TeachingPointInformationPO::getTeachingPointId, enrollmentPlanPO.getTeachingPointId()));
                List<TeachingPointInformationPO> userBelongTeachingPoints = scnuXueliTools.getUserBelongTeachingPoints();
                boolean exists = userBelongTeachingPoints.stream()
                        .anyMatch(tp -> tp.getTeachingPointName().equals(teachingPointInformationPO.getTeachingPointName()));

                if (!exists) {
                    return ResultCode.ENROLLMENT_PLAN_FAIL51.generateErrorResultInfo();
                }

                // 往下推一级 来到二级学院管理员
                enrollmentPlanPO.setStatus(RoleEnum.SECOND_COLLEGE_ADMIN.getRoleName());
                int i = getBaseMapper().updateById(enrollmentPlanPO);
                if(i > 0){
                    return SaResult.ok("提交成功");
                }else{
                    return ResultCode.ENROLLMENT_PLAN_FAIL37.generateErrorResultInfo();
                }
            }else{
                return ResultCode.ENROLLMENT_PLAN_FAIL38.generateErrorResultInfo();
            }
        }else if(roleList.contains(RoleEnum.SECOND_COLLEGE_ADMIN.getRoleName())){
            // 二级学院管理员
            if(enrollmentPlanPO.getStatus().equals(RoleEnum.SECOND_COLLEGE_ADMIN.getRoleName())){
                // 在推之前 先看看是否是自己学院的招生计划
                CollegeInformationPO collegeInformationPO = collegeInformationService.getBaseMapper().selectOne(new LambdaQueryWrapper<CollegeInformationPO>()
                        .eq(CollegeInformationPO::getCollegeId, enrollmentPlanPO.getCollegeId()));
                CollegeInformationPO userBelongCollege = scnuXueliTools.getUserBelongCollege();
                if(!collegeInformationPO.getCollegeName().equals(userBelongCollege.getCollegeName())){
                    return ResultCode.ENROLLMENT_PLAN_FAIL52.generateErrorResultInfo();
                }

                // 往下推一级 来到招生办管理员
                enrollmentPlanPO.setStatus(RoleEnum.ADMISSIONS_DEPARTMENT_ADMINISTRATOR.getRoleName());
                int i = getBaseMapper().updateById(enrollmentPlanPO);
                if(i > 0){
                    return SaResult.ok("提交成功");
                }else{
                    return ResultCode.ENROLLMENT_PLAN_FAIL37.generateErrorResultInfo();
                }
            }else{
                return ResultCode.ENROLLMENT_PLAN_FAIL38.generateErrorResultInfo();
            }
        }else if(roleList.contains(RoleEnum.ADMISSIONS_DEPARTMENT_ADMINISTRATOR.getRoleName())){
            //如果是招生办管理员，需要更新状态为已完成
            enrollmentPlanPO.setStatus("已完成");
            int i = getBaseMapper().updateById(enrollmentPlanPO);
            if(i > 0){
                return SaResult.ok("提交成功");
            }

        }

        return ResultCode.ENROLLMENT_PLAN_FAIL38.generateErrorResultInfo();
    }

    /**
     * 二级学院管理员、招生办管理员可以往下打回 让其回到对应的角色 从而她/他就可以去修改招生计划了
     * @param enrollmentPlanId
     * @param roleName
     * @return
     */
    public SaResult approvalRollbackEnrollmentPlan(Long enrollmentPlanId, String roleName,String remarks ) {
        List<String> roleList = StpUtil.getRoleList();
        EnrollmentPlanPO enrollmentPlanPO = getBaseMapper().selectById(enrollmentPlanId);
        if(roleList.contains(RoleEnum.ADMISSIONS_DEPARTMENT_ADMINISTRATOR.getRoleName())){
            // 招生办管理员 它可以选择打回给 教学点/二级学院管理员
            if(enrollmentPlanPO.getStatus().equals(RoleEnum.ADMISSIONS_DEPARTMENT_ADMINISTRATOR.getRoleName())){
                EnrollmentPlanPO enrollmentPlanPO1 = enrollmentPlanPO.setStatus(roleName);
                enrollmentPlanPO1.setRemarks(remarks);
                int i = getBaseMapper().updateById(enrollmentPlanPO1);
                if(i > 0){
                    return SaResult.ok("打回成功");
                }else{
                    return ResultCode.ENROLLMENT_PLAN_FAIL39.generateErrorResultInfo();
                }
            }else{
                return ResultCode.ENROLLMENT_PLAN_FAIL53.generateErrorResultInfo();
            }

        }else if(roleList.contains(RoleEnum.SECOND_COLLEGE_ADMIN.getRoleName())){
            // 二级学院管理员 它可以选择打回给 教学点管理员
            if(enrollmentPlanPO.getStatus().equals(RoleEnum.SECOND_COLLEGE_ADMIN.getRoleName())){
                // 判断是不是本学院的招生计划
                String collegeId = enrollmentPlanPO.getCollegeId();
                CollegeInformationPO collegeInformationPO = collegeInformationService.getBaseMapper().selectOne(new LambdaQueryWrapper<CollegeInformationPO>()
                        .eq(CollegeInformationPO::getCollegeId, collegeId));
                CollegeInformationPO userBelongCollege = scnuXueliTools.getUserBelongCollege();
                if(!collegeInformationPO.getCollegeName().equals(userBelongCollege.getCollegeName())){
                    return ResultCode.ENROLLMENT_PLAN_FAIL53.generateErrorResultInfo();
                }

                if(roleName.equals(RoleEnum.TEACHING_POINT_ADMIN.getRoleName())){
                    EnrollmentPlanPO enrollmentPlanPO1 = enrollmentPlanPO.setStatus(roleName);
                    enrollmentPlanPO1.setRemarks(remarks);
                    int i = getBaseMapper().updateById(enrollmentPlanPO1);
                    if(i > 0){
                        return SaResult.ok("打回成功");
                    }else{
                        return ResultCode.ENROLLMENT_PLAN_FAIL39.generateErrorResultInfo();
                    }
                }else{
                    return ResultCode.ENROLLMENT_PLAN_FAIL40.generateErrorResultInfo();
                }
            }else{
                return ResultCode.ENROLLMENT_PLAN_FAIL53.generateErrorResultInfo();
            }

        }

        return ResultCode.ENROLLMENT_PLAN_FAIL41.generateErrorResultInfo();
    }

    /**
     * 编辑招生计划
     * @param enrollmentPlanApplyRO
     * @return
     */
    public SaResult editEnrollmentPlan(EnrollmentPlanApplyRO enrollmentPlanApplyRO) {
        List<String> roleList = StpUtil.getRoleList();
        EnrollmentPlanPO enrollmentPlanPO = getBaseMapper().selectById(enrollmentPlanApplyRO.getId());
        // 超级管理员除外
        boolean identEdit = false;
        if(roleList.contains(enrollmentPlanPO.getStatus())){
            // 只有当招生计划的状态位 为 当前角色时 它才具有编辑权
            identEdit = true;
        }else if(roleList.contains(RoleEnum.SUPER_ADMIN.getRoleName())){
            identEdit = true;
        }

        if(identEdit){
            // 开始编辑
            // 更新专业名称
            if(enrollmentPlanApplyRO.getMajorName() != enrollmentPlanPO.getMajorName()){
                enrollmentPlanPO.setMajorName(enrollmentPlanApplyRO.getMajorName());
            }

            // 更新学习形式
            if(enrollmentPlanApplyRO.getStudyForm() != enrollmentPlanPO.getStudyForm()){
                enrollmentPlanPO.setStudyForm(enrollmentPlanApplyRO.getStudyForm());
            }

            // 更新学制
            if(enrollmentPlanApplyRO.getEducationLength() != enrollmentPlanPO.getEducationLength()){
                enrollmentPlanPO.setEducationLength(enrollmentPlanApplyRO.getEducationLength());
            }

            // 更新培养层次
            if(enrollmentPlanApplyRO.getTrainingLevel() != enrollmentPlanPO.getTrainingLevel()){
                enrollmentPlanPO.setTrainingLevel(enrollmentPlanApplyRO.getTrainingLevel());
            }

            // 更新招生人数
            if(enrollmentPlanApplyRO.getEnrollmentNumber() != enrollmentPlanPO.getEnrollmentNumber()){
                enrollmentPlanPO.setEnrollmentNumber(enrollmentPlanApplyRO.getEnrollmentNumber());
            }

            // 更新招生对象
            if(enrollmentPlanApplyRO.getTargetStudents() != enrollmentPlanPO.getTargetStudents()){
                enrollmentPlanPO.setTargetStudents(enrollmentPlanApplyRO.getTargetStudents());
            }

            // 更新招生区域
            if(enrollmentPlanApplyRO.getEnrollmentRegion() != enrollmentPlanPO.getEnrollmentRegion()){
                enrollmentPlanPO.setEnrollmentRegion(enrollmentPlanApplyRO.getEnrollmentRegion());
            }

            // 更新具体办学地点
            if(enrollmentPlanApplyRO.getSchoolLocation() != enrollmentPlanPO.getSchoolLocation()){
                enrollmentPlanPO.setSchoolLocation(enrollmentPlanApplyRO.getSchoolLocation());
            }

            // 更新联系电话
            if(enrollmentPlanApplyRO.getContactNumber() != enrollmentPlanPO.getContactNumber()){
                enrollmentPlanPO.setContactNumber(enrollmentPlanApplyRO.getContactNumber());
            }


            int i = getBaseMapper().updateById(enrollmentPlanPO);
            if(i > 0){
                return SaResult.ok("编辑招生计划成功");
            }else{
                return ResultCode.ENROLLMENT_PLAN_FAIL35.generateErrorResultInfo();
            }
        }

        return ResultCode.ENROLLMENT_PLAN_FAIL36.generateErrorResultInfo();
    }

    /**
     * 批量提交招生计划
     * 有两种角色 教学点管理员 二级学院管理员
     * @param enrollmentPlanApplyRO
     * @return
     */
    @Transactional
    public SaResult batchApprovalEnrollmentPlan(EnrollmentPlanApplyRO enrollmentPlanApplyRO) {
        if(enrollmentPlanApplyRO.getYear() == null){
            int currentYear = Year.now().getValue();
            enrollmentPlanApplyRO.setYear(currentYear);
        }
        List<String> roleList = StpUtil.getRoleList();
        if(roleList.contains(RoleEnum.TEACHING_POINT_ADMIN.getRoleName())){
            // 教学点管理员批量提交 将这一批的招生计划往下推
            List<TeachingPointInformationPO> userBelongTeachingPoints = scnuXueliTools.getUserBelongTeachingPoints();

            List<String> teachingPointNameList = userBelongTeachingPoints.stream().map(TeachingPointInformationPO::getTeachingPointName).collect(Collectors.toList());


            List<String> teachingPointNameList1 = enrollmentPlanApplyRO.getTeachingPointNameList();
            if(teachingPointNameList1 == null){
                enrollmentPlanApplyRO.setTeachingPointNameList(teachingPointNameList);
            }
            List<EnrollmentPlanPO> enrollmentPlanPOList = getBaseMapper().queryAllEnrollmentPlans(enrollmentPlanApplyRO);
            for(EnrollmentPlanPO enrollmentPlanPO : enrollmentPlanPOList){
                if(enrollmentPlanPO.getStatus().equals(RoleEnum.TEACHING_POINT_ADMIN.getRoleName())){
                    // 只针对状态为教学点管理员 才往下推
                    enrollmentPlanPO.setStatus(RoleEnum.SECOND_COLLEGE_ADMIN.getRoleName());

                }
            }
            // 使用 MyBatis-Plus 批量更新
            boolean b = updateBatchById(enrollmentPlanPOList);
            if(b){
                return SaResult.ok("批量提交成功");
            }else{
                return ResultCode.ENROLLMENT_PLAN_FAIL44.generateErrorResultInfo();
            }
        }else if(roleList.contains(RoleEnum.SECOND_COLLEGE_ADMIN.getRoleName())){
            // 二级学院管理员批量提交 将这一批招生计划往下推
            CollegeInformationPO userBelongCollege = scnuXueliTools.getUserBelongCollege();
            enrollmentPlanApplyRO.setCollege(userBelongCollege.getCollegeName());
            List<EnrollmentPlanPO> enrollmentPlanPOList = getBaseMapper().queryAllEnrollmentPlans(enrollmentPlanApplyRO);
            for(EnrollmentPlanPO enrollmentPlanPO : enrollmentPlanPOList){
                if(enrollmentPlanPO.getStatus().equals(RoleEnum.SECOND_COLLEGE_ADMIN.getRoleName())){
                    // 只针对状态为二级学院管理员 才往下推
                    enrollmentPlanPO.setStatus(RoleEnum.ADMISSIONS_DEPARTMENT_ADMINISTRATOR.getRoleName());

                }
            }
            // 使用 MyBatis-Plus 批量更新
            boolean b = updateBatchById(enrollmentPlanPOList);
            if(b){
                return SaResult.ok("批量提交成功");
            }else{
                return ResultCode.ENROLLMENT_PLAN_FAIL44.generateErrorResultInfo();
            }
        }
        return ResultCode.ENROLLMENT_PLAN_FAIL43.generateErrorResultInfo();
    }

    /**
     * 批量打回招生计划
     * @param enrollmentPlanApplyRO
     * @return
     */
    public SaResult batchApprovalRollbackEnrollmentPlan(EnrollmentPlanApplyRO enrollmentPlanApplyRO) {

        if(enrollmentPlanApplyRO.getYear() == null){
            int currentYear = Year.now().getValue();
            enrollmentPlanApplyRO.setYear(currentYear);
        }
        List<String> roleList = StpUtil.getRoleList();
        if(roleList.contains(RoleEnum.ADMISSIONS_DEPARTMENT_ADMINISTRATOR.getRoleName())){
            // 招生办管理员可以选择打回给教学点管理员 或者招生办管理员
            List<EnrollmentPlanPO> enrollmentPlanPOList = getBaseMapper().queryAllEnrollmentPlans(enrollmentPlanApplyRO);
            for(EnrollmentPlanPO enrollmentPlanPO : enrollmentPlanPOList){
                if(enrollmentPlanPO.getStatus().equals(RoleEnum.ADMISSIONS_DEPARTMENT_ADMINISTRATOR.getRoleName())){
                    enrollmentPlanPO.setStatus(enrollmentPlanApplyRO.getRoleName());
                }
            }
            // 使用 MyBatis-Plus 批量更新
            boolean b = updateBatchById(enrollmentPlanPOList);
            if(b){
                return SaResult.ok("批量打回成功");
            }else{
                return ResultCode.ENROLLMENT_PLAN_FAIL44.generateErrorResultInfo();
            }
        }else if(roleList.contains(RoleEnum.SECOND_COLLEGE_ADMIN.getRoleName())){
            // 二级学院管理员只能打回给教学点管理员
            if(!enrollmentPlanApplyRO.getRoleName().equals(RoleEnum.TEACHING_POINT_ADMIN.getRoleName())){
                return ResultCode.ENROLLMENT_PLAN_FAIL48.generateErrorResultInfo();
            }
            enrollmentPlanApplyRO.setCollege(scnuXueliTools.getUserBelongCollege().getCollegeName());
            List<EnrollmentPlanPO> enrollmentPlanPOList = getBaseMapper().queryAllEnrollmentPlans(enrollmentPlanApplyRO);
            for(EnrollmentPlanPO enrollmentPlanPO : enrollmentPlanPOList){
                if(enrollmentPlanPO.getStatus().equals(RoleEnum.TEACHING_POINT_ADMIN.getRoleName())){
                    enrollmentPlanPO.setStatus(enrollmentPlanApplyRO.getRoleName());
                }
            }
            // 使用 MyBatis-Plus 批量更新
            boolean b = updateBatchById(enrollmentPlanPOList);
            if(b){
                return SaResult.ok("批量打回成功");
            }else{
                return ResultCode.ENROLLMENT_PLAN_FAIL44.generateErrorResultInfo();
            }
        }
        return ResultCode.ENROLLMENT_PLAN_FAIL49.generateErrorResultInfo();
    }

    /**
     * 下载招生计划汇总表
     * @param enrollmentPlanApplyRO
     * @return
     */
    public List<ApprovalPlanSummaryVO> downloadApprovalPlanSummary(EnrollmentPlanApplyRO enrollmentPlanApplyRO) {
        enrollmentPlanApplyRO.setStatus("已完成");
        List<EnrollmentPlanPO> enrollmentPlanPOList = getBaseMapper().queryAllEnrollmentPlans(enrollmentPlanApplyRO);

        List<EnrollmentPlanPO> aggregatedEnrollmentPlans = enrollmentPlanPOList.stream()
                .collect(Collectors.groupingBy(EnrollmentPlanPO::getCollege))
                .values().stream()
                .flatMap(plans -> plans.stream()
                        .sorted(Comparator.comparing(plan -> { // 先按照trainingLevel进行排序
                            String trainingLevel = plan.getTrainingLevel();
                            if ("专升本".equals(trainingLevel)) {
                                return 1;
                            } else if ("高起专".equals(trainingLevel)) {
                                return 2;
                            } else {
                                return 0;
                            }
                        }))
                )
                .collect(Collectors.toList());

        List<ApprovalPlanSummaryVO> approvalPlanSummaryVOList = new ArrayList<>();
        int index = 1;

        Map<String, String> collegeMap = new HashMap<>();
        Map<String, String> teachingPointMap = new HashMap<>();

        for(EnrollmentPlanPO enrollmentPlanPO : aggregatedEnrollmentPlans){

            if(!collegeMap.containsKey(enrollmentPlanPO.getCollegeId())){
                CollegeInformationPO collegeInformationPO = collegeInformationService.getBaseMapper().selectOne(new LambdaQueryWrapper<CollegeInformationPO>()
                        .eq(CollegeInformationPO::getCollegeId, enrollmentPlanPO.getCollegeId()));
                collegeMap.put(enrollmentPlanPO.getCollegeId(), collegeInformationPO.getCollegeName());
            }

            if(!teachingPointMap.containsKey(enrollmentPlanPO.getTeachingPointId())){
                TeachingPointInformationPO teachingPointInformationPO = teachingPointInformationService.getBaseMapper().
                        selectOne(new LambdaQueryWrapper<TeachingPointInformationPO>()
                        .eq(TeachingPointInformationPO::getTeachingPointId, enrollmentPlanPO.getTeachingPointId()));
                teachingPointMap.put(enrollmentPlanPO.getTeachingPointId(), teachingPointInformationPO.getTeachingPointName());
            }

            ApprovalPlanSummaryVO approvalPlanSummaryVO = new ApprovalPlanSummaryVO()
                    .setIndex(index)
                    .setCollege(collegeMap.get(enrollmentPlanPO.getCollegeId()))
                    .setLevel(enrollmentPlanPO.getTrainingLevel())
                    .setMajorName(enrollmentPlanPO.getMajorName())
                    .setSchoolLocation(enrollmentPlanPO.getSchoolLocation())
                    .setEnrollmentSubject(enrollmentPlanPO.getEnrollmentSubject())
                    .setStudyForm(enrollmentPlanPO.getStudyForm())
                    .setEducationLength(enrollmentPlanPO.getEducationLength())
                    .setTuition(enrollmentPlanPO.getTuition().intValue() + "元/学年")
                    .setRemarks(enrollmentPlanPO.getRemarks())
                    ;
            approvalPlanSummaryVOList.add(approvalPlanSummaryVO);
            index += 1;
        }

        return approvalPlanSummaryVOList;
    }
}
