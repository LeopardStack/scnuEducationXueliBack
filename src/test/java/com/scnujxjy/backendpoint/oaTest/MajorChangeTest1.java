package com.scnujxjy.backendpoint.oaTest;

import com.scnujxjy.backendpoint.dao.mongoEntity.StudentTransferApplication;
import com.scnujxjy.backendpoint.service.oa.StudentTransferApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
@Slf4j
public class MajorChangeTest1 {

    @Resource
    private StudentTransferApplicationService service;

    @Test
    public void test1(){
        StudentTransferApplication application = new StudentTransferApplication();
        application.setCandidateNumber("2244130111110626");
        application.setName("叶凯文");
//        StudentTransferApplication application1 = service.addNewTransferApplication(application);
        log.info("新增一个转专业实例 " + application.getId());
    }

    @Test
    public void test2(){
        String id = "6555b08ab1d3e01affffcb11";
        StudentTransferApplication application1 = service.getApplicationById(id);
        log.info("获取指定 ID 实例 \n" + application1);
    }

    @Test
    public void test3(){
        String id = "6555b08ab1d3e01affffcb11";
        StudentTransferApplication application = service.updateMajor(id, "计算机科学与技术");
        log.info("更新指定 ID 实例 \n" + application);
    }

    @Test
    public void test4(){
        String id = "6555b08ab1d3e01affffcb11";
        StudentTransferApplication application = service.updateMajor(id, "新闻学");
        log.info("更新指定 ID 实例 \n" + application);
    }
}
