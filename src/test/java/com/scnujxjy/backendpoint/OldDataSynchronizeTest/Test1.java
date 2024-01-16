package com.scnujxjy.backendpoint.OldDataSynchronizeTest;

import com.scnujxjy.backendpoint.dao.mapper.core_data.PaymentInfoMapper;
import com.scnujxjy.backendpoint.service.InterBase.OldDataSynchronize;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;

@SpringBootTest
@Slf4j
public class Test1 {
    @Resource
    private OldDataSynchronize oldDataSynchronize;

    @Resource
    private PaymentInfoMapper paymentInfoMapper;

    @Test
    public void test1(){
        try {
            oldDataSynchronize.synchronizeGradeInformationData(2023, 2015, true);
        }catch (Exception e){
            log.info("同步成绩失败 ");
        }
    }

    @Test
    public void test2(){
        log.info("清除了所有的缴费数据 目前缴费数据库中的数据量为 " + paymentInfoMapper.selectCount(null));
        oldDataSynchronize.synchronizePaymentInfoDataAll(true,
                new ArrayList<>(Arrays.asList("休学", "退学", "转学")));
    }
}
