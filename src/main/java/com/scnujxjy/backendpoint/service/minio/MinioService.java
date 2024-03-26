package com.scnujxjy.backendpoint.service.minio;

import com.scnujxjy.backendpoint.model.bo.es.FileDocumentBO;
import com.scnujxjy.backendpoint.service.es.ElasticsearchService;
import io.minio.*;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.MinioException;
import io.minio.http.Method;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class MinioService {

    private final MinioClient minioClient;
    private final String bucketName;

    @Resource
    private ElasticsearchService elasticsearchService;

    @Autowired
    public MinioService(MinioClient minioClient, @Value("${minio.bucketName}") String bucketName) {
        this.minioClient = minioClient;
        this.bucketName = bucketName;
    }

    /**
     * 列出指定桶下的所有文件名
     * @return
     */
    public List<String> getAllFileNames(String bucketNameCertain){
        List<String> returnList = new ArrayList<>();
        try {
            // 列出指定桶中的所有对象
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder().bucket(bucketNameCertain).recursive(true).build());

            // 打印对象名称
            for (Result<Item> result : results) {
                Item item = result.get();
                returnList.add(item.objectName());
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return returnList;
    }

    /**
     * 根据文件的绝对路径（桶名/相对路径）删除 Minio 桶中的文件
     * @param absoluteFilePath 文件在Minio中的绝对路径
     */
    public void deleteFileByAbsolutePath(String absoluteFilePath) {
        try {
            // 检查路径是否为空
            if (!StringUtils.hasText(absoluteFilePath)) {
                throw new IllegalArgumentException("Minio 入参文件路径不能为空");
            }

            // 分割绝对路径获取桶名和文件相对路径
            String[] parts = absoluteFilePath.split("/", 2);
            if (parts.length != 2) {
                throw new IllegalArgumentException("错误的文件路径格式，文件应为 桶名/文件相对路径名 ");
            }

            String bucketNameToDelete = parts[0];
            String relativeFilePath = parts[1];

            // 删除指定的文件
            minioClient.removeObject(
                    RemoveObjectArgs.builder().bucket(bucketNameToDelete).object(relativeFilePath).build()
            );
        } catch (Exception e) {
            e.printStackTrace();
            // 可以根据需求处理异常，比如抛出自定义异常或记录日志
        }
    }

    public String getBucketName() {
        return bucketName;
    }

    /**
     * 判断文件是否存在
     * @param fileRelativeUrl 文件的相对路径
     * @param targetBucketName 文件所在的桶名
     * @return
     */
    public boolean isExist(String fileRelativeUrl, String targetBucketName){
        boolean exists = false;
        try {
            minioClient.statObject(StatObjectArgs.builder().bucket(targetBucketName).object(fileRelativeUrl).build());
            exists = true;  // 如果没有抛出异常，表示对象存在
        } catch (ErrorResponseException e) {

            // 对象不存在，exists保持为false
        } catch (Exception e) {

        }

        return exists;
    }

    /**
     * 使用学生照片地址获取临时 7天的链接
     * @param fileName
     * @return
     */
    public String searchImage(String fileName) {
        try {
            if(!isExist(fileName, bucketName)){
                return  null;
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

    /**
     * 使用文件名获取临时7天的链接。
     * 文件名格式应为 "bucketName/objectName" 或 "./bucketName/objectName"。
     * @param fileName 文件名，格式为 "bucketName/objectName" 或 "./bucketName/objectName"。
     * @return 预签名的URL，或者如果出现错误则为null。
     */
    public String generatePresignedUrl(String fileName) {
        try {
            // 如果文件名以 './' 开头，移除这两个字符
            if (fileName.startsWith("./")) {
                fileName = fileName.substring(2);
            }

            String[] parts = fileName.split("/", 2);
            if (parts.length != 2) {
                throw new IllegalArgumentException("文件名格式不正确。期望的格式为 'bucketName/objectName' 或 './bucketName/objectName'");
            }
            String bucketName = parts[0];
            String objectName = parts[1];

            String presignedUrl = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(objectName)
                            .expiry(7, TimeUnit.DAYS) // 设置链接有效期为7天
                            .build());

            return presignedUrl;
        } catch (Exception e) {
            e.printStackTrace();
            return null; // 或者根据您的错误处理策略进行处理
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
            log.error(fileName + "获取图片字节流失败: " + e.getMessage());
            throw new RuntimeException(fileName + "获取图片字节流失败: " + e.getMessage());
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
     * 使用 Minio 地址获取文件内容
     * @param minioUrl Minio 文件地址，例如 "排课表导入/import/xuelijiaoyuTest1排课表信息导入（经管学院）(1)-2023-10-29T20:36:33.171.xlsx"
     * @return 文件的字节流，如果文件不存在则返回 null
     */
    public InputStream getFileInputStreamFromMinio(String minioUrl) {
        try {
            // 找到第一个斜杠的位置
            int firstSlashIndex = minioUrl.indexOf('/');
            if (firstSlashIndex == -1) {
                log.error("无效的 Minio URL: " + minioUrl);
                return null;
            }

            // 提取桶名和文件名
            String parsedBucketName = minioUrl.substring(0, firstSlashIndex);
            String fileName = minioUrl.substring(firstSlashIndex + 1);

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

            return inputStream;
        } catch (ErrorResponseException e) {
            log.error("从 Minio 获取文件失败: " + e.getMessage());
            if (e.errorResponse().code().equals("NoSuchKey")) {
                return null; // 文件不存在
            }
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

    /**
     * 把文件流直接写入到 Minio 作为一个文件
     * @param inputStream
     * @param fileName
     */
    public boolean uploadStreamToMinio(InputStream inputStream, String fileName, String diyBucketName) {
        try {
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Disposition", "attachment; filename*=UTF-8''" + URLEncoder.encode(fileName, "UTF-8"));

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(diyBucketName)
                            .object(fileName)
                            .stream(inputStream, -1, 10485760)  // 10485760 是 10MiB
                            .headers(headers)
                            .build());

            return true;
        } catch (Exception e) {
            log.error("上传文件到 Minio 失败: " + e.getMessage());
//            throw new RuntimeException("上传文件到 Minio 失败: " + e.getMessage());
        }
        return false;
    }

    /**
     * 使用 Minio 地址下载文件
     * @param minioUrl Minio 文件地址，例如 "dataexport/12/34/学籍数据.xlsx"
     * @return 文件的字节流，如果文件不存在则返回 null
     */
    public byte[] downloadFileFromMinio(String minioUrl) {
        try {
            // 如果文件名以 './' 开头，移除这两个字符
            if (minioUrl.startsWith("./")) {
                minioUrl = minioUrl.substring(2);
            }
            // 从 URL 中解析出桶名和文件名
            String[] parts = minioUrl.split("/", 2);
            if (parts.length < 2) {
                log.error("无效的 Minio URL: " + minioUrl);
                return null;
            }

            String parsedBucketName = parts[0];
            String fileName = parts[1];

            // 获取文件内容
            return getFileFromMinio(parsedBucketName, fileName);
        } catch (Exception e) {
            log.error("从 Minio 下载文件失败: " + e.getMessage());
            throw new RuntimeException("从 Minio 下载文件失败: " + e.getMessage());
        }
    }

    /**
     * 使用 Minio 地址获取文件内容
     * @param bucketName Minio 桶名，例如 "dataexport"
     * @param fileName Minio 文件名，例如 "12/34/学籍数据.xlsx"
     * @return 文件的字节流，如果文件不存在则返回 null
     */
    public byte[] getFileFromMinio(String bucketName, String fileName) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            // 检查文件是否存在
            boolean exists = minioClient
                    .statObject(StatObjectArgs.builder().bucket(bucketName).object(fileName).build()) != null;

            if (!exists) {
                log.error("文件不存在: " + fileName);
                return null; // 文件不存在
            }

            // 获取文件内容
            try (InputStream inputStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build())) {

                byte[] buffer = new byte[4096]; // 使用更大的缓冲区
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            } catch (Exception e) {
                log.error("从 Minio 获取文件失败: " + e.getMessage());
                throw new RuntimeException("从 Minio 获取文件失败: " + e.getMessage());
            }
        } catch (ErrorResponseException e) {
            if (e.errorResponse().code().equals("NoSuchKey")) {
                log.error("文件不存在: " + fileName);
                return null; // 文件不存在
            }
            log.error("从 Minio 获取文件失败: " + e.getMessage());
            throw new RuntimeException("从 Minio 获取文件失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("从 Minio 获取文件失败: " + e.getMessage());
            throw new RuntimeException("从 Minio 获取文件失败: " + e.getMessage());
        }
        return outputStream.toByteArray();
    }


    /**
     * 使用Minio地址获取文件的URL
     * @param minioUrl Minio文件地址，例如 "./xuelistudentpictures/2023/import/2244011511501738.jpg"
     * @return 文件的URL，如果文件不存在则返回null
     */
    public String getFileUrlFromMinio(String minioUrl) {
        try {
            // 从URL中解析出桶名和文件名
            String[] parts = minioUrl.split("/", 3);
            if (parts.length < 3) {
                log.error("无效的Minio URL: " + minioUrl);
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

            // 获取文件的URL
            String presignedUrl = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(parsedBucketName)
                            .object(fileName)
                            .expiry(1, TimeUnit.DAYS) // 设置链接有效期为1天
                            .build());

            return presignedUrl;
        } catch (ErrorResponseException e) {
            if (e.errorResponse().code().equals("NoSuchKey")) {
                return null; // 文件不存在
            }
            log.error("从Minio获取文件URL失败: " + e.getMessage());
            throw new RuntimeException("从Minio获取文件URL失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("从Minio获取文件URL失败: " + e.getMessage());
            throw new RuntimeException("从Minio获取文件URL失败: " + e.getMessage());
        }
    }

    /**
     * 使用Minio地址获取照片的字节数组
     * @param minioUrl Minio文件地址，例如 "./xuelistudentpictures/2023/import/2244011511501738.jpg"
     * @return 照片的字节数组，如果文件不存在则返回null
     */
    public byte[] getPhotoBytesFromMinio(String minioUrl) {
        try {
            // 从URL中解析出桶名和文件名
            String[] parts = minioUrl.split("/", 3);
            if (parts.length < 3) {
                log.error("无效的Minio URL: " + minioUrl);
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
            log.error("从Minio获取照片字节数组失败: " + e.getMessage());
            throw new RuntimeException("从Minio获取照片字节数组失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("从Minio获取照片字节数组失败: " + e.getMessage());
            throw new RuntimeException("从Minio获取照片字节数组失败: " + e.getMessage());
        }
    }


    /**
     * 更新学生的照片信息
     * @param bucketName 照片桶名
     * @param dir 照片的本地读取目录
     * @param rootDirPath 照片的路径
     * @param picCountMap 照片的读入记录
     * @param update 是否强制更新 当照片存在时
     * @throws Exception
     */
    public void uploadDirectory(String bucketName, File dir, String rootDirPath, Map<String, Long> picCountMap, boolean update) throws Exception {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    uploadDirectory(bucketName, file, rootDirPath, picCountMap, update);
                } else {
                    uploadFile(bucketName, file, rootDirPath, picCountMap, update);
                }
            }
        }
    }
    private void uploadFile(String bucketName, File file, String rootDirPath, Map<String, Long> picCountMap, boolean update) throws Exception {
        int index = file.getAbsolutePath().lastIndexOf(bucketName) + bucketName.length();
        String objectName = file.getAbsolutePath().substring(index + 1).replace("\\", "/");


        // 根据objectName的值来更新计数器
        if (objectName.contains("import")) {
            picCountMap.put("入学照片", picCountMap.get("入学照片") + 1);
        } else if (objectName.contains("export")) {
            picCountMap.put("毕业照片", picCountMap.get("毕业照片") + 1);
        }

        // 检查对象是否已经存在
        boolean objectExists = true;
        try {
            // 如果没有异常 说明文件存在
            minioClient.statObject(StatObjectArgs.builder().bucket(bucketName).object(objectName).build());
        } catch (Exception e) {
            objectExists = false;
        }

        // 如果对象不存在，或者 update 标志为 true，则上传文件
        if (!objectExists || update) {
            ObjectWriteResponse objectWriteResponse = minioClient.uploadObject(
                    UploadObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .filename(file.getAbsolutePath())
                            .build());
            if (objectWriteResponse != null) {
                String returnedObjectName = objectWriteResponse.object();
                String returnedBucketName = objectWriteResponse.bucket();

                if (objectName.equals(returnedObjectName) && bucketName.equals(returnedBucketName)) {
//                log.info("学生照片上传成功!");
                } else {
                    log.error("学生照片上传失败. Mismatch in object or bucket name.");
                }
            } else {
                log.error("学生照片上传失败. Response is null.");
            }
        }
    }

    public long getEpochMilliFromTemporalAccessor(TemporalAccessor temporal) {
        long seconds = temporal.getLong(ChronoField.INSTANT_SECONDS);
        long nano = temporal.getLong(ChronoField.NANO_OF_SECOND);
        return seconds * 1000 + nano / 1_000_000;
    }
    public void processFilesFromBucket(String bucketName, String creator) throws Exception {
        Iterable<Result<Item>> items = minioClient.listObjects(ListObjectsArgs.builder().bucket(bucketName).build());
        for (Result<Item> itemResult : items) {
            Item item = itemResult.get();
            String fileName = item.objectName();
            String fileExtension = fileName.contains(".") ? fileName.substring(fileName.lastIndexOf(".") + 1) : "unknown";

            // 构造 Minio 文件的 URL
            String fileUrl = bucketName + "/" + fileName;

            try (InputStream stream = minioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(fileName).build())) {
                long modifyTime = getEpochMilliFromTemporalAccessor(item.lastModified());


                FileDocumentBO fileDocument = FileDocumentBO.builder()
                        .id(fileUrl) // 或者使用其他逻辑生成唯一 ID
                        .fileName(fileName)
                        .type(fileExtension)
                        .createBy(creator) // 你可能需要从其他地方获取这个值
                        .createTime(System.currentTimeMillis())
                        .minioURL(fileUrl) // 使用构造的 Minio URL
                        .modifyTime(modifyTime) // 使用获取的修改时间
                        .build();

                elasticsearchService.indexFile(fileDocument, stream);
            }
        }
    }





    // 其他 Minio 操作方法可以在这里添加

}
