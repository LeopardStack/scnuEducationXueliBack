package com.scnujxjy.backendpoint.controller.platform_message;


import cn.dev33.satoken.util.SaResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 *  批量数据导入
 *
 * @author 谢辉龙
 * @since 2023-10-29
 */
@RestController
@RequestMapping("/user-uploads")
@Slf4j
public class UserUploadsController {
    /**
     * 处理用户上传的文件和表单数据
     *
     * @param grade 年级
     * @param userId 用户ID
     * @return 处理结果
     */
    @PostMapping("/upload_new_student")
    public SaResult handleFileUpload(
            @RequestParam("grade") String grade,
            @RequestParam("userId") String userId,
            @RequestParam("isOverwrite") Boolean isOverwrite,
            @RequestParam("file") MultipartFile file) {

        // 这里添加您的逻辑，例如保存文件，处理表单数据等
        log.info("年级信息" + grade);
        log.info("是否覆盖" + isOverwrite);
        log.info("上传的文件 " + file.getOriginalFilename());
        // 返回结果
        return SaResult.ok().setMsg("文件上传成功");
    }

    /**
     * 上传学生成绩数据
     * @param file
     * @return
     */
    @PostMapping("/upload_students_pictures")
    public SaResult handleStudentsPicturesUpload(
            @RequestParam("selectedGrade") String selectedGrade,
            @RequestParam("selectedPhotoType") String selectedPhotoType,
            @RequestParam("file") MultipartFile file) {

        // 这里添加您的逻辑，例如保存文件，处理表单数据等
        log.info("上传的照片文件 " + file.getOriginalFilename());
        log.info("上传的学生照片所属年级 " + selectedGrade);
        log.info("上传的学生照片类型 " + selectedPhotoType);
        // 返回结果
        try (ZipInputStream zis = new ZipInputStream(file.getInputStream(), Charset.forName("GBK"))) {
            ZipEntry zipEntry = zis.getNextEntry();

            while (zipEntry != null) {
                if (!zipEntry.isDirectory() && zipEntry.getName().endsWith(".jpg")) {
                    // 这里处理每张图片
                    log.info("图片文件: " + zipEntry.getName());
                    // 示例：uploadToServer(zis, zipEntry.getName());
                }
                log.info("文件/文件名 " + zipEntry.getName());
                zis.closeEntry(); // 关闭当前条目
                zipEntry = zis.getNextEntry(); // 移动到下一个条目
            }
        } catch (Exception e) {
            return SaResult.error("上传失败").setCode(2001);
        }
        return SaResult.ok().setMsg("文件上传成功");
    }

    private File convertToFile(MultipartFile multipartFile) throws IOException {
        File tempFile = File.createTempFile("temp", ".7z");
        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            out.write(multipartFile.getBytes());
        }
        return tempFile;
    }

    @PostMapping("/upload_grades")
    public SaResult handleGradesUpload(
            @RequestParam("file") MultipartFile file) {

        // 这里添加您的逻辑，例如保存文件，处理表单数据等
        log.info("上传的成绩文件 " + file.getOriginalFilename());
        // 返回结果
        return SaResult.ok().setMsg("文件上传成功");
    }
}

