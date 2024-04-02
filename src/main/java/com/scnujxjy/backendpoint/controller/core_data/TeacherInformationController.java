package com.scnujxjy.backendpoint.controller.core_data;


import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.scnujxjy.backendpoint.constant.enums.UploadType;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.core_data.TeacherInformationRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.core_data.TeacherInformationVO;
import com.scnujxjy.backendpoint.model.vo.core_data.TeacherQueryArgsVO;
import com.scnujxjy.backendpoint.service.core_data.TeacherInformationService;
import com.scnujxjy.backendpoint.service.minio.MinioService;
import com.scnujxjy.backendpoint.service.platform_message.UserUploadsService;
import com.scnujxjy.backendpoint.util.ResultCode;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static com.scnujxjy.backendpoint.exception.DataException.*;

/**
 * 教师信息表
 *
 * @author leopard
 * @since 2023-08-02
 */
@RestController
@Slf4j
@RequestMapping("/teacher_information")
public class TeacherInformationController {
    @Resource
    private TeacherInformationService teacherInformationService;

    @Resource
    private MinioService minioService;

    @Resource
    private UserUploadsService userUploadsService;

    @Value("${minio.importBucketName}")
    private String importBucketName;

    @GetMapping("/detail")
    public SaResult detailById(int userId) {
        // 参数校验
        // 数据查询
        TeacherInformationVO teacherInformationVO = teacherInformationService.detailById(userId);
        if (Objects.isNull(teacherInformationVO)) {
            throw dataNotFoundError();
        }
        return SaResult.data(teacherInformationVO);
    }

    @PostMapping("/page")
    public SaResult pageQueryTeacherInformation(@RequestBody PageRO<TeacherInformationRO> teacherInformationROPageRO) {
        // 参数校验
        if (Objects.isNull(teacherInformationROPageRO)) {
            return ResultCode.PARAM_IS_NULL.generateErrorResultInfo();
        }

        // 数据查询
        PageVO<TeacherInformationVO> teacherInformationVOPageVO = teacherInformationService.pageQueryTeacherInformation(teacherInformationROPageRO);
        if (Objects.isNull(teacherInformationVOPageVO)) {
            throw dataNotFoundError();
        }
        return SaResult.data(teacherInformationVOPageVO);
    }

    @PostMapping("/get_teacher_query_args")
    public SaResult getTeacherQueryArgs(@RequestBody TeacherInformationRO teacherInformationRO) {
        // 参数校验
        if (Objects.isNull(teacherInformationRO)) {
            return ResultCode.PARAM_IS_NULL.generateErrorResultInfo();
        }

        // 数据查询
        TeacherQueryArgsVO teacherInformationVOPageVO = teacherInformationService.getTeacherQueryArgs(teacherInformationRO);
        return SaResult.ok().setData(teacherInformationVOPageVO);
    }



    @PostMapping("/add")
    @SaCheckPermission("师资库管理.添加教师")
    public SaResult addNewTeacher(@RequestBody TeacherInformationRO teacherInformationRO) {
        // 参数校验
        if (Objects.isNull(teacherInformationRO)) {
            return ResultCode.PARAM_IS_NULL.generateErrorResultInfo();
        }

        return teacherInformationService.addNewTeacher(teacherInformationRO);
    }

    @PostMapping("/edit")
    @SaCheckPermission("师资库管理.修改教师")
    public SaResult editById(@RequestBody TeacherInformationRO teacherInformationRO) {
        // 参数校验
        if (Objects.isNull(teacherInformationRO)) {
            return ResultCode.PARAM_IS_NULL.generateErrorResultInfo();
        }
        // 数据更新
        return teacherInformationService.editById(teacherInformationRO);
    }

    @DeleteMapping("/delete")
    @SaCheckPermission("师资库管理.删除教师")
    public SaResult deleteById(Integer userId) {
        // 参数校验
        if (Objects.isNull(userId)) {
            return ResultCode.PARAM_IS_NULL.generateErrorResultInfo();
        }
        // 数据删除
        return teacherInformationService.deleteById(userId);
    }

    @PostMapping("/excel/import")
    @SaCheckPermission("师资库管理.添加教师")
    public SaResult ExcelImportTeacherInformation(MultipartFile file) {
        try {

            // 使用 try-with-resources 语句来确保 InputStream 在使用后被关闭
            try (InputStream inputStream = file.getInputStream()) {
                LocalDateTime now = LocalDateTime.now();

                String originalFilename = file.getOriginalFilename();

                int lastDotPosition = originalFilename.lastIndexOf('.');
                String baseName = (lastDotPosition >= 0) ? originalFilename.substring(0, lastDotPosition) : originalFilename;
                String extension = (lastDotPosition >= 0) ? originalFilename.substring(lastDotPosition) : "";

                String relativeURL = "师资表导入/import/" + StpUtil.getLoginId() + "/" + baseName + "-" + now + extension;

                // 上传文件到 Minio，并获取文件的 URL
                boolean uploadSuccess = minioService.uploadStreamToMinio(inputStream, relativeURL, importBucketName);
                if (uploadSuccess) {
                    long b = userUploadsService.generateCourseScheduleListUploadMsg(relativeURL, UploadType.COURSE_TEACHER_LIST.getUpload_type());
                    if (b < 0) {
                        return SaResult.error("上传师资表失败，上传消息无法生成");
                    }

                    // 向消息队列发送处理排课表导入的消息
//                    boolean b1 = messageSender.sendImportMsg(b, managerFilter, (String) StpUtil.getLoginId());
                    // 这里不采用消息队列 处理 而是直接采用 异步线程来处理
                    return teacherInformationService.excelImportTeacherInformation(file, b, importBucketName);

                } else {
                    return SaResult.error("上传文件到 Minio 失败");
                }
                // 采用 EasyExcel 解析这个 excel

            }

        } catch (Exception e) {
            log.error("处理文件上传时出错: ", e);
            return SaResult.error(e.toString());
        }
    }

    @GetMapping("/get_teacher_information")
    public SaResult getTeacherInformation() {
        // 数据查询

        return SaResult.data(teacherInformationService.getTeacherInformation());
    }

    /**
     * 获取主讲教师
     * @return
     */
    @GetMapping("/get_main_teacher_information")
    public SaResult getMainTeacherInformation(){
        return SaResult.ok().setData(teacherInformationService.getMainTeacherInformation());
    }

    /**
     * 获取辅导教师
     * @return
     */
    @GetMapping("/get_tutor_information")
    public SaResult getTutorInformation(){
        return SaResult.ok().setData(teacherInformationService.geTutorInformation());
    }

}

