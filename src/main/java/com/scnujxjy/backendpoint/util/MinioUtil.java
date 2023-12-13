package com.scnujxjy.backendpoint.util;

import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.util.StrUtil;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;

@Component
@Slf4j
public class MinioUtil {
    @Autowired
    private MinioClient minioClient;


    /**
     * 判断指定桶中文件是否存在
     *
     * @param bucketName 桶名
     * @param fileName   文件名
     * @return true-存在；false-不存在
     */
    public Boolean isFileExist(String bucketName, String fileName) {
        Boolean exists = true;
        try {
            minioClient.statObject(StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .build());
        } catch (Exception e) {
            exists = false;
        }
        return exists;
    }


    /**
     * 上传文件的通用方法
     *
     * @param bucketName  桶名
     * @param inputStream 文件输入流
     * @param objectName  对象名（文件名）
     * @param contentType 对象类型
     * @return 响应
     * @throws IOException               in case of access errors (if the temporary store fails) 或者 thrown to indicate I/O error on S3 operation.
     * @throws ErrorResponseException    thrown to indicate S3 service returned an error response.
     * @throws InsufficientDataException thrown to indicate not enough data available in InputStream.
     * @throws InternalException         thrown to indicate internal library error.
     * @throws InvalidKeyException       thrown to indicate missing of HMAC SHA-256 library.
     * @throws InvalidResponseException  thrown to indicate S3 service returned invalid or no error
     *                                   response.
     * @throws NoSuchAlgorithmException  thrown to indicate missing of MD5 or SHA-256 digest library.
     * @throws XmlParserException        thrown to indicate XML parsing error.
     */
    public ObjectWriteResponse putFile(String bucketName, String objectName, String contentType, InputStream inputStream)
            throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        byte[] buffer = new byte[1024];
        int byteRead;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        while ((byteRead = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, byteRead);
        }
        byte[] inputSteamData = byteArrayOutputStream.toByteArray();
        ByteArrayInputStream steamForContentType = new ByteArrayInputStream(inputSteamData);
        if (StrUtil.isBlank(contentType)) {
            contentType = FileTypeUtil.getType(steamForContentType);
        }
        steamForContentType.close();
        ByteArrayInputStream uploadStream = new ByteArrayInputStream(inputSteamData);
        ObjectWriteResponse objectWriteResponse = minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .contentType(contentType)
                .stream(uploadStream, uploadStream.available(), -1)
                .build());
        uploadStream.close();
        return objectWriteResponse;
    }

    /**
     * 删除指定桶名中的文件
     *
     * @param bucketName 桶名
     * @param objectName 文件名
     * @return true-成功；false-失败
     */
    public Boolean removeFile(String bucketName, String objectName) {
        Boolean deleted = true;
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build());
        } catch (ErrorResponseException | NoSuchAlgorithmException | InsufficientDataException | InvalidKeyException |
                 InternalException | InvalidResponseException | IOException | ServerException | XmlParserException e) {
            deleted = false;
            log.error(String.format("Minio删除数据失败，桶名：%s，对象名：%s，错误信息：%s", bucketName, objectName, getStackTrace(e)));
        }
        return deleted;
    }


    /**
     * 批量删除文件
     *
     * @param bucketName  桶名
     * @param objectsName 对象名列表
     * @return 成功删除文件的数量
     * @see MinioUtil#removeFile(String, String)
     */
    public Integer removeBatchFiles(String bucketName, List<String> objectsName) {
        Integer count = 0;
        for (String name : objectsName) {
            if (removeFile(bucketName, name)) {
                count++;
            }
        }
        return count;
    }

    /**
     * 获取文件外链
     * <p>需要设定时间，最大七天</p>
     *
     * @param bucketName 桶名，默认值为：minio.default-bucket-name
     * @param objectName 文件名：不可为空
     * @param expires    过期时间，单位：秒，默认值60s
     * @param method     文件访问方式：get，post
     * @return 文件外链
     * @throws IOException               in case of access errors (if the temporary store fails) 或者 thrown to indicate I/O error on S3 operation.
     * @throws ErrorResponseException    thrown to indicate S3 service returned an error response.
     * @throws InsufficientDataException thrown to indicate not enough data available in InputStream.
     * @throws InternalException         thrown to indicate internal library error.
     * @throws InvalidKeyException       thrown to indicate missing of HMAC SHA-256 library.
     * @throws InvalidResponseException  thrown to indicate S3 service returned invalid or no error
     *                                   response.
     * @throws NoSuchAlgorithmException  thrown to indicate missing of MD5 or SHA-256 digest library.
     * @throws XmlParserException        thrown to indicate XML parsing error.
     */
    public String getPresignedObjectUrl(String bucketName, String objectName, Integer expires, Method method) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        if (StrUtil.isBlank(objectName)) {
            return null;
        }
        if (Objects.isNull(expires)) {
            expires = 60;
        }
        if (Objects.isNull(method)) {
            method = Method.GET;
        }
        return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                .expiry(expires)
                .bucket(bucketName)
                .object(objectName)
                .method(method)
                .build());
    }

    /**
     * 根据桶名以及文件名获取目录下所有文件信息
     *
     * @param bucketName 桶名，默认值为application.yaml中minio.default-bucket-name
     * @param filename   文件名，null时查出桶下的目录
     * @return 目录列表
     * @throws ServerException           Thrown to indicate that S3 service returning HTTP server error.
     * @throws InsufficientDataException Thrown to indicate that reading given InputStream gets EOFException before reading given length.
     * @throws ErrorResponseException    Thrown to indicate that error response is received when executing Amazon S3 operation.
     * @throws IOException               in case of access errors (if the temporary store fails) 或者 thrown to indicate I/O error on S3 operation.
     * @throws NoSuchAlgorithmException  thrown to indicate missing of MD5 or SHA-256 digest library.
     * @throws InvalidKeyException       thrown to indicate missing of HMAC SHA-256 library.
     * @throws InvalidResponseException  thrown to indicate S3 service returned invalid or no error
     * @throws XmlParserException        thrown to indicate XML parsing error.
     * @throws InternalException         thrown to indicate internal library error.
     */
    public List<Item> getDirectoryList(String bucketName, String filename) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        List<Item> minioFileVOS = new ArrayList<>();
        ListObjectsArgs listObjectsArgs = ListObjectsArgs.builder()
                .bucket(bucketName)
                .build();
        if (StrUtil.isNotBlank(filename)) {
            listObjectsArgs = ListObjectsArgs.builder()
                    .bucket(bucketName)
                    .prefix(filename)
                    .build();
        }
        Iterable<Result<Item>> listObjects = minioClient.listObjects(listObjectsArgs);
        if (Objects.isNull(listObjects)) {
            return null;
        }
        for (Result<Item> listObject : listObjects) {
            if (Objects.isNull(listObject)) {
                continue;
            }
            Item item = listObject.get();
            if (Objects.isNull(item)) {
                continue;
            }
            minioFileVOS.add(item);
        }
        return minioFileVOS;
    }

    public InputStream download(String bucketName, String filePath) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return minioClient.getObject(GetObjectArgs.builder()
                .bucket(bucketName)
                .object(filePath)
                .build());
    }

    /**
     * 移动文件
     *
     * @param originBucket 原来的桶
     * @param targetBucket 目标桶
     * @param originPath   原来文件
     * @param targetPath   目标文件
     * @return
     * @throws ServerException
     * @throws InsufficientDataException
     * @throws ErrorResponseException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws InvalidResponseException
     * @throws XmlParserException
     * @throws InternalException
     */
    public ObjectWriteResponse moveFile(String originBucket, String targetBucket, String originPath, String targetPath) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        CopySource copySource = CopySource.builder()
                .bucket(originBucket)
                .object(originPath)
                .build();
        ObjectWriteResponse objectWriteResponse = minioClient.copyObject(CopyObjectArgs.builder()
                .bucket(targetBucket)
                .object(targetPath)
                .source(copySource)
                .build());

        // 步骤 2: 删除原始文件
        minioClient.removeObject(RemoveObjectArgs.builder()
                .bucket(originBucket)
                .object(originPath)
                .build());
        return objectWriteResponse;
    }
}
