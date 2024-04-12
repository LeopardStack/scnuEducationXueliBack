package com.scnujxjy.backendpoint.OldDataSynchronizeTest;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scnujxjy.backendpoint.dao.entity.core_data.PaymentInfoPO;
import com.scnujxjy.backendpoint.dao.entity.oa.MajorChangeRecordPO;
import com.scnujxjy.backendpoint.dao.mapper.core_data.PaymentInfoMapper;
import com.scnujxjy.backendpoint.dao.mapper.oa.MajorChangeRecordMapper;
import com.scnujxjy.backendpoint.service.InterBase.OldDataSynchronize;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

import static com.scnujxjy.backendpoint.util.DataImportScnuOldSys.getStudentLuqus;

@SpringBootTest
@Slf4j
public class Test1 {
    @Resource
    private OldDataSynchronize oldDataSynchronize;

    @Resource
    private PaymentInfoMapper paymentInfoMapper;

    @Resource
    private MajorChangeRecordMapper majorChangeRecordMapper;

    @Test
    public void test1(){
        try {
            oldDataSynchronize.synchronizeGradeInformationData(2023, 2015, true);
        }catch (Exception e){
            log.info("同步成绩失败 ");
        }
    }

    @Test
    public void test2(){
        log.info("清除了所有的缴费数据 目前缴费数据库中的数据量为 " + paymentInfoMapper.selectCount(null));
        oldDataSynchronize.synchronizePaymentInfoDataAll(true,
                new ArrayList<>(Arrays.asList("休学", "退学", "转学")));
    }

    /**
     * 打印新旧系统的数据比对
     */
    @Test
    public void test3(){
        oldDataSynchronize.calculateStatistics(true);
    }

    /**
     * 同步学籍数据
     */
    @Test
    public void test4(){
        try{
            oldDataSynchronize.synchronizeStudentStatusData(2020,
                    2018, true, true);
        }catch (Exception e){
            log.info("同步学籍数据失败");
        }

    }

    /**
     * 单个学籍数据 debug 测试
     */
    @Test
    public void test4_1(){
        try{
            oldDataSynchronize.synchronizeStudentStatusDataSingleDebug(2020,true, true,
                    "522122198106291701");
        }catch (Exception e){
            log.info("测试单个学籍数据失败");
        }

    }

    /**
     * 更新单个年级的缴费数据
     */
    @Test
    public void test5(){
        try {
            for(int grade = 2023; grade >= 2023; grade--){
                int delete = paymentInfoMapper.delete(new LambdaQueryWrapper<PaymentInfoPO>()
                        .eq(PaymentInfoPO::getGrade, "" + grade));
                log.info("删除 " + grade + " 的缴费数据 " + delete);
            }
            oldDataSynchronize.synchronizePaymentInfoDataByInterval(
                    true, 2023, 2023,
                    new ArrayList<>());
        }catch (Exception e){
            log.info("同步成教缴费数据失败 " + e);
        }
    }

    /**
     * 获取旧系统所有的缴费数据 筛选出 休学、退学、复学 并一起做同步
     * 获取所有数据 筛选出 休学、退学、复学的
     */
    @Test
    public void test6_0(){
        paymentInfoMapper.truncateTable();

        oldDataSynchronize.synchronizePaymentInfoDataAll(true,
                new ArrayList<>(Arrays.asList("休学", "退学", "转学")));
    }

    /**
     * 更新新生缴费数据 包括学籍异动的缴费数据
     */
    @Test
    public void test6(){
        try {
            for(int grade = 2024; grade >= 2019; grade--){
                int delete = paymentInfoMapper.delete(new LambdaQueryWrapper<PaymentInfoPO>()
                        .eq(PaymentInfoPO::getGrade, "" + grade));
                log.info("删除 " + grade + " 的缴费数据 " + delete);
            }

            oldDataSynchronize.synchronizePaymentInfoDataByInterval(
                    true, 2024, 2019,
                    new ArrayList<>(Arrays.asList("休学", "退学", "转学")));
        }catch (Exception e){
            log.info("同步成教缴费数据失败 " + e);
        }
    }

    @Test
    public void test7(){
        String formattedMsg = String.format("新旧系统新生转专业数量对比");
        log.info(formattedMsg);
        for(int insertGrade = 2024; insertGrade >= 2010; insertGrade--){
            String grade = String.valueOf(insertGrade-1);
            if(insertGrade == 2024){
                grade = "-1";
            }
            ArrayList<HashMap<String, String>> studentLuqus = getStudentLuqus(Integer.parseInt(grade));

            // 使用 stream 过滤 CHANGEZYMC 为 null 的数据
            ArrayList<HashMap<String, String>> filteredList = studentLuqus.stream()
                    .filter(map -> (map.get("CHANGEZYMC") != null && !map.get("CHANGEZYMC").equalsIgnoreCase("NULL")) ||
                            (map.get("CHANGETIME") != null && !map.get("CHANGETIME").equalsIgnoreCase("NULL")) ||
                            (map.get("CHANGEXXXS") != null && !map.get("CHANGEXXXS").equalsIgnoreCase("NULL")))
                    .collect(Collectors.toCollection(ArrayList::new));
            formattedMsg = String.format(insertGrade + "年，旧系统中 新生转专业 的学籍异动数量: %s\n" +
                    "新系统中 新生转专业 的学籍异动数据 %s", filteredList.size(), majorChangeRecordMapper.
                    selectCount(new LambdaQueryWrapper<MajorChangeRecordPO>()
                            .eq(MajorChangeRecordPO::getRemark, "新生转专业")));
            log.info(formattedMsg);
        }
    }

    @Test
    public void test8(){
        try {
            oldDataSynchronize.synchronizeStudentStatusChangeData(true);
        }catch (Exception e){
            log.info("同步新旧系统学籍异动数据失败 " + e.toString());
        }
    }

    @Test
    public void test9(){
        try {
            oldDataSynchronize.synchronizeGradeInformationData(2024, 2023, true);
        }catch (Exception e){
            log.info("同步新旧系统成绩数据失败 " + e.toString());
        }
    }


    /**
     * 同步班级数据
     */
    @Test
    public void test10(){
        try {
            oldDataSynchronize.synchronizeClassInformationData(true);
        }catch (Exception e){
            log.info("同步新旧系统班级数据失败 " + e.toString());
        }
    }

    /**
     * 同步教学计划数据
     */
    @Test
    public void test11(){
        try {
            oldDataSynchronize.synchronizeTeachingPlansData(true, true);
        }catch (Exception e){
            log.info("同步新旧系统教学计划数据失败 " + e.toString());
        }
    }
}
