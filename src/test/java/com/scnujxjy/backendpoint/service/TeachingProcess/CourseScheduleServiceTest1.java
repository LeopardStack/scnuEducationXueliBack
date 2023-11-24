package com.scnujxjy.backendpoint.service.TeachingProcess;

import com.scnujxjy.backendpoint.dao.entity.college.CollegeAdminInformationPO;
import com.scnujxjy.backendpoint.dao.entity.college.CollegeInformationPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseSchedulePO;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseScheduleMapper;
import com.scnujxjy.backendpoint.model.vo.basic.PlatformUserVO;
import com.scnujxjy.backendpoint.service.basic.PlatformUserService;
import com.scnujxjy.backendpoint.service.college.CollegeAdminInformationService;
import com.scnujxjy.backendpoint.service.college.CollegeInformationService;
import com.scnujxjy.backendpoint.service.core_data.TeacherInformationService;
import com.scnujxjy.backendpoint.service.teaching_process.CourseScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
@Slf4j
public class CourseScheduleServiceTest1 {
    @Resource
    private CollegeInformationService collegeInformationService;

    @Resource
    private PlatformUserService platformUserService;

    @Resource
    private CollegeAdminInformationService collegeAdminInformationService;

    @Resource
    private CourseScheduleMapper courseScheduleMapper;

    @Resource
    private CourseScheduleService courseScheduleService;

    @Resource
    private TeacherInformationService teacherInformationService;


    /**
     * 测试获取某一个学院的教务员所能获取的排课表
     */
    @Test
    public void test1(){
        String account = "M001";
        PlatformUserVO platformUserVO = platformUserService.detailByUsername(account);
        CollegeAdminInformationPO collegeAdminInformationPO = collegeAdminInformationService.getById(platformUserVO.getUserId());
        CollegeInformationPO collegeInformationServiceById = collegeInformationService.getById(collegeAdminInformationPO.getCollegeId());
        String collegeName = collegeInformationServiceById.getCollegeName();

        List<CourseSchedulePO> courseSchedulePOS = courseScheduleMapper.detailByCollegeName(collegeName);
        log.info("它是哪个学院的教务员 " + collegeName);
        log.info(collegeName + " 学院的教学计划总数 " + courseSchedulePOS.size());
        log.info("最后一条记录 " + courseSchedulePOS.get(courseSchedulePOS.size()-1));
    }


    /**
     * 测试获取某一个老师的排课表
     */
    @Test
    public void test2(){
/*        String account = "T20175031";
        String findStr = account.substring(1);
        TeacherInformationPO teacherInformationPO = null;

        List<TeacherInformationPO> teacherInformationPOS = teacherInformationService.getBaseMapper().selectByWorkNumber(findStr);
        if(teacherInformationPOS.size() > 0){
            teacherInformationPO = teacherInformationPOS.get(0);
        }else{
            List<TeacherInformationPO> teacherInformationPOS1 = teacherInformationService.getBaseMapper().selectByIdCardNumber(findStr);
            if(teacherInformationPOS.size() > 0){
                teacherInformationPO = teacherInformationPOS.get(0);
            }else{
                List<TeacherInformationPO> teacherInformationPOS2 = teacherInformationService.getBaseMapper().selectByPhone(findStr);
                if(teacherInformationPOS.size() > 0){
                    teacherInformationPO = teacherInformationPOS.get(0);
                }else{

                }
            }
        }
        if(teacherInformationPO == null){
            log.error("没有找到该老师信息 " + account);
        }else{
            List<CourseSchedulePO> courseSchedulePOS = courseScheduleMapper.selectCourseSchedules3(teacherInformationPO.getName());
            log.info("它是哪个老师 " + teacherInformationPO.getName());
            log.info(teacherInformationPO.getName() + " 老师的教学计划总数 " + courseSchedulePOS.size());
            log.info("最后一条记录 " + courseSchedulePOS.get(courseSchedulePOS.size()-1));
        }*/

    }

    /**
     * 测试获取某一个学生的排课表
     */
    @Test
    public void test3(){
//        String account = "T20175031";
//        String findStr = account.substring(1);
//        TeacherInformationPO teacherInformationPO = null;
//
//        List<TeacherInformationPO> teacherInformationPOS = teacherInformationService.getBaseMapper().selectByWorkNumber(findStr);
//        if(teacherInformationPOS.size() > 0){
//            teacherInformationPO = teacherInformationPOS.get(0);
//        }else{
//            List<TeacherInformationPO> teacherInformationPOS1 = teacherInformationService.getBaseMapper().selectByIdCardNumber(findStr);
//            if(teacherInformationPOS.size() > 0){
//                teacherInformationPO = teacherInformationPOS.get(0);
//            }else{
//                List<TeacherInformationPO> teacherInformationPOS2 = teacherInformationService.getBaseMapper().selectByPhone(findStr);
//                if(teacherInformationPOS.size() > 0){
//                    teacherInformationPO = teacherInformationPOS.get(0);
//                }else{
//
//                }
//            }
//        }
//        if(teacherInformationPO == null){
//            log.error("没有找到该老师信息 " + account);
//        }else{
//            List<CourseSchedulePO> courseSchedulePOS = courseScheduleMapper.selectCourseSchedules3(teacherInformationPO.getName());
//            log.info("它是哪个老师 " + teacherInformationPO.getName());
//            log.info(teacherInformationPO.getName() + " 老师的教学计划总数 " + courseSchedulePOS.size());
//            log.info("最后一条记录 " + courseSchedulePOS.get(courseSchedulePOS.size()-1));
//        }
//
    }
}
