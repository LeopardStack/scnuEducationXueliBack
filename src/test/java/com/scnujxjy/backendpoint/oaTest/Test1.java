package com.scnujxjy.backendpoint.oaTest;

import com.scnujxjy.backendpoint.dao.repository.StudentTransferMajorRepository;
import com.scnujxjy.backendpoint.service.oa.StudentTransferApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
@Slf4j
public class Test1 {

    @Resource
    private StudentTransferApplicationService service;

    @Resource
    private StudentTransferMajorRepository studentTransferMajorRepository;

    @Test
    public void test1() {
    }
}
