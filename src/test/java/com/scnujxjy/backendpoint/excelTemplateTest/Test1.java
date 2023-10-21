package com.scnujxjy.backendpoint.excelTemplateTest;

import com.scnujxjy.backendpoint.dao.mapper.teaching_process.ScoreInformationMapper;
import com.scnujxjy.backendpoint.service.teaching_process.ScoreInformationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
@SpringBootTest
@Slf4j
public class Test1 {

    @Resource
    private ScoreInformationMapper scoreInformationMapper;

    @Resource
    private ScoreInformationService scoreInformationService;

    @Test
    public void test1(){
        try {
            String outputFileName = "data_export/评优个人成绩表.xlsx";

            // 创建工作簿和工作表
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Sheet1");

            // 创建字体样式
            Font font = workbook.createFont();
            font.setFontName("宋体");
            font.setFontHeightInPoints((short) 14);
            font.setBold(true);

            Font font1 = workbook.createFont();
            font1.setFontName("宋体");
            font1.setFontHeightInPoints((short) 12);

            CellStyle centerStyle = workbook.createCellStyle();
            centerStyle.setFont(font);
            centerStyle.setAlignment(HorizontalAlignment.CENTER);
            centerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            CellStyle centerStyle1 = workbook.createCellStyle();
            centerStyle1.setFont(font1);
            centerStyle1.setAlignment(HorizontalAlignment.CENTER);
            centerStyle1.setVerticalAlignment(VerticalAlignment.CENTER);

            // 合并第一行的前8个单元格
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));

            // 设置第一行的内容和样式
            Row row1 = sheet.createRow(0);
            Cell cell1 = row1.createCell(0);
            cell1.setCellStyle(centerStyle);
            cell1.setCellValue("2021级 经济与管理学院 电子商务专业 函授专科 3年制 惠州岭南班");

            // 设置第二行的内容和样式
            Row row2 = sheet.createRow(1);
            createStyledCell(row2, 0, "学号：", centerStyle);
            createStyledCell(row2, 1, "216309136017", centerStyle);
            createStyledCell(row2, 2, "姓名：", centerStyle);
            createStyledCell(row2, 4, "贺伟", centerStyle);

            // 合并第三行、第四行的第一列
            sheet.addMergedRegion(new CellRangeAddress(2, 3, 0, 0));
            sheet.addMergedRegion(new CellRangeAddress(2, 3, 1, 1));
            sheet.addMergedRegion(new CellRangeAddress(2, 3, 4, 4));


            // 设置第三行的内容和样式
            Row row3 = sheet.createRow(2);
            createStyledCell(row3, 0, "学年", centerStyle);

            // 合并第三行的第三列和第四列
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 2, 3));
            sheet.addMergedRegion(new CellRangeAddress(2, 2, 2, 3));
            sheet.addMergedRegion(new CellRangeAddress(2, 2, 5, 6));
            createStyledCell(row3, 2, "成绩", centerStyle);
            createStyledCell(row3, 1, "科目", centerStyle);
            createStyledCell(row3, 4, "科目", centerStyle);
            createStyledCell(row3, 5, "成绩", centerStyle);

            // 设置第四行的内容和样式
            Row row4 = sheet.createRow(3);
            createStyledCell(row4, 2, "考试", centerStyle);
            createStyledCell(row4, 3, "考查", centerStyle);
            createStyledCell(row4, 5, "考试", centerStyle);
            createStyledCell(row4, 6, "考查", centerStyle);

            // 模拟数据
            Map<Integer, Map<String, String>> data = new HashMap<>();
            Map<String, String> year1 = new HashMap<>();
            year1.put("大学英语（二）_考查课", "78");
            year1.put("政治经济学原理_考试课", "83");
            year1.put("大学英语（三）_考查课", "78");
            year1.put("计算机科学与技术_考试课", "83");
            year1.put("大学英语（四）_考试课", "83");
            data.put(1, year1);

            Map<String, String> year2 = new HashMap<>();
            year2.put("计算机网络基础_考试课", "80");
            year2.put("数据库技术_考试课", "74");
            data.put(2, year2);

            Map<String, String> year3 = new HashMap<>();
            year3.put("大学英语（二）_考查课", "78");
            year3.put("政治经济学原理_考试课", "83");
            year3.put("大学英语（三）_考查课", "78");
            year3.put("计算机科学与技术_考试课", "83");
            year3.put("大学英语（四）_考试课", "83");
            data.put(3, year3);

            Map<String, String> year4 = new HashMap<>();
            year4.put("计算机网络基础_考试课", "80");
            year4.put("数据库技术_考试课", "74");
            data.put(4, year4);

            // 开始写入数据
            int currentRowNum = 4; // 从第五行开始写入数据
            Set<Integer> rowsRecord = new HashSet<>();
            for (Map.Entry<Integer, Map<String, String>> entry : data.entrySet()) {
                Integer year = entry.getKey();
                Map<String, String> courses = entry.getValue();

                int startRowNum = currentRowNum; // 记录开始行号

                int courseColumn = 1;
                int courseCount = 0;
                for (Map.Entry<String, String> courseEntry : courses.entrySet()) {
                    courseCount++;
                    String courseName = courseEntry.getKey().split("_")[0];
                    String courseType = courseEntry.getKey().split("_")[1];
                    String score = courseEntry.getValue();

                    Row currentRow = sheet.getRow(currentRowNum);
                    if (currentRow == null) {
                        currentRow = sheet.createRow(currentRowNum);
                    }

                    if (courseColumn == 1) {
                        createStyledCell(currentRow, 0, "第" + year + "学年", centerStyle1);
                    }

                    createStyledCell(currentRow, courseColumn, courseName, centerStyle1);
                    if ("考试课".equals(courseType)) {
                        createStyledCell(currentRow, courseColumn + 1, score, centerStyle1);
                    } else {
                        createStyledCell(currentRow, courseColumn + 2, score, centerStyle1);
                    }

                    courseColumn += 3;
                    if (courseColumn > 4) {
                        currentRowNum++;
                        courseColumn = 1;
                    }
                }

                // 根据课程数量确定学年区域的行数
                int rowsForYear = (int) Math.ceil(courseCount / 2.0);

                // 设置文本方向为从上到下
                CellStyle verticalTextStyle = workbook.createCellStyle();
                verticalTextStyle.setRotation((short) 255);
                verticalTextStyle.setFont(font);
                verticalTextStyle.setAlignment(HorizontalAlignment.CENTER);
                verticalTextStyle.setVerticalAlignment(VerticalAlignment.CENTER);

                for (int i = startRowNum; i < startRowNum + rowsForYear; i++) {
                    Row yearRow = sheet.getRow(i);
                    Cell yearCell = yearRow.getCell(0);
                    if (yearCell == null) {
                        yearCell = yearRow.createCell(0);
                    }
                    yearCell.setCellStyle(verticalTextStyle);
                }

                // 如果学年区域的行数少于3行，设置一个特定的行高
                if (rowsForYear < 3) {
                    short specialHeight = (short) (85.9 * 20); // 例如设置为55.9磅
                    for (int i = startRowNum; i < startRowNum + rowsForYear; i++) {
                        Row yearRow = sheet.getRow(i);
                        yearRow.setHeight(specialHeight);
                        rowsRecord.add(i);
                    }
                }

                // 合并学年区域的第一列
                if (rowsForYear > 1) {
                    sheet.addMergedRegion(new CellRangeAddress(startRowNum, startRowNum + rowsForYear - 1, 0, 0));
                }

                currentRowNum = startRowNum + rowsForYear; // 移动到下一个学年的开始行
            }

            // 获取当前的最后一行号
            int lastRowNum = sheet.getLastRowNum();

            // 创建一个新的行
            Row averageRow = sheet.createRow(lastRowNum + 1);

            // 合并第2到第7列
            sheet.addMergedRegion(new CellRangeAddress(lastRowNum + 1, lastRowNum + 1, 1, 6));

            // 设置单元格的内容和样式
            Cell averageCell = averageRow.createCell(1);
            averageCell.setCellStyle(centerStyle1);
            averageCell.setCellValue("各科目成绩平均分：78.47");



//            int width = (int) (8.36 * 256); // 8.36字符宽度转换为1/256个字符宽度的单位
//            int width = (int) (22.98 * 256); // 8.36字符宽度转换为1/256个字符宽度的单位
            sheet.setColumnWidth(0, (int) (10.36 * 256)); // 第一列的索引是0
            sheet.setColumnWidth(1, (int) (24.98 * 256)); // 第一列的索引是1
            sheet.setColumnWidth(2, (int) (8.11 * 256)); // 第一列的索引是1
            sheet.setColumnWidth(3, (int) (8.11 * 256)); // 第一列的索引是1
            sheet.setColumnWidth(4, (int) (24.98 * 256)); // 第一列的索引是1
            sheet.setColumnWidth(5, (int) (8.11 * 256)); // 第一列的索引是1
            sheet.setColumnWidth(6, (int) (8.11 * 256)); // 第一列的索引是1


            // 设置所有行的默认高度为 27.95 磅
            short height = (short) (27.95 * 20); // 27.95磅转换为twips
            // 设置所有行的高度为 27.95 磅
            for (int i = 0; i <= sheet.getLastRowNum(); i++) {
                Row currentRow = sheet.getRow(i);
                if (currentRow == null) {
                    currentRow = sheet.createRow(i);
                }
                currentRow.setHeight(height);
            }

            log.info("这些行需要设置行高 " + rowsRecord);
            short specialHeight = (short) (85.9 * 20); // 例如设置为55.9磅
            for(Integer integer: rowsRecord){
                Row yearRow = sheet.getRow(integer);
                yearRow.setHeight(specialHeight);
            }



            // 使用FileOutputStream写入数据
            FileOutputStream fileOutputStream = new FileOutputStream(outputFileName);
            workbook.write(fileOutputStream);
            fileOutputStream.close();
            workbook.close();

        }catch (Exception e){
            log.error("生成 Excel失败" + e.toString());
        }
    }
    private static void createStyledCell(Row row, int columnIndex, String value, CellStyle style) {
        Cell cell = row.createCell(columnIndex);
        cell.setCellStyle(style);
        cell.setCellValue(value);
    }

    @Test
    public void test2(){
        scoreInformationService.exportAwardData("216309136017");
    }
}
