package com.scnujxjy.backendpoint.oldSysDataExport;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.scnujxjy.backendpoint.dao.entity.college.CollegeInformationPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.ClassInformationPO;
import com.scnujxjy.backendpoint.dao.mapper.college.CollegeInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.ClassInformationMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.concurrent.*;

import static com.scnujxjy.backendpoint.util.DataImportScnuOldSys.getClassDatas;

@SpringBootTest
@Slf4j
public class TestGetAllClassDataBetter {

    @Autowired(required = false)
    private ClassInformationMapper classInformationMapper;

    @Autowired(required = false)
    private CollegeInformationMapper collegeInformationMapper;

    private static final int CONSUMER_COUNT = 100;
    private ExecutorService executorService;

    private BlockingQueue<HashMap<String, String>> queue = new LinkedBlockingQueue<>();  // Unbounded queue

    private CountDownLatch latch;

    @Transactional
    int insertData(HashMap<String, String> hashMap){
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy.MM");
        TimeZone timeZone = TimeZone.getTimeZone("Asia/Shanghai"); // 设置为北京时间
        dateFormat1.setTimeZone(timeZone);

        if(hashMap.get("ZT").contains("文凭")){
            return -2;
        }

        ClassInformationPO classInformationPO = new ClassInformationPO();
        classInformationPO.setClassIdentifier(hashMap.get("BSHI"));
        classInformationPO.setGrade(hashMap.get("NJ"));
        classInformationPO.setClassStudentPrefix(hashMap.get("BANDH"));
        classInformationPO.setClassName(hashMap.get("BMING"));
        classInformationPO.setStudyForm(hashMap.get("XSHI"));
        String studyPeriod = hashMap.get("XZHI");
        String tempLevel = hashMap.get("CCI");
        if(studyPeriod.equals("3")){
            if(tempLevel.equals("专科")){
                classInformationPO.setLevel("高起专");
            }else if(tempLevel.equals("本科")){
                classInformationPO.setLevel("专升本");
            }else{
                log.error("异常的层次 " + tempLevel + " 学制 " + studyPeriod);
            }
        }else if(studyPeriod.equals("5")){
            if(tempLevel.equals("本科")){
                classInformationPO.setLevel("高起本");
            }
            else{
                log.error("异常的层次 " + tempLevel + " 学制 " + studyPeriod);
            }
        }else{
            log.error("异常的层次 " + tempLevel + " 学制 " + studyPeriod);
        }

        classInformationPO.setCollege(hashMap.get("XI"));
        classInformationPO.setStudyPeriod(studyPeriod);
        classInformationPO.setFemaleCount(Integer.valueOf(hashMap.get("FEMALE")));
        classInformationPO.setTotalCount(Integer.valueOf(hashMap.get("TOTAL")));
        classInformationPO.setGraduateTotalCount(Integer.valueOf(hashMap.get("BYTOTAL")));
        classInformationPO.setGraduateFemaleCount(Integer.valueOf(hashMap.get("BYFEMALE")));
        classInformationPO.setMajorName(hashMap.get("ZHY"));


        String admissionDateString = hashMap.get("RXDATE");
        if (admissionDateString != null) {
            try {
                Date admissionDate = null;
                admissionDate = dateFormat1.parse(admissionDateString);
                classInformationPO.setAdmissionDate(admissionDate);
            } catch (ParseException e) {
                log.error("解析入学日期失败 " + hashMap.get("PK"));
            }
        }

        log.info(hashMap.toString());
        String graduationDateString = hashMap.get("BYDATE");
        if (graduationDateString != null) {
            try {
                Date graduationDate = null;
                graduationDate = dateFormat1.parse(graduationDateString);
                classInformationPO.setGraduationDate(graduationDate);
            } catch (ParseException e) {
                log.error("解析毕业日期失败 " + hashMap.get("PK"));
            }
        }

        classInformationPO.setStudentStatus(hashMap.get("ZT"));
        classInformationPO.setMajorCode(hashMap.get("LQZYDM"));
        try {
            classInformationPO.setTuition(BigDecimal.valueOf(Integer.parseInt(hashMap.get("XF"))));
        }catch (Exception e){
            log.error(e.toString());
        }
        classInformationPO.setIsTeacherStudent(!"否".equals(hashMap.get("NORMAL")));

        QueryWrapper<CollegeInformationPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("college_name", classInformationPO.getCollege());
        CollegeInformationPO collegeInformationPO = collegeInformationMapper.selectOne(queryWrapper);

        if(collegeInformationPO == null){
            log.error(classInformationPO.toString() + " 的学院不存在");
        }
        return classInformationMapper.insert(classInformationPO);
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
                        HashMap<String, String> hashMap = queue.take();
                        if(hashMap.containsKey("END")){
                            break;
                        }
                        insertData(hashMap);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown();  // decrement the count
                }
            });
        }
    }

    @Test
    public void test1() throws InterruptedException {
        ArrayList<HashMap<String, String>> classDatas = getClassDatas();
        log.info("班级数据总数 " + classDatas.size());

        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy.MM");
        TimeZone timeZone = TimeZone.getTimeZone("Asia/Shanghai"); // 设置为北京时间
        dateFormat1.setTimeZone(timeZone);

        for (HashMap<String, String> hashMap : classDatas) {

            queue.put(hashMap); // Put the object in the queue
        }

        // 传递毒药对象
        for (int i = 0; i < CONSUMER_COUNT; i++) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("END", "TRUE");
            queue.put(hashMap);
        }

        latch.await();

    }

    // Make sure you shut down the executorService when the application is stopped
    @PreDestroy
    public void cleanup() {
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}
