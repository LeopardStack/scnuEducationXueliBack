package com.scnujxjy.backendpoint.CourseScheduleTest;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scnujxjy.backendpoint.dao.entity.basic.PlatformUserPO;
import com.scnujxjy.backendpoint.dao.entity.core_data.TeacherInformationPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_point.TeachingPointInformationPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseSchedulePO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.TeachingAssistantsCourseSchedulePO;
import com.scnujxjy.backendpoint.dao.mapper.core_data.TeacherInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseScheduleMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.TeachingAssistantsCourseScheduleMapper;
import com.scnujxjy.backendpoint.model.ro.basic.PlatformUserRO;
import com.scnujxjy.backendpoint.service.basic.PlatformUserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@SpringBootTest
@Slf4j
public class AssistantTest {

    @Resource
    private CourseScheduleMapper courseScheduleMapper;

    @Resource
    private TeacherInformationMapper teacherInformationMapper;

    @Resource
    private PlatformUserService platformUserService;


    @Resource
    private TeachingAssistantsCourseScheduleMapper teachingAssistantsCourseScheduleMapper;

    /**
     * 测试助教信息同步到映射表
     */
    @Test
    public void test1(){
        List<CourseSchedulePO> courseSchedulePOS = courseScheduleMapper.selectList(null);
        HashSet<TeachingAssistantsCourseSchedulePO> hashSet = new HashSet<>();
        for (CourseSchedulePO courseSchedulePO : courseSchedulePOS) {
            String teachingAssistantUsername = courseSchedulePO.getTeachingAssistantUsername();
            Long batchIndex = courseSchedulePO.getBatchIndex();
            TeachingAssistantsCourseSchedulePO teachingAssistantsCourseSchedulePO = new TeachingAssistantsCourseSchedulePO();

            // 先设置字段值
            teachingAssistantsCourseSchedulePO.setUsername(teachingAssistantUsername);
            teachingAssistantsCourseSchedulePO.setBatchId(batchIndex);

            if (!hashSet.contains(teachingAssistantsCourseSchedulePO)) {
                hashSet.add(teachingAssistantsCourseSchedulePO);
                if(teachingAssistantsCourseSchedulePO.getUsername() == null){
                    log.warn("无助教 " + courseSchedulePO);
                }else{
                    int insert = teachingAssistantsCourseScheduleMapper.insert(teachingAssistantsCourseSchedulePO);
                    log.info("插入成功 " + insert + " \n" + courseSchedulePO);
                }

            }
        }

    }

    /**
     * 获取指定名字的助教
     */
    @Test
    public void test2(){

        TeacherInformationPO teacherInformationPO = teacherInformationMapper.selectOne(new LambdaQueryWrapper<TeacherInformationPO>().
                eq(TeacherInformationPO::getTeacherType2, "辅导教师").
                eq(TeacherInformationPO::getName, "陈思瑜")
        );
        List<TeachingAssistantsCourseSchedulePO> teachingAssistantsCourseSchedulePOS = teachingAssistantsCourseScheduleMapper.selectList(new LambdaQueryWrapper<TeachingAssistantsCourseSchedulePO>().
                eq(TeachingAssistantsCourseSchedulePO::getUsername, teacherInformationPO.getTeacherUsername()));
        log.info("该老师的教师信息 和 排课表信息 \n" +
                teachingAssistantsCourseSchedulePOS);

        // 遍历一下助教表 如果没有平台账号的 立刻创建
        List<PlatformUserRO> platformUserROList = new ArrayList<>();
        List<TeachingAssistantsCourseSchedulePO> teachingAssistantsCourseSchedulePOS1 = teachingAssistantsCourseScheduleMapper.selectList(null);
        for(TeachingAssistantsCourseSchedulePO teachingAssistantsCourseSchedulePO: teachingAssistantsCourseSchedulePOS1){
            PlatformUserPO platformUserPO = platformUserService.getBaseMapper().selectOne(new LambdaQueryWrapper<PlatformUserPO>().
                    eq(PlatformUserPO::getUsername, teachingAssistantsCourseSchedulePO.getUsername()));
            if(platformUserPO == null){

                // 获取字符串的长度
                int length = teachingAssistantsCourseSchedulePO.getUsername().length();

                // 检查字符串是否足够长，至少包含六个字符
                String lastSixCharacters;
                if (length >= 6) {
                    // 获取后六位字符
                   lastSixCharacters = teachingAssistantsCourseSchedulePO.getUsername().substring(length - 6);
                } else {
                    lastSixCharacters = (teachingAssistantsCourseSchedulePO.getUsername() + "2023@").substring(length - 6);
                }

                PlatformUserRO platformUserRO = new PlatformUserRO();
                platformUserRO.setUsername(teachingAssistantsCourseSchedulePO.getUsername());
                platformUserRO.setPassword(lastSixCharacters);
                platformUserRO.setRoleId(2L);
                if(!platformUserROList.contains(platformUserRO)){
                    platformUserROList.add(platformUserRO);
                }


            }

        }
        platformUserService.batchCreateUser(platformUserROList);
    }

    @Test
    public void test3(){

        TeacherInformationPO teacherInformationPO = teacherInformationMapper.selectOne(new LambdaQueryWrapper<TeacherInformationPO>().
                eq(TeacherInformationPO::getTeacherType2, "辅导教师").
                eq(TeacherInformationPO::getName, "陈思瑜")
        );
        List<TeachingAssistantsCourseSchedulePO> teachingAssistantsCourseSchedulePOS = teachingAssistantsCourseScheduleMapper.selectList(new LambdaQueryWrapper<TeachingAssistantsCourseSchedulePO>().
                eq(TeachingAssistantsCourseSchedulePO::getUsername, teacherInformationPO.getTeacherUsername()));
        log.info("该老师的教师信息 和 排课表信息 \n" +
                teachingAssistantsCourseSchedulePOS);
    }
}
