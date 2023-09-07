package com.scnujxjy.backendpoint.TeacherInformationTest;

import com.alibaba.excel.EasyExcel;
import com.scnujxjy.backendpoint.dao.entity.core_data.TeacherInformationPO;
import com.scnujxjy.backendpoint.dao.mapper.core_data.TeacherInformationMapper;
import com.scnujxjy.backendpoint.model.vo.core_data.TeacherInformationExcelImportVO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
@Slf4j
public class Test1 {

    @Autowired(required = false)
    private TeacherInformationMapper teacherInformationMapper;

    @Test
    public void test1() {
        String directoryPath = "src/main/resources/data/授课教师信息";
        File directory = new File(directoryPath);
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".xlsx"));

        if (files == null || files.length == 0) {
            log.error("没有找到任何 Excel 文件");
            return;
        }

        Map<String, Integer> fileDataCounts = new HashMap<>();
        AtomicInteger allCount = new AtomicInteger(0); // 使用 AtomicInteger 来存储总数

        for (File file : files) {
            TeacherInformationListener listener = new TeacherInformationListener(teacherInformationMapper);
            int headRowNumber = 1;
            EasyExcel.read(file.getAbsolutePath(), TeacherInformationExcelImportVO.class, listener)
                    .sheet().headRowNumber(headRowNumber).doRead();
            fileDataCounts.put(file.getName(), listener.getDataCount());
        }

        // 打印每个文件的教师信息记录数
        fileDataCounts.forEach((fileName, count) -> {
            log.info(fileName + " 包含 " + count + " 条教师信息记录");
            allCount.addAndGet(count);
        });

        log.info("总共读入 " + allCount.get() + " 记录");
    }
}
