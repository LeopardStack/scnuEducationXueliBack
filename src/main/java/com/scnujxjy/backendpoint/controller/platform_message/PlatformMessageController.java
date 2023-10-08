package com.scnujxjy.backendpoint.controller.platform_message;

import cn.dev33.satoken.util.SaResult;
import com.scnujxjy.backendpoint.model.vo.platform_message.PlatformMessageVO;
import com.scnujxjy.backendpoint.service.minio.MinioService;
import com.scnujxjy.backendpoint.service.platform_message.PlatformMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * 平台消息表 前端控制器
 * </p>
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
    private MinioService minioService;

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

}
