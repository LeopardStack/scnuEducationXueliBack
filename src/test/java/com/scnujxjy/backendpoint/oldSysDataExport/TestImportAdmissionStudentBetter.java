package com.scnujxjy.backendpoint.oldSysDataExport;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scnujxjy.backendpoint.dao.entity.admission_information.AdmissionInformationPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.ScoreInformationPO;
import com.scnujxjy.backendpoint.dao.mapper.admission_information.AdmissionInformationMapper;
import com.scnujxjy.backendpoint.mapperTest.AdminInfoImportError;
import com.scnujxjy.backendpoint.model.vo.admission_information.AdmissionInformationVO;
import com.scnujxjy.backendpoint.util.SCNUXLJYDatabase;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;

import static com.scnujxjy.backendpoint.util.DataImportScnuOldSys.getGradeInfos;
import static com.scnujxjy.backendpoint.util.DataImportScnuOldSys.getStudentLuqus;

@Data
class ErrorAdmissionStudent extends AdmissionInformationVO {
    /**
     * 插入失败原因
     */
    @ExcelProperty(value = "插入失败原因", index = 26)
    private String errorMsg;
}

@Data
class QueueItem {
    private HashMap<String, String> studentData;
    private int grade;
}


@SpringBootTest
@Slf4j
public class TestImportAdmissionStudentBetter {

    @Resource
    private AdmissionInformationMapper admissionInformationMapper;

    /**
     * 遇到重复的记录是否覆盖
     */
    private static boolean overWrite = false;

    public void exportErrorListToExcel(List<ErrorAdmissionStudent> errorList, String outputPath) {
        EasyExcel.write(outputPath, ErrorAdmissionStudent.class).sheet("Sheet1").doWrite(errorList);
    }

    private static final int CONSUMER_COUNT = 200;
    private ExecutorService executorService;

    private BlockingQueue<QueueItem> queue = new LinkedBlockingQueue<>();


    private CountDownLatch latch;

    List<ErrorAdmissionStudent> errorList = new ArrayList<>();

    @Transactional
    int insertData(HashMap<String, String> studentData, int grade){

        ErrorAdmissionStudent errorAdmissionStudent = new ErrorAdmissionStudent();


        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
        TimeZone timeZone = TimeZone.getTimeZone("Asia/Shanghai"); // 设置为北京时间
        dateFormat1.setTimeZone(timeZone);
        dateFormat.setTimeZone(timeZone);

        AdmissionInformationPO admissionInformation = new AdmissionInformationPO();

        try {
            // 请根据实际的字段名和数据类型调整以下代码
            admissionInformation.setStudentNumber(studentData.get("KSH"));
            admissionInformation.setName(studentData.get("XM"));
            admissionInformation.setGender(studentData.get("XBDM"));
            admissionInformation.setTotalScore(Integer.valueOf(studentData.get("PXZF")));
            admissionInformation.setMajorCode(studentData.get("LQZY"));
            admissionInformation.setMajorName(studentData.get("ZYMC"));
            admissionInformation.setLevel(studentData.get("PYCC"));
            admissionInformation.setStudyForm(studentData.get("XXXS"));
            admissionInformation.setOriginalEducation(studentData.get("WHCDDM"));
            admissionInformation.setGraduationSchool(studentData.get("BYXX"));

            String graduatedDateString = studentData.get("BYRQ");
            if (graduatedDateString.trim().length() > 0) {
                try {
                    Date graduatedDate = dateFormat.parse(graduatedDateString);
                    admissionInformation.setGraduationDate(graduatedDate);
                } catch (ParseException e) {
                    try {
                        Date graduatedDate = dateFormat1.parse(graduatedDateString);
                        admissionInformation.setGraduationDate(graduatedDate);
                    } catch (Exception e1) {
                        log.error("毕业日期解析失败 " + graduatedDateString);
                        throw new RuntimeException("毕业日期解析失败 " + e.toString());
                    }
                }
            }

            admissionInformation.setPhoneNumber(studentData.get("LXDH"));
            admissionInformation.setIdCardNumber(studentData.get("SFZH"));

            String birthDateString = studentData.get("CSRQ");
            try {
                Date birthDate = dateFormat.parse(birthDateString);
                admissionInformation.setBirthDate(birthDate);
            } catch (ParseException e) {
                try {
                    Date birthDate = dateFormat1.parse(birthDateString);
                    admissionInformation.setBirthDate(birthDate);
                } catch (Exception e1) {
                    log.error("出生日期解析失败 " + birthDateString);
                    throw new RuntimeException("出生日期解析失败 " + e.toString());
                }
            }
            admissionInformation.setAddress(studentData.get("TXDZ"));
            admissionInformation.setPostalCode(studentData.get("YZBM"));
            admissionInformation.setEthnicity(studentData.get("MINZU"));
            admissionInformation.setPoliticalStatus(studentData.get("ZZMM"));
            admissionInformation.setAdmissionNumber(studentData.get("ZKZH"));
            admissionInformation.setShortStudentNumber(studentData.get("KSH"));
            admissionInformation.setGrade(String.valueOf(grade));

            List<AdmissionInformationPO> admissionInformationPOS = admissionInformationMapper.selectList(new LambdaQueryWrapper<AdmissionInformationPO>().eq(
                    AdmissionInformationPO::getGrade, String.valueOf(grade)
            ).eq(
                    AdmissionInformationPO::getIdCardNumber, admissionInformation.getIdCardNumber()
            ));

            if(admissionInformationPOS.size() > 1){
                throw new RuntimeException("该学生已经在数据库中出现了两次");
            }else if(admissionInformationPOS.size() == 1){
                if(overWrite){
                    // 直接覆盖掉这条记录
                    admissionInformationMapper.update(admissionInformation,
                            new LambdaQueryWrapper<AdmissionInformationPO>().eq(
                                    AdmissionInformationPO::getGrade, String.valueOf(grade)
                            ).eq(
                                    AdmissionInformationPO::getIdCardNumber, admissionInformation.getIdCardNumber()
                            ));
                    throw new RuntimeException("已覆盖");
                }else{
                    // 已存在，不覆盖
                }
            }else{
                int insert = admissionInformationMapper.insert(admissionInformation);
                if(insert == 1){
                    return 1;
                }else{
                    log.error("插入数据库失败 " + insert);
                    throw new RuntimeException("插入数据库失败 " + insert);
                }
            }
        }catch (Exception e){
            BeanUtils.copyProperties(admissionInformation, errorAdmissionStudent);
            errorAdmissionStudent.setErrorMsg(e.toString());
            errorList.add(errorAdmissionStudent);
        }
        return  -1;
    }

    @PostConstruct
    public void init() {

        latch = new CountDownLatch(CONSUMER_COUNT);
        // 创建消费者线程
        executorService = Executors.newFixedThreadPool(CONSUMER_COUNT);

        for (int i = 0; i < CONSUMER_COUNT; i++) {
            executorService.execute(() -> {
                try {
                    while (true) {
                        QueueItem item = queue.take();
                        if (item.getStudentData().containsKey("END")) {
                            break;
                        }
                        insertData(item.getStudentData(), item.getGrade());
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown();  // decrement the count
                }
            });
        }
    }

    @PreDestroy
    public void cleanup() {
        if (executorService != null) {
            executorService.shutdown();
        }
    }


    /**
     * 导入所有的录取新生名单
     * 2009 - 2023，注意，2023 是传递 -1 进去
     * @throws InterruptedException
     */
    @Test
    public void test1() throws InterruptedException {
        StringBuilder allGrades = new StringBuilder();
        for (int i = 2022; i >= 2010; i--) {
            // 检测新旧系统的成绩数目是否相同，相同则不需要更新
            long countAdminssionNew = admissionInformationMapper.selectCount(new LambdaQueryWrapper<AdmissionInformationPO>()
                    .eq(AdmissionInformationPO::getGrade,
                    i));
            log.info("新系统中导入的成绩记录数 " + countAdminssionNew);


            SCNUXLJYDatabase scnuxljyDatabase = new SCNUXLJYDatabase();
            String sql = "select count(*) from luqudata" + (i == 2023 ? "" : String.valueOf(i-1)) + "";
            int countAdminssionOld = (int) scnuxljyDatabase.getValue(sql);

            boolean updateInsert = true;
            try{
                if(countAdminssionOld == countAdminssionNew){
                    updateInsert = true; // 相等仍然要覆盖
                    overWrite = true;
                    log.info(i + " 年新旧两个系统的数据相等 新系统 " + countAdminssionNew + " 旧系统 " + countAdminssionOld);
                }

            }catch (Exception e){
                log.error("获取新旧系统成绩数据失败 " + i + "\n" + e.toString());
            }

            if(updateInsert){
                ArrayList<HashMap<String, String>> studentAdmissions = getStudentLuqus(i == 2023 ? -1 : i-1);
                for (HashMap<String, String> hashMap : studentAdmissions) {

                    QueueItem item = new QueueItem();
                    item.setStudentData(hashMap);
                    item.setGrade(i);
                    queue.put(item);

                }
                allGrades.append(i).append("_");
            }
        }

        // 传递毒药对象
        for (int i = 0; i < CONSUMER_COUNT; i++) {
            QueueItem poison = new QueueItem();
            HashMap<String, String> endMap = new HashMap<>();
            endMap.put("END", "TRUE");
            poison.setStudentData(endMap);
            queue.put(poison);
        }

        latch.await();

        // 调用新方法导出errorList
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String currentDateTime = LocalDateTime.now().format(formatter);
        String relativePath = "./data_import_error_excel/admissionStudent/";

        // 检查并创建目录
        try {
            Files.createDirectories(Paths.get(relativePath));
        } catch (IOException e) {
            e.printStackTrace();
            // 如果目录创建失败, 你可以决定是否继续，或者在此处终止程序。
        }

        String errorFileName = relativePath + currentDateTime + "_" + allGrades + "导入新生数据失败的部分数据.xlsx";
        exportErrorListToExcel(errorList, errorFileName);
    }





}
