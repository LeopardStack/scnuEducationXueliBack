package com.scnujxjy.backendpoint.controller.platform_message;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scnujxjy.backendpoint.dao.entity.basic.PlatformUserPO;
import com.scnujxjy.backendpoint.dao.entity.platform_message.PlatformMessagePO;
import com.scnujxjy.backendpoint.dao.entity.platform_message.UserUploadsPO;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.platform_message.UserUploadsFilesGetRO;
import com.scnujxjy.backendpoint.model.ro.platform_message.UserUploadsRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseScheduleFilterRO;
import com.scnujxjy.backendpoint.model.vo.platform_message.PlatformMessageVO;
import com.scnujxjy.backendpoint.service.basic.PlatformUserService;
import com.scnujxjy.backendpoint.service.minio.MinioService;
import com.scnujxjy.backendpoint.service.platform_message.PlatformMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;

/**
 * 平台消息表
 *
 * @author 谢辉龙
 * @since 2023-09-23
 */
@RestController
@RequestMapping("/platform-message")
@Slf4j
public class PlatformMessageController {
    @Resource
    private PlatformMessageService platformMessageService;

    @Resource
    private PlatformUserService platformUserService;

    @Resource
    private MinioService minioService;

    @Value("${minio.importBucketName}")
    private String importBucketName;

    /**
     * 获取用户下载消息列表
     *
     * @param requestBody 消息类型
     * @return 下载消息列表
     */
    @PostMapping("/get_msg")
    public SaResult getUserDownloadMessages(@RequestBody Map<String, String> requestBody) {
        String msgType = requestBody.get("msgType");
        // 校验参数
        if (Objects.isNull(msgType)) {
            throw new IllegalArgumentException("消息类型不能为空");
        }
        // 查询
        PlatformMessageVO platformMessageVO = platformMessageService.getUserMsg(msgType);
        if (Objects.isNull(platformMessageVO)) {
            throw new RuntimeException("数据未找到");
        }
        return SaResult.data(platformMessageVO);
    }

    /**
     * 设置消息已读
     *
     * @param msgId 消息类型
     * @return 下载消息列表
     */
    @PostMapping("/set_msg_read")
    public SaResult setMsgRead(Long msgId) {
        String loginIdAsString = StpUtil.getLoginIdAsString();
        PlatformUserPO platformUserPO = platformUserService.getBaseMapper().selectOne(new LambdaQueryWrapper<PlatformUserPO>()
                .eq(PlatformUserPO::getUsername, loginIdAsString));

        PlatformMessagePO platformMessagePO = platformMessageService.getBaseMapper().selectOne(new LambdaQueryWrapper<PlatformMessagePO>()
                .eq(PlatformMessagePO::getRelatedMessageId, msgId)
                .eq(PlatformMessagePO::getUserId, platformUserPO.getUserId())
        );
        if(platformMessagePO == null){
            return SaResult.error("设置消息已读未读错误，根据 消息 ID 找不到该消息");
        }
        platformMessagePO.setIsRead(true);
        int i = platformMessageService.getBaseMapper().updateById(platformMessagePO);
        return SaResult.ok("设置成功");
    }


    /**
     * 获取用户下载消息列表 限制为 10条
     *
     * @return 下载消息列表
     */
    @GetMapping("/get_system_msg")
    public SaResult getUserSystemMessages() {
        // 查询
        PlatformMessageVO platformMessageVO = platformMessageService.getUserSystemMsg();

        return SaResult.ok().setData(platformMessageVO);
    }

    /**
     * 获取用户上传消息列表
     *
     * @param userUploadsROPageRO 筛选参数
     * @return 上传消息列表
     */
    @PostMapping("/get_upload_msg")
    public SaResult getUserUploadMessages(@RequestBody PageRO<UserUploadsRO> userUploadsROPageRO) {
        // 校验参数
        if (Objects.isNull(userUploadsROPageRO)) {
            return SaResult.error("消息筛选参数不能为空");
        }
        // 查询
        PlatformMessageVO platformMessageVO = null;
        try {
            platformMessageVO = platformMessageService.getUserMessage(userUploadsROPageRO);
        }catch (Exception e){
            return SaResult.error("获取消息失败 " + e.toString());
        }
        if (Objects.isNull(platformMessageVO)) {
            return SaResult.error("数据未找到");
        }
        return SaResult.data(platformMessageVO);
    }

    /**
     * 获取下载文件
     *
     * @param requestBody 下载文件地址
     * @return 下载消息列表
     */
    @PostMapping("/get_download_file")
    public ResponseEntity<byte[]> downloadFile(@RequestBody Map<String, String> requestBody) {
        String fileURL = requestBody.get("minioURL");
        // 校验参数
        if (Objects.isNull(fileURL)) {
            throw new IllegalArgumentException("下载地址为空");
        }
        // 查询
        byte[] fileBytes = minioService.downloadFileFromMinio(fileURL);
        if (Objects.isNull(fileBytes)) {
            throw new RuntimeException("数据未找到");
        }
        log.info("下载的文件大小 " + fileBytes.length);
        // 设置响应头以下载文件
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename(fileURL.substring(fileURL.lastIndexOf("/") + 1))
                .build());

        return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
    }

    /**
     * 获取上传下载文件
     *
     * @param userUploadsFilesGetRO 下载消息相关文件所需参数
     * @return 下载消息列表
     */
    @PostMapping("/get_upload_file")
    public ResponseEntity<byte[]> getUploadFiles(@RequestBody UserUploadsFilesGetRO userUploadsFilesGetRO) {
        String fileURL = importBucketName + "/";
        if(userUploadsFilesGetRO.getUploadFileUrl() != null){
            fileURL += userUploadsFilesGetRO.getUploadFileUrl();
        } else if (userUploadsFilesGetRO.getUploadResultFileUrl() != null) {
            fileURL += userUploadsFilesGetRO.getUploadResultFileUrl();
        }
        // 校验参数
        if (Objects.isNull(fileURL)) {
            throw new IllegalArgumentException("下载地址为空");
        }
        // 查询
        byte[] fileBytes = minioService.downloadFileFromMinio(fileURL);
        if (Objects.isNull(fileBytes)) {
            throw new RuntimeException("数据未找到");
        }
        log.info("下载的文件大小 " + fileBytes.length);

        // 获取文件名
        String filename = fileURL.substring(fileURL.lastIndexOf("/") + 1);

        try {
            // URL编码文件名
            String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8.toString());


            // 设置响应头以下载文件
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDisposition(ContentDisposition.builder("attachment")
                    .filename(encodedFilename)  // 使用编码后的文件名
                    .build());

            return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
        }catch (Exception e){
            log.error("返回文件失败 " + e.toString());
        }
        return null;
    }

}
