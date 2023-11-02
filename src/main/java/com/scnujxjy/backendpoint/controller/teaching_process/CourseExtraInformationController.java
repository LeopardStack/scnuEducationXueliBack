package com.scnujxjy.backendpoint.controller.teaching_process;


import cn.dev33.satoken.util.SaResult;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseInformationPO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseBatchExtraInformationRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseCoverChangeRO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.CourseInformationVO;
import com.scnujxjy.backendpoint.service.minio.MinioService;
import com.scnujxjy.backendpoint.service.teaching_process.CourseInformationService;
import com.scnujxjy.backendpoint.service.teaching_process.CourseScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;

/**
 * 课程信息管理，比如课程的标题、公告等
 *
 * @author 谢辉龙
 * @since 2023-10-10
 */
@RestController
@RequestMapping("/course-extra-information")
@Slf4j
public class CourseExtraInformationController {

    @Resource
    private CourseScheduleService courseScheduleService;

    @Resource
    private CourseInformationService courseInformationService;

    @Resource
    private MinioService minioService;

    @Value("${minio.courseCoverDir}")
    private String courseCoverDir;

    @PostMapping("/file_upload")
    public SaResult handleFileUpload(@RequestParam("courseCoverImage") MultipartFile courseCoverFile) {
        try {
            log.info("前端上传的文件为 " + courseCoverFile.getOriginalFilename() + " 大小为 " + courseCoverFile.getSize());

            // 使用 try-with-resources 语句来确保 InputStream 在使用后被关闭
            try (InputStream inputStream = courseCoverFile.getInputStream()) {

                long timestamp = System.currentTimeMillis();
                String uniqueFilename = UUID.randomUUID().toString() + "_" + timestamp + "_" + courseCoverFile.getOriginalFilename();
                String relativeURL = "courseCover/" + uniqueFilename;

                if(minioService.isExist(relativeURL, courseCoverDir)){
                    // 文件已存在，返回一个错误消息
                    return SaResult.error("文件已存在");
                }
                // 上传文件到 Minio，并获取文件的 URL
                boolean uploadSuccess = minioService.uploadStreamToMinio(inputStream, relativeURL, courseCoverDir);
                if (uploadSuccess) {
                    return SaResult.ok().set("fileURL", relativeURL);
                } else {
                    return SaResult.error("上传文件到 Minio 失败");
                }
            }

        } catch (Exception e) {
            log.error("处理文件上传时出错: ", e);
            return SaResult.error(e.toString());
        }
    }



    /**
     * 批量修改教学班的课程简介信息
     * @param courseBatchExtraInformationRO
     * @return
     */
    @PostMapping(value = "/update_schedule_courses_extra_info")
    public SaResult updateScheduleCoursesExtraInformation(
            @RequestBody CourseBatchExtraInformationRO courseBatchExtraInformationRO
    ) {
        // 校验参数
        if (Objects.isNull(courseBatchExtraInformationRO)) {
            return SaResult.error("未做任何修改");
        }

        log.info("更新的教学班课程简介信息为 " + courseBatchExtraInformationRO);

        // 将 courseCoverFile 保存到您的文件系统或对象存储服务...
        // ...


        return SaResult.ok();
    }


    /**
     * 修改单个课程的封面
     * @param courseCoverChangeRO 修改单个课程封面的筛选参数 用来匹配教学计划中的课程
     * @return
     */
    @PostMapping(value = "/change_single_course_cover")
    public SaResult changeSingleCourseCover(@RequestBody CourseCoverChangeRO courseCoverChangeRO) {
        // 校验参数
        if (Objects.isNull(courseCoverChangeRO)) {
            log.error("修改课程的参数不能为空 " + courseCoverChangeRO);
            return SaResult.error("修改课程的参数不能为空");
        }

        try {
            CourseInformationVO courseInformationVO = courseInformationService.getBaseMapper().selectSingleCourse(courseCoverChangeRO);
            // 后端之前返回的 Minio 图片地址是相对路径
            CourseInformationPO courseInformationPO = new CourseInformationPO();
            BeanUtils.copyProperties(courseInformationVO, courseInformationPO);
            courseInformationPO.setCourseCover(courseCoverDir + "/" + courseCoverChangeRO.getNewCourseCover());
            int i = courseInformationService.getBaseMapper().updateById(courseInformationPO);
            if(i <= 0){
                log.error("课程封面图插入数据库失败 " + i);
                return SaResult.error("图片更新失败");
            }
        }catch (Exception e){
            log.error("获取教学计划中的课程失败 " + courseCoverChangeRO + "\n" + e.toString());
            return SaResult.error("获取教学计划中的课程失败");
        }


        return SaResult.ok();
    }

    /**
     * 批量修改同一门课程的封面
     * @param courseCoverChangeRO 批量修改同一门课程封面的筛选参数 用来匹配教学计划中的课程
     * @return
     */
    @PostMapping(value = "/change_batch_course_cover")
    public SaResult changeBatchCourseCover(@RequestBody CourseCoverChangeRO courseCoverChangeRO) {
        // 校验参数
        if (Objects.isNull(courseCoverChangeRO)) {
            log.error("修改课程的参数不能为空 " + courseCoverChangeRO);
            return SaResult.error("修改课程的参数不能为空");
        }

        try {
            CourseInformationVO courseInformationVO = courseInformationService.getBaseMapper().selectSingleCourse(courseCoverChangeRO);
            // 后端之前返回的 Minio 图片地址是相对路径
            CourseInformationPO courseInformationPO = new CourseInformationPO();
            BeanUtils.copyProperties(courseInformationVO, courseInformationPO);
            courseInformationPO.setCourseCover(courseCoverDir + "/" + courseCoverChangeRO.getNewCourseCover());
            int i = courseInformationService.getBaseMapper().updateById(courseInformationPO);
            if(i <= 0){
                log.error("课程封面图插入数据库失败 " + i);
                return SaResult.error("图片更新失败");
            }
        }catch (Exception e){
            log.error("获取教学计划中的课程失败 " + courseCoverChangeRO + "\n" + e.toString());
            return SaResult.error("获取教学计划中的课程失败");
        }


        return SaResult.ok();
    }
}

