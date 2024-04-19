package com.scnujxjy.backendpoint.service.courses_learning;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.constant.enums.course_learning.CourseAttachementsEnum;
import com.scnujxjy.backendpoint.dao.entity.courses_learning.CourseAssignmentsPO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scnujxjy.backendpoint.dao.entity.courses_learning.CoursesLearningPO;
import com.scnujxjy.backendpoint.dao.entity.platform_message.AttachmentPO;
import com.scnujxjy.backendpoint.dao.mapper.courses_learning.CourseAssignmentsMapper;
import com.scnujxjy.backendpoint.model.ro.courses_learning.CourseAssignmentRO;
import com.scnujxjy.backendpoint.model.vo.course_learning.CourseAssignmentVO;
import com.scnujxjy.backendpoint.model.vo.platform_message.AttachmentVO;
import com.scnujxjy.backendpoint.service.minio.MinioService;
import com.scnujxjy.backendpoint.service.platform_message.AttachmentService;
import com.scnujxjy.backendpoint.util.ResultCode;
import com.scnujxjy.backendpoint.util.tool.ScnuXueliTools;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.utils.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
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
 * 课程作业表 服务类
 * </p>
 *
 * @author 谢辉龙
 * @since 2024-04-15
 */
@Service
@Slf4j
public class CourseAssignmentsService extends ServiceImpl<CourseAssignmentsMapper, CourseAssignmentsPO> implements IService<CourseAssignmentsPO> {

    @Resource
    private CoursesLearningService coursesLearningService;

    @Resource
    private MinioService minioService;

    @Value("${minio.courseCoverDir}")
    private String courseBucket;

    @Resource
    private AttachmentService attachmentService;

    public SaResult createCourseAssignment(CourseAssignmentRO courseAssignmentRO) {

        // 先校验课程 ID 是否真实存在
        Long courseId = courseAssignmentRO.getCourseId();
        CoursesLearningPO coursesLearningPO = null;
        if(courseId == null){
            return ResultCode.UPDATE_COURSE_FAI24.generateErrorResultInfo();
        }else{
            // 不为空检查课程信息是否存在
            coursesLearningPO = coursesLearningService.getBaseMapper().selectOne(new LambdaQueryWrapper<CoursesLearningPO>()
                    .eq(CoursesLearningPO::getId, courseAssignmentRO.getCourseId()));
            if(coursesLearningPO == null){
                return ResultCode.UPDATE_COURSE_FAI25.generateErrorResultInfo();
            }
        }

        if(StringUtils.isBlank(courseAssignmentRO.getAssignmentName())){
            return ResultCode.UPDATE_COURSE_FAI26.generateErrorResultInfo();
        }

        // 使用上述方法验证截止日期
        Date dueDate = courseAssignmentRO.getDueDate();
        // 获取当前时间
        Date now = new Date();

        // 检查截止日期是否在当前时间之后
        if (dueDate == null) {

        } else if(dueDate.before(now)){
            return ResultCode.UPDATE_COURSE_FAI27.generateErrorResultInfo();
        }

        // 创建课程作业对象
        CourseAssignmentsPO courseAssignmentsPO = new CourseAssignmentsPO()
                .setCourseId(courseId)
                .setAssignmentName(courseAssignmentRO.getAssignmentName())
                .setAssignmentDescription(courseAssignmentRO.getAssignmentDescription())
                .setDueDate(courseAssignmentRO.getDueDate())
                ;
        int insert1 = getBaseMapper().insert(courseAssignmentsPO);
        if(insert1 <= 0){
            return ResultCode.UPDATE_COURSE_FAI28.generateErrorResultInfo();
        }

        if(courseAssignmentRO.getAssignmentAttachments() != null &&
                !courseAssignmentRO.getAssignmentAttachments().isEmpty()) {
            List<AttachmentPO> attachmentPOList = new ArrayList<>();
            int order = 1;
            for (MultipartFile multipartFile : courseAssignmentRO.getAssignmentAttachments()) {
                log.info("收到了这些文件 " + multipartFile.getOriginalFilename());
                log.info("\n文件的信息 " + multipartFile.getContentType() + " " + multipartFile.getSize());
                try {
                    String minioUrl = CourseAttachementsEnum.COURSE_ASSIGNMENTS_ATTACHEMENTS.getAttachmentPrefix()
                    + "/" + courseAssignmentsPO.getId() + "-"
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
                    return ResultCode.UPDATE_COURSE_FAI13.generateErrorResultInfo();
                }
            }

            // 将附件插入到 Minio 和数据库中后 才开始 更新课程公告的 附件列表信息
            courseAssignmentsPO.setAssignmentAttachments(attachmentPOList.stream()
                    .map(AttachmentPO::getId) // 将 AttachmentPO 转换为 Long (它的 id)
                    .collect(Collectors.toList()));
            int i = getBaseMapper().updateById(courseAssignmentsPO);
            if (i <= 0) {
                return ResultCode.UPDATE_COURSE_FAI15.generateErrorResultInfo();
            }
        }

        log.info("拿到了作业数据 \n" + courseAssignmentRO.getAssignmentAttachments());
        return SaResult.ok("创建作业成功");
    }

    /**
     * 删除课程作业
     * @param courseAssignmentId
     * @return
     */
    public SaResult deleteCourseAssignment(Long courseAssignmentId) {
        CourseAssignmentsPO courseAssignmentsPO = getBaseMapper().selectOne(new LambdaQueryWrapper<CourseAssignmentsPO>()
                .eq(CourseAssignmentsPO::getId, courseAssignmentId));
        if(courseAssignmentsPO == null){
            return SaResult.ok("已删除，无需再删除");
        }

        List<Long> assignmentAttachments = courseAssignmentsPO.getAssignmentAttachments();
        for(Long assignmentAttachId : assignmentAttachments){
            AttachmentPO attachmentPO = attachmentService.getById(assignmentAttachId);
            String attachmentMinioPath = attachmentPO.getAttachmentMinioPath();
            minioService.deleteFileByAbsolutePath(attachmentMinioPath);
            int i = attachmentService.getBaseMapper().deleteById(assignmentAttachId);
            if(i <= 0){
                return ResultCode.UPDATE_COURSE_FAI29.generateErrorResultInfo();
            }
        }

        // 还缺少删除学生对这门作业的提交
        int i = getBaseMapper().deleteById(courseAssignmentId);
        if(i <= 0 ){
            return ResultCode.UPDATE_COURSE_FAI30.generateErrorResultInfo();
        }

        return SaResult.ok("删除课程作业成功");
    }

    /**
     * 编辑课程作业
     * @param courseAssignmentRO
     * @return
     */
    public SaResult editCourseAssignment(CourseAssignmentRO courseAssignmentRO) {
        // 先校验课程 ID 是否真实存在
        Long courseId = courseAssignmentRO.getCourseId();
        CoursesLearningPO coursesLearningPO = null;
        if(courseId == null){
            return ResultCode.UPDATE_COURSE_FAI31.generateErrorResultInfo();
        }else{
            // 不为空检查课程信息是否存在
            coursesLearningPO = coursesLearningService.getBaseMapper().selectOne(new LambdaQueryWrapper<CoursesLearningPO>()
                    .eq(CoursesLearningPO::getId, courseAssignmentRO.getCourseId()));
            if(coursesLearningPO == null){
                return ResultCode.UPDATE_COURSE_FAI32.generateErrorResultInfo();
            }
        }

        if(StringUtils.isBlank(courseAssignmentRO.getAssignmentName())){
            return ResultCode.UPDATE_COURSE_FAI33.generateErrorResultInfo();
        }

        // 使用上述方法验证截止日期
        Date dueDate = courseAssignmentRO.getDueDate();
        // 获取当前时间
        Date now = new Date();

        // 检查截止日期是否在当前时间之后
        if (dueDate == null) {

        } else if(dueDate.before(now)){
            return ResultCode.UPDATE_COURSE_FAI34.generateErrorResultInfo();
        }

        // 获取原来的作业对象 然后 把不同的地方进行修改
        CourseAssignmentsPO courseAssignmentsPO = getBaseMapper().selectById(courseAssignmentRO.getId());
        if(courseAssignmentsPO == null){
            return ResultCode.UPDATE_COURSE_FAI41.generateErrorResultInfo();
        }

        if(!courseAssignmentRO.getAssignmentName().equals(courseAssignmentsPO.getAssignmentName())){
            courseAssignmentsPO.setAssignmentName(courseAssignmentRO.getAssignmentName());
        }

        if(!courseAssignmentRO.getAssignmentDescription().equals(courseAssignmentsPO.getAssignmentDescription())){
            courseAssignmentsPO.setAssignmentDescription(courseAssignmentRO.getAssignmentDescription());
        }

        if(!courseAssignmentRO.getDueDate().equals(courseAssignmentsPO.getDueDate()))
        {
            courseAssignmentsPO.setDueDate(courseAssignmentRO.getDueDate());
        }
        int update = getBaseMapper().updateById(courseAssignmentsPO);
        if(update <= 0){
            return ResultCode.UPDATE_COURSE_FAI36.generateErrorResultInfo();
        }

        // 在更新课程 作业附件时 需要匹配两个文件是否相同

        List<Long> assignmentAttachments1 = courseAssignmentsPO.getAssignmentAttachments();
        for(Long assignmentAttachmentId : assignmentAttachments1){
            AttachmentPO attachmentPO = attachmentService.getById(assignmentAttachmentId);
            minioService.deleteFileByAbsolutePath(attachmentPO.getAttachmentMinioPath());
            int i = attachmentService.getBaseMapper().deleteById(attachmentPO);
            if(i <= 0){
                return ResultCode.UPDATE_COURSE_FAI42.generateErrorResultInfo();
            }
        }

        // 简单点实现 就是 先看该文件是否在 Minio 中存在  如果存在则不需要删除

        if(courseAssignmentRO.getAssignmentAttachments() != null &&
                !courseAssignmentRO.getAssignmentAttachments().isEmpty()) {
            List<AttachmentPO> attachmentPOList = new ArrayList<>();
            int order = 1;
            for (MultipartFile multipartFile : courseAssignmentRO.getAssignmentAttachments()) {
                log.info("收到了这些文件 " + multipartFile.getOriginalFilename());
                log.info("\n文件的信息 " + multipartFile.getContentType() + " " + multipartFile.getSize());
                try {
                    String minioUrl = CourseAttachementsEnum.COURSE_ASSIGNMENTS_ATTACHEMENTS.getAttachmentPrefix()
                            + "/" + courseAssignmentRO.getId() + "-"
                            + coursesLearningPO.getCourseName() + "/"
                            + new Date() + "-" + multipartFile.getOriginalFilename();
                    try (InputStream is = multipartFile.getInputStream()) {
                        minioService.uploadStreamToMinio(is, minioUrl, courseBucket);
                    } catch (IOException e) {
                        log.error("处理文件上传时出错", e);
                        return ResultCode.UPDATE_COURSE_FAI37.generateErrorResultInfo();
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
                        return ResultCode.UPDATE_COURSE_FAI38.generateErrorResultInfo();
                    }
                    attachmentPOList.add(attachmentPO);
                    order += 1;
                } catch (Exception e) {
                    log.error(StpUtil.getLoginIdAsString() + "上传课程作业附件失败 " + e);
                    return ResultCode.UPDATE_COURSE_FAI39.generateErrorResultInfo();
                }
            }

            // 将附件插入到 Minio 和数据库中后 才开始 更新课程公告的 附件列表信息
            courseAssignmentsPO.setAssignmentAttachments(attachmentPOList.stream()
                    .map(AttachmentPO::getId) // 将 AttachmentPO 转换为 Long (它的 id)
                    .collect(Collectors.toList()));
            int i = getBaseMapper().updateById(courseAssignmentsPO);
            if (i <= 0) {
                return ResultCode.UPDATE_COURSE_FAI40.generateErrorResultInfo();
            }
        }

        log.info("更新了作业数据 \n" + courseAssignmentRO.getAssignmentAttachments());
        return SaResult.ok("更新作业成功");
    }

    /**
     * 查询课程作业
     * @param courseAssignmentRO
     * @return
     */
    public SaResult queryCourseAssignment(CourseAssignmentRO courseAssignmentRO) {
        Long courseId = courseAssignmentRO.getCourseId();
        if(courseId == null){
            return ResultCode.UPDATE_COURSE_FAI43.generateErrorResultInfo();
        }
        CoursesLearningPO coursesLearningPO = coursesLearningService.getBaseMapper().selectById(courseId);
        if (coursesLearningPO == null) {
            return ResultCode.UPDATE_COURSE_FAI42.generateErrorResultInfo();
        }
        List<CourseAssignmentsPO> courseAssignmentsPOS = getBaseMapper().selectList(new LambdaQueryWrapper<CourseAssignmentsPO>()
                .eq(CourseAssignmentsPO::getCourseId, courseId));

        List<CourseAssignmentVO> courseAssignmentVOList = new ArrayList<>();

        for(CourseAssignmentsPO courseAssignmentsPO : courseAssignmentsPOS){
            CourseAssignmentVO courseAssignmentVO = new CourseAssignmentVO()
                    .setId(courseAssignmentsPO.getId())
                    .setCourseId(courseId)
                    .setAssignmentName(courseAssignmentsPO.getAssignmentName())
                    .setAssignmentDescription(courseAssignmentsPO.getAssignmentDescription())
                    .setDueDate(courseAssignmentsPO.getDueDate())
                    .setAttachmentVOList(new ArrayList<>())
                    ;
            List<Long> assignmentAttachments = courseAssignmentsPO.getAssignmentAttachments();
            if(assignmentAttachments != null && !assignmentAttachments.isEmpty()){
                for(Long assignmentId : assignmentAttachments){
                    AttachmentPO attachmentPO = attachmentService.getById(assignmentId);
                    String minioViewPath = minioService.generatePresignedUrl(attachmentPO.getAttachmentMinioPath());
                    AttachmentVO attachmentVO = new AttachmentVO();
                    BeanUtils.copyProperties(attachmentPO, attachmentVO);
                    attachmentVO.setAttachmentMinioViewPath(minioViewPath);
                    courseAssignmentVO.getAttachmentVOList().add(attachmentVO);
                }
            }
            courseAssignmentVOList.add(courseAssignmentVO);
        }
        return SaResult.ok().setData(courseAssignmentVOList);
    }

    /**
     * 查询课程的作业总数
     * @param courseAssignmentRO
     * @return
     */
    public SaResult queryCourseAssignmentTotalInfo(CourseAssignmentRO courseAssignmentRO) {
        List<CourseAssignmentsPO> courseAssignmentsPOS = getBaseMapper().selectList(new LambdaQueryWrapper<CourseAssignmentsPO>()
                .eq(CourseAssignmentsPO::getCourseId, courseAssignmentRO.getCourseId()));
        return SaResult.ok().setData(courseAssignmentsPOS);
    }
}
