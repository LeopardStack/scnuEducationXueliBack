package com.scnujxjy.backendpoint.cdnMsgTest;

import com.scnujxjy.backendpoint.constant.enums.FILEOperation;
import com.scnujxjy.backendpoint.model.bo.cdn_file_manage.FileCommuBO;
import com.scnujxjy.backendpoint.util.MessageSender;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@Slf4j
public class Test1 {
    @Resource
    private MessageSender messageSender;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 查询 根目录信息
     * @throws InterruptedException
     */
    @Test
    public void test1() throws InterruptedException {
        FileCommuBO fileCommuBO = new FileCommuBO();
        fileCommuBO.setFileOperation(FILEOperation.QUERY_ROOT_DIRECTORY);
        boolean send = messageSender.send(fileCommuBO);
        // 将 FileCommuBO 存储到 Redis
        redisTemplate.opsForValue().set("FileCommuBO:" + fileCommuBO.getSerialNumber(), fileCommuBO, 1, TimeUnit.MINUTES);
        FileCommuBO fileCommuBOBack = null;
        if(send){
            // 消息发送成功了 看反馈值
            boolean ident = false;
            // 循环检查 Redis 中的响应
            for (int i = 0; i < 60; i++) {
                FileCommuBO response = (FileCommuBO) redisTemplate.opsForValue().get("FileCommuBO:" + fileCommuBO.getSerialNumber());
                if (response != null && response.getFileOperationResponse() != null) {
                    // 响应已更新
                    fileCommuBOBack = response;
                    ident = true;
                    break;
                }
                // 等待一秒再次检查
                Thread.sleep(1000);
            }
            if(!ident){
                log.info("CDN 操作失败，无任何响应 ");
            }
        }

        if(fileCommuBOBack != null){
            // 拿到了返回值
            switch (fileCommuBOBack.getFileOperationResponse()){
                case QUERY_ROOT_DIRECTORY_SUCCESS:
                    log.info("成功查询到了根目录的文件/文件夹信息 \n" + fileCommuBOBack.getFiles());
                    break;
                default:
                    log.error("错误的返回值 " + fileCommuBOBack.getFileOperationResponse());
            }
        }
    }


    /**
     * 查询 指定相对目录信息
     * @throws InterruptedException
     */
    @Test
    public void test2() throws InterruptedException {
        FileCommuBO fileCommuBO = new FileCommuBO();
        fileCommuBO.setFileSearchRelativeURL("test/旧生收费");
        fileCommuBO.setFileOperation(FILEOperation.QUERY_SPECIFIC_DIRECTORY);
        boolean send = messageSender.send(fileCommuBO);
        // 将 FileCommuBO 存储到 Redis
        redisTemplate.opsForValue().set("FileCommuBO:" + fileCommuBO.getSerialNumber(), fileCommuBO, 1, TimeUnit.MINUTES);
        FileCommuBO fileCommuBOBack = null;
        if(send){
            // 消息发送成功了 看反馈值
            boolean ident = false;
            // 循环检查 Redis 中的响应
            for (int i = 0; i < 60; i++) {
                FileCommuBO response = (FileCommuBO) redisTemplate.opsForValue().get("FileCommuBO:" + fileCommuBO.getSerialNumber());
                if (response != null && response.getFileOperationResponse() != null) {
                    // 响应已更新
                    fileCommuBOBack = response;
                    ident = true;
                    break;
                }
                // 等待一秒再次检查
                Thread.sleep(1000);
            }
            if(!ident){
                log.info("CDN 操作失败，无任何响应 ");
            }
        }

        if(fileCommuBOBack != null){
            // 拿到了返回值
            switch (fileCommuBOBack.getFileOperationResponse()){
                case QUERY_ROOT_DIRECTORY_SUCCESS:
                    log.info("成功查询到了根目录的文件/文件夹信息 \n" + fileCommuBOBack.getFiles());
                    break;
                case QUERY_SPECIFIC_DIRECTORY_SUCCESS:
                    log.info("成功查询到了指定目录 + " + fileCommuBOBack.getFileSearchRelativeURL() +
                            " 的文件/文件夹信息 \n" + fileCommuBOBack.getFiles());
                    break;
                default:
                    log.error("错误的返回值 " + fileCommuBOBack.getFileOperationResponse());
            }
        }
    }


    /**
     * 上传指定 Minio URL 的文件到 CDN 目录中
     * @throws InterruptedException
     */
    @Test
    public void test3() throws InterruptedException {
        FileCommuBO fileCommuBO = new FileCommuBO();
        fileCommuBO.setUploadFileRelativeURL("test/旧生收费/成教老生收费20240229.xlsx");
        fileCommuBO.setFileOperation(FILEOperation.UPLOAD_SPECIFIC_FILE);
        boolean send = messageSender.send(fileCommuBO);
        // 将 FileCommuBO 存储到 Redis
        redisTemplate.opsForValue().set("FileCommuBO:" + fileCommuBO.getSerialNumber(), fileCommuBO, 1, TimeUnit.MINUTES);
        FileCommuBO fileCommuBOBack = null;
        if(send){
            // 消息发送成功了 看反馈值
            boolean ident = false;
            // 循环检查 Redis 中的响应
            for (int i = 0; i < 60; i++) {
                FileCommuBO response = (FileCommuBO) redisTemplate.opsForValue().get("FileCommuBO:" + fileCommuBO.getSerialNumber());
                if (response != null && response.getFileOperationResponse() != null) {
                    // 响应已更新
                    fileCommuBOBack = response;
                    ident = true;
                    break;
                }
                // 等待一秒再次检查
                Thread.sleep(1000);
            }
            if(!ident){
                log.info("CDN 操作失败，无任何响应 ");
            }
        }

        if(fileCommuBOBack != null){
            // 拿到了返回值
            switch (fileCommuBOBack.getFileOperationResponse()){
                case QUERY_ROOT_DIRECTORY_SUCCESS:
                    log.info("成功查询到了根目录的文件/文件夹信息 \n" + fileCommuBOBack.getFiles());
                    break;
                case QUERY_SPECIFIC_DIRECTORY_SUCCESS:
                    log.info("成功查询到了指定目录 " + fileCommuBOBack.getFileSearchRelativeURL() +
                            " 的文件/文件夹信息 \n" + fileCommuBOBack.getFiles());
                    break;
                case UPLOAD_SPECIFIC_FILE_SUCCESS:
                    log.info("成功上传了指定文件到 CDN " + fileCommuBOBack.getCdnFileAbsoluteURL());
                    log.info("成功上传了指定文件到 CDN " + fileCommuBOBack);
                    break;
                default:
                    log.error("错误的返回值 " + fileCommuBOBack.getFileOperationResponse());
            }
        }
    }
}
