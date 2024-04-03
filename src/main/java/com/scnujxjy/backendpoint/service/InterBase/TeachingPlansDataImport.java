package com.scnujxjy.backendpoint.service.InterBase;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.ClassInformationPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseInformationPO;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.ClassInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.StudentStatusMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseInformationMapper;
import com.scnujxjy.backendpoint.service.minio.MinioService;
import com.scnujxjy.backendpoint.util.ApplicationContextProvider;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.*;

import static com.scnujxjy.backendpoint.service.InterBase.OldDataSynchronize.CONSUMER_COUNT;

@Data
@Slf4j
public class TeachingPlansDataImport {
    private ClassInformationMapper classInformationMapper;

    private CourseInformationMapper courseInformationMapper;

    private MinioService minioService;

    // 记录文凭班教学计划数量
    private int wpCount;
    private int fwpCount;

    // 是否强行覆盖
    private static boolean updateAny = false;

    // 记录每年的更新记录数
    public Map<String, Long> updateCountMap = new ConcurrentHashMap<>();


    // 存储插入日志
    public List<String> insertLogsList = Collections.synchronizedList(new ArrayList<>());

    public void setUpdateAny(boolean updateAnySet){
        updateAny = updateAnySet;
    }

    public TeachingPlansDataImport(){
        ApplicationContext ctx = ApplicationContextProvider.getApplicationContext();
        this.classInformationMapper = ctx.getBean(ClassInformationMapper.class);
        this.courseInformationMapper = ctx.getBean(CourseInformationMapper.class);
        this.minioService = ctx.getBean(MinioService.class);
        wpCount = 0;
        fwpCount = 0;

        this.init();
    }

    public ExecutorService executorService;

    public BlockingQueue<HashMap<String, String>> queue = new LinkedBlockingQueue<>();  // Unbounded queue

    public CountDownLatch latch;

    public void init() {

        latch = new CountDownLatch(CONSUMER_COUNT);
        // 创建消费者线程
        executorService = Executors.newFixedThreadPool(CONSUMER_COUNT);

        for (int i = 0; i < CONSUMER_COUNT; i++) {
            executorService.execute(() -> {
                try {
                    while (true) {
                        log.info("Thread ID: " + Thread.currentThread().getId() + " - Queue size: " + queue.size());

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



    @Transactional(rollbackFor = Exception.class)
    public int insertData(HashMap<String, String> hashMap){
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
                insertLogsList.add("找不到任何班级信息 但是存在教学计划 而且不是文凭班的 " + hashMap);
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
                insertLogsList.add("错误的课时格式 " + kshi);
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
//            LambdaQueryWrapper<CourseInformationPO> courseInformationPOLambdaQueryWrapper = new LambdaQueryWrapper<CourseInformationPO>().
//                    eq(CourseInformationPO::getGrade, courseInformationPO.getGrade()).
//                    eq(CourseInformationPO::getMajorName, courseInformationPO.getMajorName()).
//                    eq(CourseInformationPO::getLevel, courseInformationPO.getLevel()).
//                    eq(CourseInformationPO::getStudyForm, courseInformationPO.getStudyForm()).
//                    eq(CourseInformationPO::getCourseCode, courseInformationPO.getCourseCode()).
//                    eq(CourseInformationPO::getCourseName, courseInformationPO.getCourseName()).
//                    eq(CourseInformationPO::getTeachingSemester, courseInformationPO.getTeachingSemester());
//            List<CourseInformationPO> courseInformationPOS = courseInformationMapper.selectList(courseInformationPOLambdaQueryWrapper);
//            if(courseInformationPOS.size() > 1){
//                insertLogsList.add(hashMap + " 存在多条相同的教学计划记录");
//                return  -1;
//            }else if(courseInformationPOS.size() == 1){
//                if(updateAny){
//                    int update = courseInformationMapper.update(courseInformationPO, courseInformationPOLambdaQueryWrapper);
//                    if(update > 0){
//                        String key1 = "教学计划更新";
//                        synchronized (this){
//                            if(!updateCountMap.containsKey(key1)){
//                                updateCountMap.put(key1, 1L);
//                            }else{
//                                updateCountMap.put(key1, updateCountMap.get(key1) + 1L);
//                            }
//                        }
//                        return update;
//                    }
//                }
//                return -3;
//            }else{
//                int insert = courseInformationMapper.insert(courseInformationPO);
//                if(insert != 1){
//                    insertLogsList.add("插入失败 " + hashMap);
//                }
//                return insert;
//            }

            if(updateAny){
                int insert = courseInformationMapper.insert(courseInformationPO);
                if(insert != 1){
                    insertLogsList.add("插入失败 " + hashMap);
                }
                return insert;
            }


        } catch (RuntimeException e) {
            insertLogsList.add("数据库插入失败 " + hashMap);
        }

        return -1;

    }


}
