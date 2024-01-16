package com.scnujxjy.backendpoint.paymentInfoImport;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scnujxjy.backendpoint.TeacherInformationTest.TeacherInformationListener;
import com.scnujxjy.backendpoint.dao.entity.core_data.PaymentInfoPO;
import com.scnujxjy.backendpoint.dao.mapper.admission_information.AdmissionInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.core_data.PaymentInfoMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.StudentStatusMapper;
import com.scnujxjy.backendpoint.model.ro.core_data.PaymentInfoImportRO;
import com.scnujxjy.backendpoint.model.vo.core_data.TeacherInformationExcelImportVO;
import com.scnujxjy.backendpoint.util.excelListener.NewStudentPaymentInfoConvertToOldSystemListener;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
@Slf4j
public class Test1 {

    @Resource
    private PaymentInfoMapper paymentInfoMapper;

    @Resource
    private StudentStatusMapper studentStatusMapper;

    @Resource
    private AdmissionInformationMapper admissionInformationMapper;

    /**
     * 将财务部导出的数据 转成梁院系统所需的格式 从而导入
     */
    @Test
    public void test1() {
        String directoryPath = "src/main/resources/data/缴费信息导入/老生";
        String outputDirectoryPath = "src/main/resources/data/缴费信息结果";
        File directory = new File(directoryPath);
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".xlsx"));

        if (files == null || files.length == 0) {
            log.error("没有找到任何 Excel 文件");
            return;
        }

        Map<String, Integer> fileDataCounts = new HashMap<>();
        AtomicInteger allCount = new AtomicInteger(0); // 使用 AtomicInteger 来存储总数

        for (File file : files) {
            String originalFileName = file.getName();
            PaymentInfoListener listener = new PaymentInfoListener(paymentInfoMapper, studentStatusMapper, outputDirectoryPath, originalFileName);
            int headRowNumber = 1;
            EasyExcel.read(file.getAbsolutePath(), PaymentInfoImportRO.class, listener)
                    .sheet().headRowNumber(headRowNumber).doRead();
            fileDataCounts.put(file.getName(), listener.getDataCount());
        }

        // 打印每个文件的教师信息记录数
        fileDataCounts.forEach((fileName, count) -> {
            log.info(fileName + " 包含 " + count + " 条缴费信息记录");
            allCount.addAndGet(count);
        });

        log.info("总共读入 " + allCount.get() + " 记录");
    }


    /**
     * 更新新生缴费数据
     */
    @Test
    public void test2(){
        // 清除 2024 的缴费数据
//        int delete = paymentInfoMapper.delete(new LambdaQueryWrapper<PaymentInfoPO>()
//                .eq(PaymentInfoPO::getGrade, "2024"));
//        log.info("删除了 2024级的缴费数据 " + delete);

        String newStudentFee = "D:\\ScnuWork\\xueli\\xueliBackEnd\\src\\main\\resources\\data\\缴费信息导入\\2024级新生\\成教新生收费20240115.xlsx";

        NewStudentPaymentInfoListener listener = new NewStudentPaymentInfoListener(paymentInfoMapper,
                admissionInformationMapper);
        int headRowNumber = 1;
        // 使用EasyExcel读取文件
        EasyExcel.read(newStudentFee, PaymentInfoImportRO.class, listener)
                .sheet().headRowNumber(headRowNumber).doRead();
    }


    /**
     * 将新生数据转为旧系统所能识别的缴费数据
     */
    @Test
    public void test3(){
        String directoryPath = "src/main/resources/data/缴费信息导入/2024级新生";
        String outputDirectoryPath = "src/main/resources/data/缴费信息结果/2024级新生";
        File directory = new File(directoryPath);
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".xlsx"));

        if (files == null || files.length == 0) {
            log.error("没有找到任何 Excel 文件");
            return;
        }

        Map<String, Integer> fileDataCounts = new HashMap<>();
        AtomicInteger allCount = new AtomicInteger(0); // 使用 AtomicInteger 来存储总数

        for (File file : files) {
            String originalFileName = file.getName();
            NewStudentPaymentInfoConvertToOldSystemListener listener = new NewStudentPaymentInfoConvertToOldSystemListener(admissionInformationMapper, outputDirectoryPath, originalFileName);
            int headRowNumber = 1;
            EasyExcel.read(file.getAbsolutePath(), PaymentInfoImportRO.class, listener)
                    .sheet().headRowNumber(headRowNumber).doRead();
            fileDataCounts.put(file.getName(), listener.getCurrentDataCount());
        }

        // 打印每个文件的教师信息记录数
        fileDataCounts.forEach((fileName, count) -> {
            log.info(fileName + " 包含 " + count + " 条新生缴费信息记录");
            allCount.addAndGet(count);
        });

        log.info("总共读入 " + allCount.get() + " 记录");
    }

}
