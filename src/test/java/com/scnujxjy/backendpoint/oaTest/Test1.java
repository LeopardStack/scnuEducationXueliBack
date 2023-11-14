package com.scnujxjy.backendpoint.oaTest;

import com.scnujxjy.backendpoint.dao.mongoEntity.StudentTransferApplication;
import com.scnujxjy.backendpoint.service.oa.StudentTransferApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import javax.swing.plaf.PanelUI;
import java.util.Optional;

@SpringBootTest
@Slf4j
public class Test1 {

    @Resource
    private StudentTransferApplicationService service;

    @Test
    public void test1(){
        StudentTransferApplication newApplication = service.createNewApplication();
        log.info("ID 为 " + newApplication.getId());
        String id = newApplication.getId();


    }

    @Test
    public void test2(){
        String id = "65526dc37d96af194131b608";
        StudentTransferApplication studentTransferApplication = new StudentTransferApplication();
        studentTransferApplication.setTransferOutApprover("不允许高分转低分");
        StudentTransferApplication studentTransferApplication1 = service.updateApplication(id, studentTransferApplication);
        log.info("修改后的实例 \n" + studentTransferApplication1);
    }

    @Test
    public void test2_1(){
        String id = "65526dc37d96af194131b608";
        StudentTransferApplication studentTransferApplication = new StudentTransferApplication();
        studentTransferApplication.setIntendedMajor("新闻学");
        StudentTransferApplication studentTransferApplication1 = service.updateApplication(id, studentTransferApplication);
        log.info("修改后的实例 \n" + studentTransferApplication1);
    }

    @Test
    public void test3(){
        String id = "65526dc37d96af194131b608";
        Optional<StudentTransferApplication> applicationById = service.getApplicationById(id);
        StudentTransferApplication studentTransferApplication = applicationById.get();
        log.info(studentTransferApplication.toString());
    }
}
