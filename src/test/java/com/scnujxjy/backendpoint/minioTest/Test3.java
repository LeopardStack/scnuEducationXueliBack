package com.scnujxjy.backendpoint.minioTest;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scnujxjy.backendpoint.dao.entity.basic.GlobalConfigPO;
import com.scnujxjy.backendpoint.dao.mapper.basic.GlobalConfigMapper;
import com.scnujxjy.backendpoint.model.vo.exam.ExamTeachersInfoVO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.StudentStatusAllVO;
import com.scnujxjy.backendpoint.service.InterBase.OldDataSynchronize;
import com.scnujxjy.backendpoint.service.minio.MinioService;
import io.minio.MinioClient;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import io.minio.errors.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@SpringBootTest
@Slf4j
public class Test3 {
    @Resource
    MinioService minioService;

    @Resource
    MinioClient minioClient;

    @Resource
    private OldDataSynchronize oldDataSynchronize;

    @Resource
    private GlobalConfigMapper globalConfigMapper;

    @Test
    public void test1() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String bucketName = "datasynchronize";
        String objectName = "data_import_error_excel/studentStatusData/" +
                "20231007235228_2023_导入学籍数据失败的部分数据.xlsx";
        String objectName1 = "data_import_error_excel/studentStatusData/" +
                "20231007235228_2023_导入学籍数据失败的部分数据1.xlsx";
        StatObjectResponse statObjectResponse = minioClient.statObject(StatObjectArgs.builder().bucket(bucketName).object(objectName).build());
        log.info(statObjectResponse.toString());
        try {
            StatObjectResponse statObjectResponse1 = minioClient.statObject(StatObjectArgs.builder().bucket(bucketName).object(objectName1).build());
            log.info(statObjectResponse1.toString());
        }catch (Exception e){
            log.error(e.toString());
        }
    }

    @Test
    public void test2(){
        ArrayList<String> list1 = new ArrayList<>();
        list1.add("23451254412321412341234阿斯蒂芬嘎达沙发上放大沙发上");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String currentDateTime = LocalDateTime.now().format(formatter);
        String relativePath = "data_import_error_excel/statistics/";
        String errorFileName = relativePath + currentDateTime + "_" + "新旧系统数据同步总览.txt";
        oldDataSynchronize.exportListToTxtAndUploadToMinio(list1, errorFileName, "datasynchronize");

    }

    @Data
    class DateInfo {
        private String year;
        private String month;
        private String day;

        // 构造器、getters 和 setters
    }

    /**
     * 获取模板 Excel 文件的输入流
     */
    @Test
    public void test3(){
        // 设置填表时间
        DateInfo dateInfo = new DateInfo();
        dateInfo.setYear("2023");
        dateInfo.setMonth("09");
        dateInfo.setDay("01");

        // 或者使用 Map
        Map<String, Object> dateInfoMap = new HashMap<>();
        dateInfoMap.put("year", "2023");
        dateInfoMap.put("month", "09");
        dateInfoMap.put("day", "01");

        List<ExamTeachersInfoVO> examTeachersInfoVOS = Arrays.asList(
                new ExamTeachersInfoVO(1, "学院A", "2024", "学前教育", "全日制", "本科",
                        "教学班A", "教学班A", "课程A", "是", "开卷",
                        "张三", "1234567890", "李四",
                        "0987654321", "备注信息A"),
                new ExamTeachersInfoVO(2, "学院B","2024", "教学班A", "计算机科学与技术",
                        "非全日制", "研究生", "教学班B", "课程B",
                        "否", "闭卷", "王五\n阿龙", "1231231234\n14122341414",
                        "赵六", "4321432143", "备注信息B")
                // 您可以继续添加更多数据
        );

        InputStream fileInputStreamFromMinio = minioService.getFileInputStreamFromMinio(globalConfigMapper.selectOne(new LambdaQueryWrapper<GlobalConfigPO>()
                .eq(GlobalConfigPO::getConfigKey, "考试信息导出模板")).getConfigValue());

        // 生成文件名
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String currentDateTime = LocalDateTime.now().format(formatter);
        String relativePath = "data_export/examInfo/";
        String errorFileName = relativePath + currentDateTime + "_" + "新旧系统数据同步总览.xlsx";

        // 配置 Excel 写入操作
        ExcelWriter excelWriter = null;
        try {
            // 设置响应头（如果在Web环境中）
            // ExcelDataUtil.setResponseHeader(response, errorFileName);

            excelWriter = EasyExcel.write(errorFileName)
                    .withTemplate(fileInputStreamFromMinio)
                    .build();

            WriteSheet writeSheet = EasyExcel.writerSheet().build();
            FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.TRUE).build();
            excelWriter.fill(examTeachersInfoVOS, fillConfig, writeSheet);
            // 填充填表时间
            excelWriter.fill(dateInfoMap, writeSheet);  // 如果使用 Map

            excelWriter.finish();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (excelWriter != null) {
                excelWriter.finish();
            }
        }
    }
}
