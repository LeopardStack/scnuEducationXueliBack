package com.scnujxjy.backendpoint.oldSysDataExport;

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


    @Test
    public void test1(){
        DataImportScnuOldSys dataImportScnuOldSys = new DataImportScnuOldSys();
        ArrayList<HashMap<String, String>> studentStatusData = dataImportScnuOldSys.getStudentStatusChangeData();
        log.info("旧系统中总学籍异动数量 " + studentStatusData.size());
        // 使用流来统计 name 属性值为 "复学" 的记录数量
        long count = studentStatusData.stream()
                .filter(map -> "复学".equals(map.get("CTYPE")))
                .count();

        log.info("旧系统中 '复学' 的学籍异动数量: " + count);

        // 使用流来统计同时满足 name 属性值为 "转学" 且 new_bshi 也为 "转学" 的记录数量
        long count1 = studentStatusData.stream()
                .filter(map -> "转学".equals(map.get("CTYPE")))
                .filter(map -> "转学".equals(map.get("new_BSHI")))
                .count();

        log.info("旧系统中 '转学' 并且 'new_bshi' 也为 '转学' 的学籍异动数量: " + count1);
    }

    @Resource
    OldDataSynchronize oldDataSynchronize;

    @Test
    public void test2() throws InterruptedException {
        oldDataSynchronize.synchronizeStudentStatusChangeData(true);
    }
}
