package com.scnujxjy.backendpoint.service.InterBase;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.ClassInformationPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseInformationPO;
import com.scnujxjy.backendpoint.dao.mapper.oa.*;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.ClassInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseInformationMapper;
import com.scnujxjy.backendpoint.service.minio.MinioService;
import com.scnujxjy.backendpoint.util.ApplicationContextProvider;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.*;

import static com.scnujxjy.backendpoint.service.InterBase.OldDataSynchronize.CONSUMER_COUNT;

@Data
@Slf4j
public class StudentStatusChangeDataImport {

    private MajorChangeRecordMapper majorChangeRecordMapper;
    private DropoutRecordMapper dropoutRecordMapper;
    private ResumptionRecordMapper resumptionRecordMapper;
    private RetentionRecordMapper retentionRecordMapper;
    private SuspensionRecordMapper suspensionRecordMapper;

    private MinioService minioService;

    // 是否强行覆盖
    private static boolean updateAny = false;

    // 记录各种审批类型的数量
    public Map<String, Integer> applyCount = new ConcurrentHashMap<>();
    public Map<Map<String, String>, String> insertLogList = new ConcurrentHashMap<>();

    // 存储插入日志
    public List<String> insertLogsList = Collections.synchronizedList(new ArrayList<>());

    public void setUpdateAny(boolean updateAnySet){
        updateAny = updateAnySet;
    }

    public StudentStatusChangeDataImport(){
        ApplicationContext ctx = ApplicationContextProvider.getApplicationContext();
        this.majorChangeRecordMapper = ctx.getBean(MajorChangeRecordMapper.class);
        this.dropoutRecordMapper = ctx.getBean(DropoutRecordMapper.class);
        this.resumptionRecordMapper = ctx.getBean(ResumptionRecordMapper.class);
        this.retentionRecordMapper = ctx.getBean(RetentionRecordMapper.class);
        this.suspensionRecordMapper = ctx.getBean(SuspensionRecordMapper.class);
        this.minioService = ctx.getBean(MinioService.class);

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
        try {
            if (hashMap.containsKey("CTYPE")) {
                String s = hashMap.get("CTYPE");
                if("转学".equals(s)){
                    log.info("转学到校内");

                    if("转学".equals(hashMap.get("new_BSHI"))){
                        // 转到校外或者省外的
                    }
                }else if("休学".equals(s)){

                }else if("复学".equals(s)){

                }else if("退学".equals(s)){

                }else if("留级".equals(s)){

                }else{
                    throw new RuntimeException("出现了休复转退留之外的审批类型" + hashMap);
                }
                if(applyCount.containsKey(s)){
                    Integer i = applyCount.get(s);
                    applyCount.put(s, i+1);
                }else{
                    applyCount.put(s, 1);
                }
                String allKey = "总数";
                synchronized (this) {
                    if (applyCount.containsKey(allKey)) {
                        Integer i = applyCount.get(allKey);
                        applyCount.put(allKey, i + 1);
                    } else {
                        applyCount.put(allKey, 1);
                    }
                }
            } else {
                throw new IllegalArgumentException("该学籍异动记录 没有类型字段 ");
            }
        }catch (Exception e){
            if(insertLogList.containsKey(hashMap)){
                throw new RuntimeException("出现重复的学籍异动记录 " + e.toString() + "\n" + hashMap);
            }
            insertLogList.put(hashMap, e.toString());
        }


        return 1;

    }
}
