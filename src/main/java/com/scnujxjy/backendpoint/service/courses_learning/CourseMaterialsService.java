package com.scnujxjy.backendpoint.service.courses_learning;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.constant.enums.course_learning.CourseAttachementsEnum;
import com.scnujxjy.backendpoint.dao.entity.courses_learning.CourseMaterialsPO;
import com.scnujxjy.backendpoint.dao.entity.courses_learning.CoursesLearningPO;
import com.scnujxjy.backendpoint.dao.entity.platform_message.AttachmentPO;
import com.scnujxjy.backendpoint.dao.mapper.courses_learning.CourseMaterialsMapper;
import com.scnujxjy.backendpoint.model.ro.courses_learning.CourseMaterialsPostRO;
import com.scnujxjy.backendpoint.service.minio.MinioService;
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

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 谢辉龙
 * @since 2024-03-05
 */
@Service
@Slf4j
public class CourseMaterialsService extends ServiceImpl<CourseMaterialsMapper, CourseMaterialsPO> implements IService<CourseMaterialsPO> {

    @Resource
    private CoursesLearningService coursesLearningService;

    @Resource
    private MinioService minioService;

    @Value("${minio.courseCoverDir}")
    private String courseBucket;

    /**
     * 上传课程资料
     * @param courseMaterialsPostRO
     * @return
     */
    @Transactional
    public SaResult postCourseMaterials(CourseMaterialsPostRO courseMaterialsPostRO) {
        Long courseId = courseMaterialsPostRO.getCourseId();
        if(courseId == null){
            return ResultCode.COURSE_POST_MATERIALS_FAI1.generateErrorResultInfo();
        }

        CoursesLearningPO coursesLearningPO = coursesLearningService.getById(courseId);
        if(coursesLearningPO == null){
            return ResultCode.COURSE_POST_MATERIALS_FAI2.generateErrorResultInfo();
        }

        if(courseMaterialsPostRO.getPostMaterials() == null || courseMaterialsPostRO.getPostMaterials().isEmpty()){
            return ResultCode.COURSE_POST_MATERIALS_FAI3.generateErrorResultInfo();
        }


        List<MultipartFile> postMaterials = courseMaterialsPostRO.getPostMaterials();
        List<CourseMaterialsPO> materialsList = new ArrayList<>(); // 创建列表以存储所有要插入的对象

        for (MultipartFile multipartFile : postMaterials) {
            log.info("收到了这些文件 " + multipartFile.getOriginalFilename());
            log.info("\n文件的信息 " + multipartFile.getContentType() + " " + multipartFile.getSize());
            try {
                String minioUrl = CourseAttachementsEnum.COURSE_MATERIALS_ATTACHEMENTS.getAttachmentPrefix()
                        + "/" + coursesLearningPO.getId() + "-" + coursesLearningPO.getCourseName() + "/"
                        + new Date() + "-" + multipartFile.getOriginalFilename();
                try (InputStream is = multipartFile.getInputStream()) {
                    minioService.uploadStreamToMinio(is, minioUrl, courseBucket);
                } catch (IOException e) {
                    log.error("处理文件上传时出错", e);
                    return ResultCode.COURSE_POST_MATERIALS_FAI4.generateErrorResultInfo();
                }

                CourseMaterialsPO courseMaterialsPO = new CourseMaterialsPO()
                        .setCourseId(courseId)
                        .setFileName(multipartFile.getOriginalFilename())
                        .setFileSize(multipartFile.getSize())
                        .setMinioStorageUrl(courseBucket + "/" + minioUrl)
                        .setUsername(StpUtil.getLoginIdAsString())
                        .setPermissionInfo(courseMaterialsPostRO.getPermissionInfo());

                materialsList.add(courseMaterialsPO); // 添加到列表中，而不是立即插入数据库

            } catch (Exception e) {
                log.error(StpUtil.getLoginIdAsString() + "上传课程作业附件失败 " + e);
                return ResultCode.COURSE_POST_MATERIALS_FAI4.generateErrorResultInfo();
            }
        }

        if (!materialsList.isEmpty()) {
            boolean result = this.saveBatch(materialsList); // 批量保存所有对象
            if (!result) {
                return ResultCode.COURSE_POST_MATERIALS_FAI5.generateErrorResultInfo();
            }
        }

        return SaResult.ok("添加课程资料成功");
    }

    /**
     * 查询课程资料
     * @param courseId
     * @return
     */
    public SaResult getCourseMaterials(Long courseId) {
        CoursesLearningPO coursesLearningPO = coursesLearningService.getById(courseId);
        if(coursesLearningPO == null){
            return ResultCode.COURSE_POST_MATERIALS_FAI6.generateErrorResultInfo();
        }

        List<CourseMaterialsPO> courseMaterialsPOS = getBaseMapper().selectList(new LambdaQueryWrapper<CourseMaterialsPO>()
                .eq(CourseMaterialsPO::getCourseId, courseId));
        return SaResult.ok().setData(courseMaterialsPOS);
    }

    /**
     * 删除课程资料
     * @param courseMaterialId
     * @return
     */
    public SaResult deleteCourseMaterial(Long courseMaterialId) {
        CourseMaterialsPO courseMaterialsPO = getBaseMapper().selectOne(new LambdaQueryWrapper<CourseMaterialsPO>()
                .eq(CourseMaterialsPO::getId, courseMaterialId));
        if (courseMaterialsPO == null) {
            return SaResult.ok("已删除，无需再删除");
        }

        minioService.deleteFileByAbsolutePath(courseMaterialsPO.getMinioStorageUrl());
        int i = getBaseMapper().deleteById(courseMaterialId);
        if(i <= 0){
            return ResultCode.COURSE_POST_MATERIALS_FAI7.generateErrorResultInfo();
        }

        return SaResult.ok("删除成功");
    }
}
