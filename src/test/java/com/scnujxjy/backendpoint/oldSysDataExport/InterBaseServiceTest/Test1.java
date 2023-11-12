package com.scnujxjy.backendpoint.oldSysDataExport.InterBaseServiceTest;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scnujxjy.backendpoint.dao.entity.core_data.PaymentInfoPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.StudentStatusPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseInformationPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.ScoreInformationPO;
import com.scnujxjy.backendpoint.dao.mapper.core_data.PaymentInfoMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.ClassInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.DegreeInfoMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.GraduationInfoMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.StudentStatusMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.ScoreInformationMapper;
import com.scnujxjy.backendpoint.service.InterBase.OldDataSynchronize;
import com.scnujxjy.backendpoint.util.MessageSender;
import com.scnujxjy.backendpoint.util.SCNUXLJYDatabase;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

@SpringBootTest
@Slf4j
public class Test1 {
    @Resource
    OldDataSynchronize oldDataSynchronize;

    @Resource
    StudentStatusMapper studentStatusMapper;
    @Resource
    DegreeInfoMapper degreeInfoMapper;


    @Resource
    GraduationInfoMapper graduationInfoMapper;

    @Resource
    ClassInformationMapper classInformationMapper;

    @Resource
    CourseInformationMapper courseInformationMapper;

    @Resource
    ScoreInformationMapper scoreInformationMapper;
    @Resource
    PaymentInfoMapper paymentInfoMapper;

    @Resource
    MessageSender messageSender;

    @Test
    public void test0() {
        try {
            ArrayList<String> dataCheckLogs = new ArrayList<>();
//        log.info(msg1);
            // 获取当前时间
            String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

            // 获取当前的方法名 (需要考虑性能开销)
            String methodName = new Throwable().getStackTrace()[0].getMethodName();

            // 获取当前的类名
            String className = this.getClass().getSimpleName();

            String formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "新旧系统学籍数据的校验");
            dataCheckLogs.add(formattedMsg);

            int startYear = 2023;
            int endYear = 2010;
            boolean allEqual = true;
            SCNUXLJYDatabase scnuxljyDatabase = new SCNUXLJYDatabase();

            for (int i = startYear; i >= endYear; i--) {
                Integer integer = studentStatusMapper.selectCount(new LambdaQueryWrapper<StudentStatusPO>().eq(
                        StudentStatusPO::getGrade, "" + i
                ));

                int value_xl = (int) scnuxljyDatabase.getValue("SELECT count(*) FROM STUDENT_VIEW_WITHPIC WHERE NJ='" + i +
                        "' and bshi not like 'WP%';");
                formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, i + " 年旧系统学历教育生数量 " + value_xl + " 新系统学历教育生数量 " +
                        integer + ((int) value_xl == integer ? "  一致" : "  不同"));
                dataCheckLogs.add(formattedMsg);
                if (value_xl != integer) {
                    allEqual = false;

                    try {
                        int delete = studentStatusMapper.delete(new LambdaQueryWrapper<StudentStatusPO>().
                                eq(StudentStatusPO::getGrade, i + ""));
                        if (studentStatusMapper.selectCount(new LambdaQueryWrapper<StudentStatusPO>().
                                eq(StudentStatusPO::getGrade, i + "")) == 0) {
                            // 成功清除脏数据，同步旧系统最新的数据
                            oldDataSynchronize.synchronizeStudentStatusData(i, i, true);
                        }

                    } catch (Exception e) {
                        formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, i + " 年同步旧系统学籍数据失败 " + e.toString());
                        dataCheckLogs.add(formattedMsg);
                    }
                }

            }

            if (allEqual) {
                formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "新旧系统 " + startYear + " 年到 " + endYear + " 年的学籍数据完全相等");
                dataCheckLogs.add(formattedMsg);
            }


            formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "新旧系统班级信息对比");
            dataCheckLogs.add(formattedMsg);

            Integer new_class_count = classInformationMapper.selectCount(null);
            int old_class_count = (int) scnuxljyDatabase.getValue("SELECT count(*) FROM classdata where bshi not like'WP%';");
            if (new_class_count != old_class_count) {
                formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "新系统学历教育班级数量 " + new_class_count + " 旧系统学历教育班级数量 " + old_class_count + " 不同");
                dataCheckLogs.add(formattedMsg);
            } else {
                formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "新系统学历教育班级数量 " + new_class_count + " 旧系统学历教育班级数量 " + old_class_count + " 相同");
                dataCheckLogs.add(formattedMsg);
            }


            formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "新旧系统缴费数据对比");
            dataCheckLogs.add(formattedMsg);

            for (int payYear = 2023; payYear >= 2000; payYear--) {
                Integer new_pay_count = paymentInfoMapper.selectCount(new LambdaQueryWrapper<PaymentInfoPO>().eq(PaymentInfoPO::getGrade, "" + payYear));
                int old_pay_count = (int) scnuxljyDatabase.getValue("SELECT count(*) FROM CWPAY_VIEW WHERE NJ='" + payYear + "'");
                if (new_pay_count != old_pay_count) {
                    formattedMsg = String.format(payYear + "年 [%s] [%s.%s] %s", timeStamp, className, methodName, "新系统学历教育缴费数据 " + new_pay_count + " 旧系统学历教育缴费数据 " + old_pay_count + " 不同");
                    dataCheckLogs.add(formattedMsg);
                    int delete = paymentInfoMapper.delete(new LambdaQueryWrapper<PaymentInfoPO>().eq(PaymentInfoPO::getGrade, "" + payYear));
                    oldDataSynchronize.synchronizePaymentInfoData(true, true, String.valueOf(payYear));
                } else {
                    formattedMsg = String.format(payYear + "年 [%s] [%s.%s] %s", timeStamp, className, methodName, "新系统学历教育缴费数据 " + new_pay_count + " 旧系统学历教育缴费数据 " + old_pay_count + " 相同");
                    dataCheckLogs.add(formattedMsg);
                }
            }
//        Integer new_pay_count = paymentInfoMapper.selectCount(null);
//        int old_pay_count = (int) scnuxljyDatabase.getValue("SELECT count(*) FROM CWPAY_VIEW;");


            formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "新旧系统教学计划对比");
            dataCheckLogs.add(formattedMsg);


            formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "新旧系统教学计划对比");
            dataCheckLogs.add(formattedMsg);


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
                    formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, i +
                            " 年，新系统学历教育教学计划数量 " + new_teachingPlans_count1 + " 旧系统学历教育教学计划数量 " +
                            old_teachingPlans_count1 + " 两者不同");
                    dataCheckLogs.add(formattedMsg);
                }
            }
            if (allEqual) {
                formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "新旧系统 " + startYear + " 年到 " + endYear + " 年的教学计划完全相等");
                dataCheckLogs.add(formattedMsg);
            } else {
                // 存在不同，直接将教学计划完全替换
                try {
                    oldDataSynchronize.synchronizeTeachingPlansData(true, true);
                } catch (Exception e) {
                    formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "清除教学计划并同步旧系统数据失败 " + e.toString());
                    dataCheckLogs.add(formattedMsg);
                }
            }


            formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "新旧系统成绩数量对比");
            dataCheckLogs.add(formattedMsg);

            startYear = 2023;
            endYear = 2015;
            allEqual = true;
            for (int i = startYear; i >= endYear; i--) {
                Integer new_gradeInformation_count = scoreInformationMapper.selectCount(new LambdaQueryWrapper<ScoreInformationPO>().
                        eq(ScoreInformationPO::getGrade, "" + i));

                int old_gradeInformation_count = (int) scnuxljyDatabase.getValue(
                        "select count(*) from RESULT_VIEW_FULL where nj='" + i + "' and bshi not LIKE 'WP%';");
                formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, i + " 年，新系统学历教育成绩数量 " +
                        new_gradeInformation_count + " 旧系统学历教育成绩数量 " +
                        old_gradeInformation_count + (new_gradeInformation_count != old_gradeInformation_count
                        ? " 两者不同" : " 两者相同"));
                dataCheckLogs.add(formattedMsg);

                if (new_gradeInformation_count != old_gradeInformation_count) {
                    allEqual = false;

                    try {
                        int delete = scoreInformationMapper.delete(new LambdaQueryWrapper<ScoreInformationPO>().
                                eq(ScoreInformationPO::getGrade, "" + i));
                        if (scoreInformationMapper.selectCount(new LambdaQueryWrapper<ScoreInformationPO>().
                                eq(ScoreInformationPO::getGrade, "" + i)) == 0) {
                            // 清除成绩
                            oldDataSynchronize.synchronizeGradeInformationData(i, i, true);
                        }
                    } catch (Exception e) {
                        formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, i + " 年清除成绩并同步旧系统成绩" +
                                "失败 " + e.toString());
                        dataCheckLogs.add(formattedMsg);
                    }

                }
            }
            if (allEqual) {
                formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "新旧系统 " + startYear + " 年到 " + endYear + " 年的成绩数据完全相等");
                dataCheckLogs.add(formattedMsg);
            }

            ArrayList<String> checkLogs = calculateStatistics();
            dataCheckLogs.addAll(checkLogs);


            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            String currentDateTime = LocalDateTime.now().format(formatter);
            String relativePath = "data_import_error_excel/statistics/";
            String errorFileName = relativePath + currentDateTime + "_" + "新旧系统数据同步总览.txt";
            oldDataSynchronize.exportListToTxtAndUploadToMinio(dataCheckLogs, errorFileName, "datasynchronize");

            log.info("新旧系统校验同步结束");
            log.info("开始记录校验同步之后的新旧系统的数据差异");
            calculateStatistics();
        } catch (Exception e) {
            log.error(e.toString());
        }
    }

    /**
     * 测试学籍数据同步
      */
    @Test
    public void test1(){
        try {
            oldDataSynchronize.synchronizeStudentStatusData(2023, 2023, true);
        }catch (Exception e){
            log.error("同步学籍数据错误 " + e.toString());
        }
    }

    @Test
    public void test2(){
        try {
            oldDataSynchronize.synchronizeTeachingPlansData(true, true);
        }catch (Exception e){
            log.error("同步教学计划数据错误 " + e.toString());
        }
    }

    @Test
    public void test3(){
        try {
            oldDataSynchronize.synchronizeClassInformationData(true);
        }catch (Exception e){
            log.error("同步班级数据错误 " + e.toString());
        }
    }

    @Test
    public void test4(){
        SCNUXLJYDatabase scnuxljyDatabase = new SCNUXLJYDatabase();
        Object value = scnuxljyDatabase.getValue("select count(*) from classdata");
        Object value_fwp = scnuxljyDatabase.getValue("select count(*) from classdata where bshi LIKE 'WP%';");
        log.info(value_fwp + " " + value);
    }

    /**
     * 同步成绩
     */
    @Test
    public void test5(){
        int grade = 2021;
        try {
            int delete = scoreInformationMapper.delete(new LambdaQueryWrapper<ScoreInformationPO>().
                    eq(ScoreInformationPO::getGrade, "" + grade));
            log.info("查看删除 " + 2021 + "年 所有成绩的结果 " + delete);
            oldDataSynchronize.synchronizeGradeInformationData(grade, grade, true);
        }catch (Exception e){
            log.error("同步成绩数据错误 " + e.toString());
        }
    }

    /**
     * 同步缴费数据
     */
    @Test
    public void test5_1(){
        try {
            paymentInfoMapper.truncateTable();
            for(int year=2023; year >= 2000; year--){
                oldDataSynchronize.synchronizePaymentInfoData(true, true, String.valueOf(year));
            }

        }catch (Exception e){
            log.error("同步缴费数据错误 " + e.toString());
        }
    }

    /**
     * 测试学籍数据是否一致
     */
    @Test
    public void test6(){
        log.info("新旧系统学籍数据的校验");
        int startYear = 2023;
        int endYear = 2010;
        SCNUXLJYDatabase scnuxljyDatabase = new SCNUXLJYDatabase();

        for(int i = startYear; i >= endYear; i--){
            Integer integer = studentStatusMapper.selectCount(new LambdaQueryWrapper<StudentStatusPO>().eq(
                    StudentStatusPO::getGrade, "" + i
            ));

            Object value_xl = scnuxljyDatabase.getValue("SELECT count(*) FROM STUDENT_VIEW_WITHPIC WHERE NJ='" + i +
                    "' and bshi not like'WP%';");
            log.info(i + " 年旧系统学历教育生数量 " + value_xl + " 新系统学历教育生数量 " + integer + ((int)value_xl == integer ? "  一致" : "  不同"));
        }

        log.info("新旧系统班级信息对比");
        Integer new_class_count = classInformationMapper.selectCount(null);
        Object old_class_count = scnuxljyDatabase.getValue("SELECT count(*) FROM classdata where bshi not like'WP%';");
        log.info("新系统学历教育班级数量 " + new_class_count + " 旧系统学历教育班级数量 " + old_class_count);


        log.info("新旧系统缴费数据对比");

        for(int payYear = 2023; payYear >= 2000; payYear--){
            Integer new_pay_count = paymentInfoMapper.selectCount(new LambdaQueryWrapper<PaymentInfoPO>().eq(PaymentInfoPO::getGrade, "" + payYear));
            int old_pay_count = (int) scnuxljyDatabase.getValue("SELECT count(*) FROM CWPAY_VIEW WHERE NJ='" + payYear + "'");
            if(new_pay_count != old_pay_count){
                log.info("新系统学历教育缴费数据 " + new_pay_count + " 旧系统学历教育缴费数据 " + old_pay_count + " 不同");
                int delete = paymentInfoMapper.delete(new LambdaQueryWrapper<PaymentInfoPO>().eq(PaymentInfoPO::getGrade, "" + payYear));
            }else{
                log.info("新系统学历教育缴费数据 " + new_pay_count + " 旧系统学历教育缴费数据 " + old_pay_count + " 相同");

            }
        }


        log.info("新旧系统教学计划对比");
        Integer new_teachingPlans_count = courseInformationMapper.selectCount(null);
        Object old_teachingPlans_count = scnuxljyDatabase.getValue("select count(*) from courseDATA where bshi not LIKE 'WP%';");
        log.info("新系统学历教育教学计划数量 " + new_teachingPlans_count + " 旧系统学历教育教学计划数量 " + old_teachingPlans_count);

        startYear = 2023;
        endYear = 2015;
        boolean allEqual = true;
        for(int i = startYear; i >= endYear; i--){
            Integer new_teachingPlans_count1 = courseInformationMapper.selectCount(new LambdaQueryWrapper<CourseInformationPO>().
                    eq(CourseInformationPO::getGrade, "" + i));
            String year_c = i + "";
            year_c = year_c.substring(year_c.length()-2);
            int old_teachingPlans_count1 = (int) scnuxljyDatabase.getValue(
                    "select count(*) from courseDATA where bshi not LIKE 'WP%' and bshi LIKE '" + year_c + "%';");

            if(new_teachingPlans_count1 != old_teachingPlans_count1){
                allEqual = false;
                log.info(i + " 年，新系统学历教育教学计划数量 " + new_teachingPlans_count1 + " 旧系统学历教育教学计划数量 " +
                        old_teachingPlans_count1 + " 两者不同");
            }
        }
        if(allEqual){
            log.info("新旧系统 " + startYear + " 年到 " + endYear + " 年的教学计划完全相等");
        }


        log.info("新旧系统成绩数量对比");
        startYear = 2023;
        endYear = 2015;
        allEqual = true;
        for(int i = startYear; i >= endYear; i--){
            Integer new_gradeInformation_count = scoreInformationMapper.selectCount(new LambdaQueryWrapper<ScoreInformationPO>().
                    eq(ScoreInformationPO::getGrade, "" + i));

            int old_gradeInformation_count = (int) scnuxljyDatabase.getValue(
                    "select count(*) from RESULT_VIEW_FULL where nj='" + i + "' and bshi not LIKE 'WP%';");

            if(new_gradeInformation_count != old_gradeInformation_count){
                allEqual = false;
                log.info(i + " 年，新系统学历教育成绩数量 " + new_gradeInformation_count + " 旧系统学历教育成绩数量 " +
                        old_gradeInformation_count + " 两者不同");
            }
        }
        if(allEqual){
            log.info("新旧系统 " + startYear + " 年到 " + endYear + " 年的成绩数据完全相等");
        }
    }

    private ArrayList<String> calculateStatistics(){
        ArrayList<String> dataCheckLogs = new ArrayList<>();

//        log.info(msg1);
        // 获取当前时间
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        // 获取当前的方法名 (需要考虑性能开销)
        String methodName = new Throwable().getStackTrace()[0].getMethodName();

        // 获取当前的类名
        String className = this.getClass().getSimpleName();

        String formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "新旧系统学籍数据的校验");
        dataCheckLogs.add(formattedMsg);

        int startYear = 2023;
        int endYear = 2010;
        boolean allEqual = true;

        for(int i = startYear; i >= endYear; i--){
            SCNUXLJYDatabase scnuxljyDatabase = new SCNUXLJYDatabase();
            Integer integer = studentStatusMapper.selectCount(new LambdaQueryWrapper<StudentStatusPO>().eq(
                    StudentStatusPO::getGrade, "" + i
            ));

            int value_xl = (int) scnuxljyDatabase.getValue("SELECT count(*) FROM STUDENT_VIEW_WITHPIC WHERE NJ='" + i +
                    "' and bshi not like 'WP%';");
            formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, i + " 年旧系统学历教育生数量 " + value_xl + " 新系统学历教育生数量 " +
                    integer + ((int)value_xl == integer ? "  一致" : "  不同"));
            dataCheckLogs.add(formattedMsg);
            if(value_xl != integer){
                allEqual = false;
            }

        }

        if(allEqual){
            formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "新旧系统 " + startYear + " 年到 " + endYear + " 年的学籍数据完全相等");
            dataCheckLogs.add(formattedMsg);
        }


        formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "新旧系统班级信息对比");
        dataCheckLogs.add(formattedMsg);
        {
            SCNUXLJYDatabase scnuxljyDatabase = new SCNUXLJYDatabase();
            Integer new_class_count = classInformationMapper.selectCount(null);
            int old_class_count = (int) scnuxljyDatabase.getValue("SELECT count(*) FROM classdata where bshi not like'WP%';");
            if(new_class_count != old_class_count){
                formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "新系统学历教育班级数量 " + new_class_count + " 旧系统学历教育班级数量 " + old_class_count + " 不同");
                dataCheckLogs.add(formattedMsg);
            }else{
                formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "新系统学历教育班级数量 " + new_class_count + " 旧系统学历教育班级数量 " + old_class_count + " 相同");
                dataCheckLogs.add(formattedMsg);
            }

            formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "新旧系统教学计划对比");
            dataCheckLogs.add(formattedMsg);
        }


        startYear = 2023;
        endYear = 2015;
        allEqual = true;
        for(int i = startYear; i >= endYear; i--){
            SCNUXLJYDatabase scnuxljyDatabase = new SCNUXLJYDatabase();
            Integer new_teachingPlans_count1 = courseInformationMapper.selectCount(new LambdaQueryWrapper<CourseInformationPO>().
                    eq(CourseInformationPO::getGrade, "" + i));
            String year_c = i + "";
            year_c = year_c.substring(year_c.length()-2);
            int old_teachingPlans_count1 = (int) scnuxljyDatabase.getValue(
                    "select count(*) from courseDATA where bshi not LIKE 'WP%' and bshi LIKE '" + year_c + "%';");

            if(new_teachingPlans_count1 != old_teachingPlans_count1){
                allEqual = false;
                formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, i +
                        " 年，新系统学历教育教学计划数量 " + new_teachingPlans_count1 + " 旧系统学历教育教学计划数量 " +
                        old_teachingPlans_count1 + " 两者不同");
                dataCheckLogs.add(formattedMsg);
            }
        }
        if(allEqual){
            formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "新旧系统 " + startYear + " 年到 " + endYear + " 年的教学计划完全相等");
            dataCheckLogs.add(formattedMsg);
        }


        formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "新旧系统成绩数量对比");
        dataCheckLogs.add(formattedMsg);

        startYear = 2023;
        endYear = 2015;
        allEqual = true;
        for(int i = startYear; i >= endYear; i--){
            SCNUXLJYDatabase scnuxljyDatabase = new SCNUXLJYDatabase();
            Integer new_gradeInformation_count = scoreInformationMapper.selectCount(new LambdaQueryWrapper<ScoreInformationPO>().
                    eq(ScoreInformationPO::getGrade, "" + i));

            int old_gradeInformation_count = (int) scnuxljyDatabase.getValue(
                    "select count(*) from RESULT_VIEW_FULL where nj='" + i + "' and bshi not LIKE 'WP%';");

            formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, i + " 年，新系统学历教育教学计划数量 " + new_gradeInformation_count + " 旧系统学历教育教学计划数量 " +
                    old_gradeInformation_count + (new_gradeInformation_count != old_gradeInformation_count ? " 两者不同" : " 两者相同"));
            dataCheckLogs.add(formattedMsg);
            if(new_gradeInformation_count != old_gradeInformation_count){
                allEqual = false;

            }
        }
        if(allEqual){
            formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "新旧系统 " + startYear + " 年到 " + endYear + " 年的成绩数据完全相等");
            dataCheckLogs.add(formattedMsg);
        }

        return dataCheckLogs;
    }

    /**
     * 根据同步前的数据对比 来针对特定年份数据进行同步
     */
    @Test
    public void test7(){
        ArrayList<String> dataCheckLogs = new ArrayList<>();
//        log.info(msg1);
        // 获取当前时间
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        // 获取当前的方法名 (需要考虑性能开销)
        String methodName = new Throwable().getStackTrace()[0].getMethodName();

        // 获取当前的类名
        String className = this.getClass().getSimpleName();

        String formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "新旧系统学籍数据的校验");
        dataCheckLogs.add(formattedMsg);

        int startYear = 2023;
        int endYear = 2010;
        boolean allEqual = true;
        SCNUXLJYDatabase scnuxljyDatabase = new SCNUXLJYDatabase();

        for(int i = startYear; i >= endYear; i--){
            Integer integer = studentStatusMapper.selectCount(new LambdaQueryWrapper<StudentStatusPO>().eq(
                    StudentStatusPO::getGrade, "" + i
            ));

            int value_xl = (int) scnuxljyDatabase.getValue("SELECT count(*) FROM STUDENT_VIEW_WITHPIC WHERE NJ='" + i +
                    "' and bshi not like'WP%';");
            formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, i + " 年旧系统学历教育生数量 " + value_xl + " 新系统学历教育生数量 " +
                    integer + ((int)value_xl == integer ? "  一致" : "  不同"));
            dataCheckLogs.add(formattedMsg);
            if(value_xl != integer){
                allEqual = false;

                try {
                    int delete = studentStatusMapper.delete(new LambdaQueryWrapper<StudentStatusPO>().
                            eq(StudentStatusPO::getGrade, i + ""));
                    if(studentStatusMapper.selectCount(new LambdaQueryWrapper<StudentStatusPO>().
                            eq(StudentStatusPO::getGrade, i + "")) == 0){
                        // 成功清除脏数据，同步旧系统最新的数据
                        oldDataSynchronize.synchronizeStudentStatusData(i, i, true);
                    }

                }catch (Exception e){
                    formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, i + " 年同步旧系统学籍数据失败 " + e.toString());
                    dataCheckLogs.add(formattedMsg);
                }
            }

        }

        if(allEqual){
            formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "新旧系统 " + startYear + " 年到 " + endYear + " 年的学籍数据完全相等");
            dataCheckLogs.add(formattedMsg);
        }


        formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "新旧系统班级信息对比");
        dataCheckLogs.add(formattedMsg);

        Integer new_class_count = classInformationMapper.selectCount(null);
        int old_class_count = (int) scnuxljyDatabase.getValue("SELECT count(*) FROM classdata where bshi not like'WP%';");
        if(new_class_count != old_class_count){
            formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "新系统学历教育班级数量 " + new_class_count + " 旧系统学历教育班级数量 " + old_class_count + " 不同");
            dataCheckLogs.add(formattedMsg);
        }else{
            formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "新系统学历教育班级数量 " + new_class_count + " 旧系统学历教育班级数量 " + old_class_count + " 相同");
            dataCheckLogs.add(formattedMsg);
        }

        formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "新旧系统教学计划对比");
        dataCheckLogs.add(formattedMsg);
//        Integer new_teachingPlans_count = courseInformationMapper.selectCount(null);
//        Object old_teachingPlans_count = scnuxljyDatabase.getValue("select count(*) from courseDATA where bshi not LIKE 'WP%';");
//        log.info("新系统学历教育教学计划数量 " + new_teachingPlans_count + " 旧系统学历教育教学计划数量 " + old_teachingPlans_count);

        startYear = 2023;
        endYear = 2015;
        allEqual = true;
        for(int i = startYear; i >= endYear; i--){
            Integer new_teachingPlans_count1 = courseInformationMapper.selectCount(new LambdaQueryWrapper<CourseInformationPO>().
                    eq(CourseInformationPO::getGrade, "" + i));
            String year_c = i + "";
            year_c = year_c.substring(year_c.length()-2);
            int old_teachingPlans_count1 = (int) scnuxljyDatabase.getValue(
                    "select count(*) from courseDATA where bshi not LIKE 'WP%' and bshi LIKE '" + year_c + "%';");

            if(new_teachingPlans_count1 != old_teachingPlans_count1){
                allEqual = false;
                formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, i +
                        " 年，新系统学历教育教学计划数量 " + new_teachingPlans_count1 + " 旧系统学历教育教学计划数量 " +
                        old_teachingPlans_count1 + " 两者不同");
                dataCheckLogs.add(formattedMsg);
            }
        }
        if(allEqual){
            formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "新旧系统 " + startYear + " 年到 " + endYear + " 年的教学计划完全相等");
            dataCheckLogs.add(formattedMsg);
        }else{
            // 存在不同，直接将教学计划完全替换
            try {
                oldDataSynchronize.synchronizeTeachingPlansData(true, true);
            }catch (Exception e){
                formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "清除教学计划并同步旧系统数据失败 " + e.toString());
                dataCheckLogs.add(formattedMsg);
            }
        }


        formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "新旧系统成绩数量对比");
        dataCheckLogs.add(formattedMsg);

        startYear = 2023;
        endYear = 2015;
        allEqual = true;
        for(int i = startYear; i >= endYear; i--){
            Integer new_gradeInformation_count = scoreInformationMapper.selectCount(new LambdaQueryWrapper<ScoreInformationPO>().
                    eq(ScoreInformationPO::getGrade, "" + i));

            int old_gradeInformation_count = (int) scnuxljyDatabase.getValue(
                    "select count(*) from RESULT_VIEW_FULL where nj='" + i + "' and bshi not LIKE 'WP%';");
            formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, i + " 年，新系统学历教育教学计划数量 " + new_gradeInformation_count + " 旧系统学历教育教学计划数量 " +
                    old_gradeInformation_count + (new_gradeInformation_count != old_gradeInformation_count
                    ? " 两者不同" : " 两者相同"));
            dataCheckLogs.add(formattedMsg);

            if(new_gradeInformation_count != old_gradeInformation_count){
                allEqual = false;

                try{
                    int delete = scoreInformationMapper.delete(new LambdaQueryWrapper<ScoreInformationPO>().
                            eq(ScoreInformationPO::getGrade, "" + i));
                    if(scoreInformationMapper.selectCount(new LambdaQueryWrapper<ScoreInformationPO>().
                            eq(ScoreInformationPO::getGrade, "" + i)) == 0){
                        // 清除成绩
                        oldDataSynchronize.synchronizeGradeInformationData(i, i, true);
                    }
                }catch (Exception e){
                    formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, i + " 年清除成绩并同步旧系统成绩" +
                            "失败 " + e.toString());
                    dataCheckLogs.add(formattedMsg);
                }

            }
        }
        if(allEqual){
            formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "新旧系统 " + startYear + " 年到 " + endYear + " 年的成绩数据完全相等");
            dataCheckLogs.add(formattedMsg);
        }

        ArrayList<String> checkLogs = calculateStatistics();
        dataCheckLogs.addAll(checkLogs);


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String currentDateTime = LocalDateTime.now().format(formatter);
        String relativePath = "data_import_error_excel/statistics/";
        String errorFileName = relativePath + currentDateTime + "_" + "新旧系统数据同步总览.txt";
        oldDataSynchronize.exportListToTxtAndUploadToMinio(dataCheckLogs, errorFileName, "datasynchronize");
    }

    @Test
    public void testLogsToMinio(){
        ArrayList<String> dataCheckLogs = new ArrayList<>();
        ArrayList<String> checkLogs = calculateStatistics();
        dataCheckLogs.addAll(checkLogs);


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String currentDateTime = LocalDateTime.now().format(formatter);
        String relativePath = "data_import_error_excel/statistics/";
        String errorFileName = relativePath + currentDateTime + "_" + "新旧系统数据同步总览.txt";
        oldDataSynchronize.exportListToTxtAndUploadToMinio(dataCheckLogs, errorFileName, "datasynchronize");
    }

    @Value("${spring.rabbitmq.queue1}")
    private String queue1;
    @Test
    public void test8(){
        for(int i = 0; i < 1; i++){
            messageSender.send(queue1, "数据同步");
        }
    }


    @Test
    public void test9(){
        ArrayList<String> dataCheckLogs = new ArrayList<>();
//        log.info(msg1);
        // 获取当前时间
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        // 获取当前的方法名 (需要考虑性能开销)
        String methodName = new Throwable().getStackTrace()[0].getMethodName();

        // 获取当前的类名
        String className = this.getClass().getSimpleName();

        String formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "新旧系统学籍数据的校验");
        dataCheckLogs.add(formattedMsg);

        int startYear = 2019;
        int endYear = 2010;
        boolean allEqual = true;
        SCNUXLJYDatabase scnuxljyDatabase = new SCNUXLJYDatabase();

        for(int i = startYear; i >= endYear; i--){
            Integer integer = studentStatusMapper.selectCount(new LambdaQueryWrapper<StudentStatusPO>().eq(
                    StudentStatusPO::getGrade, "" + i
            ));

            int value_xl = (int) scnuxljyDatabase.getValue("SELECT count(*) FROM STUDENT_VIEW_WITHPIC WHERE NJ='" + i +
                    "' and bshi not like'WP%';");
            formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, i + " 年旧系统学历教育生数量 " + value_xl + " 新系统学历教育生数量 " +
                    integer + ((int)value_xl == integer ? "  一致" : "  不同"));
            dataCheckLogs.add(formattedMsg);
            if(value_xl != integer){
                allEqual = false;

                try {
                    int delete = studentStatusMapper.delete(new LambdaQueryWrapper<StudentStatusPO>().
                            eq(StudentStatusPO::getGrade, i + ""));
                    if(studentStatusMapper.selectCount(new LambdaQueryWrapper<StudentStatusPO>().
                            eq(StudentStatusPO::getGrade, i + "")) == 0){
                        // 成功清除脏数据，同步旧系统最新的数据
                        oldDataSynchronize.synchronizeStudentStatusData(i, i, true);
                    }

                }catch (Exception e){
                    formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, i + " 年同步旧系统学籍数据失败 " + e.toString());
                    dataCheckLogs.add(formattedMsg);
                }
            }

        }

        if(allEqual){
            formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "新旧系统 " + startYear + " 年到 " + endYear + " 年的学籍数据完全相等");
            dataCheckLogs.add(formattedMsg);
        }
    }

    @Test
    public void test10(){
        log.info("新旧系统获得学位信息对比");
        SCNUXLJYDatabase scnuxljyDatabase = new SCNUXLJYDatabase();
        Integer new_class_count = degreeInfoMapper.selectCount(null);
        Object old_class_count = scnuxljyDatabase.getValue("SELECT count(*) FROM xwdata;");
        log.info("新系统获得学位的数量 " + new_class_count + " 旧系统获得学位的数量 " + old_class_count);
    }

    /**
     * 根据同步前的数据对比 来针对特定年份数据进行同步
     */
    @Test
    public void test11(){
        ArrayList<String> dataCheckLogs = new ArrayList<>();
//        log.info(msg1);
        // 获取当前时间
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        // 获取当前的方法名 (需要考虑性能开销)
        String methodName = new Throwable().getStackTrace()[0].getMethodName();

        // 获取当前的类名
        String className = this.getClass().getSimpleName();

        String formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "新旧系统学籍数据的校验");
        dataCheckLogs.add(formattedMsg);

        int startYear = 2023;
        int endYear = 2023;
        boolean allEqual = true;
        SCNUXLJYDatabase scnuxljyDatabase = new SCNUXLJYDatabase();

        for(int i = startYear; i >= endYear; i--){
            Integer integer = studentStatusMapper.selectCount(new LambdaQueryWrapper<StudentStatusPO>().eq(
                    StudentStatusPO::getGrade, "" + i
            ));

            int value_xl = (int) scnuxljyDatabase.getValue("SELECT count(*) FROM STUDENT_VIEW_WITHPIC WHERE NJ='" + i +
                    "' and bshi not like 'WP%';");
            formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, i + " 年旧系统学历教育生数量 " + value_xl + " 新系统学历教育生数量 " +
                    integer + ((int)value_xl == integer ? "  一致" : "  不同"));
            dataCheckLogs.add(formattedMsg);
            if(value_xl != integer){
                allEqual = false;

                try {
                    int delete = studentStatusMapper.delete(new LambdaQueryWrapper<StudentStatusPO>().
                            eq(StudentStatusPO::getGrade, i + ""));
                    if(studentStatusMapper.selectCount(new LambdaQueryWrapper<StudentStatusPO>().
                            eq(StudentStatusPO::getGrade, i + "")) == 0){
                        // 成功清除脏数据，同步旧系统最新的数据
                        oldDataSynchronize.synchronizeStudentStatusData(i, i, true);
                    }

                }catch (Exception e){
                    formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, i + " 年同步旧系统学籍数据失败 " + e.toString());
                    dataCheckLogs.add(formattedMsg);
                }
            }

        }

        if(allEqual){
            formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "新旧系统 " + startYear + " 年到 " + endYear + " 年的学籍数据完全相等");
            dataCheckLogs.add(formattedMsg);
        }


        formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "新旧系统班级信息对比");
        dataCheckLogs.add(formattedMsg);

        Integer new_class_count = classInformationMapper.selectCount(null);
        int old_class_count = (int) scnuxljyDatabase.getValue("SELECT count(*) FROM classdata where bshi not like'WP%';");
        if(new_class_count != old_class_count){
            formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "新系统学历教育班级数量 " + new_class_count + " 旧系统学历教育班级数量 " + old_class_count + " 不同");
            dataCheckLogs.add(formattedMsg);
            try {
                oldDataSynchronize.synchronizeClassInformationData(true);
            }catch (Exception e){
                dataCheckLogs.add("同步班级数据失败 " + e.toString());
            }
        }else{
            formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "新系统学历教育班级数量 " + new_class_count + " 旧系统学历教育班级数量 " + old_class_count + " 相同");
            dataCheckLogs.add(formattedMsg);
        }


        formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "新旧系统缴费数据对比");
        dataCheckLogs.add(formattedMsg);

        Integer new_pay_count = paymentInfoMapper.selectCount(null);
        int old_pay_count = (int) scnuxljyDatabase.getValue("SELECT count(*) FROM CWPAY_VIEW;");
        if(new_pay_count != old_pay_count){
            formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "新系统学历教育缴费数据 " + new_pay_count + " 旧系统学历教育缴费数据 " + old_pay_count + " 不同");
            dataCheckLogs.add(formattedMsg);
            paymentInfoMapper.truncateTable();
            for(int year = 2023; year >= 2000; year--){
                oldDataSynchronize.synchronizePaymentInfoData(true, true, String.valueOf(year));
            }

        }else{
            formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "新系统学历教育缴费数据 " + new_pay_count + " 旧系统学历教育缴费数据 " + old_pay_count + " 相同");
            dataCheckLogs.add(formattedMsg);
        }

        formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "新旧系统教学计划对比");
        dataCheckLogs.add(formattedMsg);



        formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "新旧系统教学计划对比");
        dataCheckLogs.add(formattedMsg);


        startYear = 2023;
        endYear = 2023;
        allEqual = true;
        for(int i = startYear; i >= endYear; i--){
            Integer new_teachingPlans_count1 = courseInformationMapper.selectCount(new LambdaQueryWrapper<CourseInformationPO>().
                    eq(CourseInformationPO::getGrade, "" + i));
            String year_c = i + "";
            year_c = year_c.substring(year_c.length()-2);
            int old_teachingPlans_count1 = (int) scnuxljyDatabase.getValue(
                    "select count(*) from courseDATA where bshi not LIKE 'WP%' and bshi LIKE '" + year_c + "%';");

            if(new_teachingPlans_count1 != old_teachingPlans_count1){
                allEqual = false;
                formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, i +
                        " 年，新系统学历教育教学计划数量 " + new_teachingPlans_count1 + " 旧系统学历教育教学计划数量 " +
                        old_teachingPlans_count1 + " 两者不同");
                dataCheckLogs.add(formattedMsg);
            }
        }
        if(allEqual){
            formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "新旧系统 " + startYear + " 年到 " + endYear + " 年的教学计划完全相等");
            dataCheckLogs.add(formattedMsg);
        }else{
            // 存在不同，直接将教学计划完全替换
            try {
                oldDataSynchronize.synchronizeTeachingPlansData(true, true);
            }catch (Exception e){
                formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "清除教学计划并同步旧系统数据失败 " + e.toString());
                dataCheckLogs.add(formattedMsg);
            }
        }


        formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "新旧系统成绩数量对比");
        dataCheckLogs.add(formattedMsg);

        startYear = 2023;
        endYear = 2023;
        allEqual = true;
        for(int i = startYear; i >= endYear; i--){
            Integer new_gradeInformation_count = scoreInformationMapper.selectCount(new LambdaQueryWrapper<ScoreInformationPO>().
                    eq(ScoreInformationPO::getGrade, "" + i));

            int old_gradeInformation_count = (int) scnuxljyDatabase.getValue(
                    "select count(*) from RESULT_VIEW_FULL where nj='" + i + "' and bshi not LIKE 'WP%';");
            formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, i + " 年，新系统学历教育成绩数量 " +
                    new_gradeInformation_count + " 旧系统学历教育成绩数量 " +
                    old_gradeInformation_count + (new_gradeInformation_count != old_gradeInformation_count
                    ? " 两者不同" : " 两者相同"));
            dataCheckLogs.add(formattedMsg);

            if(new_gradeInformation_count != old_gradeInformation_count){
                allEqual = false;

                try{
                    int delete = scoreInformationMapper.delete(new LambdaQueryWrapper<ScoreInformationPO>().
                            eq(ScoreInformationPO::getGrade, "" + i));
                    if(scoreInformationMapper.selectCount(new LambdaQueryWrapper<ScoreInformationPO>().
                            eq(ScoreInformationPO::getGrade, "" + i)) == 0){
                        // 清除成绩
                        oldDataSynchronize.synchronizeGradeInformationData(i, i, true);
                    }
                }catch (Exception e){
                    formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, i + " 年清除成绩并同步旧系统成绩" +
                            "失败 " + e.toString());
                    dataCheckLogs.add(formattedMsg);
                }

            }
        }
        if(allEqual){
            formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "新旧系统 " + startYear + " 年到 " + endYear + " 年的成绩数据完全相等");
            dataCheckLogs.add(formattedMsg);
        }

        ArrayList<String> checkLogs = calculateStatistics();
        dataCheckLogs.addAll(checkLogs);


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String currentDateTime = LocalDateTime.now().format(formatter);
        String relativePath = "data_import_error_excel/statistics/";
        String errorFileName = relativePath + currentDateTime + "_" + "新旧系统数据同步总览.txt";
        oldDataSynchronize.exportListToTxtAndUploadToMinio(dataCheckLogs, errorFileName, "datasynchronize");

        log.info("新旧系统校验同步结束");
        log.info("开始记录校验同步之后的新旧系统的数据差异");
        oldDataSynchronize.calculateStatistics();
    }
}
