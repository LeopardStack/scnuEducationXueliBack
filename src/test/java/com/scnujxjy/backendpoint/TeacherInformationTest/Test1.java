package com.scnujxjy.backendpoint.TeacherInformationTest;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scnujxjy.backendpoint.dao.entity.basic.PlatformUserPO;
import com.scnujxjy.backendpoint.dao.entity.core_data.TeacherInformationPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseSchedulePO;
import com.scnujxjy.backendpoint.dao.mapper.basic.PlatformUserMapper;
import com.scnujxjy.backendpoint.dao.mapper.core_data.TeacherInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseScheduleMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.TeachingAssistantsCourseScheduleMapper;
import com.scnujxjy.backendpoint.model.ro.basic.PlatformUserRO;
import com.scnujxjy.backendpoint.model.vo.core_data.TeacherInformationExcelImportVO;
import com.scnujxjy.backendpoint.service.basic.PlatformUserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
@Slf4j
public class Test1 {

    @Autowired(required = false)
    private TeacherInformationMapper teacherInformationMapper;

    @Resource
    private PlatformUserMapper platformUserMapper;

    @Resource
    private PlatformUserService platformUserService;

    @Resource
    private CourseScheduleMapper courseScheduleMapper;

    @Resource
    private TeachingAssistantsCourseScheduleMapper teachingAssistantsCourseScheduleMapper;

    @Test
    public void test1() {
        String directoryPath = "src/main/resources/data/教师信息导入";
        File directory = new File(directoryPath);
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".xlsx"));

        if (files == null || files.length == 0) {
            log.error("没有找到任何 Excel 文件");
            return;
        }

        Map<String, Integer> fileDataCounts = new HashMap<>();
        AtomicInteger allCount = new AtomicInteger(0); // 使用 AtomicInteger 来存储总数

        for (File file : files) {
            TeacherInformationListener listener = new TeacherInformationListener(teacherInformationMapper);
            int headRowNumber = 1;
            EasyExcel.read(file.getAbsolutePath(), TeacherInformationExcelImportVO.class, listener)
                    .sheet().headRowNumber(headRowNumber).doRead();
            fileDataCounts.put(file.getName(), listener.getDataCount());
        }

        // 打印每个文件的教师信息记录数
        fileDataCounts.forEach((fileName, count) -> {
            log.info(fileName + " 包含 " + count + " 条教师信息记录");
            allCount.addAndGet(count);
        });

        log.info("总共读入 " + allCount.get() + " 记录");
    }

    /**
     * 遍历师资库 为没有账号的老师生成平台登录账号
     */
    @Test
    public void test2(){
        List<TeacherInformationPO> teacherInformationPOS = teacherInformationMapper.selectList(null);

        List<PlatformUserRO> platformUserROList = new ArrayList<>();

        for(TeacherInformationPO teacherInformationPO: teacherInformationPOS){
            String teacherUsername = teacherInformationPO.getTeacherUsername();
            PlatformUserPO platformUserPO = platformUserMapper.selectOne(new LambdaQueryWrapper<PlatformUserPO>().
                    eq(PlatformUserPO::getUsername, teacherUsername));
            if(platformUserPO == null){
                // 创建账号
                PlatformUserRO platformUserRO = new PlatformUserRO();
                platformUserRO.setName(teacherInformationPO.getName());
                platformUserRO.setUsername(teacherUsername);
                platformUserRO.setRoleId(2L);
                platformUserRO.setPassword(teacherUsername.substring(teacherUsername.length() - 6));
                platformUserROList.add(platformUserRO);
            }
        }

        platformUserService.batchCreateUser(platformUserROList);
        log.info("共计生成教师账号 " + platformUserROList.size() + " 个");
    }

    /**
     * 为指定主讲老师 配置指定助教
     */
    @Test
    public void test3(){
        String mainTeacher = "秦晓华";
        String tutorName = "黄云霞";
        TeacherInformationPO teacherInformationPO = teacherInformationMapper.selectOne(new LambdaQueryWrapper<TeacherInformationPO>()
                .eq(TeacherInformationPO::getName, mainTeacher));
        TeacherInformationPO tutor = teacherInformationMapper.selectOne(new LambdaQueryWrapper<TeacherInformationPO>()
                .eq(TeacherInformationPO::getName, tutorName));
        List<CourseSchedulePO> courseSchedulePOS = courseScheduleMapper.selectList(new LambdaQueryWrapper<CourseSchedulePO>()
                .eq(CourseSchedulePO::getTeacherUsername, teacherInformationPO.getTeacherUsername()));
        if(!courseSchedulePOS.isEmpty() && teacherInformationPO != null && tutor != null){

        }
    }
}
