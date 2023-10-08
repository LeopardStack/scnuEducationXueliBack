package com.scnujxjy.backendpoint.service.InterBase;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.scnujxjy.backendpoint.dao.entity.college.CollegeInformationPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.ClassInformationPO;
import com.scnujxjy.backendpoint.dao.mapper.admission_information.AdmissionInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.college.CollegeInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.*;
import com.scnujxjy.backendpoint.service.minio.MinioService;
import com.scnujxjy.backendpoint.util.ApplicationContextProvider;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

import static com.scnujxjy.backendpoint.service.InterBase.OldDataSynchronize.CONSUMER_COUNT;

@Data
@Slf4j
public class ClassInformationDataImport {
    private ClassInformationMapper classInformationMapper;
    private CollegeInformationMapper collegeInformationMapper;

    private MinioService minioService;

    public ExecutorService executorService;

    public BlockingQueue<HashMap<String, String>> queue = new LinkedBlockingQueue<>();  // Unbounded queue

    public CountDownLatch latch;

    // 记录非学历的班级个数
    private int fxl_class_count = 0;
    private int xl_class_count = 0;

    // 记录额外的插入日志
    public List<String> insertLogs = Collections.synchronizedList(new ArrayList<>());

    // 记录每年的更新记录数
    public Map<String, Long> updateCountMap = new ConcurrentHashMap<>();

    // 强行更新标志，即无论数据数量是否一致，覆盖库中的数据进行强制更新
    private static boolean updateAny = true;

    public void setUpdateAny(boolean updateAnySet){
        updateAny = updateAnySet;
    }


    public ClassInformationDataImport() {
        ApplicationContext ctx = ApplicationContextProvider.getApplicationContext();
        this.classInformationMapper = ctx.getBean(ClassInformationMapper.class);
        this.minioService = ctx.getBean(MinioService.class);
        this.collegeInformationMapper = ctx.getBean(CollegeInformationMapper.class);

        this.init();
    }

    @Transactional(rollbackFor = Exception.class)
    int insertData(HashMap<String, String> hashMap){
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy.MM");

        if(hashMap.get("ZT").contains("文凭")){
            synchronized (this){
                fxl_class_count += 1;
            }
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
                insertLogs.add("异常的层次 " + tempLevel + " 学制 " + studyPeriod);
//                log.error("异常的层次 " + tempLevel + " 学制 " + studyPeriod);
            }
        }else if(studyPeriod.equals("5")){
            if(tempLevel.equals("本科")){
                classInformationPO.setLevel("高起本");
            }
            else{
                insertLogs.add("异常的层次 " + tempLevel + " 学制 " + studyPeriod);
//                log.error("异常的层次 " + tempLevel + " 学制 " + studyPeriod);
            }
        }else{
            insertLogs.add("异常的层次 " + tempLevel + " 学制 " + studyPeriod);
//            log.error("异常的层次 " + tempLevel + " 学制 " + studyPeriod);
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
                insertLogs.add("解析入学日期失败 " + hashMap.get("PK"));
//                log.error("解析入学日期失败 " + hashMap.get("PK"));
            }
        }

        String graduationDateString = hashMap.get("BYDATE");
        if (graduationDateString != null) {
            try {
                Date graduationDate = null;
                graduationDate = dateFormat1.parse(graduationDateString);
                classInformationPO.setGraduationDate(graduationDate);
            } catch (ParseException e) {
                insertLogs.add("解析毕业日期失败 " + hashMap.get("PK"));
//                log.error("解析毕业日期失败 " + hashMap.get("PK"));
            }
        }

        classInformationPO.setStudentStatus(hashMap.get("ZT"));
        classInformationPO.setMajorCode(hashMap.get("LQZYDM"));
        try {
            classInformationPO.setTuition(BigDecimal.valueOf(Integer.parseInt(hashMap.get("XF"))));
        }catch (Exception e){
            insertLogs.add(hashMap + " 设置班级学费失败 " + e.toString());
        }
        classInformationPO.setIsTeacherStudent(!"否".equals(hashMap.get("NORMAL")));

        QueryWrapper<CollegeInformationPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("college_name", classInformationPO.getCollege());
        CollegeInformationPO collegeInformationPO = collegeInformationMapper.selectOne(queryWrapper);

        if(collegeInformationPO == null){
            insertLogs.add(classInformationPO.toString() + " 的学院不存在");
        }

        List<ClassInformationPO> classInformationPOS = classInformationMapper.selectList(new LambdaQueryWrapper<ClassInformationPO>().
                eq(ClassInformationPO::getClassIdentifier, classInformationPO.getClassIdentifier()));
        if(classInformationPOS.size() > 1){
            insertLogs.add("存在多个班级记录 " + classInformationPO.toString());
        }else if(classInformationPOS.size() == 1){
            if(updateAny){
                int update = classInformationMapper.update(classInformationPO, new LambdaQueryWrapper<ClassInformationPO>().
                        eq(ClassInformationPO::getClassIdentifier, classInformationPO.getClassIdentifier()));
                if(update > 0){
                    String key1 = "成功更新学生班级数据";
                    synchronized (this){
                        if(!updateCountMap.containsKey(key1)){
                            updateCountMap.put(key1, 1L);
                        }else{
                            updateCountMap.put(key1, updateCountMap.get(key1) + 1L);
                        }
                    }
                }else{
                    String key1 = "失败更新学生班级数据";
                    synchronized (this){
                        if(!updateCountMap.containsKey(key1)){
                            updateCountMap.put(key1, 1L);
                        }else{
                            updateCountMap.put(key1, updateCountMap.get(key1) + 1L);
                        }
                    }
                }
            }
        }else{
            int insert = classInformationMapper.insert(classInformationPO);
            if(insert > 0){
                String key1 = "成功插入学生班级数据";
                synchronized (this){
                    if(!updateCountMap.containsKey(key1)){
                        updateCountMap.put(key1, 1L);
                    }else{
                        updateCountMap.put(key1, updateCountMap.get(key1) + 1L);
                    }
                }
            }else{
                String key1 = "失败插入学生班级数据";
                synchronized (this){
                    if(!updateCountMap.containsKey(key1)){
                        updateCountMap.put(key1, 1L);
                    }else{
                        updateCountMap.put(key1, updateCountMap.get(key1) + 1L);
                    }
                }
            }
        }

        synchronized (this){
            xl_class_count += 1;
        }

        return 0;
    }


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
}
