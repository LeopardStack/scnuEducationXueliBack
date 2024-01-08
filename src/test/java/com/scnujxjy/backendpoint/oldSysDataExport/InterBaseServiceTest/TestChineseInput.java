package com.scnujxjy.backendpoint.oldSysDataExport.InterBaseServiceTest;

import com.scnujxjy.backendpoint.util.SCNUXLJYDatabase;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class TestChineseInput {
    @Test
    public void test1(){
        SCNUXLJYDatabase scnuxljyDatabase1 = new SCNUXLJYDatabase();
        String query = "SELECT COUNT(*) FROM CWPAY_VIEW WHERE NJ = '休学'";
        int total = (int) scnuxljyDatabase1.getValue(query);
        log.info("休学的缴费数据数量 " + total);
    }
}
