package com.scnujxjy.backendpoint.minioTest;

import com.scnujxjy.backendpoint.service.minio.MinioService;
import io.minio.MinioClient;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@SpringBootTest
@Slf4j
public class Test3 {
    @Resource
    MinioService minioService;

    @Resource
    MinioClient minioClient;

    @Test
    public void test1() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String bucketName = "datasynchronize";
        String objectName = "data_import_error_excel/studentStatusData/" +
                "20231007235228_2023_导入学籍数据失败的部分数据.xlsx";
        String objectName1 = "data_import_error_excel/studentStatusData/" +
                "20231007235228_2023_导入学籍数据失败的部分数据1.xlsx";
        StatObjectResponse statObjectResponse = minioClient.statObject(StatObjectArgs.builder().bucket(bucketName).object(objectName).build());
        log.info(statObjectResponse.toString());
        try {
            StatObjectResponse statObjectResponse1 = minioClient.statObject(StatObjectArgs.builder().bucket(bucketName).object(objectName1).build());
            log.info(statObjectResponse1.toString());
        }catch (Exception e){
            log.error(e.toString());
        }
    }
}
