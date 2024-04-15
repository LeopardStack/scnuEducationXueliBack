package com.scnujxjy.backendpoint.service.courses_learning;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.dao.entity.courses_learning.CourseNotificationsPO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scnujxjy.backendpoint.dao.entity.courses_learning.CoursesLearningPO;
import com.scnujxjy.backendpoint.dao.entity.platform_message.AttachmentPO;
import com.scnujxjy.backendpoint.dao.mapper.courses_learning.CourseNotificationsMapper;
import com.scnujxjy.backendpoint.model.ro.courses_learning.CourseNotificationsRO;
import com.scnujxjy.backendpoint.model.vo.course_learning.CourseNotificationBasicInfoVO;
import com.scnujxjy.backendpoint.model.vo.course_learning.CourseNotificationDetailInfoVO;
import com.scnujxjy.backendpoint.model.vo.platform_message.AttachmentVO;
import com.scnujxjy.backendpoint.service.minio.MinioService;
import com.scnujxjy.backendpoint.service.platform_message.AttachmentService;
import com.scnujxjy.backendpoint.util.ResultCode;
import com.scnujxjy.backendpoint.util.tool.ScnuXueliTools;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 课程通知表 服务类
 * </p>
 *
 * @author 谢辉龙
 * @since 2024-04-15
 */
@Service
@Slf4j
public class CourseNotificationsService extends ServiceImpl<CourseNotificationsMapper, CourseNotificationsPO> implements IService<CourseNotificationsPO> {

    @Resource
    private MinioService minioService;

    @Resource
    private AttachmentService attachmentService;

    @Resource
    private CoursesLearningService coursesLearningService;

    @Value("${minio.courseCoverDir}")
    private String courseBucket;

    @Transactional
    public SaResult createCourseNotification(CourseNotificationsRO courseNotificationsRO) {
        log.info("获取到了课程通知参数 \n" + courseNotificationsRO);
        CoursesLearningPO coursesLearningPO = coursesLearningService.getBaseMapper().selectOne(new LambdaQueryWrapper<CoursesLearningPO>()
                .eq(CoursesLearningPO::getId, courseNotificationsRO.getCourseId()));
        if (coursesLearningPO == null) {
            return ResultCode.UPDATE_COURSE_FAI12.generateErrorResultInfo();
        }

        // 先插入公告本身
        CourseNotificationsPO courseNotificationsPO = new CourseNotificationsPO()
                .setCourseId(coursesLearningPO.getId())
                .setNotificationTitle(courseNotificationsRO.getNotificationTitle())
                .setNotificationContent(courseNotificationsRO.getNotificationContent())
                .setIsPinned(courseNotificationsRO.getIsPinned())
                ;
        int insert1 = getBaseMapper().insert(courseNotificationsPO);
        if(insert1 <= 0){
            return ResultCode.UPDATE_COURSE_FAI14.generateErrorResultInfo();
        }

        if(courseNotificationsRO.getNotificationAttachments() != null &&
                !courseNotificationsRO.getNotificationAttachments().isEmpty()) {
            List<AttachmentPO> attachmentPOList = new ArrayList<>();
            int order = 1;
            for (MultipartFile multipartFile : courseNotificationsRO.getNotificationAttachments()) {
                log.info("收到了这些文件 " + multipartFile.getOriginalFilename());
                log.info("\n文件的信息 " + multipartFile.getContentType() + " " + multipartFile.getSize());
                try {
                    String minioUrl = coursesLearningPO.getId() + "-"
                            + coursesLearningPO.getCourseName() + "/"
                            + new Date() + "-" + multipartFile.getOriginalFilename();
                    try (InputStream is = multipartFile.getInputStream()) {
                        minioService.uploadStreamToMinio(is, minioUrl, courseBucket);
                    } catch (IOException e) {
                        log.error("处理文件上传时出错", e);
                        return ResultCode.UPDATE_COURSE_FAI13.generateErrorResultInfo();
                    }
                    AttachmentPO attachmentPO = new AttachmentPO()
                            .setAttachmentName(multipartFile.getOriginalFilename())
                            .setAttachmentMinioPath(courseBucket + "/" + minioUrl)
                            .setAttachmentSize(multipartFile.getSize())
                            .setAttachmentOrder(order)
                            .setRelatedId(courseNotificationsPO.getId())
                            .setAttachmentType(ScnuXueliTools.getFileExtension(multipartFile))
                            .setUsername(StpUtil.getLoginIdAsString());
                    int insert = attachmentService.getBaseMapper().insert(attachmentPO);
                    if (insert <= 0) {
                        return ResultCode.DATABASE_INSERT_ERROR.generateErrorResultInfo();
                    }
                    attachmentPOList.add(attachmentPO);
                    order += 1;
                } catch (Exception e) {
                    log.error(StpUtil.getLoginIdAsString() + "上传课程公告附件失败 " + e);
                    return ResultCode.UPDATE_COURSE_FAI13.generateErrorResultInfo();
                }
            }

            // 将附件插入到 Minio 和数据库中后 才开始 更新课程公告的 附件列表信息
            courseNotificationsPO.setNotificationAttachment(attachmentPOList.stream()
                    .map(AttachmentPO::getId) // 将 AttachmentPO 转换为 Long (它的 id)
                    .collect(Collectors.toList()));
            int i = getBaseMapper().updateById(courseNotificationsPO);
            if (i <= 0) {
                return ResultCode.UPDATE_COURSE_FAI15.generateErrorResultInfo();
            }
        }


        return SaResult.ok("创建课程公告成功");
    }

    /**
     * 编辑课程公告
     * @param courseNotificationsRO
     * @return
     */
    public SaResult editCourseNotification(CourseNotificationsRO courseNotificationsRO) {

        log.info("获取到了课程通知参数 \n" + courseNotificationsRO);
        CoursesLearningPO coursesLearningPO = coursesLearningService.getBaseMapper().selectOne(new LambdaQueryWrapper<CoursesLearningPO>()
                .eq(CoursesLearningPO::getId, courseNotificationsRO.getCourseId()));
        if (coursesLearningPO == null) {
            return ResultCode.UPDATE_COURSE_FAI19.generateErrorResultInfo();
        }

        // 先获取原始公告
        CourseNotificationsPO courseNotificationsPO1 = getBaseMapper().selectById(courseNotificationsRO.getId());
        if(courseNotificationsPO1 == null){
            return ResultCode.UPDATE_COURSE_FAI20.generateErrorResultInfo();
        }

        // 先更新
        if(!courseNotificationsRO.getNotificationTitle().equals(courseNotificationsPO1.getNotificationTitle())){
            courseNotificationsPO1.setNotificationTitle(courseNotificationsRO.getNotificationTitle());
        }

        if(!courseNotificationsRO.getNotificationContent().equals(courseNotificationsPO1.getNotificationContent())){
            courseNotificationsPO1.setNotificationContent(courseNotificationsRO.getNotificationContent());
        }

        if(!courseNotificationsRO.getIsPinned().equals(courseNotificationsPO1.getIsPinned())){
            courseNotificationsPO1.setIsPinned(courseNotificationsRO.getIsPinned());
        }

        // 附件更新 直接删除再覆盖
        List<Long> notificationAttachment = courseNotificationsPO1.getNotificationAttachment();
        for(Long attachmentId : notificationAttachment){
            AttachmentPO attachmentPO = attachmentService.getById(attachmentId);
            minioService.deleteFileByAbsolutePath(attachmentPO.getAttachmentMinioPath());
            int i = attachmentService.getBaseMapper().deleteById(attachmentId);
            if(i <= 0){
                return ResultCode.UPDATE_COURSE_FAI17.generateErrorResultInfo();
            }
        }
        courseNotificationsPO1.setNotificationAttachment(new ArrayList<>());
        int insert1 = getBaseMapper().updateById(courseNotificationsPO1);
        if(insert1 <= 0){
            return ResultCode.UPDATE_COURSE_FAI21.generateErrorResultInfo();
        }

        if(courseNotificationsRO.getNotificationAttachments() != null &&
                !courseNotificationsRO.getNotificationAttachments().isEmpty()){
            List<AttachmentPO> attachmentPOList = new ArrayList<>();
            int order = 1;
            for(MultipartFile multipartFile : courseNotificationsRO.getNotificationAttachments()){
                log.info("收到了这些文件 " + multipartFile.getOriginalFilename());
                log.info("\n文件的信息 " + multipartFile.getContentType() + " " + multipartFile.getSize());
                try {
                    String minioUrl =coursesLearningPO.getId() + "-"
                            + coursesLearningPO.getCourseName() + "/"
                            + new Date() + "-" + multipartFile.getOriginalFilename();
                    try (InputStream is = multipartFile.getInputStream()) {
                        minioService.uploadStreamToMinio(is, minioUrl, courseBucket);
                    } catch (IOException e) {
                        log.error("处理文件上传时出错", e);
                        return ResultCode.UPDATE_COURSE_FAI13.generateErrorResultInfo();
                    }
                    AttachmentPO attachmentPO = new AttachmentPO()
                            .setAttachmentName(multipartFile.getOriginalFilename())
                            .setAttachmentMinioPath(courseBucket + "/" + minioUrl)
                            .setAttachmentSize(multipartFile.getSize())
                            .setAttachmentOrder(order)
                            .setRelatedId(courseNotificationsPO1.getId())
                            .setAttachmentType(ScnuXueliTools.getFileExtension(multipartFile))
                            .setUsername(StpUtil.getLoginIdAsString())
                            ;
                    int insert = attachmentService.getBaseMapper().insert(attachmentPO);
                    if(insert <= 0){
                        return ResultCode.DATABASE_INSERT_ERROR.generateErrorResultInfo();
                    }
                    attachmentPOList.add(attachmentPO);
                    order += 1;
                }catch (Exception e){
                    log.error(StpUtil.getLoginIdAsString() + "上传课程公告附件失败 " + e);
                    return ResultCode.UPDATE_COURSE_FAI13.generateErrorResultInfo();
                }
            }

            // 将附件插入到 Minio 和数据库中后 才开始 更新课程公告的 附件列表信息
            courseNotificationsPO1.setNotificationAttachment(attachmentPOList.stream()
                    .map(AttachmentPO::getId) // 将 AttachmentPO 转换为 Long (它的 id)
                    .collect(Collectors.toList()));
            int i = getBaseMapper().updateById(courseNotificationsPO1);
            if(i <= 0){
                return ResultCode.UPDATE_COURSE_FAI22.generateErrorResultInfo();
            }
        }

        return SaResult.ok("更新课程公告成功");
    }

    /**
     * 删除公告 及其在系统内的附件
     * @param courseNotificationId
     * @return
     */
    public SaResult deleteCourseNotification(Long courseNotificationId) {
        CourseNotificationsPO courseNotificationsPO = getBaseMapper().selectOne(new LambdaQueryWrapper<CourseNotificationsPO>()
                .eq(CourseNotificationsPO::getId, courseNotificationId));

        List<Long> notificationAttachment = courseNotificationsPO.getNotificationAttachment();
        for(Long attachmentId : notificationAttachment){
            AttachmentPO attachmentPO = attachmentService.getById(attachmentId);
            minioService.deleteFileByAbsolutePath(attachmentPO.getAttachmentMinioPath());
            int i = attachmentService.getBaseMapper().deleteById(attachmentId);
            if(i <= 0){
                return ResultCode.UPDATE_COURSE_FAI17.generateErrorResultInfo();
            }
        }
        int i = getBaseMapper().deleteById(courseNotificationId);
        if(i <= 0){
            return ResultCode.UPDATE_COURSE_FAI18.generateErrorResultInfo();
        }


        return SaResult.ok("删除课程公告成功");
    }


    /**
     * 获取课程公告的基本信息
     * @param courseNotificationsRO
     * @return
     */
    public SaResult getCourseNotificationBasicInfo(CourseNotificationsRO courseNotificationsRO) {
        CoursesLearningPO coursesLearningPO = coursesLearningService.getBaseMapper().selectOne(new LambdaQueryWrapper<CoursesLearningPO>()
                .eq(CoursesLearningPO::getId, courseNotificationsRO.getCourseId()));
        if (coursesLearningPO == null) {
            return ResultCode.UPDATE_COURSE_FAI12.generateErrorResultInfo();
        }
        List<CourseNotificationsPO> courseNotificationsPOS = getBaseMapper().selectList(new LambdaQueryWrapper<CourseNotificationsPO>()
                .eq(CourseNotificationsPO::getCourseId, courseNotificationsRO.getCourseId()));

        List<CourseNotificationBasicInfoVO> courseNotificationBasicInfoVOList = new ArrayList<>();
        for(CourseNotificationsPO courseNotificationsPO : courseNotificationsPOS){
            CourseNotificationBasicInfoVO courseNotificationBasicInfoVO = new CourseNotificationBasicInfoVO()
                    .setNotificationTitle(courseNotificationsPO.getNotificationTitle())
                    .setCreatedAt(courseNotificationsPO.getCreatedAt())
                    .setId(courseNotificationsPO.getId())
                    ;
            courseNotificationBasicInfoVOList.add(courseNotificationBasicInfoVO);
        }

        return SaResult.ok().setData(courseNotificationBasicInfoVOList);
    }

    public SaResult getCourseNotificationDetailInfo(CourseNotificationsRO courseNotificationsRO) {
        CourseNotificationsPO courseNotificationsPO = getBaseMapper().selectOne(new LambdaQueryWrapper<CourseNotificationsPO>()
                .eq(CourseNotificationsPO::getId, courseNotificationsRO.getId()));
        if(courseNotificationsPO == null){
            return ResultCode.UPDATE_COURSE_FAI16.generateErrorResultInfo();
        }

        List<Long> notificationAttachment = courseNotificationsPO.getNotificationAttachment();
        List<AttachmentVO> attachmentPOList = new ArrayList<>();
        for(Long attachmentId : notificationAttachment){
            AttachmentPO attachmentPO = attachmentService.getById(attachmentId);
            AttachmentVO attachmentVO = new AttachmentVO();
            BeanUtils.copyProperties(attachmentPO, attachmentVO);
            String minioViewPath = minioService.generatePresignedUrl(attachmentPO.getAttachmentMinioPath());
            attachmentVO.setAttachmentMinioViewPath(minioViewPath);
            attachmentPOList.add(attachmentVO);
        }

        CourseNotificationDetailInfoVO courseNotificationDetailInfoVO = new CourseNotificationDetailInfoVO()
                .setId(courseNotificationsPO.getId())
                .setCreatedAt(courseNotificationsPO.getCreatedAt())
                .setUpdatedAt(courseNotificationsPO.getUpdatedAt())
                .setNotificationTitle(courseNotificationsPO.getNotificationTitle())
                .setNotificationContent(courseNotificationsPO.getNotificationContent())
                .setAttachmentVOList(attachmentPOList)
                ;

        return SaResult.ok().setData(courseNotificationDetailInfoVO);
    }
}
