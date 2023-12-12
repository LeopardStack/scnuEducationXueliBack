package com.scnujxjy.backendpoint.controller.InterBase;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.lang.hash.Hash;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scnujxjy.backendpoint.constant.enums.OldDataType;
import com.scnujxjy.backendpoint.dao.entity.core_data.PaymentInfoPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.StudentStatusPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseInformationPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.ScoreInformationPO;
import com.scnujxjy.backendpoint.dao.mapper.core_data.PaymentInfoMapper;
import com.scnujxjy.backendpoint.dao.mapper.exam.CourseExamInfoMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.ClassInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.StudentStatusMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.ScoreInformationMapper;
import com.scnujxjy.backendpoint.model.ro.oldData.OldDataFilterRO;
import com.scnujxjy.backendpoint.service.InterBase.AsyncDataSynchronizeService;
import com.scnujxjy.backendpoint.util.SCNUXLJYDatabase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * 旧系统的数据同步 和查看
 * @author 谢辉龙
 */
@RestController
@RequestMapping("/oldDataSynchronize")
@Slf4j
@SaCheckPermission("数据同步")
public class oldSystemDataSynchronize {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private AsyncDataSynchronizeService asyncDataSynchronizeService;

    @GetMapping("/summary")
    public SaResult getChartData() {
        String cacheKey = StpUtil.getLoginIdAsString() + "syncOldDataSummary";
        String taskRunningKey = StpUtil.getLoginIdAsString() + "syncTaskRunning";
        Object cachedData = redisTemplate.opsForValue().get(cacheKey);

        if (cachedData != null) {
            // 直接返回找到的缓存数据
            return SaResult.data(cachedData);
        } else {
            Boolean isTaskRunning = redisTemplate.opsForValue().get(taskRunningKey) != null;
            if (isTaskRunning) {
                return SaResult.ok("数据同步任务已在执行，请稍后查询");
            } else {
                // 假设同步任务最多运行10分钟
                redisTemplate.opsForValue().set(taskRunningKey, true, 10, TimeUnit.MINUTES);
                CompletableFuture<Void> future = asyncDataSynchronizeService.synchronizeData(cacheKey, taskRunningKey);
                return SaResult.ok("数据同步任务正在执行，请稍后查询");
            }
        }
    }

    /**
     * 更新各项数据
     * @return
     */
    @PostMapping("/updateData")
    public SaResult updateData(@RequestBody OldDataFilterRO oldDataFilterRO){
        log.info("更新数据的参数 " + oldDataFilterRO);
        String dataType = oldDataFilterRO.getDataType();

        String cacheKey = StpUtil.getLoginIdAsString() + dataType + "SyncData";
        String taskRunningKey = StpUtil.getLoginIdAsString() + dataType + "TaskRunning";
        // 检查 dataType 是否在枚举类中
        if (!isValidDataType(dataType)) {
            return SaResult.error("无效的数据类型: " + dataType);
        }

        // 检查是否有缓存的同步结果
        Object cachedData = redisTemplate.opsForValue().get(cacheKey);
        if (cachedData != null && !oldDataFilterRO.getUpdateAny()) {
            // 直接返回同步结果，前提是 redis 中有内容 并且没有强制更新
            return SaResult.ok().setData(cachedData);
        }

        Boolean isTaskRunning = redisTemplate.opsForValue().get(taskRunningKey) != null;
        if (isTaskRunning) {
            return SaResult.ok("数据同步任务已在执行，请稍后查询");
        } else {
            redisTemplate.opsForValue().set(taskRunningKey, true, 3, TimeUnit.HOURS);
            if (dataType.equals(OldDataType.STUDENT_STATUS.getOld_data_type())) {
                // 学籍信息同步
                asyncDataSynchronizeService.synchronizeStudentStatus(cacheKey, taskRunningKey, oldDataFilterRO);
            } else if (dataType.equals(OldDataType.STUDENT_FEES.getOld_data_type())) {
                // 缴费信息同步
                asyncDataSynchronizeService.synchronizeStudentFees(cacheKey, taskRunningKey, oldDataFilterRO);
            } else if (dataType.equals(OldDataType.CLASS_INFO.getOld_data_type())) {
                // 班级信息同步
            } else if (dataType.equals(OldDataType.GRADE_INFO.getOld_data_type())) {
                // 成绩信息同步
                asyncDataSynchronizeService.synchronizeStudentGrades(cacheKey, taskRunningKey, oldDataFilterRO);
            } else if (dataType.equals(OldDataType.TEACHING_PLANS.getOld_data_type())) {
                // 教学计划同步
                asyncDataSynchronizeService.synchronizeTeachingPlans(cacheKey, taskRunningKey, oldDataFilterRO);
            } else if (dataType.equals(OldDataType.STUDENT_STATUS_CHANGE.getOld_data_type())) {
                // 学籍异动数据同步
                asyncDataSynchronizeService.synchronizeStudentStatusChange(cacheKey, taskRunningKey, oldDataFilterRO);
            } else {
                // 其他形式
                return SaResult.error("错误的数据同步形式").setCode(2001);
            }
        }
        return SaResult.ok();
    }

    private boolean isValidDataType(String dataType) {
        for (OldDataType type : OldDataType.values()) {
            if (type.getOld_data_type().equals(dataType)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 获取特定数据的新旧系统对比
     * @param oldDataFilterRO 只需要用到数据类型这一个参数
     * @return
     */
    @PostMapping("/get_certain_data_type_summary")
    public SaResult getSummaryCertainDataType(@RequestBody OldDataFilterRO oldDataFilterRO){
        log.info("获取新旧系统对比详情数据的参数 " + oldDataFilterRO);
        String dataType = oldDataFilterRO.getDataType();

        String cacheKey = StpUtil.getLoginIdAsString() + dataType + "Summary";
        String taskRunningKey = StpUtil.getLoginIdAsString() + dataType + "SummaryTaskRunning";

//        if(oldDataFilterRO.getStartYear() )

        // 检查 dataType 是否在枚举类中
        if (!isValidDataType(dataType)) {
            return SaResult.error("无效的数据类型: " + dataType);
        }

        // 检查是否有缓存的同步结果
        Object cachedData = redisTemplate.opsForValue().get(cacheKey);
        if (cachedData != null) {
            // 直接返回同步结果
            return SaResult.ok().setData(cachedData);
        }

        Boolean isTaskRunning = redisTemplate.opsForValue().get(taskRunningKey) != null;
        if (isTaskRunning) {
            return SaResult.ok("数据对比任务已在执行，请稍后查询");
        } else {
            redisTemplate.opsForValue().set(taskRunningKey, true, 20, TimeUnit.MINUTES);
            if (dataType.equals(OldDataType.STUDENT_STATUS.getOld_data_type())) {
                // 学籍信息新旧系统对比
                asyncDataSynchronizeService.studentStatusSummary(cacheKey, taskRunningKey, oldDataFilterRO);
            } else if (dataType.equals(OldDataType.STUDENT_FEES.getOld_data_type())) {
                // 缴费信息新旧系统对比
                asyncDataSynchronizeService.studentFeesSummary(cacheKey, taskRunningKey, oldDataFilterRO);
            } else if (dataType.equals(OldDataType.CLASS_INFO.getOld_data_type())) {
                // 班级信息新旧系统对比
                asyncDataSynchronizeService.classInformationsSummary(cacheKey, taskRunningKey, oldDataFilterRO);
            } else if (dataType.equals(OldDataType.GRADE_INFO.getOld_data_type())) {
                // 成绩信息新旧系统对比
                asyncDataSynchronizeService.studentGradesSummary(cacheKey, taskRunningKey, oldDataFilterRO);
            } else if (dataType.equals(OldDataType.TEACHING_PLANS.getOld_data_type())) {
                // 教学计划新旧系统对比
                asyncDataSynchronizeService.teachingPlansSummary(cacheKey, taskRunningKey, oldDataFilterRO);
            } else if (dataType.equals(OldDataType.STUDENT_STATUS_CHANGE.getOld_data_type())) {
                // 学籍异动数据新旧系统对比
                asyncDataSynchronizeService.studentStatusChangeSummary(cacheKey, taskRunningKey, oldDataFilterRO);
            } else {
                // 其他形式
                return SaResult.error("错误的数据类型").setCode(2001);
            }
        }
        return SaResult.ok();
    }

    @GetMapping("/get_student_basic_data")
    public SaResult getStudentBasicData(String year){
        log.info("获取到请求数据参数 " +  year);
        HashMap<String, Integer> ret =  asyncDataSynchronizeService.getStudentBasicData(year);
        return SaResult.ok().setData(ret);
    }

}
