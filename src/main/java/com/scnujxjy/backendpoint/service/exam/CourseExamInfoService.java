package com.scnujxjy.backendpoint.service.exam;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.constant.enums.CourseContentType;
import com.scnujxjy.backendpoint.dao.entity.college.CollegeInformationPO;
import com.scnujxjy.backendpoint.dao.entity.core_data.TeacherInformationPO;
import com.scnujxjy.backendpoint.dao.entity.courses_learning.CourseAssistantsPO;
import com.scnujxjy.backendpoint.dao.entity.exam.CourseExamAssistantsPO;
import com.scnujxjy.backendpoint.dao.entity.exam.CourseExamInfoPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseInformationPO;
import com.scnujxjy.backendpoint.dao.mapper.core_data.TeacherInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.exam.CourseExamAssistantsMapper;
import com.scnujxjy.backendpoint.dao.mapper.exam.CourseExamInfoMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseInformationMapper;
import com.scnujxjy.backendpoint.model.ro.courses_learning.CourseClassMappingRO;
import com.scnujxjy.backendpoint.model.ro.exam.BatchSetTeachersInfoRO;
import com.scnujxjy.backendpoint.model.ro.exam.SingleSetTeachersInfoRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseInformationRO;
import com.scnujxjy.backendpoint.model.vo.course_learning.CoursesInfoByClassMappingVO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.CourseInformationVO;
import com.scnujxjy.backendpoint.service.InterBase.OldDataSynchronize;
import com.scnujxjy.backendpoint.service.courses_learning.CourseAssistantsService;
import com.scnujxjy.backendpoint.service.courses_learning.CoursesClassMappingService;
import com.scnujxjy.backendpoint.util.ResultCode;
import com.scnujxjy.backendpoint.util.tool.ScnuXueliTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import static com.scnujxjy.backendpoint.constant.enums.RoleEnum.*;

/**
 * <p>
 * 存储考试信息 服务类
 * </p>
 *
 * @author 谢辉龙
 * @since 2023-11-15
 */
@Slf4j
@Service
public class CourseExamInfoService extends ServiceImpl<CourseExamInfoMapper, CourseExamInfoPO> implements IService<CourseExamInfoPO>  {

    @Resource
    private CourseExamInfoMapper courseExamInfoMapper;

    @Resource
    private CourseExamAssistantsMapper courseExamAssistantsMapper;

    @Resource
    private TeacherInformationMapper teacherInformationMapper;

    @Resource
    private CourseInformationMapper courseInformationMapper;

    @Resource
    private ScnuXueliTools scnuXueliTools;

    @Resource
    private CourseAssistantsService courseAssistantsService;

    @Resource
    private CoursesClassMappingService coursesClassMappingService;

    @Resource
    private OldDataSynchronize oldDataSynchronize;


    /**
     * 单个设置一条教学计划的考试方式
     * @param id
     */
    public boolean singleSetExamType(Long id) {
        CourseInformationPO courseInformationPO = courseInformationMapper.selectOne(new LambdaQueryWrapper<CourseInformationPO>()
                .eq(CourseInformationPO::getId, id));
        log.info("获取的教学计划为 " + courseInformationPO);
        CourseExamInfoPO courseExamInfoPO = courseExamInfoMapper.selectOne(new LambdaQueryWrapper<CourseExamInfoPO>()
                .eq(CourseExamInfoPO::getClassIdentifier, courseInformationPO.getAdminClass())
                .eq(CourseExamInfoPO::getCourse, courseInformationPO.getCourseName())
        );
        courseExamInfoPO.setExamType("开卷".equals(courseExamInfoPO.getExamType()) ? "闭卷" : "开卷");
        int i = courseExamInfoMapper.updateById(courseExamInfoPO);
        if(i > 0){
            return true;
        }
        return false;
    }

    /**
     * 单个设置一条考试信息记录为 闭卷
     * @param id
     * @return
     */
    public boolean singleSetExamMethod(Long id) {
        CourseInformationPO courseInformationPO = courseInformationMapper.selectOne(new LambdaQueryWrapper<CourseInformationPO>()
                .eq(CourseInformationPO::getId, id));
        log.info("获取的教学计划为 " + courseInformationPO);
        CourseExamInfoPO courseExamInfoPO = courseExamInfoMapper.selectOne(new LambdaQueryWrapper<CourseExamInfoPO>()
                .eq(CourseExamInfoPO::getClassIdentifier, courseInformationPO.getAdminClass())
                .eq(CourseExamInfoPO::getCourse, courseInformationPO.getCourseName())
        );
        courseExamInfoPO.setExamMethod("机考".equals(courseExamInfoPO.getExamMethod()) ? "线下" : "机考");
        int i = courseExamInfoMapper.updateById(courseExamInfoPO);
        if(i > 0){
            return true;
        }
        return false;
    }

    public boolean batchSetExamType(BatchSetTeachersInfoRO batchSetTeachersInfoRO) {
        List<String> roleList = StpUtil.getRoleList();

        if (roleList.isEmpty()) {
            log.error(ResultCode.ROLE_INFO_FAIL1.getMessage());
            return false;
        } else {
            if (roleList.contains(SECOND_COLLEGE_ADMIN.getRoleName())) {
                // 二级学院管理员
                CollegeInformationPO userBelongCollege = scnuXueliTools.getUserBelongCollege();
                batchSetTeachersInfoRO.setCollege(userBelongCollege.getCollegeName());

            } else if (roleList.contains(XUELIJIAOYUBU_ADMIN.getRoleName()) || roleList.contains(CAIWUBU_ADMIN.getRoleName())) {
                // 继续教育学院管理员
            }
        }
        log.info("考试信息筛选参数为 " + batchSetTeachersInfoRO);
        int count = 0;
        List<CourseExamInfoPO> courseExamInfoPOs = courseExamInfoMapper.batchSelectData(batchSetTeachersInfoRO);
        for(CourseExamInfoPO examDataBO: courseExamInfoPOs){
            Long examId = examDataBO.getId();
            CourseExamInfoPO courseExamInfoPO = courseExamInfoMapper.selectOne(new LambdaQueryWrapper<CourseExamInfoPO>()
                    .eq(CourseExamInfoPO::getId, examId));
            courseExamInfoPO.setExamType("闭卷");
            int i = courseExamInfoMapper.updateById(courseExamInfoPO);
            if(i <= 0){
                log.error("更新失败 " + courseExamInfoPO);
            }else{
                count += 1;
            }
        }

        log.info("批量获取到了所有的教学计划所对应的考试信息表 " + courseExamInfoPOs.size() + " 条 成功了 " + count + " 条");

        return true;
    }

    /**
     * 批量设置闭卷
     * @param batchSetTeachersInfoRO
     * @return
     */
    public boolean batchSetExamMethod(BatchSetTeachersInfoRO batchSetTeachersInfoRO) {
        List<String> roleList = StpUtil.getRoleList();

        if (roleList.isEmpty()) {
            log.error(ResultCode.ROLE_INFO_FAIL1.getMessage());
            return false;
        } else {
            if (roleList.contains(SECOND_COLLEGE_ADMIN.getRoleName())) {
                // 二级学院管理员
                CollegeInformationPO userBelongCollege = scnuXueliTools.getUserBelongCollege();
                batchSetTeachersInfoRO.setCollege(userBelongCollege.getCollegeName());

            } else if (roleList.contains(XUELIJIAOYUBU_ADMIN.getRoleName()) || roleList.contains(CAIWUBU_ADMIN.getRoleName())) {
                // 继续教育学院管理员
            }
        }
        log.info("考试信息筛选参数为 " + batchSetTeachersInfoRO);
        int count = 0;
        List<CourseExamInfoPO> courseExamInfoPOs = courseExamInfoMapper.batchSelectData(batchSetTeachersInfoRO);
        for(CourseExamInfoPO examDataBO: courseExamInfoPOs){
            Long examId = examDataBO.getId();
            CourseExamInfoPO courseExamInfoPO = courseExamInfoMapper.selectOne(new LambdaQueryWrapper<CourseExamInfoPO>()
                    .eq(CourseExamInfoPO::getId, examId));
            courseExamInfoPO.setExamMethod("机考");
            int i = courseExamInfoMapper.updateById(courseExamInfoPO);
            if(i <= 0){
                log.error("更新失败 " + courseExamInfoPO);
            }else{
                count += 1;
            }
        }

        log.info("批量获取到了所有的教学计划所对应的考试信息表 " + courseExamInfoPOs.size() + " 条 成功了 " + count + " 条");

        return true;
    }

    /**
     * 批量取消闭卷
     * @param batchSetTeachersInfoRO
     * @return
     */
    public boolean batchUnSetExamType(BatchSetTeachersInfoRO batchSetTeachersInfoRO) {
        List<String> roleList = StpUtil.getRoleList();

        // 获取访问者 ID
        String userId = (String) StpUtil.getLoginId();

        if (roleList.isEmpty()) {
            log.error(ResultCode.ROLE_INFO_FAIL1.getMessage());
            return false;
        } else {
            if (roleList.contains(SECOND_COLLEGE_ADMIN.getRoleName())) {
                // 二级学院管理员
                CollegeInformationPO userBelongCollege = scnuXueliTools.getUserBelongCollege();
                batchSetTeachersInfoRO.setCollege(userBelongCollege.getCollegeName());

            } else if (roleList.contains(XUELIJIAOYUBU_ADMIN.getRoleName()) || roleList.contains(CAIWUBU_ADMIN.getRoleName())) {
                // 继续教育学院管理员
            }
        }
        log.info("考试信息筛选参数为 " + batchSetTeachersInfoRO);
        int count = 0;
        List<CourseExamInfoPO> courseExamInfoPOs = courseExamInfoMapper.batchSelectData(batchSetTeachersInfoRO);
        for(CourseExamInfoPO examDataBO: courseExamInfoPOs){
            Long examId = examDataBO.getId();
            CourseExamInfoPO courseExamInfoPO = courseExamInfoMapper.selectOne(new LambdaQueryWrapper<CourseExamInfoPO>()
                    .eq(CourseExamInfoPO::getId, examId));
            courseExamInfoPO.setExamType("开卷");
            int i = courseExamInfoMapper.updateById(courseExamInfoPO);
            if(i <= 0){
                log.error("更新失败 " + courseExamInfoPO);
            }else{
                count += 1;
            }
        }
        log.info("批量获取到了所有的教学计划所对应的考试信息表 " + courseExamInfoPOs.size() + " 条 成功了 " + count + " 条");
        return true;
    }

    /**
     * 批量取消机考
     * @param batchSetTeachersInfoRO
     * @return
     */
    public boolean batchUnSetExamMethod(BatchSetTeachersInfoRO batchSetTeachersInfoRO) {
        List<String> roleList = StpUtil.getRoleList();

        // 获取访问者 ID
        String userId = (String) StpUtil.getLoginId();

        if (roleList.isEmpty()) {
            log.error(ResultCode.ROLE_INFO_FAIL1.getMessage());
            return false;
        } else {
            if (roleList.contains(SECOND_COLLEGE_ADMIN.getRoleName())) {
                // 二级学院管理员
                CollegeInformationPO userBelongCollege = scnuXueliTools.getUserBelongCollege();
                batchSetTeachersInfoRO.setCollege(userBelongCollege.getCollegeName());

            } else if (roleList.contains(XUELIJIAOYUBU_ADMIN.getRoleName()) || roleList.contains(CAIWUBU_ADMIN.getRoleName())) {
                // 继续教育学院管理员
            }
        }
        log.info("考试信息筛选参数为 " + batchSetTeachersInfoRO);
        int count = 0;
        List<CourseExamInfoPO> courseExamInfoPOs = courseExamInfoMapper.batchSelectData(batchSetTeachersInfoRO);
        for(CourseExamInfoPO examDataBO: courseExamInfoPOs){
            Long examId = examDataBO.getId();
            CourseExamInfoPO courseExamInfoPO = courseExamInfoMapper.selectOne(new LambdaQueryWrapper<CourseExamInfoPO>()
                    .eq(CourseExamInfoPO::getId, examId));
            courseExamInfoPO.setExamMethod("线下");
            int i = courseExamInfoMapper.updateById(courseExamInfoPO);
            if(i <= 0){
                log.error("更新失败 " + courseExamInfoPO);
            }else{
                count += 1;
            }
        }
        log.info("批量获取到了所有的教学计划所对应的考试信息表 " + courseExamInfoPOs.size() + " 条 成功了 " + count + " 条");
        return true;
    }

    /**
     * 单个设置主讲教师和助教信息
     * @param entity
     * @return
     */
    public boolean singleSetTeachers(SingleSetTeachersInfoRO entity) {
        Long examId = Long.valueOf(entity.getId());
        CourseExamInfoPO courseExamInfoPO = courseExamInfoMapper.selectOne(new LambdaQueryWrapper<CourseExamInfoPO>()
                .eq(CourseExamInfoPO::getId, examId));
        String mainTeacher1 = entity.getMainTeacher();
        if(mainTeacher1 == null || mainTeacher1.trim().isEmpty()){
        }else{
            int mainTeacher = Integer.parseInt(mainTeacher1);
            TeacherInformationPO teacherInformationPO = teacherInformationMapper.selectOne(new LambdaQueryWrapper<TeacherInformationPO>()
                    .eq(TeacherInformationPO::getUserId, mainTeacher));
            courseExamInfoPO.setMainTeacher(teacherInformationPO.getName()) ;
            courseExamInfoPO.setTeacherUsername(teacherInformationPO.getTeacherUsername());
            int i = courseExamInfoMapper.updateById(courseExamInfoPO);
            if(i > 0){
                log.info("更新考试信息主讲教师成功 !");
            }else{
                log.error("更新考试信息主讲教师以失败 !" + courseExamInfoPO + "\n更新结果 " + i);
                return false;
            }

        }

        if(entity.getAssistants() != null && entity.getAssistants().size() > 0){
            int delete = courseExamAssistantsMapper.delete(new LambdaQueryWrapper<CourseExamAssistantsPO>()
                    .eq(CourseExamAssistantsPO::getCourseId, courseExamInfoPO.getId()));
            log.info("删除指定教学计划所对应的考试计划的所有助教信息 " + delete);
        }
        int count = 0;
        for(String tutorId: entity.getAssistants()){
            int userId = Integer.parseInt(tutorId);
            TeacherInformationPO teacherInformationPO = teacherInformationMapper.selectOne(new LambdaQueryWrapper<TeacherInformationPO>()
                    .eq(TeacherInformationPO::getUserId, userId));
            CourseExamAssistantsPO courseExamAssistantsPO = new CourseExamAssistantsPO();
            courseExamAssistantsPO.setCourseId(courseExamInfoPO.getId());
            courseExamAssistantsPO.setAssistantName(teacherInformationPO.getName());
            courseExamAssistantsPO.setTeacherUsername(teacherInformationPO.getTeacherUsername());
            int insert = courseExamAssistantsMapper.insert(courseExamAssistantsPO);
            if(insert > 0){
                count += 1;
            }
        }
        if(count > 0){
            log.info(courseExamInfoPO + "插入更新 " + count + " 个助教");
        }
        return true;
    }

    /**
     * 单个清除某一个考试的命题人和阅卷助教信息
     * @param entity
     * @return
     */
    public int singleDeleteTeachers(SingleSetTeachersInfoRO entity) {
        Long examId = Long.valueOf(entity.getId());
        CourseExamInfoPO courseExamInfoPO = courseExamInfoMapper.selectOne(new LambdaQueryWrapper<CourseExamInfoPO>()
                .eq(CourseExamInfoPO::getId, examId));
        UpdateWrapper<CourseExamInfoPO> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("main_teacher", null)
                .eq("id", courseExamInfoPO.getId());
        updateWrapper.set("teacher_username", null)
                .eq("id", courseExamInfoPO.getId());
        int i = courseExamInfoMapper.update(null, updateWrapper);
        int delete1 = 0;
        if(i > 0){
            delete1 += 1;
        }

        int delete2 = 0;
        int delete_ = courseExamAssistantsMapper.delete(new LambdaQueryWrapper<CourseExamAssistantsPO>()
                .eq(CourseExamAssistantsPO::getCourseId, courseExamInfoPO.getId()));
        if(delete_ > 0){
            delete2 += delete_;
        }

        return delete1 + delete2;
    }

    /**
     * 批量设置命题人和阅卷人
     * @param batchSetTeachersInfoRO
     * @return
     */
    public boolean batchSetTeachers(BatchSetTeachersInfoRO batchSetTeachersInfoRO) {
        List<CourseExamInfoPO> courseExamInfoPOs = courseExamInfoMapper.batchSelectData(batchSetTeachersInfoRO);
        // 获取到所有的考试信息后 开始更新 主讲和助教信息
        for(CourseExamInfoPO courseExamInfoPO: courseExamInfoPOs){
            String mainTeacher1 = batchSetTeachersInfoRO.getMainTeacher();
            if(mainTeacher1 == null || mainTeacher1.trim().isEmpty()){
                if(batchSetTeachersInfoRO.getClearAllTeachers()){
                    // 清除主讲老师信息
                    UpdateWrapper<CourseExamInfoPO> updateWrapper = new UpdateWrapper<>();
                    updateWrapper.set("main_teacher", null)
                            .eq("id", courseExamInfoPO.getId());
                    updateWrapper.set("teacher_username", null)
                            .eq("id", courseExamInfoPO.getId());
                    int i = courseExamInfoMapper.update(null, updateWrapper);

                }
            } else{
                int mainTeacher = Integer.parseInt(mainTeacher1);
                TeacherInformationPO teacherInformationPO = teacherInformationMapper.selectOne(new LambdaQueryWrapper<TeacherInformationPO>()
                        .eq(TeacherInformationPO::getUserId, mainTeacher));
                courseExamInfoPO.setMainTeacher(teacherInformationPO.getName()) ;
                courseExamInfoPO.setTeacherUsername(teacherInformationPO.getTeacherUsername());
                int i = courseExamInfoMapper.updateById(courseExamInfoPO);
                if(i > 0){
                    log.info("更新考试信息主讲教师成功 !");
                }else{
                    log.error("更新考试信息主讲教师以失败 !" + courseExamInfoPO + "\n更新结果 " + i);
                    return false;
                }

            }

            if(batchSetTeachersInfoRO.getAssistants() != null && !batchSetTeachersInfoRO.getAssistants().isEmpty()){
                int delete = courseExamAssistantsMapper.delete(new LambdaQueryWrapper<CourseExamAssistantsPO>()
                        .eq(CourseExamAssistantsPO::getCourseId, courseExamInfoPO.getId()));
                log.info("删除指定教学计划所对应的考试计划的所有助教信息 " + delete);
            }
            int count = 0;
            if(batchSetTeachersInfoRO.getAssistants() != null && batchSetTeachersInfoRO.getAssistants().size() != 0) {
                for (String tutorId : batchSetTeachersInfoRO.getAssistants()) {
                    int userId = Integer.parseInt(tutorId);
                    TeacherInformationPO teacherInformationPO = teacherInformationMapper.selectOne(new LambdaQueryWrapper<TeacherInformationPO>()
                            .eq(TeacherInformationPO::getUserId, userId));
                    CourseExamAssistantsPO courseExamAssistantsPO = new CourseExamAssistantsPO();
                    courseExamAssistantsPO.setCourseId(courseExamInfoPO.getId());
                    courseExamAssistantsPO.setAssistantName(teacherInformationPO.getName());
                    courseExamAssistantsPO.setTeacherUsername(teacherInformationPO.getTeacherUsername());
                    int insert = courseExamAssistantsMapper.insert(courseExamAssistantsPO);
                    if (insert > 0) {
                        count += 1;
                    }
                }
                if (count > 0) {
                    log.info(courseExamInfoPO + "插入更新 " + count + " 个助教");
                }
            }else{
                if(batchSetTeachersInfoRO.getClearAllTeachers()){
                    // 清除阅卷助教老师信息
                    int delete_ = courseExamAssistantsMapper.delete(new LambdaQueryWrapper<CourseExamAssistantsPO>()
                            .eq(CourseExamAssistantsPO::getCourseId, courseExamInfoPO.getId()));

                }
            }
        }
        log.info("批量修改主讲和助教信息 \n" + courseExamInfoPOs);
        return false;
    }

    @Async
    public void batchUpdateExamInfo() {
        getBaseMapper().truncateTableInfo();
        courseExamAssistantsMapper.truncateTableInfo();
        log.info("已清除机考旧信息记录");

        for(int grade = 2024; grade >= 2020; grade --) {
            List<CourseInformationVO> courseInformationVOList = courseInformationMapper.
                    selectCourseInformationWithClassInfo(new CourseInformationRO().setGrade(""+ grade));

            for(CourseInformationVO courseInformationVO : courseInformationVOList){
                CourseExamInfoPO courseExamInfoPO = new CourseExamInfoPO()
                        .setClassName(courseInformationVO.getClassName())
                        .setClassIdentifier(courseInformationVO.getAdminClass())
                        .setCourse(courseInformationVO.getCourseName())
                        .setExamMethod("线下")    // 默认值
                        .setExamType("闭卷")  // 默认值
                        .setIsValid("Y")   // 默认有效
                        .setExamStatus("未开始")   // 默认有效
                        .setTeachingSemester(courseInformationVO.getTeachingSemester())
                        ;
                // 查看课程学习表 看是否有主讲信息、助教信息 有的话 直接写入
                CoursesInfoByClassMappingVO coursesInfoByClassMappingVO = null;
                List<CoursesInfoByClassMappingVO> coursesInfoByClassMappingVOS = coursesClassMappingService.getBaseMapper()
                        .selectCoursesInfo(new CourseClassMappingRO()
                                .setCourseName(courseInformationVO.getCourseName())
                                .setClassIdentifier(courseInformationVO.getAdminClass())
                        );
                if(!coursesInfoByClassMappingVOS.isEmpty()){
                    if(coursesInfoByClassMappingVOS.size() > 1){
                        log.error("该门教学计划存在多个课程学习记录 "+ courseInformationVO);
                    }
                    coursesInfoByClassMappingVO = coursesInfoByClassMappingVOS.get(0);
                }

                // 设置主讲教师信息
                if (coursesInfoByClassMappingVO != null) {

                    if(!coursesInfoByClassMappingVO.getCourseType().equals(CourseContentType.LIVING.getContentType())){
                        continue;
                    }
                    courseExamInfoPO.setCourseId(coursesInfoByClassMappingVO.getCourseId());
                    courseExamInfoPO.setExamMethod("机考");

                    String defaultMainTeacherUsername = coursesInfoByClassMappingVO.getDefaultMainTeacherUsername();
                    TeacherInformationPO teacherInformationPO = teacherInformationMapper.selectOne(new LambdaQueryWrapper<TeacherInformationPO>()
                            .eq(TeacherInformationPO::getTeacherUsername, defaultMainTeacherUsername));
                    if(teacherInformationPO != null){
                        courseExamInfoPO.setTeacherUsername(teacherInformationPO.getTeacherUsername());
                        courseExamInfoPO.setMainTeacher(teacherInformationPO.getName());
                    }else{
                        if(coursesInfoByClassMappingVO.getCourseType().equals(CourseContentType.LIVING.getContentType())){
                            log.error("该直播课没有设置主讲老师 " + coursesInfoByClassMappingVO + "\n 教学计划为 "
                                    + courseInformationVO);
                        }
                    }

                    int insert = getBaseMapper().insert(courseExamInfoPO);
                    if(insert <= 0){
                        log.error("数据库插入考试信息失败 " + courseExamInfoPO);
                    }

                    // 设置考试助教信息
                    List<CourseAssistantsPO> courseAssistantsPOS = courseAssistantsService.getBaseMapper().selectList(new LambdaQueryWrapper<CourseAssistantsPO>()
                            .eq(CourseAssistantsPO::getCourseId, coursesInfoByClassMappingVO.getCourseId()));

                    for(CourseAssistantsPO courseAssistantsPO : courseAssistantsPOS){
                        TeacherInformationPO teacherInformationPO1 = null;
                        try{
                            teacherInformationPO1 = teacherInformationMapper.selectOne(new LambdaQueryWrapper<TeacherInformationPO>()
                                    .eq(TeacherInformationPO::getTeacherUsername, courseAssistantsPO.getUsername()));
                        }catch (Exception e){
                            log.error("获取考试助教信息失败 " + e);
                        }
                        if(teacherInformationPO1 != null){
                            CourseExamAssistantsPO courseExamAssistantsPO = new CourseExamAssistantsPO()
                                    .setCourseId(coursesInfoByClassMappingVO.getCourseId())
                                    .setExamId(courseExamInfoPO.getId())
                                    .setTeacherUsername(courseAssistantsPO.getUsername())
                                    .setAssistantName(teacherInformationPO1.getName())
                                    ;

                            CourseExamAssistantsPO courseExamAssistantsPO1 = courseExamAssistantsMapper.selectOne(new LambdaQueryWrapper<CourseExamAssistantsPO>()
                                    .eq(CourseExamAssistantsPO::getCourseId, coursesInfoByClassMappingVO.getCourseId())
                                    .eq(CourseExamAssistantsPO::getExamId, courseExamInfoPO.getId())
                                    .eq(CourseExamAssistantsPO::getTeacherUsername,
                                            courseExamAssistantsPO.getTeacherUsername())
                            );
                            if (courseExamAssistantsPO1 != null) {
                                // 无需对同一门课的助教重复插入
                            }else{
                                int insert1 = courseExamAssistantsMapper.insert(courseExamAssistantsPO);
                                if(insert1 <= 0){
                                    log.error("插入考试助教信息失败");
                                }
                            }
                        }
                    }
                }
            }
        }

        log.info("已更新完毕所有的机考信息记录");
    }


}
