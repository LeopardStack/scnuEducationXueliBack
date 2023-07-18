package com.scnujxjy.backendpoint.minioTest;

import com.alibaba.fastjson.JSON;
import io.minio.*;
import io.minio.errors.ErrorResponseException;
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
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
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

    public boolean isUrlValid(String urlStr) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            int code = connection.getResponseCode();
            return code == 200;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 验证 Minio 文件下载链接是否有效
     */
    @Test
    public void testURL(){
        boolean urlValid = isUrlValid("http://192.168.52.111:8001/test2/11/%E5%A4%B4%E5%83%8F.png?X-" +
                "Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=NPVCI4U2U6P4I9HJZPDG%2F2" +
                "0230718%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20230718T040400Z&X-Amz" +
                "-Expires=100&X-Amz-SignedHeaders=host&X-Amz-Signature=00079f2c6019be4fc13e" +
                "c239430aab8837ca30723dac8a0582d4032c97b06ed7");
        logger.info("URL 是否有效 " + urlValid);
    }


    /**
 * 查询指定 Minio 服务器中的指定 bucket 中有哪些文件（文件名称）以及文件大小
 */
    @Test
    public void test1(){
        try {
            Iterable<Result<Item>> myObjects = minioClient.listObjects(
                    ListObjectsArgs.builder().bucket(bucketName).recursive(true).build()
            );

            Iterator<Result<Item>> iterator = myObjects.iterator();
            List<Object> items = new ArrayList<>();
            String format = "{'fileName':'%s', 'fileSize':'%s'}";
            while (iterator.hasNext()) {
                Item item = iterator.next().get();
                items.add(JSON.parse(String.format(format, item.objectName(), formatFileSize(item.size()))));
            }
            logger.info("所有文件 " + items.toString());

            String objectName = "11/头像.png";
            try {
                minioClient.statObject(StatObjectArgs.builder().bucket(bucketName).object(objectName).build());
                String url =
                        minioClient.getPresignedObjectUrl(
                                GetPresignedObjectUrlArgs.builder()
                                        .method(Method.GET)
                                        .bucket(bucketName)
                                        .object(objectName)
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
                                             .object(objectName)
                                             .offset(offset)
                                             .length(len)
                                             .ssec(ssec)
                                             .build()
                             )) {
                    logger.info("文件流：" + stream);
                }
            } catch (ErrorResponseException e) {
                logger.info("文件 " + objectName + " 不存在");
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
