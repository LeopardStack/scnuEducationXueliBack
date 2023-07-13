package com.scnujxjy.backendpoint.controller.project_manage;


import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.scnujxjy.backendpoint.dto.AllProjectSummaryFiles;
import com.scnujxjy.backendpoint.dto.AllProjectSummaryInfos;
import com.scnujxjy.backendpoint.dto.CreateProjectInfo;
import com.scnujxjy.backendpoint.entity.project_manage.TrainingProject;
import com.scnujxjy.backendpoint.entity.project_manage.TrainingSummary;
import com.scnujxjy.backendpoint.service.project_manage.TrainingProjectService;
import com.scnujxjy.backendpoint.service.project_manage.TrainingSummaryService;
import com.scnujxjy.backendpoint.util.ResultCode;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.activation.MimetypesFileTypeMap;
import javax.swing.text.AbstractWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.baomidou.mybatisplus.core.toolkit.Wrappers.lambdaQuery;

/**
 * training-project
 *   培训项目概要文件
 *
 * @author leopard
 * @since 2023-07-02
 */
@RestController
@RequestMapping("/training-summary")
public class TrainingSummaryController {
    private static final Logger logger = LoggerFactory.getLogger(TrainingSummaryController.class);

    @Autowired
    private TrainingProjectService trainingProjectService;

    @Autowired
    private TrainingSummaryService trainingSummaryService;

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucketName}")
    private String bucketName;

    /**
     * loadAllProjectsFiles
     * 获取所有项目的项目名称以及当前最新项目的文件概要信息
     * @return SaResult
     */
    @RequestMapping("load_all_projects_files")
    @SaCheckPermission("project.manage.brief.view")
    public SaResult loadAllProjectsFiles() {
        LambdaQueryWrapper<TrainingProject> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(TrainingProject::getCreatedAt);

        List<TrainingProject> trainingProjects = trainingProjectService.list(queryWrapper);
        HashMap<Long, String> projectNames = new HashMap<>();
        for(TrainingProject trainingProject: trainingProjects){
            projectNames.put(trainingProject.getId(), trainingProject.getName());
        }

        if(trainingProjects.size() > 0){
            TrainingProject latestTrainingProject = trainingProjects.get(0);

            // Check if the project ID exists in TrainingSummary
            boolean exists = trainingSummaryService.lambdaQuery().eq(TrainingSummary::getProjectId, latestTrainingProject.getId()).one() != null;
            if (!exists) {
                return SaResult.ok(ResultCode.PROJECT_MANAGE_BRIEF_GET2.getMessage()).setCode(ResultCode.PROJECT_MANAGE_BRIEF_GET2.getCode())
                        .set("project_names", projectNames);
            }

            List<AllProjectSummaryFiles> projectSummaryFiles = trainingProjectService.getProjectSummaryFiles(latestTrainingProject.getId());
            ArrayList<AllProjectSummaryInfos> allProjectSummaryInfos = new ArrayList<>();
            int count = 1;
            for(AllProjectSummaryFiles allProjectSummaryFiles: projectSummaryFiles){
                AllProjectSummaryInfos allProjectSummaryInfos1 = new AllProjectSummaryInfos(
                        allProjectSummaryFiles.getId(),
                        allProjectSummaryFiles.getFileName(),
                        allProjectSummaryFiles.getFileType(),
                        allProjectSummaryFiles.getFileType(),
                        allProjectSummaryFiles.getUploader(),
                        allProjectSummaryFiles.getUploaderName(),
                        allProjectSummaryFiles.getUploadedAt(),
                        allProjectSummaryFiles.getFileSize(),
                        allProjectSummaryFiles.getProjectId(),
                        allProjectSummaryFiles.getProjectName()
                );
                allProjectSummaryInfos1.setId_index((long) count);
                count += 1;
                allProjectSummaryInfos.add(allProjectSummaryInfos1);
            }

            return SaResult.ok(ResultCode.PROJECT_MANAGE_BRIEF_GET.getMessage()).set("latest_projects_files", allProjectSummaryInfos).
                    setCode(ResultCode.PROJECT_MANAGE_BRIEF_GET.getCode()).set("project_names", projectNames);
        }


        return SaResult.error(ResultCode.PROJECT_MANAGE_BRIEF_GET_FAIL.getMessage()).setCode(ResultCode.PROJECT_MANAGE_BRIEF_GET_FAIL.getCode());
    }

    /**
     * loadAllProjectsFilesFilterPages
     * 加载所有的培训概要文件
     * @return SaResult
     */
    @RequestMapping("load_all_projects_files_filter_pages")
    @SaCheckPermission("project.manage.brief.view")
    public SaResult loadAllProjectsFilesFilterPages() {
        List<AllProjectSummaryFiles> allProjectSummaryFiles = trainingSummaryService.getAllProjectSummaryFiles();
        return SaResult.ok().set("allProjectSummaryFiles", allProjectSummaryFiles);
    }

    /**
     * uploadSummaryFile
     * 上传培训概要文件
     * @param file 前端传递过来的文件
     * @param metadataJson  前端传递过来的文件的说明信息
     * @return SaResult
     */
    @RequestMapping("upload_project_summary_file")
    @SaCheckPermission("project.manage.brief.fileUpload")
    @Transactional
    public SaResult uploadSummaryFile(@RequestParam("file") MultipartFile file, @RequestParam("metadata") String metadataJson) {
        // Check if the file is empty
        if (file.isEmpty()) {
            return SaResult.error("File is empty");
        }

        // Get the file name
        String fileName = file.getOriginalFilename();

        // Detect file type with Tika
        Tika tika = new Tika();
        String mimeType = "";
        try (InputStream is = file.getInputStream()) {
            mimeType = tika.detect(is);
        } catch (IOException e) {
            return SaResult.error("Failed to detect file type: " + e.getMessage());
        }

        // Get file size
        long fileSize = file.getSize();

        // Get the file content, you could save it to the file system, or store in the database
        byte[] fileContent;
        try {
            fileContent = file.getBytes();
        } catch (IOException e) {
            return SaResult.error("Failed to read file content");
        }

        // Parse the metadata parameter
        JSONObject metadata = JSON.parseObject(metadataJson);
        String projectId = metadata.getString("project_id");
        logger.info("项目名称为 为 " + projectId + " 上传的文件名称为 " + fileName + " 上传的文件类型为 " + mimeType + "\n上传的文件大小为：" + fileSize);


        // Upload the file to Minio
        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(mimeType)
                            .build());

            Long loginId = Long.parseLong((String)StpUtil.getLoginId());
            TrainingSummary trainingSummary = new TrainingSummary(
                    fileName,
                    mimeType,
                    bucketName + "/" + fileName,
                    loginId,
                    new Date(),
                    fileSize,
                    Long.parseLong(projectId)
            );
            trainingSummaryService.save(trainingSummary);
        } catch (Exception e) {
            logger.error(e.toString());
            return SaResult.error("Failed to upload file to Minio: " + e.getMessage());
        }
        return SaResult.ok("File uploaded successfully");
    }

    /**
     * download_project_summary_file
     * 下载培训概要文件
     * @param fileId 文件 id
     * @return ResponseEntity<Resource>
     */
    @RequestMapping("download_project_summary_file")
    public ResponseEntity<Resource> downloadProjectSummaryFile(@RequestParam("fileId") Long fileId) {
        logger.info("前端传递过来的 fileID " + fileId);
        // 从数据库中获取文件信息
        TrainingSummary trainingSummary = trainingSummaryService.getById(fileId);
        if (trainingSummary == null) {
            return ResponseEntity.notFound().build();
        }

        // 从 Minio 中获取文件
        InputStream fileStream;
        try {
            String fileName = trainingSummary.getFilePath().split("/")[1];
            logger.info("文件名为 " + fileName);
            fileStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build());
        } catch (Exception e) {
            logger.error(e.toString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        // 创建一个 Spring Resource 对象
        InputStreamResource resource = new InputStreamResource(fileStream);

        // 返回文件
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(trainingSummary.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + trainingSummary.getFileName() + "\"")
                .body(resource);
    }

    @RequestMapping("delete_project_summary_file")
    public SaResult deleteProjectSummaryFile(@RequestParam("fileId") Long fileId) {
        try {
            TrainingSummary trainingSummary = trainingSummaryService.getById(fileId);
            String fileName = trainingSummary.getFileName();
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build());
            trainingSummaryService.removeById(fileId);
            logger.info("文件删除成功！");
            return SaResult.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return SaResult.error(ResultCode.PROJECT_MANAGE_BRIEF_FILE_DELETE_FAIL.getMessage()).
                    setCode(ResultCode.PROJECT_MANAGE_BRIEF_FILE_DELETE_FAIL.getCode());
        }

    }
}

