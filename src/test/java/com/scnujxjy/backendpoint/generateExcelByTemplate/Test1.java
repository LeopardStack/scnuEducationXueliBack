package com.scnujxjy.backendpoint.generateExcelByTemplate;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import lombok.Data;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test1 {

    @Test
    public void fillTemplate() {
        String templateFileName = "D:\\ScnuWork\\xueliBackEnd\\src\\main\\resources\\data\\附件8-1：新生（未注册）转专业审批表.xlsx";  // 模板文件路径
        String dataFileName = "D:\\ScnuWork\\xueliBackEnd\\src\\main\\resources\\data\\2024级深圳中鹏教学点学生转教学点情况表.xlsx"; // 数据文件路径

        // 读取数据
        List<StudentDataRO> data = EasyExcel.read(dataFileName)
                .head(StudentDataRO.class)
                .sheet()
                .doReadSync();

        List<StudentData> data1 = new ArrayList<>();
        for(StudentDataRO dataRO : data){
            StudentData studentData = new StudentData();
            studentData.setName(dataRO.getName());
            studentData.setGender(dataRO.getGender());
            studentData.setCandidateNumber(dataRO.getCandidateNumber());
            studentData.setTotalScore(dataRO.getTotalScore());
            studentData.setLevel(dataRO.getLevel());
            studentData.setMajorAdmitted(dataRO.getMajorAdmitted());
            studentData.setTuitionStandard(dataRO.getTuitionStandard());
            studentData.setStudyForm(dataRO.getStudyForm());
            studentData.setStudyDuration(dataRO.getLevel().equals("高起本") ? "5" : "3");
            studentData.setOldClassName("深圳中鹏");
            studentData.setGrade("2024");
            studentData.setNewClassName(dataRO.getImportClassName().replace("班", ""));
            studentData.setApplyReason("因深圳中鹏教学点无法开班，故申请转教学点。");

            data1.add(studentData);
        }

        for (StudentData info : data1) {
            String outputFileName = "D:\\ScnuWork\\xueliBackEnd\\src\\main\\resources\\data\\result\\" +
                    String.format("%s %s.xlsx", info.getCandidateNumber(), info.getName());  // 输出文件名格式：考生号 + 空格 + 学生姓名

            // 填充模板并保存为新文件
            EasyExcel.write(outputFileName)
                    .withTemplate(templateFileName)
                    .sheet()
                    .doFill(info); // 直接传入 info 对象，确保使用类型安全的方式填充数据
        }
    }



}


class ExcelDataProcessor {

    public void processStudentData(String fileName) {
        ExcelReaderBuilder readerBuilder = EasyExcel.read(fileName, StudentData.class, new StudentDataListener());

        readerBuilder.sheet().doRead();
    }
}

class StudentDataListener extends AnalysisEventListener<StudentData> {
    @Override
    public void invoke(StudentData data, AnalysisContext context) {
        // 这里可以处理每一行数据，例如填充模板并写入新的 Excel 文件
        System.out.println("处理数据: " + data.getName());
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        System.out.println("所有数据处理完毕");
    }
}
