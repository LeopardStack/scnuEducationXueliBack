package com.scnujxjy.backendpoint.mapperTest;

import com.scnujxjy.backendpoint.dao.mapper.admission_information.AdmissionInformationMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class Test1 {
    @Autowired(required = false)
    private AdmissionInformationMapper admissionInformationMapper;

    /**
     * 获取录取学生总人数
     */
    @Test
    public void test1(){
        Integer integer = admissionInformationMapper.selectCount(null);
        log.info("学生总数 " + integer);
    }
}
