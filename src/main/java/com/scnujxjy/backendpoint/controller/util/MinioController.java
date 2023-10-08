package com.scnujxjy.backendpoint.controller.util;


import cn.dev33.satoken.util.SaResult;
import com.scnujxjy.backendpoint.model.vo.platform_message.PlatformMessageVO;
import com.scnujxjy.backendpoint.service.minio.MinioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * 获取 Minio 文件
 * </p>
 *
 * @author 谢辉龙
 * @since 2023-09-23
 */
@RestController
@RequestMapping("/file")
@Slf4j
public class MinioController {
    @Resource
    private MinioService minioService;

    /**
     * 获取用户下载消息列表
     *
     * @param requestBody 消息类型
     * @return 下载消息列表
     */
    @PostMapping("/get_picture")
    public SaResult getUserPictureURL(@RequestBody Map<String, String> requestBody) {
        String fileURL = requestBody.get("minio_url");
        // 校验参数
        if (Objects.isNull(fileURL)) {
            return SaResult.error("文件地址不能为空");
//            throw new IllegalArgumentException("文件地址不能为空");
        }
        // 查询
        String retURL = null;
        try {
            retURL = minioService.getFileUrlFromMinio(fileURL);
        }catch (Exception e){
            return SaResult.error("获取照片失败");
        }

        return SaResult.data(retURL);
    }


    /**
     * 获取用户下载消息列表
     *
     * @param requestBody 消息类型
     * @return 下载消息列表
     */
    @PostMapping("/get_picture_bytes")
    public ResponseEntity<byte[]> getUserPictureBytes(@RequestBody Map<String, String> requestBody) {
        String fileURL = requestBody.get("minio_url");
        // 校验参数
        if (Objects.isNull(fileURL)) {
            return ResponseEntity.badRequest().body("文件地址不能为空".getBytes());
        }
        // 查询
        byte[] photoBytesFromMinio;
        try {
            photoBytesFromMinio = minioService.getPhotoBytesFromMinio(fileURL);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("获取照片失败".getBytes());
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG); // 这里假设所有的图片都是JPEG格式，如果不是，请进行适当的调整
        return new ResponseEntity<>(photoBytesFromMinio, headers, HttpStatus.OK);
    }

}
