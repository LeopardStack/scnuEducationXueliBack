package com.scnujxjy.backendpoint.projectSummaryTest;

import com.scnujxjy.backendpoint.dto.AllProjectSummaryFiles;
import com.scnujxjy.backendpoint.mapper.project_manage.TrainingProjectMapper;
import com.scnujxjy.backendpoint.mapper.project_manage.TrainingSummaryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class Test {
    @Autowired(required = false)
    private TrainingSummaryMapper trainingSummaryMapper;

    @org.junit.jupiter.api.Test
    public void test1(){
        List<AllProjectSummaryFiles> allProjectSummaryFiles = trainingSummaryMapper.getAllProjectSummaryFiles();
        System.out.println(allProjectSummaryFiles);
    }
}
