package com.scnujxjy.backendpoint.controller.util;


import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scnujxjy.backendpoint.dao.entity.basic.GlobalConfigPO;
import com.scnujxjy.backendpoint.model.vo.platform_message.PlatformMessageVO;
import com.scnujxjy.backendpoint.service.minio.MinioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;

/**
 * Minio 分部署存储
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

    /**
     * 获取 Minio 指定桶中的文件
     * @param MinioUrl Minio 的地址 桶名/相对路径名
     * @return
     */
    @PostMapping("/get-minio-file")
    public SaResult getMinioFile(@RequestParam("minioUrl") String MinioUrl) {
        try {

            // 从 MinioService 获取文件
            byte[] fileData = minioService.getFileFromMinio("./" + MinioUrl);

            // 返回成功的 SaResult
            return SaResult.ok().setData(fileData);

        } catch (Exception e) {
            // 返回失败的 SaResult
            log.error("获取 Minio 中的文件失败" + e.toString());
            return SaResult.error("获取文件失败 ");
        }
    }

    /**
     * 根据 Minio 地址获取文件的 URL
     * @param minioUrl
     * @return
     */
    @PostMapping("/get_minio_attachment")
    public SaResult getMinioAttachment(@RequestParam("minioUrl") String minioUrl){
        String s = minioService.generatePresignedUrl(minioUrl);
        return SaResult.data(s).setCode(200);
    }

}
