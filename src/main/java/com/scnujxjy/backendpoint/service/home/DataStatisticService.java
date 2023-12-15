package com.scnujxjy.backendpoint.service.home;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.StudentStatusPO;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.StudentStatusMapper;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.StudentStatusFilterRO;
import com.scnujxjy.backendpoint.model.vo.home.StatisticTableForStudentStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DataStatisticService extends ServiceImpl<StudentStatusMapper, StudentStatusPO> implements IService<StudentStatusPO> {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Async
    public void loadTable1DataAsync(int yearRange) {
        // 数据加载逻辑
        List<StatisticTableForStudentStatus> data = getTable1Data(yearRange); // 加载数据
        redisTemplate.opsForValue().set("table1Data", data, 1, TimeUnit.HOURS); // 存储到 Redis，有效期1小时
    }

    public List<Map<String, StatisticTableForStudentStatus>> getTable1DataWithRedis(int yearRange) {
        if (!redisTemplate.hasKey("table1DataForStatistics")) {
            loadTable1DataAsync(yearRange);
            return new ArrayList<>(); // 返回空列表
        } else {
            return (List<Map<String, StatisticTableForStudentStatus>>) redisTemplate.opsForValue().get("table1DataForStatistics");
        }
    }
    /**
     * 获取指定范围内的毕业数据、学籍数据和学位数据
     * @param yearRange
     * @return
     */
    public List<StatisticTableForStudentStatus> getTable1Data(int yearRange) {
        log.info("开始执行数据统计");
        // 获取所有的年份
        List<String> distinctGrades = baseMapper.getDistinctGradesForStatistics(new StudentStatusFilterRO());

        if (distinctGrades.isEmpty()) {
            return Collections.emptyList();
        }

        // 对年份进行排序
        Collections.sort(distinctGrades, Collections.reverseOrder()); // 降序排序

        // 获取最大的年份
        String maxGrade = distinctGrades.get(0);

        // 计算开始年份
        int startYear = Integer.parseInt(maxGrade) - yearRange + 1;  // +1 是因为我们也要包括最大年份

        // 确保开始年份在列表中，如果不在则调整到最小的年份
        if (!distinctGrades.contains(String.valueOf(startYear))) {
            startYear = Integer.parseInt(distinctGrades.get(distinctGrades.size() - 1));
        }

        // 获取数据
        // 获取学生状态数据
        List<StatisticTableForStudentStatus> studentStatusData = baseMapper.getCountOfStudentStatus(String.valueOf(startYear), maxGrade);

        // 获取录取信息数据
        List<StatisticTableForStudentStatus> admissionInfoData = baseMapper.getCountOfAdmissionInformation(String.valueOf(startYear), maxGrade);

        // 创建一个新的列表来存储合并后的数据
        List<StatisticTableForStudentStatus> mergedData = new ArrayList<>();

        // 将学生状态数据添加到合并后的列表
        mergedData.addAll(admissionInfoData);

        // 遍历录取信息数据，更新或添加到合并后的列表
        for (StatisticTableForStudentStatus studentStatus : admissionInfoData) {
            StatisticTableForStudentStatus studentStatus1 = studentStatusData.stream().filter(staData -> staData.getGrade().
                    equals(studentStatus.getGrade())).findFirst().orElse(null);

            if(studentStatus1 != null && studentStatus1.getGrade().equals(studentStatus.getGrade())){
                studentStatus.setStudentCount(studentStatus1.getStudentCount());
                studentStatus.setGraduationCount(studentStatus1.getGraduationCount());
                studentStatus.setDegreeCount(studentStatus1.getDegreeCount());
            }
        }


        return mergedData;
    }

    /**
     * 获取指定范围内的毕业数据
     * @param yearRange
     * @return
     */
    public List<Map<String, StatisticTableForGraduation>> getGraduationDataInRange(int yearRange) {
        // 获取所有的毕业年份
        List<String> distinctGraduationYears = baseMapper.getDistinctGrades(new StudentStatusFilterRO());

        if (distinctGraduationYears.isEmpty()) {
            return Collections.emptyList();
        }

        // 对年份进行排序
        Collections.sort(distinctGraduationYears, Collections.reverseOrder()); // 降序排序

        // 获取最大的年份
        String maxYear = distinctGraduationYears.get(0);

        // 计算开始年份
        int startYear = Integer.parseInt(maxYear) - yearRange + 1;  // +1 是因为我们也要包括最大年份

        // 确保开始年份在列表中，如果不在则调整到最小的年份
        if (!distinctGraduationYears.contains(String.valueOf(startYear))) {
            startYear = Integer.parseInt(distinctGraduationYears.get(distinctGraduationYears.size() - 1));
        }

        // 获取数据
        return baseMapper.getCountOfGraduation(startYear, Integer.parseInt(maxYear));
    }


}
