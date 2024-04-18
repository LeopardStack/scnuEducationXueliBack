package com.scnujxjy.backendpoint.service.courses_learning;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.constant.enums.course_learning.CourseAttachementsEnum;
import com.scnujxjy.backendpoint.dao.entity.courses_learning.CourseAssignmentSubmissionsPO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scnujxjy.backendpoint.dao.entity.courses_learning.CourseAssignmentsPO;
import com.scnujxjy.backendpoint.dao.entity.courses_learning.CoursesLearningPO;
import com.scnujxjy.backendpoint.dao.entity.platform_message.AttachmentPO;
import com.scnujxjy.backendpoint.dao.mapper.courses_learning.CourseAssignmentSubmissionsMapper;
import com.scnujxjy.backendpoint.model.ro.courses_learning.CourseAssignmentRO;
import com.scnujxjy.backendpoint.service.minio.MinioService;
import com.scnujxjy.backendpoint.service.platform_message.AttachmentService;
import com.scnujxjy.backendpoint.util.ResultCode;
import com.scnujxjy.backendpoint.util.tool.ScnuXueliTools;
import lombok.extern.slf4j.Slf4j;
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
 * 课程作业提交表 服务类
 * </p>
 *
 * @author 谢辉龙
 * @since 2024-04-15
 */
@Service
@Slf4j
public class CourseAssignmentSubmissionsService extends ServiceImpl<CourseAssignmentSubmissionsMapper, CourseAssignmentSubmissionsPO> implements IService<CourseAssignmentSubmissionsPO> {

    @Resource
    private CoursesLearningService coursesLearningService;

    @Resource
    private CourseAssignmentsService courseAssignmentsService;

    @Resource
    private MinioService minioService;

    @Resource
    private AttachmentService attachmentService;

    @Value("${minio.courseCoverDir}")
    private String courseBucket;

    /**
     * 上传课程作业
     * @param courseAssignmentRO
     * @return
     */
    @Transactional
    public SaResult postCourseAssignment(CourseAssignmentRO courseAssignmentRO) {
        Long courseId = courseAssignmentRO.getCourseId();
        if(courseId == null){
            return ResultCode.COURSE_POST_ASSIGNMENT_FAI1.generateErrorResultInfo();
        }
        CoursesLearningPO coursesLearningPO = coursesLearningService.getById(courseId);
        if(coursesLearningPO == null){
            return ResultCode.COURSE_POST_ASSIGNMENT_FAI2.generateErrorResultInfo();
        }
        if(courseAssignmentRO.getId() == null){
            return ResultCode.COURSE_POST_ASSIGNMENT_FAI3.generateErrorResultInfo();
        }

        CourseAssignmentsPO courseAssignmentsPO = courseAssignmentsService.getById(courseAssignmentRO.getId());
        if(courseAssignmentsPO == null){
            return ResultCode.COURSE_POST_ASSIGNMENT_FAI4.generateErrorResultInfo();
        }

        if(courseAssignmentRO.getPostAssignmentAttachments() == null || courseAssignmentRO.getPostAssignmentAttachments().isEmpty()){
            return ResultCode.COURSE_POST_ASSIGNMENT_FAI5.generateErrorResultInfo();
        }

        // 使用上述方法验证截止日期
        Date dueDate = courseAssignmentsPO.getDueDate();
        // 获取当前时间
        Date now = new Date();

        // 检查截止日期是否在当前时间之后
        if (dueDate == null) {

        } else if(dueDate.before(now)){
            return ResultCode.COURSE_POST_ASSIGNMENT_FAI9.generateErrorResultInfo();
        }
        // 检查是否 已经提交过作业了 如果是 只要没过期 可以覆盖写入
        CourseAssignmentSubmissionsPO courseAssignmentSubmissionsPO1 = getBaseMapper().selectOne(new LambdaQueryWrapper<CourseAssignmentSubmissionsPO>()
                .eq(CourseAssignmentSubmissionsPO::getCourseId, courseAssignmentRO.getCourseId())
                .eq(CourseAssignmentSubmissionsPO::getAssignmentId, courseAssignmentsPO.getId())
                .eq(CourseAssignmentSubmissionsPO::getUsername, StpUtil.getLoginIdAsString())
        );

        if(courseAssignmentSubmissionsPO1 != null && courseAssignmentSubmissionsPO1.getScore() != null){
            // 老师已打分 虽然没过截止时间 但不允许提交了
            return ResultCode.COURSE_POST_ASSIGNMENT_FAI17.generateErrorResultInfo();
        }

        if(courseAssignmentSubmissionsPO1 != null){
            // 如果不为空 就要删除覆盖
            List<Long> submissionAttachments = courseAssignmentSubmissionsPO1.getSubmissionAttachments();
            if(submissionAttachments != null && !submissionAttachments.isEmpty()){
                for(Long submissionAttachmentId : submissionAttachments){
                    AttachmentPO attachmentPO = attachmentService.getById(submissionAttachmentId);
                    String attachmentMinioPath = attachmentPO.getAttachmentMinioPath();
                    minioService.deleteFileByAbsolutePath(attachmentMinioPath);
                    int i = attachmentService.getBaseMapper().deleteById(attachmentPO.getId());
                    if(i <= 0){
                        return ResultCode.COURSE_POST_ASSIGNMENT_FAI15.generateErrorResultInfo();
                    }
                }
            }
            int i1 = getBaseMapper().deleteById(courseAssignmentSubmissionsPO1.getId());
            if(i1 <= 0){
                return ResultCode.COURSE_POST_ASSIGNMENT_FAI16.generateErrorResultInfo();
            }
        }


        CourseAssignmentSubmissionsPO courseAssignmentSubmissionsPO = new CourseAssignmentSubmissionsPO()
                .setCourseId(courseAssignmentRO.getCourseId())
                .setAssignmentId(courseAssignmentsPO.getId())
                .setUsername(StpUtil.getLoginIdAsString())
                ;
        int insert1 = getBaseMapper().insert(courseAssignmentSubmissionsPO);
        if(insert1 <= 0){
            return ResultCode.COURSE_POST_ASSIGNMENT_FAI6.generateErrorResultInfo();
        }


        List<MultipartFile> postAssignmentAttachments = courseAssignmentRO.getPostAssignmentAttachments();
        List<AttachmentPO> attachmentPOList = new ArrayList<>();
        int order = 1;
        for (MultipartFile multipartFile : postAssignmentAttachments) {
            log.info("收到了这些文件 " + multipartFile.getOriginalFilename());
            log.info("\n文件的信息 " + multipartFile.getContentType() + " " + multipartFile.getSize());
            try {
                String minioUrl = CourseAttachementsEnum.COURSE_POST_ASSIGNMENTS_ATTACHEMENTS.getAttachmentPrefix()
                        + "/" + coursesLearningPO.getCourseName() + "-"
                        + courseAssignmentsPO.getId() + "-" + courseAssignmentsPO.getAssignmentName()
                        + "/" + courseAssignmentSubmissionsPO.getUsername() + "-"
                        + new Date() + "-" + multipartFile.getOriginalFilename();
                try (InputStream is = multipartFile.getInputStream()) {
                    minioService.uploadStreamToMinio(is, minioUrl, courseBucket);
                } catch (IOException e) {
                    log.error("处理文件上传时出错", e);
                    return ResultCode.COURSE_POST_ASSIGNMENT_FAI7.generateErrorResultInfo();
                }
                AttachmentPO attachmentPO = new AttachmentPO()
                        .setAttachmentName(multipartFile.getOriginalFilename())
                        .setAttachmentMinioPath(courseBucket + "/" + minioUrl)
                        .setAttachmentSize(multipartFile.getSize())
                        .setAttachmentOrder(order)
                        .setRelatedId(courseAssignmentsPO.getId())
                        .setAttachmentType(ScnuXueliTools.getFileExtension(multipartFile))
                        .setUsername(StpUtil.getLoginIdAsString());
                int insert = attachmentService.getBaseMapper().insert(attachmentPO);
                if (insert <= 0) {
                    return ResultCode.DATABASE_INSERT_ERROR.generateErrorResultInfo();
                }
                attachmentPOList.add(attachmentPO);
                order += 1;
            } catch (Exception e) {
                log.error(StpUtil.getLoginIdAsString() + "上传课程作业附件失败 " + e);
                return ResultCode.COURSE_POST_ASSIGNMENT_FAI7.generateErrorResultInfo();
            }
        }

        courseAssignmentSubmissionsPO.setSubmissionAttachments(attachmentPOList.stream()
                .map(AttachmentPO::getId) // 将 AttachmentPO 转换为 Long (它的 id)
                .collect(Collectors.toList()));
        int i = getBaseMapper().updateById(courseAssignmentSubmissionsPO);
        if(i <= 0){
            return ResultCode.COURSE_POST_ASSIGNMENT_FAI8.generateErrorResultInfo();
        }

        return SaResult.ok("成功提交作业");
    }

    /**
     * 根据学生用户名和课程 ID、作业 ID 查询学生作业
     * @param courseAssignmentRO
     * @return
     */
    public SaResult queryCourseAssignmentSubmissionInfo(CourseAssignmentRO courseAssignmentRO) {
        String username = courseAssignmentRO.getUsername();
        Long courseAssignmentId = courseAssignmentRO.getId();
        Long courseId = courseAssignmentRO.getCourseId();

        CourseAssignmentSubmissionsPO courseAssignmentSubmissionsPO = getBaseMapper().selectOne(new LambdaQueryWrapper<CourseAssignmentSubmissionsPO>()
                .eq(CourseAssignmentSubmissionsPO::getCourseId, courseId)
                .eq(CourseAssignmentSubmissionsPO::getAssignmentId, courseAssignmentId)
                .eq(CourseAssignmentSubmissionsPO::getUsername, username)
        );
        if (courseAssignmentSubmissionsPO == null) {
            return ResultCode.COURSE_POST_ASSIGNMENT_FAI13.generateErrorResultInfo();
        }

        return SaResult.ok().setData(courseAssignmentSubmissionsPO);
    }

    /**
     * 作业打分
     * @param courseAssignmentRO
     * @return
     */
    public SaResult courseAssignmentMarking(CourseAssignmentRO courseAssignmentRO) {
        if(courseAssignmentRO.getAssignmentPostId() == null){
            return ResultCode.COURSE_POST_ASSIGNMENT_FAI10.generateErrorResultInfo();
        }

        Long assignmentPostId = courseAssignmentRO.getAssignmentPostId();
        CourseAssignmentSubmissionsPO courseAssignmentSubmissionsPO = getById(assignmentPostId);
        if(courseAssignmentSubmissionsPO == null){
            return ResultCode.COURSE_POST_ASSIGNMENT_FAI11.generateErrorResultInfo();
        }

        courseAssignmentSubmissionsPO.setScore(courseAssignmentRO.getScore());

        int i = getBaseMapper().updateById(courseAssignmentSubmissionsPO);
        if(i <= 0){
            return ResultCode.COURSE_POST_ASSIGNMENT_FAI12.generateErrorResultInfo();
        }

        return SaResult.ok("成功打分");
    }

    /**
     * 学生查询自己的作业打分情况
     * @param courseAssignmentRO
     * @return
     */
    public SaResult queryStudentCourseAssignmentMarking(CourseAssignmentRO courseAssignmentRO) {
        String username = StpUtil.getLoginIdAsString();
        Long courseAssignmentId = courseAssignmentRO.getId();
        Long courseId = courseAssignmentRO.getCourseId();

        CourseAssignmentSubmissionsPO courseAssignmentSubmissionsPO = getBaseMapper().selectOne(new LambdaQueryWrapper<CourseAssignmentSubmissionsPO>()
                .eq(CourseAssignmentSubmissionsPO::getCourseId, courseId)
                .eq(CourseAssignmentSubmissionsPO::getAssignmentId, courseAssignmentId)
                .eq(CourseAssignmentSubmissionsPO::getUsername, username)
        );
        if (courseAssignmentSubmissionsPO == null) {
            return ResultCode.COURSE_POST_ASSIGNMENT_FAI14.generateErrorResultInfo();
        }

        return SaResult.ok().setData(courseAssignmentSubmissionsPO);
    }
}
