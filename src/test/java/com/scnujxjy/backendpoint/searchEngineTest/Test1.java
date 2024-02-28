package com.scnujxjy.backendpoint.searchEngineTest;

import com.scnujxjy.backendpoint.model.bo.es.FileDocumentBO;
import com.scnujxjy.backendpoint.service.es.ElasticsearchService;
import com.scnujxjy.backendpoint.service.minio.MinioService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
@Slf4j
public class Test1 {
    @Resource
    private ElasticsearchService elasticsearchService;

    @Resource
    private MinioService minioService;

    @Test
    public void test0(){
        // 遍历并处理文件
        try {
            minioService.processFilesFromBucket("searchtest", "Admin");
        } catch (Exception e) {
            log.error("Test failed", e);
        }
    }

    @Test
    public void test1(){
        try {

            // 搜索关键词
            List<FileDocumentBO> searchResults = elasticsearchService.accurateSearchWithoutFileRawData( "去有风的地方");
            searchResults.forEach(result -> log.info(result.toString()));
        } catch (Exception e) {
            log.error("Test failed", e);
        }
    }


}
