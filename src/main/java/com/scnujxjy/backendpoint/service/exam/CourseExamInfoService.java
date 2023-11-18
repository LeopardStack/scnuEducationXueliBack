package com.scnujxjy.backendpoint.service.exam;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.dao.entity.college.CollegeInformationPO;
import com.scnujxjy.backendpoint.dao.entity.exam.CourseExamAssistantsPO;
import com.scnujxjy.backendpoint.dao.entity.exam.CourseExamInfoPO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseInformationPO;
import com.scnujxjy.backendpoint.dao.mapper.exam.CourseExamAssistantsMapper;
import com.scnujxjy.backendpoint.dao.mapper.exam.CourseExamInfoMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseInformationMapper;
import com.scnujxjy.backendpoint.model.bo.exam.ExamDataBO;
import com.scnujxjy.backendpoint.model.ro.exam.ExamFilterRO;
import com.scnujxjy.backendpoint.util.tool.ScnuXueliTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import static com.scnujxjy.backendpoint.constant.enums.RoleEnum.*;
import static com.scnujxjy.backendpoint.exception.DataException.dataNotFoundError;

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
    private CourseInformationMapper courseInformationMapper;

    @Resource
    private ScnuXueliTools scnuXueliTools;


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
        courseExamInfoPO.setExamMethod("机考".equals(courseExamInfoPO.getExamMethod()) ? "线下" : "机考");
        int i = courseExamInfoMapper.updateById(courseExamInfoPO);
        if(i > 0){
            return true;
        }
        return false;
    }

    public boolean batchSetExamType(ExamFilterRO entity) {
        List<String> roleList = StpUtil.getRoleList();

        // 获取访问者 ID
        String userId = (String) StpUtil.getLoginId();

        if (roleList.isEmpty()) {
            throw dataNotFoundError();
        } else {
            if (roleList.contains(SECOND_COLLEGE_ADMIN.getRoleName())) {
                // 二级学院管理员
                CollegeInformationPO userBelongCollege = scnuXueliTools.getUserBelongCollege();
                entity.setCollege(userBelongCollege.getCollegeName());

            } else if (roleList.contains(XUELIJIAOYUBU_ADMIN.getRoleName()) || roleList.contains(CAIWUBU_ADMIN.getRoleName())) {
                // 继续教育学院管理员
            }
        }
        log.info("考试信息筛选参数为 " + entity);
        int count = 0;
        List<ExamDataBO> examDataBOS = courseInformationMapper.selectAllExamData(entity);
        for(ExamDataBO examDataBO: examDataBOS){
            Long examId = examDataBO.getExamId();
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

        log.info("批量获取到了所有的教学计划所对应的考试信息表 " + examDataBOS.size() + " 条 成功了 " + count + " 条");

        return true;
    }

    public boolean batchUnSetExamType(ExamFilterRO entity) {
        List<String> roleList = StpUtil.getRoleList();

        // 获取访问者 ID
        String userId = (String) StpUtil.getLoginId();

        if (roleList.isEmpty()) {
            throw dataNotFoundError();
        } else {
            if (roleList.contains(SECOND_COLLEGE_ADMIN.getRoleName())) {
                // 二级学院管理员
                CollegeInformationPO userBelongCollege = scnuXueliTools.getUserBelongCollege();
                entity.setCollege(userBelongCollege.getCollegeName());

            } else if (roleList.contains(XUELIJIAOYUBU_ADMIN.getRoleName()) || roleList.contains(CAIWUBU_ADMIN.getRoleName())) {
                // 继续教育学院管理员
            }
        }
        log.info("考试信息筛选参数为 " + entity);
        int count = 0;
        List<ExamDataBO> examDataBOS = courseInformationMapper.selectAllExamData(entity);
        for(ExamDataBO examDataBO: examDataBOS){
            Long examId = examDataBO.getExamId();
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
        log.info("批量获取到了所有的教学计划所对应的考试信息表 " + examDataBOS.size() + " 条 成功了 " + count + " 条");
        return true;
    }
}
