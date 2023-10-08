package com.scnujxjy.backendpoint.mapperTest;

import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.StudentStatusMapper;
import com.scnujxjy.backendpoint.model.vo.home.StatisticTableForStudentStatus;
import com.scnujxjy.backendpoint.service.home.StatisticTableForGraduation;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@SpringBootTest
@Slf4j
public class StudentStatusMapperTest {
    @Resource
    private StudentStatusMapper studentStatusMapper;

    @Test
    public void test1(){
        List<Map<String, StatisticTableForStudentStatus>> countOfStudentStatus = studentStatusMapper.getCountOfStudentStatus("2010", "2023");
        log.info(countOfStudentStatus.toString());

        List<Map<String, StatisticTableForGraduation>> countOfGraduation = studentStatusMapper.getCountOfGraduation(2010, 2023);
        log.info(countOfGraduation.toString());

    }
}
