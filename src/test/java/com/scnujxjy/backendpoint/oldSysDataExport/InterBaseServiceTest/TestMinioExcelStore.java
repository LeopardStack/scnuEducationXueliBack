package com.scnujxjy.backendpoint.oldSysDataExport.InterBaseServiceTest;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.ExcelProperty;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.StudentStatusCommonPO;
import com.scnujxjy.backendpoint.service.minio.MinioService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
class ErrorStudentStatusExportExcelTest extends StudentStatusCommonPO {
    /**
     * 插入失败原因
     */
    @ExcelProperty(value = "插入失败原因", index = 26)
    private String errorMsg;
}

@SpringBootTest
@Slf4j
public class TestMinioExcelStore {
    @Resource
    MinioService minioService;

    /**
     * 将 map 存储到 minio 中的一个 txt 文件中
     * @param data
     * @param fileName
     * @param diyBucketName
     */
    public void exportMapToTxtAndUploadToMinio(Map<String, String> data, String fileName, String diyBucketName) {
        // Step 1: Convert the map to a string
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            stringBuilder.append(entry.getKey()).append(": ").append(entry.getValue()).append(System.lineSeparator());
        }

        // Step 2: Convert the string to ByteArrayInputStream
        ByteArrayInputStream inputStream = new ByteArrayInputStream(stringBuilder.toString().getBytes());

        // Step 3: Upload to Minio
        boolean success = minioService.uploadStreamToMinio(inputStream, fileName.endsWith(".txt") ? fileName : fileName + ".txt", diyBucketName);

        if (success) {
            log.info("Successfully uploaded to Minio.");
        } else {
            log.error("Failed to upload to Minio.");
        }

        // Close the stream
        try {
            inputStream.close();
        } catch (IOException e) {
            log.error("Error closing stream: " + e.getMessage());
        }
    }


    /**
     * 将 数据同步的结果 excel 存储到 minio
     * @param errorList
     * @param fileName
     * @param diyBucketName
     */
    public void exportErrorListToExcelAndUploadToMinio(List<ErrorStudentStatusExportExcelTest> errorList, String fileName, String diyBucketName) {
        // Step 1: Write data to ByteArrayOutputStream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        EasyExcel.write(outputStream, ErrorStudentStatusExportExcelTest.class).sheet("Sheet1").doWrite(errorList);

        // Step 2: Convert ByteArrayOutputStream to ByteArrayInputStream
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        // Step 3: Upload to Minio
        boolean success = minioService.uploadStreamToMinio(inputStream, fileName, diyBucketName);

        if (success) {
            log.info("Successfully uploaded to Minio.");
        } else {
            log.error("Failed to upload to Minio.");
        }

        // Close the streams
        try {
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            log.error("Error closing streams: " + e.getMessage());
        }
    }

    @Test
    public void testUploadToMinio() {
        // 1. 创建一个模拟的错误列表
        List<ErrorStudentStatusExportExcelTest> errorList = new ArrayList<>();

        ErrorStudentStatusExportExcelTest student1 = new ErrorStudentStatusExportExcelTest();
        student1.setStudentNumber("S12345");
        student1.setGrade("Grade1");
        student1.setCollege("College1");
        student1.setTeachingPoint("Point1");
        student1.setMajorName("Major1");
        student1.setStudyForm("Form1");
        student1.setErrorMsg("Sample error message 1");
        errorList.add(student1);

        ErrorStudentStatusExportExcelTest student2 = new ErrorStudentStatusExportExcelTest();
        student2.setStudentNumber("S67890");
        student2.setGrade("Grade2");
        student2.setCollege("College2");
        student2.setTeachingPoint("Point2");
        student2.setMajorName("Major2");
        student2.setStudyForm("Form2");
        student2.setErrorMsg("Sample error message 2");
        errorList.add(student2);

        // 2. 调用方法将错误列表导出到Excel并上传到Minio
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String currentDateTime = LocalDateTime.now().format(formatter);
        String relativePath = "data_import_error_excel/studentStatusData/";
        String errorFileName = relativePath + currentDateTime + "_" + "2023" + "导入学籍数据失败的部分数据.xlsx";
//        exportErrorListToExcel(errorList, errorFileName);
        String testBucketName = "datasynchronize";
        exportErrorListToExcelAndUploadToMinio(errorList, errorFileName, testBucketName);

        // Note: 为了完整的测试，你可能还需要在Minio中验证上传的文件。此外，确保MinioService正常工作。
    }

    @Test
    public void testExportMapToTxtAndUploadToMinio() {
        // 1. Create a mock map
        Map<String, String> mockData = new HashMap<>();
        mockData.put("Key1", "Value1");
        mockData.put("Key2", "Value2");
        mockData.put("Key3", "Value3");

        // 2. Call the method
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String currentDateTime = LocalDateTime.now().format(formatter);
        String relativePath = "data_import_error_excel/studentStatusData/";
        String errorFileName = relativePath + currentDateTime + "_" + "学籍数据同步总览.txt";
//        exportErrorListToExcel(errorList, errorFileName);
        String testBucketName = "datasynchronize";
        exportMapToTxtAndUploadToMinio(mockData, errorFileName, testBucketName);

        // Note: For a full test, you might want to also check Minio to ensure the file was uploaded correctly.
    }

    @Test
    public void test4(){
        // 获取指定桶中的所有文件名 查看中文文件是否正常显示
        List<String> datasynchronize = minioService.getAllFileNames("datasynchronize");
        log.info("文件数量总计 " + datasynchronize.size());
        log.info(datasynchronize.toString());
    }

}
