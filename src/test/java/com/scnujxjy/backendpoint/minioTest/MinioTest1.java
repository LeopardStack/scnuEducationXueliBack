package com.scnujxjy.backendpoint.minioTest;

import com.alibaba.fastjson.JSON;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Item;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class MinioTest1 {
    private static final Logger logger = LoggerFactory.getLogger(MinioTest1.class);

    @Autowired(required = false)
    private MinioClient minioClient;

    @Value("${minio.bucketName}")
    private String bucketName;

    public String formatFileSize(long size) {
        String[] units = {"B", "KB", "MB", "GB", "TB"};
        int index = 0;
        double fileSize = size;

        while (fileSize >= 1024 && index < units.length - 1) {
            fileSize /= 1024;
            index++;
        }

        return String.format("%.2f %s", fileSize, units[index]);
    }


/**
 * 查询指定 Minio 服务器中的指定 bucket 中有哪些文件（文件名称）以及文件大小
 */
    @Test
    public void test1(){
        try {
            Iterable<Result<Item>> myObjects = minioClient.listObjects(ListObjectsArgs.builder().bucket(bucketName).build());


            Iterator<Result<Item>> iterator = myObjects.iterator();
            List<Object> items = new ArrayList<>();
            String format = "{'fileName':'%s', 'fileSize':'%s'}";
            while (iterator.hasNext()) {
                Item item = iterator.next().get();
                items.add(JSON.parse(String.format(format, item.objectName(), formatFileSize(item.size()))));
            }
            logger.info(items.toString());

            String url =
                    minioClient.getPresignedObjectUrl(
                            GetPresignedObjectUrlArgs.builder()
                                    .method(Method.GET)
                                    .bucket(bucketName)
                                    .object("头像.png")
                                    .expiry(100, TimeUnit.SECONDS)
                                    .build());
            System.out.println(url);

            logger.info("头像下载地址："+url);

            long offset = 0;
            Long len = null; // 不设置长度参数会下载整个文件
            ServerSideEncryptionCustomerKey ssec = null; // 如果没有服务器端加密，可以设置为null

            try (InputStream stream =
                         minioClient.getObject(
                                 GetObjectArgs.builder()
                                         .bucket(bucketName)
                                         .object("头像.png")
                                         .offset(offset)
                                         .length(len)
                                         .ssec(ssec)
                                         .build()
                         )) {
                logger.info("文件流：" + stream);
            }

        }catch (Exception e){
            logger.error(e.toString());
        }
    }

    /**
     * 删除指定文件
     */
    @Test
    public void testDeleteFile() {
        String objectName = "test1.txt"; // 在bucket中的对象名，通常与文件名相同
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build());
            logger.info("文件删除成功！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Autowired
    private ResourceLoader resourceLoader;

    /**
     * 上传本地文件
     */
    @Test
    public void test04(){
        String filePath = "classpath:data/test1.txt"; // 替换为你的文件路径
        String objectName = "test1.txt"; // 在bucket中的对象名，通常与文件名相同
        String newBucket = "test01";
        try {
            Resource resource = resourceLoader.getResource(filePath);
            File file = resource.getFile();

            minioClient.uploadObject(
                    UploadObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .filename(file.getAbsolutePath())
                            .build());
            logger.info("Uploaded the file successfully.");
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }


    /**
     * 重命名文件
     */
    @Test
    public void testRenameFile() {
        try {
            // 复制文件到新的名称
            minioClient.copyObject(
                    CopyObjectArgs.builder()
                            .bucket("bucketName")
                            .object("newObjectName")
                            .source(CopySource.builder().bucket("bucketName").object("oldObjectName").build())
                            .build());

            // 删除原来的文件
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket("bucketName")
                            .object("oldObjectName")
                            .build());

            System.out.println("文件重命名成功！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
