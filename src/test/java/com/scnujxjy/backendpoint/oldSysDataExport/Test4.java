package com.scnujxjy.backendpoint.oldSysDataExport;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;

import static com.scnujxjy.backendpoint.util.DataImportScnuOldSys.getTeachingPlans;

@SpringBootTest
@Slf4j
public class Test4 {
    @Test
    public void test1(){
        ArrayList<HashMap<String, String>> teachingPlans = getTeachingPlans("2022");
        log.info("总共多少数据 " + teachingPlans.size());
    }
}
