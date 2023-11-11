package com.scnujxjy.backendpoint.model.vo.teaching_process;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ScheduleCourseManagetArgs implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 年份的筛选项
     */
    List<String> grades;


    /**
     * 学院的筛选项
     */
    List<String> collegeNames;

    /**
     * 专业名称的筛选项
     */
    List<String> majorNames;

    /**
     * 行政班别的筛选项
     */
    List<String> ClassNames;

    /**
     * 专业名称的筛选
     */
    List<String> courseNames;

    /**
     * 直播状态
     */
    List<String> livingStatuses;
}
