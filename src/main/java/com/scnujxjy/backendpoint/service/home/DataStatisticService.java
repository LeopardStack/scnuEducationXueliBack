package com.scnujxjy.backendpoint.service.home;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.StudentStatusPO;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.StudentStatusMapper;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.StudentStatusFilterRO;
import com.scnujxjy.backendpoint.model.vo.home.StatisticTableForStudentStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class DataStatisticService extends ServiceImpl<StudentStatusMapper, StudentStatusPO> implements IService<StudentStatusPO> {
    /**
     * 获取指定范围内的毕业数据、学籍数据和学位数据
     * @param yearRange
     * @return
     */
    public List<Map<String, StatisticTableForStudentStatus>> getTable1Data(int yearRange) {
        // 获取所有的年份
        List<String> distinctGrades = baseMapper.getDistinctGrades(new StudentStatusFilterRO());

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
        return baseMapper.getCountOfStudentStatus(String.valueOf(startYear), maxGrade);
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
