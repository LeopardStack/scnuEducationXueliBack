package com.scnujxjy.backendpoint.oldSysDataExport;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.ClassInformationPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseInformationPO;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.ClassInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseInformationMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.scnujxjy.backendpoint.util.DataImportScnuOldSys.getTeachingPlans;

@SpringBootTest
@Slf4j
public class TestGetAllTeachingPlansBetter {
    @Autowired(required = false)
    private ClassInformationMapper classInformationMapper;

    @Autowired(required = false)
    private CourseInformationMapper courseInformationMapper;

    private static final int CONSUMER_COUNT = 200;
    private ExecutorService executorService;

    private BlockingQueue<HashMap<String, String>> queue = new LinkedBlockingQueue<>();  // Unbounded queue

    private CountDownLatch latch;

    // 记录文凭班教学计划数量
    private int wpCount = 0;
    private int fwpCount = 0;


    @Transactional
    int insertData(HashMap<String, String> hashMap){
        CourseInformationPO courseInformationPO = new CourseInformationPO();
        String classIdentifier = hashMap.get("BSHI");

        QueryWrapper<ClassInformationPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("class_identifier", classIdentifier);
        ClassInformationPO classInformationPO = classInformationMapper.selectOne(queryWrapper);

        if(classInformationPO == null){
            if(classIdentifier.startsWith("WP")){
                synchronized(this) {
                    wpCount += 1;
                }
            }else{
                // 找不到任何班级信息 但是存在教学计划 而且不是文凭班的
                log.info("找不到任何班级信息 但是存在教学计划 而且不是文凭班的 " + hashMap);
            }
//            log.error(hashMap.toString() + " 文凭班级的教学计划");
            return -2;
        }else{
            synchronized(this) {
                fwpCount += 1;
            }
        }



        courseInformationPO.setGrade(classInformationPO.getGrade());
        courseInformationPO.setMajorName(classInformationPO.getMajorName());
        courseInformationPO.setLevel(classInformationPO.getLevel());
        courseInformationPO.setStudyForm(classInformationPO.getStudyForm());
        courseInformationPO.setAdminClass(hashMap.get("BSHI"));
        courseInformationPO.setCourseName(hashMap.get("KCHM"));
        String kshi = hashMap.get("KSHI");
        if(kshi == null || kshi.trim() .length() == 0 || kshi.equals("NULL")){


        }else{
            try {
                courseInformationPO.setStudyHours(Integer.valueOf(kshi));
            }catch (NumberFormatException n){
                log.error("错误的课时格式 " + kshi);
                if(kshi.contains("周")){
                    kshi = kshi.replace("周", "");
                    courseInformationPO.setStudyHours(Integer.parseInt(kshi) * 24);
                }else{
                    kshi = kshi.replace("/", "");
                    courseInformationPO.setStudyHours(Integer.valueOf(kshi));
                }
            }
        }

        courseInformationPO.setAssessmentType(hashMap.get("FSHI"));
        courseInformationPO.setTeachingMethod("线下");
        courseInformationPO.setCourseType(hashMap.get("TYPES"));
        courseInformationPO.setTeachingSemester(hashMap.get("XQI"));
        courseInformationPO.setCourseCode(hashMap.get("KCHH"));

        try {
            int insert = courseInformationMapper.insert(courseInformationPO);
            if(insert != 1){
                log.error("插入失败 " + hashMap);
            }
            return insert;
        } catch (RuntimeException e) {
            // Log or handle the exception
            // Maybe return a special value or throw a custom exception to indicate the failure
            log.error("数据库插入失败 " + hashMap);
        }

        return -1;

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
        ArrayList<HashMap<String, String>> teachingPlans = getTeachingPlans();
        log.info("教学计划总数 " + teachingPlans.size());


        for (HashMap<String, String> hashMap : teachingPlans) {

            queue.put(hashMap); // Put the object in the queue
        }

        // 传递毒药对象
        for (int i = 0; i < CONSUMER_COUNT; i++) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("END", "TRUE");
            queue.put(hashMap);
        }

        latch.await();

        log.info("文凭班教学计划总数为 " + wpCount);
        log.info("非文凭班教学计划总数为 " + fwpCount);
    }

    // Make sure you shut down the executorService when the application is stopped
    @PreDestroy
    public void cleanup() {
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}
