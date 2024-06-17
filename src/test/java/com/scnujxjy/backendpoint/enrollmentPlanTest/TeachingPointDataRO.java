package com.scnujxjy.backendpoint.enrollmentPlanTest;


import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeachingPointDataRO {
    /**
     * 序号
     */
    @ExcelProperty(index=0, value = "序号")
    private String index;

    /**
     * 教学点简称
     */
    @ExcelProperty(index=1, value = "教学点简称")
    private String teachingPointName;

    /**
     * 校外教学点地址
     */
    @ExcelProperty(index=2, value = "校外教学点地址")
    private String teachingPointAddress;

    /**
     * 咨询电话
     */
    @ExcelProperty(index=3, value = "咨询电话")
    private String teachingPointContactNumbers;

    /**
     * 招生区域
     */
    @ExcelProperty(index=4, value = "招生区域")
    private String enrollmentArea;


    public List<String> getContactNumbersList() {
        if (teachingPointContactNumbers == null || teachingPointContactNumbers.isEmpty()) {
            return new ArrayList<>();
        }

        // 去除换行符、回车符和多余空格
        String cleanedNumbers = teachingPointContactNumbers.replaceAll("\\s+", " ").trim();

        // 使用空格分割电话号码
        String[] numbersArray = cleanedNumbers.split(" ");

        List<String> contactNumbersList = new ArrayList<>();
        for (String number : numbersArray) {
            if (!number.isEmpty()) {
                contactNumbersList.add(number);
            }
        }

        return contactNumbersList;
    }
}
