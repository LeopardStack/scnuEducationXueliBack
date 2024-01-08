package com.scnujxjy.backendpoint.service.InterBase;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scnujxjy.backendpoint.constant.enums.OldDataType;
import com.scnujxjy.backendpoint.dao.entity.core_data.PaymentInfoPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.StudentStatusPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseInformationPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.ScoreInformationPO;
import com.scnujxjy.backendpoint.dao.mapper.admission_information.AdmissionInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.core_data.PaymentInfoMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.ClassInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.StudentStatusMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.ScoreInformationMapper;
import com.scnujxjy.backendpoint.model.ro.oldData.OldDataFilterRO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.StudentStatusFilterRO;
import com.scnujxjy.backendpoint.util.SCNUXLJYDatabase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;


@Service
@Slf4j
public class AsyncDataSynchronizeService {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private OldDataSynchronize oldDataSynchronize;

    @Resource
    private StudentStatusMapper studentStatusMapper;

    @Resource
    private AdmissionInformationMapper admissionInformationMapper;

    @Resource
    private ClassInformationMapper classInformationMapper;
    @Resource
    private PaymentInfoMapper paymentInfoMapper;
    @Resource
    private CourseInformationMapper courseInformationMapper;
    @Resource
    private ScoreInformationMapper scoreInformationMapper;

    @Async
    public CompletableFuture<Void> synchronizeData(String cacheKey, String taskRunningKey) {
        try {
            // 执行耗时的数据同步操作
            String result = performDataSynchronization();

            // 将结果存储到 Redis，并设置过期时间
            redisTemplate.opsForValue().set(cacheKey, result, 1, TimeUnit.HOURS);
        } finally {
            // 清除运行标记
            redisTemplate.delete(taskRunningKey);
        }

        return CompletableFuture.completedFuture(null);
    }

    private String performDataSynchronization() {
        log.info("获取新旧系统数据概要开始进行");
        SCNUXLJYDatabase scnuxljyDatabase = new SCNUXLJYDatabase();
        // 实现数据同步逻辑
        StringBuilder ret = new StringBuilder();
        try {
            // 获取当前时间
            String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

            String formattedMsg = timeStamp + " 新旧系统学籍数据的校验";
            ret.append(formattedMsg).append("\n");

            int startYear = 2023;
            int endYear = 2010;
            boolean allEqual = true;


            for (int i = startYear; i >= endYear; i--) {
                Integer integer = studentStatusMapper.selectCount(new LambdaQueryWrapper<StudentStatusPO>().eq(
                        StudentStatusPO::getGrade, "" + i
                ));

                int value_xl = (int) scnuxljyDatabase.getValue("SELECT count(*) FROM STUDENT_VIEW_WITHPIC WHERE NJ='" + i +
                        "' and bshi not like 'WP%';");
                formattedMsg = String.format(i + " 年旧系统学历教育生数量 " + value_xl + " 新系统学历教育生数量 " +
                        integer + ((int) value_xl == integer ? "  一致" : "  不同"));
                ret.append(formattedMsg).append("\n");
                if(integer != value_xl){
                    allEqual = false;
                }

            }

            if (allEqual) {
                formattedMsg = String.format("新旧系统 " + startYear + " 年到 " + endYear + " 年的学籍数据完全相等");
                ret.append(formattedMsg).append("\n");
            }


            formattedMsg = "新旧系统班级信息对比";
            ret.append(formattedMsg).append("\n");

            Integer new_class_count = classInformationMapper.selectCount(null);
            int old_class_count = (int) scnuxljyDatabase.getValue("SELECT count(*) FROM classdata where bshi not like'WP%';");
            if (new_class_count != old_class_count) {
                formattedMsg = String.format("新系统学历教育班级数量 " + new_class_count + " 旧系统学历教育班级数量 " + old_class_count + " 不同");
                ret.append(formattedMsg).append("\n");
            } else {
                formattedMsg = String.format("新系统学历教育班级数量 " + new_class_count + " 旧系统学历教育班级数量 " + old_class_count + " 相同");
                ret.append(formattedMsg).append("\n");
            }


            formattedMsg = "新旧系统缴费数据对比";
            ret.append(formattedMsg).append("\n");

            for (int payYear = 2023; payYear >= 2000; payYear--) {
                Integer new_pay_count = paymentInfoMapper.selectCount(new LambdaQueryWrapper<PaymentInfoPO>().eq(PaymentInfoPO::getGrade, "" + payYear));
                int old_pay_count = (int) scnuxljyDatabase.getValue("SELECT count(*) FROM CWPAY_VIEW WHERE NJ='" + payYear + "'");
                if (new_pay_count != old_pay_count) {
                    formattedMsg = String.format(payYear + "年 新系统学历教育缴费数据 " + new_pay_count + " 旧系统学历教育缴费数据 " + old_pay_count + " 不同");
                    ret.append(formattedMsg).append("\n");
                } else {
                    formattedMsg = String.format(payYear + "年 新系统学历教育缴费数据 " + new_pay_count + " 旧系统学历教育缴费数据 " + old_pay_count + " 相同");
                    ret.append(formattedMsg).append("\n");
                }
            }

            formattedMsg = "新旧系统教学计划对比";
            ret.append(formattedMsg).append("\n");


            startYear = 2023;
            endYear = 2015;
            allEqual = true;
            for (int i = startYear; i >= endYear; i--) {
                Integer new_teachingPlans_count1 = courseInformationMapper.selectCount(new LambdaQueryWrapper<CourseInformationPO>().
                        eq(CourseInformationPO::getGrade, "" + i));
                String year_c = i + "";
                year_c = year_c.substring(year_c.length() - 2);
                int old_teachingPlans_count1 = (int) scnuxljyDatabase.getValue(
                        "select count(*) from courseDATA where bshi not LIKE 'WP%' and bshi LIKE '" + year_c + "%';");

                if (new_teachingPlans_count1 != old_teachingPlans_count1) {
                    allEqual = false;
                    formattedMsg = String.format(i +
                            " 年，新系统学历教育教学计划数量 " + new_teachingPlans_count1 + " 旧系统学历教育教学计划数量 " +
                            old_teachingPlans_count1 + " 两者不同");
                    ret.append(formattedMsg).append("\n");
                }
            }
            if (allEqual) {
                formattedMsg = String.format("新旧系统 " + startYear + " 年到 " + endYear + " 年的教学计划完全相等");
                ret.append(formattedMsg).append("\n");
            }


            formattedMsg = "新旧系统成绩数量对比";
            ret.append(formattedMsg).append("\n");

            startYear = 2023;
            endYear = 2015;
            allEqual = true;
            for (int i = startYear; i >= endYear; i--) {
                Integer new_gradeInformation_count = scoreInformationMapper.selectCount(new LambdaQueryWrapper<ScoreInformationPO>().
                        eq(ScoreInformationPO::getGrade, "" + i));

                int old_gradeInformation_count = (int) scnuxljyDatabase.getValue(
                        "select count(*) from RESULT_VIEW_FULL where nj='" + i + "' and bshi not LIKE 'WP%';");
                formattedMsg = String.format(i + " 年，新系统学历教育成绩数量 " +
                        new_gradeInformation_count + " 旧系统学历教育成绩数量 " +
                        old_gradeInformation_count + (new_gradeInformation_count != old_gradeInformation_count
                        ? " 两者不同" : " 两者相同"));
                ret.append(formattedMsg).append("\n");
                if(new_gradeInformation_count != old_gradeInformation_count){
                    allEqual = false;
                }

            }
            if (allEqual) {
                formattedMsg = String.format("新旧系统 " + startYear + " 年到 " + endYear + " 年的成绩数据完全相等");
                ret.append(formattedMsg).append("\n");
            }


            ret.append("开始记录校验同步之后的新旧系统的数据差异");
        } catch (Exception e) {
            ret.append(e.toString());
        }finally {
            scnuxljyDatabase.close();
        }
        log.info("获取新旧系统数据概要操作结束");
        return ret.toString();
    }

    @Async
    public CompletableFuture<Void> synchronizeStudentStatus(String cacheKey, String taskRunningKey, OldDataFilterRO oldDataFilterRO) {
        try {
            String result = performStudentStatusSync(oldDataFilterRO);
            redisTemplate.opsForValue().set(cacheKey, result, 3, TimeUnit.HOURS);
        } finally {
            redisTemplate.delete(taskRunningKey);
        }
        return CompletableFuture.completedFuture(null);
    }

    private String performStudentStatusSync(OldDataFilterRO oldDataFilterRO) {
        // TODO: 实现学籍信息同步逻辑
        try {
            oldDataSynchronize.synchronizeStudentStatusData(Integer.parseInt(oldDataFilterRO.getEndYear()),
                    Integer.parseInt(oldDataFilterRO.getStartYear()), true);
        }catch (Exception e){
            log.error("同步学籍数据错误 " + e.toString());
            return "学籍同步失败";
        }
        return "学籍信息同步结果";
    }

    @Async
    public CompletableFuture<Void> synchronizeStudentFees(String cacheKey, String taskRunningKey, OldDataFilterRO oldDataFilterRO) {
        try {
            String result = performStudentFeesSync(oldDataFilterRO);
            redisTemplate.opsForValue().set(cacheKey, result, 3, TimeUnit.HOURS);
        } finally {
            redisTemplate.delete(taskRunningKey);
        }
        return CompletableFuture.completedFuture(null);
    }

    private String performStudentFeesSync(OldDataFilterRO oldDataFilterRO) {
        // TODO: 实现缴费信息同步逻辑
        try {
//            paymentInfoMapper.truncateTable();
            for(int year = Integer.parseInt(oldDataFilterRO.getEndYear()); year >= Integer.parseInt(oldDataFilterRO.getStartYear()); year--){
                int delete = paymentInfoMapper.delete(new LambdaQueryWrapper<PaymentInfoPO>().eq(PaymentInfoPO::getGrade, "" + year));
                log.info("删除 " + year + " 年的缴费数据，开始同步");
                oldDataSynchronize.synchronizePaymentInfoData(true, true, String.valueOf(year));
            }

        }catch (Exception e){
            log.error("同步缴费数据错误 " + e.toString());
            return "同步缴费数据失败";
        }
        return "同步缴费数据成功";
    }

    @Async
    public CompletableFuture<Void> synchronizeStudentGrades(String cacheKey, String taskRunningKey, OldDataFilterRO oldDataFilterRO) {
        try {
            String result = performStudentGradesSync(oldDataFilterRO);
            redisTemplate.opsForValue().set(cacheKey, result, 3, TimeUnit.HOURS);
        } finally {
            redisTemplate.delete(taskRunningKey);
        }
        return CompletableFuture.completedFuture(null);
    }

    private String performStudentGradesSync(OldDataFilterRO oldDataFilterRO) {
        // TODO: 实现成绩信息同步逻辑
        for(int startYear = Integer.parseInt(oldDataFilterRO.getStartYear()); startYear <= Integer.parseInt(oldDataFilterRO.getEndYear()); startYear ++){
            try {
                int delete = scoreInformationMapper.delete(new LambdaQueryWrapper<ScoreInformationPO>().
                        eq(ScoreInformationPO::getGrade, "" + startYear));
                log.info("查看删除 " + startYear + "年 所有成绩的结果 " + delete);
                oldDataSynchronize.synchronizeGradeInformationData(startYear, startYear, true);
            }catch (Exception e){
                log.error("同步成绩数据错误 " + e.toString());
                return "同步 " + startYear + " 年成绩数据失败";
            }
        }
        return "同步成绩数据成功";
    }

    @Async
    public CompletableFuture<Void> synchronizeTeachingPlans(String cacheKey, String taskRunningKey, OldDataFilterRO oldDataFilterRO) {
        try {
            String result = "";
            try {
                oldDataSynchronize.synchronizeTeachingPlansData(true, true);
                result = "同步教学计划成功";
            }catch (Exception e){
                log.error("同步教学计划数据错误 " + e.toString());
                result = "同步教学计划失败";
            }
            redisTemplate.opsForValue().set(cacheKey, result, 3, TimeUnit.HOURS);
        } finally {
            redisTemplate.delete(taskRunningKey);
        }
        return CompletableFuture.completedFuture(null);
    }


    @Async
    public CompletableFuture<Void> synchronizeStudentStatusChange(String cacheKey, String taskRunningKey, OldDataFilterRO oldDataFilterRO) {
        try {
            String result = performStudentStatusChangeSync();
            redisTemplate.opsForValue().set(cacheKey, result, 3, TimeUnit.HOURS);
        } finally {
            redisTemplate.delete(taskRunningKey);
        }
        return CompletableFuture.completedFuture(null);
    }

    private String performStudentStatusChangeSync() {
        // TODO: 实现学籍异动信息同步逻辑
        return "学籍异动信息同步结果";
    }

    @Async
    public CompletableFuture<Void> studentStatusSummary(String cacheKey, String taskRunningKey, OldDataFilterRO oldDataFilterRO) {
        try {
            StringBuilder ret = new StringBuilder();
            // 获取当前时间
            String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

            String formattedMsg = timeStamp + " 新旧系统学籍数据的校验";
            ret.append(formattedMsg).append("\n");

            int startYear = Integer.parseInt(oldDataFilterRO.getEndYear());
            int endYear = Integer.parseInt(oldDataFilterRO.getStartYear());
            boolean allEqual = true;
            SCNUXLJYDatabase scnuxljyDatabase = new SCNUXLJYDatabase();

            for (int i = startYear; i >= endYear; i--) {
                Integer integer = studentStatusMapper.selectCount(new LambdaQueryWrapper<StudentStatusPO>().eq(
                        StudentStatusPO::getGrade, "" + i
                ));

                int value_xl = (int) scnuxljyDatabase.getValue("SELECT count(*) FROM STUDENT_VIEW_WITHPIC WHERE NJ='" + i +
                        "' and bshi not like 'WP%';");
                formattedMsg = String.format(i + " 年旧系统学历教育生数量 " + value_xl + " 新系统学历教育生数量 " +
                        integer + ((int) value_xl == integer ? "  一致" : "  不同"));
                ret.append(formattedMsg).append("\n");
                if(integer != value_xl){
                    allEqual = false;
                }

            }

            if (allEqual) {
                formattedMsg = String.format("新旧系统 " + startYear + " 年到 " + endYear + " 年的学籍数据完全相等");
                ret.append(formattedMsg).append("\n");
            }
            redisTemplate.opsForValue().set(cacheKey, ret.toString(), 10, TimeUnit.MINUTES);
        } finally {
            redisTemplate.delete(taskRunningKey);
        }
        return CompletableFuture.completedFuture(null);
        
    }


    @Async
    public CompletableFuture<Void> studentFeesSummary(String cacheKey, String taskRunningKey, OldDataFilterRO oldDataFilterRO) {
        SCNUXLJYDatabase scnuxljyDatabase = new SCNUXLJYDatabase();
        try{
            StringBuilder ret = new StringBuilder();
            // 获取当前时间
            String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            String formattedMsg = timeStamp + "新旧系统缴费数据对比";
            ret.append(formattedMsg).append("\n");

            for (int payYear = Integer.parseInt(oldDataFilterRO.getEndYear()); payYear >= Integer.parseInt(oldDataFilterRO.getStartYear()); payYear--) {
                Integer new_pay_count = paymentInfoMapper.selectCount(new LambdaQueryWrapper<PaymentInfoPO>().eq(PaymentInfoPO::getGrade, "" + payYear));
                int old_pay_count = (int) scnuxljyDatabase.getValue("SELECT count(*) FROM CWPAY_VIEW WHERE NJ='" + payYear + "'");
                if (new_pay_count != old_pay_count) {
                    formattedMsg = String.format(payYear + "年 新系统学历教育缴费数据 " + new_pay_count + " 旧系统学历教育缴费数据 " + old_pay_count + " 不同");
                    ret.append(formattedMsg).append("\n");
                } else {
                    formattedMsg = String.format(payYear + "年 新系统学历教育缴费数据 " + new_pay_count + " 旧系统学历教育缴费数据 " + old_pay_count + " 相同");
                    ret.append(formattedMsg).append("\n");
                }
            }
            redisTemplate.opsForValue().set(cacheKey, ret.toString(), 10, TimeUnit.MINUTES);
        }catch (Exception e){
            log.error("获取缴费对比信息失败 " + e);
        }finally {
            scnuxljyDatabase.close();
            redisTemplate.delete(taskRunningKey);
        }
        return CompletableFuture.completedFuture(null);
    }


    @Async
    public CompletableFuture<Void> classInformationsSummary(String cacheKey, String taskRunningKey, OldDataFilterRO oldDataFilterRO) {
        SCNUXLJYDatabase scnuxljyDatabase = new SCNUXLJYDatabase();
        try {
            StringBuilder ret = new StringBuilder();
            // 获取当前时间
            String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            String formattedMsg = timeStamp + "新旧系统班级信息对比";
            ret.append(formattedMsg).append("\n");

            Integer new_class_count = classInformationMapper.selectCount(null);
            int old_class_count = (int) scnuxljyDatabase.getValue("SELECT count(*) FROM classdata where bshi not like'WP%';");
            if (new_class_count != old_class_count) {
                formattedMsg = String.format("新系统学历教育班级数量 " + new_class_count + " 旧系统学历教育班级数量 " + old_class_count + " 不同");
                ret.append(formattedMsg).append("\n");
            } else {
                formattedMsg = String.format("新系统学历教育班级数量 " + new_class_count + " 旧系统学历教育班级数量 " + old_class_count + " 相同");
                ret.append(formattedMsg).append("\n");
            }
            redisTemplate.opsForValue().set(cacheKey, ret.toString(), 10, TimeUnit.MINUTES);
        }catch (Exception e){
            log.error("获取班级对比信息失败 " + e);
        }finally {
            scnuxljyDatabase.close();
            redisTemplate.delete(taskRunningKey);
        }
        return CompletableFuture.completedFuture(null);
    }

    public CompletableFuture<Void> studentGradesSummary(String cacheKey, String taskRunningKey, OldDataFilterRO oldDataFilterRO) {
        SCNUXLJYDatabase scnuxljyDatabase = new SCNUXLJYDatabase();
        try{
            int startYear = Integer.parseInt(oldDataFilterRO.getEndYear());
            int endYear = Integer.parseInt(oldDataFilterRO.getStartYear());


            boolean allEqual = true;
            StringBuilder ret = new StringBuilder();
            // 获取当前时间
            String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            String formattedMsg = timeStamp + "新旧系统成绩信息对比";
            ret.append(formattedMsg).append("\n");
            for (int i = startYear; i >= endYear; i--) {
                Integer new_gradeInformation_count = scoreInformationMapper.selectCount(new LambdaQueryWrapper<ScoreInformationPO>().
                        eq(ScoreInformationPO::getGrade, "" + i));

                int old_gradeInformation_count = (int) scnuxljyDatabase.getValue(
                        "select count(*) from RESULT_VIEW_FULL where nj='" + i + "' and bshi not LIKE 'WP%';");
                formattedMsg = String.format(i + " 年，新系统学历教育成绩数量 " +
                        new_gradeInformation_count + " 旧系统学历教育成绩数量 " +
                        old_gradeInformation_count + (new_gradeInformation_count != old_gradeInformation_count
                        ? " 两者不同" : " 两者相同"));
                ret.append(formattedMsg).append("\n");
                if(new_gradeInformation_count != old_gradeInformation_count){
                    allEqual = false;
                }

            }
            if (allEqual) {
                formattedMsg = String.format("新旧系统 " + startYear + " 年到 " + endYear + " 年的成绩数据完全相等");
                ret.append(formattedMsg).append("\n");
            }
            redisTemplate.opsForValue().set(cacheKey, ret.toString(), 10, TimeUnit.MINUTES);
        }catch (Exception e){
            log.error("获取班级对比信息失败 " + e);
        }finally {
            scnuxljyDatabase.close();
            redisTemplate.delete(taskRunningKey);
        }
        return CompletableFuture.completedFuture(null);
    }

    @Async
    public CompletableFuture<Void> teachingPlansSummary(String cacheKey, String taskRunningKey, OldDataFilterRO oldDataFilterRO) {
        SCNUXLJYDatabase scnuxljyDatabase = new SCNUXLJYDatabase();
        try {
            int startYear = 2023;
            int endYear = 2015;


            boolean allEqual = true;
            StringBuilder ret = new StringBuilder();
            // 获取当前时间
            String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            String formattedMsg = timeStamp + "新旧系统教学计划信息对比";
            ret.append(formattedMsg).append("\n");

            for (int i = startYear; i >= endYear; i--) {
                Integer new_teachingPlans_count1 = courseInformationMapper.selectCount(new LambdaQueryWrapper<CourseInformationPO>().
                        eq(CourseInformationPO::getGrade, "" + i));
                String year_c = i + "";
                year_c = year_c.substring(year_c.length() - 2);
                int old_teachingPlans_count1 = (int) scnuxljyDatabase.getValue(
                        "select count(*) from courseDATA where bshi not LIKE 'WP%' and bshi LIKE '" + year_c + "%';");

                formattedMsg = String.format(i + " 年，新系统学历教育教学计划数量 " +
                        new_teachingPlans_count1 + " 旧系统学历教育教学计划数量 " +
                        old_teachingPlans_count1 + (new_teachingPlans_count1 != old_teachingPlans_count1
                        ? " 两者不同" : " 两者相同"));
                ret.append(formattedMsg).append("\n");
                if (new_teachingPlans_count1 != old_teachingPlans_count1) {
                    allEqual = false;
                }
            }
            if (allEqual) {
                formattedMsg = String.format("新旧系统 " + startYear + " 年到 " + endYear + " 年的教学计划完全相等");
                ret.append(formattedMsg).append("\n");
            }
            redisTemplate.opsForValue().set(cacheKey, ret.toString(), 10, TimeUnit.MINUTES);
        }catch (Exception e){
            log.error("获取教学计划对比信息失败 " + e);
        }finally {
            scnuxljyDatabase.close();
            redisTemplate.delete(taskRunningKey);
        }
        return CompletableFuture.completedFuture(null);
    }

    @Async
    public CompletableFuture<Void> studentStatusChangeSummary(String cacheKey, String taskRunningKey, OldDataFilterRO oldDataFilterRO) {
        SCNUXLJYDatabase scnuxljyDatabase = new SCNUXLJYDatabase();
        try {
            int startYear = Integer.parseInt(oldDataFilterRO.getEndYear());
            int endYear = Integer.parseInt(oldDataFilterRO.getStartYear());
            boolean allEqual = true;

            StringBuilder ret = new StringBuilder();
            // 获取当前时间
            String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            String formattedMsg = timeStamp + "新旧系统成绩数量对比";
            ret.append(formattedMsg).append("\n");

            for (int i = startYear; i >= endYear; i--) {
                Integer new_gradeInformation_count = scoreInformationMapper.selectCount(new LambdaQueryWrapper<ScoreInformationPO>().
                        eq(ScoreInformationPO::getGrade, "" + i));

                int old_gradeInformation_count = (int) scnuxljyDatabase.getValue(
                        "select count(*) from RESULT_VIEW_FULL where nj='" + i + "' and bshi not LIKE 'WP%';");
                formattedMsg = String.format(i + " 年，新系统学历教育成绩数量 " +
                        new_gradeInformation_count + " 旧系统学历教育成绩数量 " +
                        old_gradeInformation_count + (new_gradeInformation_count != old_gradeInformation_count
                        ? " 两者不同" : " 两者相同"));
                ret.append(formattedMsg).append("\n");
                if(new_gradeInformation_count != old_gradeInformation_count){
                    allEqual = false;
                }

            }
            if (allEqual) {
                formattedMsg = String.format("新旧系统 " + startYear + " 年到 " + endYear + " 年的成绩数据完全相等");
                ret.append(formattedMsg).append("\n");
            }
            redisTemplate.opsForValue().set(cacheKey, ret.toString(), 10, TimeUnit.MINUTES);
        }catch (Exception e){
            log.error("获取成绩对比信息失败 " + e);
        }finally {
            scnuxljyDatabase.close();
            redisTemplate.delete(taskRunningKey);
        }
        return CompletableFuture.completedFuture(null);
    }

    public HashMap<String, Integer> getStudentBasicData(String year) {
        List<String> distinctGrades = studentStatusMapper.getDistinctGrades(new StudentStatusFilterRO());
//        for()
        return null;
    }
}
