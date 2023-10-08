package com.scnujxjy.backendpoint.service.home;

import lombok.Data;

@Data
public class StatisticTableForGraduation {
    /**
     * 每年 7月的毕业人数
     */
    private long julyGraduationCount;
    /**
     * 年份
     */
    private String graduationYear;
    /**
     * 每年 1月的毕业人数
     */
    private long januaryGraduationCount;
    /**
     * 每年 的总毕业人数
     */
    private long annualGraduationCount;
}
