package com.scnujxjy.backendpoint.service.basic;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.constant.enums.enrollment_plan.EnrollmentPlanEnum;
import com.scnujxjy.backendpoint.dao.entity.basic.GlobalConfigPO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scnujxjy.backendpoint.dao.entity.basic.PermissionPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_point.TeachingPointInformationPO;
import com.scnujxjy.backendpoint.dao.mapper.basic.GlobalConfigMapper;
import com.scnujxjy.backendpoint.dao.mapper.basic.PermissionMapper;
import com.scnujxjy.backendpoint.model.ro.admission_information.EnrollmentPlanRO;
import com.scnujxjy.backendpoint.service.teaching_point.TeachingPointInformationService;
import com.scnujxjy.backendpoint.util.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 谢辉龙
 * @since 2023-10-29
 */
@Service
@Slf4j
public class GlobalConfigService extends ServiceImpl<GlobalConfigMapper, GlobalConfigPO> implements IService<GlobalConfigPO>{

    @Resource
    private TeachingPointInformationService teachingPointInformationService;

    public GlobalConfigPO getGlobalConfigInfo(String keyInfo){
        List<String> globalConfigKeyInfoList = baseMapper.selectList(null).stream().map(GlobalConfigPO::getConfigKey)
                .collect(Collectors.toList());
        if(!globalConfigKeyInfoList.contains(keyInfo)){
            log.error("输入的 全局参数 不合法 " + globalConfigKeyInfoList);
        }
        return baseMapper.selectOne(new LambdaQueryWrapper<GlobalConfigPO>()
                .eq(GlobalConfigPO::getConfigKey, keyInfo));
    }

    /**
     * 获取所有的全局配置参数项
     * @return
     */
    public List<String> getGlobalConfigKeyInfo(){
        return baseMapper.selectList(null).stream().map(GlobalConfigPO::getConfigKey)
                .collect(Collectors.toList());
    }


    /**
     * 招生办管理员设置招生计划 是否开启
     * @param enrollmentPlanRO
     * @return
     */
    public SaResult setupEnrollmentPlanApply(EnrollmentPlanRO enrollmentPlanRO) {
        GlobalConfigPO globalConfigInfo = getGlobalConfigInfo(EnrollmentPlanEnum.ENROLLMENT_SETUP_KEY.getKey());
        if(enrollmentPlanRO.getApplyOpen().equals(Boolean.TRUE)){
            globalConfigInfo.setConfigValue(String.valueOf(enrollmentPlanRO.getApplyOpen()));
        }

        // 对教学点 ID 进行检查
        for(String teachingPointId : enrollmentPlanRO.getTeachingPointIdList()){
            TeachingPointInformationPO teachingPointInformationPO = teachingPointInformationService.getBaseMapper().selectOne(new LambdaQueryWrapper<TeachingPointInformationPO>()
                    .eq(TeachingPointInformationPO::getTeachingPointId, teachingPointId));
            if(teachingPointInformationPO == null){
                log.error(StpUtil.getLoginIdAsString() + " 设置招生计划时  传入的教学点 ID 没法找到该教学点信息");
                return ResultCode.ENROLLMENT_PLAN_FAIL3.generateErrorResultInfo();
            }
        }

        // 将 EnrollmentPlanRO 对象序列化为 JSON 字符串
        String enrollmentPlanJson = JSON.toJSONString(enrollmentPlanRO);
        // 将类名和 JSON 字符串分别存储
        globalConfigInfo.setArgs1(EnrollmentPlanRO.class.getName());
        globalConfigInfo.setArgs2(enrollmentPlanJson);
        getBaseMapper().updateById(globalConfigInfo);

        return SaResult.ok("招生计划申报设置成功");
    }
}
