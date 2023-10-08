package com.scnujxjy.backendpoint.model.vo.home;

import lombok.Data;

@Data
public class StatisticTableForStudentStatus {
    private long studentCount;
    private String grade;
    private long degreeCount;
    private long graduationCount;
}
