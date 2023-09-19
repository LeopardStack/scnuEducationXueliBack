package com.scnujxjy.backendpoint.service.minio;

import io.minio.*;
import io.minio.errors.ErrorResponseException;
import io.minio.http.Method;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class MinioService {

    private final MinioClient minioClient;
    private final String bucketName;

    @Autowired
    public MinioService(MinioClient minioClient, @Value("${minio.bucketName}") String bucketName) {
        this.minioClient = minioClient;
        this.bucketName = bucketName;
    }

    public String getBucketName() {
        return bucketName;
    }

    /**
     * 使用学生照片地址获取临时 7天的链接
     * @param fileName
     * @return
     */
    public String searchImage(String fileName) {
        try {
            boolean exists = minioClient
                    .statObject(StatObjectArgs.builder().bucket(bucketName).object(fileName).build()) != null;

            if (!exists) {
                return null; // 文件不存在
            }

            String presignedUrl = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(fileName)
                            .expiry(7, TimeUnit.DAYS) // 设置链接有效期
                            .build());

            return presignedUrl;
        } catch (Exception e) {
            log.error("文件搜索失败: " + e.getMessage());
            throw new RuntimeException("文件搜索失败: " + e.getMessage());
        }
    }

    // 将图片转换为字节流
    public byte[] getImageAsBytes(String fileName) {
        try {
            InputStream inputStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build());

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;

            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }

            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("获取图片字节流失败: " + e.getMessage());
            throw new RuntimeException("获取图片字节流失败: " + e.getMessage());
        }
    }

    /**
     * 使用 Minio 地址获取文件内容
     * @param minioUrl Minio 文件地址，例如 "./xueweipictures/20220622/函授/440684199111302742.jpg"
     * @return 文件的字节流，如果文件不存在则返回 null
     */
    public byte[] getFileFromMinio(String minioUrl) {
        try {
            // 从 URL 中解析出桶名和文件名
            String[] parts = minioUrl.split("/", 3);
            if (parts.length < 3) {
                log.error("无效的 Minio URL: " + minioUrl);
                return null;
            }

            String parsedBucketName = parts[1];
            String fileName = parts[2];

            // 检查文件是否存在
            boolean exists = minioClient
                    .statObject(StatObjectArgs.builder().bucket(parsedBucketName).object(fileName).build()) != null;

            if (!exists) {
                return null; // 文件不存在
            }

            // 获取文件内容
            InputStream inputStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(parsedBucketName)
                            .object(fileName)
                            .build());

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;

            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }

            return outputStream.toByteArray();
        } catch (ErrorResponseException e) {
            if (e.errorResponse().code().equals("NoSuchKey")) {
                return null; // 文件不存在
            }
            log.error("从 Minio 获取文件失败: " + e.getMessage());
            throw new RuntimeException("从 Minio 获取文件失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("从 Minio 获取文件失败: " + e.getMessage());
            throw new RuntimeException("从 Minio 获取文件失败: " + e.getMessage());
        }
    }


    /**
     * 将指定目录下的文件上传到 Minio。
     *
     * @param localFilePath 指定的本地文件路径，例如 "./xuelistudentpictures/2001/import/1234141.jpg"
     * @return 上传文件后的 Minio URL
     * @throws Exception 文件读取或上传失败时抛出异常
     */
    public String uploadFileToMinio(String localFilePath) throws Exception {

        // 获取当前的工作目录
        String currentDir = System.getProperty("user.dir");
        Path absoluteFilePath = Paths.get(currentDir, localFilePath.substring(2)); // substring(2) 用于去掉 "./"

        // 判断文件是否存在且非空
        if (!Files.exists(absoluteFilePath) || Files.size(absoluteFilePath) == 0) {
            log.error("无效的文件路径或文件为空: " + absoluteFilePath.toString());
            throw new RuntimeException("无效的文件路径或文件为空: " + absoluteFilePath.toString());
        }

        log.info("本地绝对路径: " + absoluteFilePath.toString());
        log.info("本地 URL " + localFilePath);

        // 使用 localFilePath 来进行路径的解析
        String regexSeparator = "/";
        String[] parts = localFilePath.split(regexSeparator);

        // 确保路径中有预期的部分
        if (parts.length < 5 || !parts[1].equals("xuelistudentpictures")) {
            log.error("无效的文件路径: " + localFilePath);
            throw new RuntimeException("无效的文件路径: " + localFilePath);
        }

        String parsedBucketName = parts[1];
        // 先前是 String.join(File.separator, Arrays.copyOfRange(parts, 2, parts.length));
        // 我们需要确保使用 "/" 作为分隔符，以使Minio中的结构与localFilePath一致。
        String fileName = String.join("/", Arrays.copyOfRange(parts, 2, parts.length));


        // 确保桶名与配置的桶名相同
        if (!parsedBucketName.equals(bucketName)) {
            log.error("桶名不匹配: " + parsedBucketName + " != " + bucketName);
            throw new RuntimeException("桶名不匹配: " + parsedBucketName + " != " + bucketName);
        }

        // 读取本地文件
        try (InputStream is = Files.newInputStream(absoluteFilePath)) {
            // 上传文件到 Minio
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(is, -1, 10485760)  // 10485760 是 10MiB
                            .build());

            return localFilePath;
        } catch (Exception e) {
            log.error("上传文件到 Minio 失败: " + e.getMessage());
            throw new RuntimeException("上传文件到 Minio 失败: " + e.getMessage());
        }
    }


    // 其他 Minio 操作方法可以在这里添加
}
