package com.scnujxjy.backendpoint.service.courses_learning;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.dao.entity.courses_learning.CoursesClassMappingPO;
import com.scnujxjy.backendpoint.dao.entity.courses_learning.RetakeStudentsPO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.PersonalInfoPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.StudentStatusPO;
import com.scnujxjy.backendpoint.dao.mapper.courses_learning.RetakeStudentsMapper;
import com.scnujxjy.backendpoint.model.bo.SingleLiving.ChannelInfoRequest;
import com.scnujxjy.backendpoint.model.ro.courses_learning.CourseRetakeRO;
import com.scnujxjy.backendpoint.model.vo.video_stream.StudentWhiteListVO;
import com.scnujxjy.backendpoint.service.registration_record_card.PersonalInfoService;
import com.scnujxjy.backendpoint.service.registration_record_card.StudentStatusService;
import com.scnujxjy.backendpoint.service.video_stream.SingleLivingService;
import com.scnujxjy.backendpoint.util.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
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
public class RetakeStudentsService extends ServiceImpl<RetakeStudentsMapper, RetakeStudentsPO>
        implements IService<RetakeStudentsPO> {
    @Resource
    private StudentStatusService studentStatusService;

    @Resource
    private PersonalInfoService personalInfoService;

    @Resource
    private SingleLivingService singleLivingService;

    @Resource
    private CoursesClassMappingService coursesClassMappingService;

    /**
     * 添加重修学生
     * @param courseRetakeRO
     * @return
     */
    public boolean addRetakeStudents(CourseRetakeRO courseRetakeRO) {
        StudentStatusPO studentStatusPO = studentStatusService.getBaseMapper().selectOne(new LambdaQueryWrapper<StudentStatusPO>()
                .eq(StudentStatusPO::getStudentNumber, courseRetakeRO.getStudentNumber()));
        PersonalInfoPO personalInfoPO = personalInfoService.getBaseMapper().selectOne(new LambdaQueryWrapper<PersonalInfoPO>()
                .eq(PersonalInfoPO::getGrade, studentStatusPO.getGrade())
                .eq(PersonalInfoPO::getIdNumber, studentStatusPO.getIdNumber())
        );

        if(studentStatusPO != null){
            // 防止在籍的正常的学生被添加到重修名单
            List<CoursesClassMappingPO> coursesClassMappingPOS = coursesClassMappingService.getBaseMapper().selectList(new LambdaQueryWrapper<CoursesClassMappingPO>()
                    .eq(CoursesClassMappingPO::getCourseId, courseRetakeRO.getCourseId()));
            if(coursesClassMappingPOS.isEmpty()){
                log.info(StpUtil.getLoginIdAsString() + " 添加重修名单时，课程主键 ID 找不到");
                return false;
            }
            String studentClassIdentifier = studentStatusPO.getClassIdentifier();
            boolean b = coursesClassMappingPOS.stream()
                    .anyMatch(mapping -> studentClassIdentifier.equals(mapping.getClassIdentifier()));
            if(b){
                // 如果它是正常的班级里的学生 也为 false
                log.info(StpUtil.getLoginIdAsString() + " 添加重修名单时，该学生已经在正常的班级里 "
                        + courseRetakeRO.getStudentNumber());
                return false;
            }
            RetakeStudentsPO retakeStudentsPO  = new RetakeStudentsPO()
                    .setCourseId(courseRetakeRO.getCourseId())
                    .setStudentNumber(courseRetakeRO.getStudentNumber())
                    ;
            Integer i = getBaseMapper().selectCount(new LambdaQueryWrapper<RetakeStudentsPO>()
                    .eq(RetakeStudentsPO::getStudentNumber, courseRetakeRO.getStudentNumber())
                    .eq(RetakeStudentsPO::getCourseId, courseRetakeRO.getCourseId())
            );
            if(i > 0){
                log.info(StpUtil.getLoginIdAsString() + " 添加重修名单时，该学生已经在重修库中");
                return false;
            }
            int insert = getBaseMapper().insert(retakeStudentsPO);
            if(insert > 0){
                // 添加重修名单 是需要添加白名单的
                ChannelInfoRequest channelInfoRequest = new ChannelInfoRequest();
                List<StudentWhiteListVO> studentWhiteListVOS = new ArrayList<>();
                StudentWhiteListVO studentWhiteListVO = new StudentWhiteListVO()
                        .setCode(personalInfoPO.getIdNumber())
                        .setName(personalInfoPO.getName())
                        ;
                studentWhiteListVOS.add(studentWhiteListVO);
                channelInfoRequest.setStudentWhiteList(studentWhiteListVOS);
                SaResult saResult = singleLivingService.addChannelWhiteStudent(channelInfoRequest);
                if(saResult.getCode().equals(ResultCode.PARTIALSUCCESS.getCode())){
                    return true;
                }
                log.info(StpUtil.getLoginIdAsString() + " 添加白名单失败 " + saResult.getMsg());
                return false;
            }
            log.info(StpUtil.getLoginIdAsString() + " 添加重修名单时，数据库插入失败 " + insert);
        }
        return true;
    }

    /**
     * 删除重修名单
     * @param courseRetakeRO
     * @return
     */
    public SaResult deleteRetakeStudents(CourseRetakeRO courseRetakeRO) {
        StudentStatusPO studentStatusPO = studentStatusService.getBaseMapper().selectOne(new LambdaQueryWrapper<StudentStatusPO>()
                .eq(StudentStatusPO::getStudentNumber, courseRetakeRO.getStudentNumber()));
        if(studentStatusPO != null){

            Integer i = getBaseMapper().selectCount(new LambdaQueryWrapper<RetakeStudentsPO>()
                    .eq(RetakeStudentsPO::getCourseId, courseRetakeRO.getCourseId())
                    .eq(RetakeStudentsPO::getStudentNumber, courseRetakeRO.getStudentNumber())
            );
            if(i == 0){
                return SaResult.ok("已删除 无需再删除");
            }

            int delete = getBaseMapper().delete(new LambdaQueryWrapper<RetakeStudentsPO>()
                    .eq(RetakeStudentsPO::getCourseId, courseRetakeRO.getCourseId())
                    .eq(RetakeStudentsPO::getStudentNumber, courseRetakeRO.getStudentNumber())
            );
            if(delete <= 0){
                return SaResult.error("删除失败").setCode(500);
            }
            // 删除成功后 是需要删除白名单的
            ChannelInfoRequest channelInfoRequest = new ChannelInfoRequest();
            List<String> deleteList = new ArrayList<>();
            deleteList.add(studentStatusPO.getIdNumber());

            channelInfoRequest.setDeleteCodeList(deleteList);
            SaResult saResult = singleLivingService.deleteChannelWhiteStudent(channelInfoRequest);
            if(!saResult.getCode().equals(ResultCode.PARTIALSUCCESS.getCode())){
                log.error(StpUtil.getLoginIdAsString() + " 删除重修学生时删除白名单失败 " + saResult.getMsg());
                return SaResult.ok("删除成功");
            }
        }

        return SaResult.ok("删除成功");
    }
}
