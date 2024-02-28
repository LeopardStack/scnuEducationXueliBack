package com.scnujxjy.backendpoint.oldSysDataExport;

import com.scnujxjy.backendpoint.dao.mapper.oa.*;
import com.scnujxjy.backendpoint.service.InterBase.OldDataSynchronize;
import com.scnujxjy.backendpoint.util.DataImportScnuOldSys;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;

@SpringBootTest
@Slf4j
public class TestStudentStatusChange {

    @Resource
    private MajorChangeRecordMapper majorChangeRecordMapper;

    @Resource
    private DropoutRecordMapper dropoutRecordMapper;
    @Resource
    private ResumptionRecordMapper resumptionRecordMapper;
    @Resource
    private RetentionRecordMapper retentionRecordMapper;
    @Resource
    private SuspensionRecordMapper suspensionRecordMapper;

    @Test
    public void test1(){
        DataImportScnuOldSys dataImportScnuOldSys = new DataImportScnuOldSys();
        ArrayList<HashMap<String, String>> studentStatusData = dataImportScnuOldSys.getStudentStatusChangeData();
        log.info("旧系统中总学籍异动数量 " + studentStatusData.size());
        // 使用流来统计 name 属性值为 "复学" 的记录数量
        long count = studentStatusData.stream()
                .filter(map -> "复学".equals(map.get("CTYPE")))
                .count();

        log.info("旧系统中 '复学' 的学籍异动数量: " + count + "\n" +
                "新系统中 复学 的学籍异动数据 " + resumptionRecordMapper.selectCount(null));

        // 使用流来统计同时满足 name 属性值为 "转学" 且 new_bshi 也为 "转学" 的记录数量
        long count1 = studentStatusData.stream()
                .filter(map -> "转学".equals(map.get("CTYPE")))
                .filter(map -> "转学".equals(map.get("new_BSHI")))
                .count();

        log.info("旧系统中 转学到校外/省外 的学籍异动数量: " + count1);

        long count2 = studentStatusData.stream()
                .filter(map -> "转学".equals(map.get("CTYPE")))
                .count();

        log.info("旧系统中 转专业 的学籍异动数量: " + count2 + "\n" +
                "新系统中 转专业 的学籍异动数据 " + majorChangeRecordMapper.selectCount(null));

        long count3 = studentStatusData.stream()
                .filter(map -> "休学".equals(map.get("CTYPE")))
                .count();

        log.info("旧系统中 休学 的学籍异动数量: " + count3 + "\n" +
                "新系统中 休学 的学籍异动数据 " + suspensionRecordMapper.selectCount(null));

        long count4 = studentStatusData.stream()
                .filter(map -> "退学".equals(map.get("CTYPE")))
                .count();

        log.info("旧系统中 退学 的学籍异动数量: " + count4 + "\n" +
                "新系统中 退学 的学籍异动数据 " + dropoutRecordMapper.selectCount(null));

        long count5 = studentStatusData.stream()
                .filter(map -> "留级".equals(map.get("CTYPE")))
                .count();

        log.info("旧系统中 留级 的学籍异动数量: " + count5 + "\n" +
                "新系统中 留级 的学籍异动数据 " + retentionRecordMapper.selectCount(null));
    }

    @Resource
    OldDataSynchronize oldDataSynchronize;

    @Test
    public void test2() throws InterruptedException {
        oldDataSynchronize.synchronizeStudentStatusChangeData(true);

    }
}
