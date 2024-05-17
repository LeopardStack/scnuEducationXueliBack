package com.scnujxjy.backendpoint.service.admission_information;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.constant.enums.RoleEnum;
import com.scnujxjy.backendpoint.dao.entity.admission_information.AdmissionInformationPO;
import com.scnujxjy.backendpoint.dao.entity.admission_information.EnrollmentPlanPO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scnujxjy.backendpoint.dao.entity.college.CollegeInformationPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_point.TeachingPointInformationPO;
import com.scnujxjy.backendpoint.dao.mapper.admission_information.AdmissionInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.admission_information.EnrollmentPlanMapper;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.admission_information.EnrollmentPlanApplyRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.admission_information.EnrollmentPlanFilterItemsVO;
import com.scnujxjy.backendpoint.service.college.CollegeInformationService;
import com.scnujxjy.backendpoint.service.core_data.TeacherInformationService;
import com.scnujxjy.backendpoint.service.teaching_point.TeachingPointInformationService;
import com.scnujxjy.backendpoint.util.ResultCode;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

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

            TeachingPointInformationPO teachingPointInformationPO = teachingPointInformationService.getBaseMapper().selectOne(new LambdaQueryWrapper<TeachingPointInformationPO>()
                    .eq(TeachingPointInformationPO::getTeachingPointName, enrollmentPlanApplyRO.getTeachingPointName()));
            if (teachingPointInformationPO == null) {
                return ResultCode.ENROLLMENT_PLAN_FAIL17.generateErrorResultInfo();
            }

            // 更新学院信息 教学点信息
            enrollmentPlanPO.setCollegeId(collegeInformationPO.getCollegeId());
            enrollmentPlanPO.setTeachingLocation(teachingPointInformationPO.getTeachingPointName());
            enrollmentPlanPO.setTeachingPointId(teachingPointInformationPO.getTeachingPointId());

        }else if(roleList.contains(RoleEnum.SECOND_COLLEGE_ADMIN.getRoleName())){
            // 二级学院教务员 需要提供教学点
            if(enrollmentPlanApplyRO.getTeachingPointName() == null){
                return ResultCode.ENROLLMENT_PLAN_FAIL17.generateErrorResultInfo();
            }

            TeachingPointInformationPO teachingPointInformationPO = teachingPointInformationService.getBaseMapper().selectOne(new LambdaQueryWrapper<TeachingPointInformationPO>()
                    .eq(TeachingPointInformationPO::getTeachingPointName, enrollmentPlanApplyRO.getTeachingPointName()));
            if (teachingPointInformationPO == null) {
                return ResultCode.ENROLLMENT_PLAN_FAIL17.generateErrorResultInfo();
            }
            enrollmentPlanPO.setTeachingPointId(teachingPointInformationPO.getTeachingPointId());

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
            // 教学点添加完招生计划 此时审核的肯定是 教学点教务员 因为它需要批量提交
            enrollmentPlanPO.setStatus(RoleEnum.TEACHING_POINT_ADMIN.getRoleName());
        }else{
            return ResultCode.ENROLLMENT_PLAN_FAIL18.generateErrorResultInfo();
        }

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
}
