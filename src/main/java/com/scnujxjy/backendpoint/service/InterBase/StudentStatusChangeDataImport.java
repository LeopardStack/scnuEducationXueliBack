package com.scnujxjy.backendpoint.service.InterBase;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scnujxjy.backendpoint.dao.entity.office_automation.approval_result.*;
import com.scnujxjy.backendpoint.dao.mapper.office_automation.approval_result.*;
import com.scnujxjy.backendpoint.model.bo.interbase.OldStudentStatusChangeDataBO;
import com.scnujxjy.backendpoint.service.minio.MinioService;
import com.scnujxjy.backendpoint.util.ApplicationContextProvider;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.context.ApplicationContext;

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

    // 记录各种审批类型的数量
    public Map<String, Integer> applyCount = new ConcurrentHashMap<>();
    public Map<String, Integer> applyImportCount = new ConcurrentHashMap<>();
    public Map<Map<String, String>, String> insertLogList = new ConcurrentHashMap<>();

    // 存储插入日志
    public List<String> insertLogsList = Collections.synchronizedList(new ArrayList<>());


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

    public int insertData(HashMap<String, String> hashMap){
        try {
            if (hashMap.containsKey("CTYPE")) {
                String s = hashMap.get("CTYPE");
                // 使用 BeanUtils 将 map 的内容映射到 myBean
                OldStudentStatusChangeDataBO oldStudentStatusChangeDataBO = new OldStudentStatusChangeDataBO();
                BeanUtils.populate(oldStudentStatusChangeDataBO, hashMap);
                if("转学".equals(s)){
//                    log.info("转学到校内");
                    MajorChangeRecordPO majorChangeRecordPO = new MajorChangeRecordPO();
                    String idnum = oldStudentStatusChangeDataBO.getIDNUM();
                    majorChangeRecordPO.setIdNumber(oldStudentStatusChangeDataBO.getSFZH());
                    majorChangeRecordPO.setCurrentYear(idnum.substring(0, 4));
                    int index = Integer.parseInt(idnum.substring(4).replaceFirst("^0+(?!$)", ""));
                    majorChangeRecordPO.setSerialNumber(index);
                    majorChangeRecordPO.setApprovalDate(oldStudentStatusChangeDataBO.getUPDATETIME());
                    majorChangeRecordPO.setStudentName(oldStudentStatusChangeDataBO.getXM());
                    majorChangeRecordPO.setStudentNumber(oldStudentStatusChangeDataBO.getXHAO());
                    majorChangeRecordPO.setReason(oldStudentStatusChangeDataBO.getREASON());
                    majorChangeRecordPO.setExamRegistrationNumber(oldStudentStatusChangeDataBO.getZKZH());
                    majorChangeRecordPO.setOldClassIdentifier(oldStudentStatusChangeDataBO.getBSHI());
                    majorChangeRecordPO.setOldGrade(oldStudentStatusChangeDataBO.getNJ());
                    majorChangeRecordPO.setOldStudyForm(oldStudentStatusChangeDataBO.getXSHI());
                    majorChangeRecordPO.setOldMajorName(oldStudentStatusChangeDataBO.getZHY());



                    if("转学".equals(hashMap.get("new_BSHI"))){
                        // 转到校外或者省外的
//                        log.info("转学到校外");
                        majorChangeRecordPO.setRemark("转学到校外/省外");
                    }else{
                        majorChangeRecordPO.setNewClassIdentifier(oldStudentStatusChangeDataBO.getNew_BSHI());
                        majorChangeRecordPO.setNewGrade(oldStudentStatusChangeDataBO.getNew_NJ());
                        majorChangeRecordPO.setNewStudyForm(oldStudentStatusChangeDataBO.getNew_XSHI());
                        majorChangeRecordPO.setNewMajorName(oldStudentStatusChangeDataBO.getNew_ZHY());
                        majorChangeRecordPO.setRemark("在籍生校内转专业");
                    }
                    // 检测重复导入
                    Integer i = majorChangeRecordMapper.selectCount(new LambdaQueryWrapper<MajorChangeRecordPO>()
                            .eq(MajorChangeRecordPO::getCurrentYear, idnum.substring(0, 4))
                            .eq(MajorChangeRecordPO::getIdNumber, majorChangeRecordPO.getIdNumber())
                            .eq(MajorChangeRecordPO::getSerialNumber, index)
                    );
                    if(i > 0){
                        log.info("已重复，不必导入 " + hashMap);
                    }else{
                        int insert = majorChangeRecordMapper.insert(majorChangeRecordPO);
                        if(insert <= 0){
                            log.info("插入失败", insert);
                        }else{
                            applyImportCount.compute(s, (key, val) -> (val == null) ? 1 : val + 1);
                        }
                    }

                }else if("休学".equals(s)){
//                    log.info("办理休学");
                    SuspensionRecordPO suspensionRecordPO = new SuspensionRecordPO();
                    suspensionRecordPO.setApprovalDate(oldStudentStatusChangeDataBO.getUPDATETIME());
                    suspensionRecordPO.setName(oldStudentStatusChangeDataBO.getXM());
                    suspensionRecordPO.setIdNumber(oldStudentStatusChangeDataBO.getSFZH());
                    suspensionRecordPO.setExamRegistrationNumber(oldStudentStatusChangeDataBO.getZKZH());
                    String idnum = oldStudentStatusChangeDataBO.getIDNUM();
                    suspensionRecordPO.setCurrentYear(idnum.substring(0, 4));
                    int index = Integer.parseInt(idnum.substring(4).replaceFirst("^0+(?!$)", ""));
                    suspensionRecordPO.setSerialNumber(index);
                    suspensionRecordPO.setReason(oldStudentStatusChangeDataBO.getREASON());
                    suspensionRecordPO.setSuspensionStartDate(oldStudentStatusChangeDataBO.getFROM1());
                    suspensionRecordPO.setSuspensionEndDate(oldStudentStatusChangeDataBO.getTO1());
                    suspensionRecordPO.setSuspensionDealStartDate(oldStudentStatusChangeDataBO.getFROM2());
                    suspensionRecordPO.setSuspensionDealEndDate(oldStudentStatusChangeDataBO.getTO2());

                    suspensionRecordPO.setOldClassIdentifier(oldStudentStatusChangeDataBO.getBSHI());
                    suspensionRecordPO.setOldGrade(oldStudentStatusChangeDataBO.getNJ());
                    suspensionRecordPO.setOldStudyForm(oldStudentStatusChangeDataBO.getXSHI());
                    suspensionRecordPO.setOldMajorName(oldStudentStatusChangeDataBO.getZHY());

                    suspensionRecordPO.setStudentNumber(oldStudentStatusChangeDataBO.getXHAO());
                    suspensionRecordPO.setRemarkSerialNumber(oldStudentStatusChangeDataBO.getABOUT());

                    // 检测重复导入
                    Integer i = suspensionRecordMapper.selectCount(new LambdaQueryWrapper<SuspensionRecordPO>()
                            .eq(SuspensionRecordPO::getCurrentYear, idnum.substring(0, 4))
                            .eq(SuspensionRecordPO::getSerialNumber, index)
                    );
                    if(i > 0){
                        log.info("已重复，不必导入");
                    }else{
                        int insert = suspensionRecordMapper.insert(suspensionRecordPO);
                        if(insert <= 0){
                            log.info("插入失败", insert);
                        }else{
                            applyImportCount.compute(s, (key, val) -> (val == null) ? 1 : val + 1);
                        }
                    }

                }else if("复学".equals(s)){
//                    log.info("复学");
                    ResumptionRecordPO resumptionRecordPO = new ResumptionRecordPO();
                    resumptionRecordPO.setApprovalDate(oldStudentStatusChangeDataBO.getUPDATETIME());
                    resumptionRecordPO.setName(oldStudentStatusChangeDataBO.getXM());
                    resumptionRecordPO.setIdNumber(oldStudentStatusChangeDataBO.getSFZH());
                    resumptionRecordPO.setStudentNumber(oldStudentStatusChangeDataBO.getXHAO());
                    resumptionRecordPO.setExamRegistrationNumber(oldStudentStatusChangeDataBO.getZKZH());
                    String idnum = oldStudentStatusChangeDataBO.getIDNUM();
                    resumptionRecordPO.setCurrentYear(idnum.substring(0, 4));
                    int index = Integer.parseInt(idnum.substring(4).replaceFirst("^0+(?!$)", ""));
                    resumptionRecordPO.setSerialNumber(index);
                    resumptionRecordPO.setOldGrade(oldStudentStatusChangeDataBO.getNJ());
                    resumptionRecordPO.setOldClassIdentifier(oldStudentStatusChangeDataBO.getBSHI());
                    resumptionRecordPO.setOldStudyForm(oldStudentStatusChangeDataBO.getXSHI());
                    resumptionRecordPO.setOldMajorName(oldStudentStatusChangeDataBO.getZHY());

                    resumptionRecordPO.setNewGrade(oldStudentStatusChangeDataBO.getNew_NJ());
                    resumptionRecordPO.setNewStudyForm(oldStudentStatusChangeDataBO.getNew_XSHI());
                    resumptionRecordPO.setNewClassIdentifier(oldStudentStatusChangeDataBO.getNew_BSHI());
                    resumptionRecordPO.setNewMajorName(oldStudentStatusChangeDataBO.getNew_ZHY());

                    resumptionRecordPO.setResumptionDate(oldStudentStatusChangeDataBO.getFROM1());
                    resumptionRecordPO.setSuspensionSerialNumber(oldStudentStatusChangeDataBO.getABOUT());
                    resumptionRecordPO.setReason(oldStudentStatusChangeDataBO.getREASON());

                    // 检测重复导入
                    Integer i = resumptionRecordMapper.selectCount(new LambdaQueryWrapper<ResumptionRecordPO>()
                            .eq(ResumptionRecordPO::getCurrentYear, idnum.substring(0, 4))
                            .eq(ResumptionRecordPO::getSerialNumber, index)
                    );
                    if(i > 0){
                        log.info("已重复，不必导入");
                    }else{
                        int insert = resumptionRecordMapper.insert(resumptionRecordPO);
                        if(insert <= 0){
                            log.info("插入失败", insert);
                        }else{
                            applyImportCount.compute(s, (key, val) -> (val == null) ? 1 : val + 1);
                        }
                    }
                }else if("退学".equals(s)){
//                    log.info("退学");
                    DropoutRecordPO dropoutRecordPO = new DropoutRecordPO();
                    dropoutRecordPO.setSerialNumber(oldStudentStatusChangeDataBO.getIDNUM());
                    dropoutRecordPO.setExamRegistrationNumber(oldStudentStatusChangeDataBO.getZKZH());
                    dropoutRecordPO.setIdNumber(oldStudentStatusChangeDataBO.getSFZH());
                    dropoutRecordPO.setStudentNumber(oldStudentStatusChangeDataBO.getXHAO());
                    dropoutRecordPO.setName(oldStudentStatusChangeDataBO.getXM());
                    dropoutRecordPO.setOldStudyForm(oldStudentStatusChangeDataBO.getXSHI());
                    dropoutRecordPO.setOldGrade(oldStudentStatusChangeDataBO.getNJ());
                    dropoutRecordPO.setOldMajorName(oldStudentStatusChangeDataBO.getZHY());
                    dropoutRecordPO.setOldClassIdentifier(oldStudentStatusChangeDataBO.getBSHI());
                    dropoutRecordPO.setReason(oldStudentStatusChangeDataBO.getREASON());
                    dropoutRecordPO.setApprovalDate(oldStudentStatusChangeDataBO.getUPDATETIME());
// 检测重复导入
                    Integer i = dropoutRecordMapper.selectCount(new LambdaQueryWrapper<DropoutRecordPO>()
                            .eq(DropoutRecordPO::getOldGrade, dropoutRecordPO.getOldGrade())
                            .eq(DropoutRecordPO::getSerialNumber, dropoutRecordPO.getSerialNumber())
                    );
                    if(i > 0){
                        log.info("已重复，不必导入");
                    }else{
                        int insert = dropoutRecordMapper.insert(dropoutRecordPO);
                        if(insert <= 0){
                            log.info("插入失败", insert);
                        }else{
                            applyImportCount.compute(s, (key, val) -> (val == null) ? 1 : val + 1);
                        }
                    }
                }else if("留级".equals(s)){
//                    log.info("留级");
                    RetentionRecordPO retentionRecordPO = new RetentionRecordPO();
                    retentionRecordPO.setSerialNumber(oldStudentStatusChangeDataBO.getIDNUM());
                    retentionRecordPO.setIdNumber(oldStudentStatusChangeDataBO.getSFZH());
                    retentionRecordPO.setExamRegistrationNumber(oldStudentStatusChangeDataBO.getZKZH());
                    retentionRecordPO.setStudentNumber(oldStudentStatusChangeDataBO.getXHAO());
                    retentionRecordPO.setName(oldStudentStatusChangeDataBO.getXM());
                    retentionRecordPO.setOldClassIdentifier(oldStudentStatusChangeDataBO.getXSHI());
                    retentionRecordPO.setOldGrade(oldStudentStatusChangeDataBO.getNJ());
                    retentionRecordPO.setOldStudyForm(oldStudentStatusChangeDataBO.getXSHI());
                    retentionRecordPO.setOldMajorName(oldStudentStatusChangeDataBO.getZHY());
                    retentionRecordPO.setNewClassIdentifier(oldStudentStatusChangeDataBO.getNew_BSHI());
                    retentionRecordPO.setNewGrade(oldStudentStatusChangeDataBO.getNew_NJ());
                    retentionRecordPO.setNewStudyForm(oldStudentStatusChangeDataBO.getNew_XSHI());
                    retentionRecordPO.setNewMajorName(oldStudentStatusChangeDataBO.getNew_ZHY());
                    retentionRecordPO.setRetentionStartDate(oldStudentStatusChangeDataBO.getFROM1());
                    retentionRecordPO.setReason(oldStudentStatusChangeDataBO.getREASON());
                    retentionRecordPO.setApprovalDate(oldStudentStatusChangeDataBO.getUPDATETIME());

                    Integer i = retentionRecordMapper.selectCount(new LambdaQueryWrapper<RetentionRecordPO>()
                            .eq(RetentionRecordPO::getOldGrade, retentionRecordPO.getOldGrade())
                            .eq(RetentionRecordPO::getSerialNumber, retentionRecordPO.getSerialNumber())
                    );
                    if(i > 0){
                        log.info("已重复，不必导入");
                    }else{
                        int insert = retentionRecordMapper.insert(retentionRecordPO);
                        if(insert <= 0){
                            log.info("插入失败", insert);
                        }else{
                            applyImportCount.compute(s, (key, val) -> (val == null) ? 1 : val + 1);
                        }
                    }
                }else{
                    throw new RuntimeException("出现了休复转退留之外的审批类型" + hashMap);
                }
                applyCount.compute(s, (key, val) -> (val == null) ? 1 : val + 1);

                String allKey = "总数";
                applyCount.compute(allKey, (key, val) -> (val == null) ? 1 : val + 1);
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
