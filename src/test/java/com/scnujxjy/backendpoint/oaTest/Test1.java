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
        StudentTransferApplication newApplication = service.addNewTransferApplication(new StudentTransferApplication());
        log.info("ID 为 " + newApplication.getId());
        String id = newApplication.getId();


    }
}
