package com.scnujxjy.backendpoint.controller.registration_record_card;


import cn.dev33.satoken.util.SaResult;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.GraduationInfoRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.GraduationInfoVO;
import com.scnujxjy.backendpoint.service.registration_record_card.GraduationInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static com.scnujxjy.backendpoint.exception.DataException.*;

/**
 * 毕业信息表
 *
 * @author leopard
 * @since 2023-08-04
 */
@RestController
@RequestMapping("/graduation-info")
@Slf4j
public class GraduationInfoController {

    @Resource
    private GraduationInfoService graduationInfoService;

    /**
     * 根据id查询毕业信息
     *
     * @param id 毕业信息id
     * @return 毕业信息
     */
    @GetMapping("/detail")
    public SaResult detailById(Long id) {
        // 参数校验
        if (Objects.isNull(id)) {
            throw dataMissError();
        }
        // 查询数据
        GraduationInfoVO graduationInfoVO = graduationInfoService.detailById(id);
        if (Objects.isNull(graduationInfoVO)) {
            throw dataNotFoundError();
        }
        return SaResult.data(graduationInfoVO);
    }

    /**
     * 分页查询毕业信息
     *
     * @param graduationInfoROPageRO 分页参数
     * @return 分页结果
     */
    @PostMapping("/page")
    public SaResult pageQueryGraduationInfo(@RequestBody PageRO<GraduationInfoRO> graduationInfoROPageRO) {
        // 参数校验
        if (Objects.isNull(graduationInfoROPageRO)) {
            throw dataMissError();
        }
        if (Objects.isNull(graduationInfoROPageRO.getEntity())) {
            graduationInfoROPageRO.setEntity(new GraduationInfoRO());
        }
        // 数据查询
        PageVO<GraduationInfoVO> graduationInfoVOPageVO = graduationInfoService.pageQueryGraduationInfo(graduationInfoROPageRO);
        if (Objects.isNull(graduationInfoVOPageVO)) {
            throw dataNotFoundError();
        }
        return SaResult.data(graduationInfoVOPageVO);
    }

    /**
     * 更新毕业信息
     *
     * @param graduationInfoRO 毕业信息
     * @return 更新后的毕业信息
     */
    @PutMapping("/edit")
    public SaResult editById(@RequestBody GraduationInfoRO graduationInfoRO) {
        // 参数校验
        if (Objects.isNull(graduationInfoRO)) {
            throw dataMissError();
        }
        // 数据更新
        GraduationInfoVO graduationInfoVO = graduationInfoService.editById(graduationInfoRO);
        if (Objects.isNull(graduationInfoVO)) {
            throw dataUpdateError();
        }
        return SaResult.data(graduationInfoVO);
    }

    /**
     * 数据删除
     *
     * @param id 毕业信息主键id
     * @return 删除数量
     */
    @DeleteMapping("/delete")
    public SaResult deleteById(Long id) {
        // 参数校验
        if (Objects.isNull(id)) {
            throw dataMissError();
        }
        // 数据删除
        Integer count = graduationInfoService.deleteById(id);
        if (count <= 0) {
            throw dataDeleteError();
        }
        return SaResult.data(count);
    }

    /**
     * 上传学生照片
     *
     * @param file 上传的文件
     * @param grade 选择的年级
     * @param photoType 照片类型（入学照片或毕业照片）
     * @return 上传结果
     */
    @PostMapping("/upload-student-photo")
    public SaResult uploadStudentPhoto(@RequestParam("file") MultipartFile[] file,
                                       @RequestParam("grade") String grade,
                                       @RequestParam("photoType") String photoType) {
        // 参数校验
        if (file.length == 0 || Objects.isNull(grade) || Objects.isNull(photoType)) {
            throw dataMissError();
        }
        for (MultipartFile file1 : file) {
            if (!file1.isEmpty()) {
                log.info("文件名 " + file1.getOriginalFilename() + "\n 文件大小 " + file1.getSize() + "\n 文件类型 " + file1.getContentType());
            }
        }

        for (MultipartFile temp_file : file) {
            if (!temp_file.isEmpty() && "application/x-zip-compressed".equals(temp_file.getContentType())) {
                File tempFile = null;
                try {
                    // Save the uploaded file to a temporary file
                    tempFile = File.createTempFile("tempZip", ".zip");
                    temp_file.transferTo(tempFile);

                    // Use ZipFile with GBK charset to process the zip content
                    try (ZipFile zipFile = new ZipFile(tempFile, Charset.forName("GBK"))) {
                        processZipEntries(zipFile);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (tempFile != null && tempFile.exists()) {
                        tempFile.delete(); // Delete the temporary file
                    }
                }
            }
        }

        // TODO: 在这里处理文件上传逻辑，例如保存文件到服务器、数据库等

        // 返回成功响应
        return SaResult.ok("文件上传成功");
    }

    private boolean isValidImage(ZipFile zipFile, ZipEntry entry) throws IOException {
        try (InputStream stream = zipFile.getInputStream(entry)) {
            BufferedImage image = ImageIO.read(stream);
            if (image == null) {
                return false; // Not a valid image format
            }
            int width = image.getWidth();
            int height = image.getHeight();
            log.info("相片宽高是 " + width + " " + height);
            return width == 180 && height == 240;
        }
    }

    private void processZipEntries(ZipFile zipFile) throws IOException {
        Queue<ZipEntry> queue = new LinkedList<>();
        zipFile.stream().forEach(queue::offer);

        while (!queue.isEmpty()) {
            ZipEntry entry = queue.poll();
            if (entry.isDirectory()) {
                final int slashCount = entry.getName().length() - entry.getName().replace("/", "").length();
                zipFile.stream()
                        .filter(e -> e.getName().startsWith(entry.getName()) &&
                                e.getName().length() - e.getName().replace("/", "").length() == slashCount + 1)
                        .forEach(queue::offer);
            } else if (entry.getName().endsWith(".jpg")) {
                log.info("Found jpg file: " + entry.getName());
                if (isValidImage(zipFile, entry)) {
                    log.info("Valid image dimensions for: " + entry.getName());
                    // Process the jpg file as needed
                } else {
                    log.info("Invalid image dimensions for: " + entry.getName());
                }
            }
        }
    }




}

