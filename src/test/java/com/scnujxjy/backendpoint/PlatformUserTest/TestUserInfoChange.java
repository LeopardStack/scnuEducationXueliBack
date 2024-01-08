package com.scnujxjy.backendpoint.PlatformUserTest;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.scnujxjy.backendpoint.dao.entity.basic.PlatformUserPO;
import com.scnujxjy.backendpoint.dao.entity.college.CollegeAdminInformationPO;
import com.scnujxjy.backendpoint.dao.entity.college.CollegeInformationPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.PersonalInfoPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.StudentStatusPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_point.TeachingPointAdminInformationPO;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.PersonalInfoMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.StudentStatusMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_point.TeachingPointAdminInformationMapper;
import com.scnujxjy.backendpoint.model.ro.basic.PlatformUserRO;
import com.scnujxjy.backendpoint.model.vo.basic.PlatformUserVO;
import com.scnujxjy.backendpoint.service.basic.PlatformUserService;
import com.scnujxjy.backendpoint.service.college.CollegeAdminInformationService;
import com.scnujxjy.backendpoint.service.college.CollegeInformationService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SpringBootTest
@Slf4j
public class TestUserInfoChange {

    @Resource
    private PlatformUserService platformUserService;

    @Resource
    private CollegeAdminInformationService collegeAdminInformationService;

    @Resource
    private CollegeInformationService collegeInformationService;

    @Resource
    private StudentStatusMapper studentStatusMapper;

    @Resource
    private TeachingPointAdminInformationMapper teachingPointAdminInformationMapper;

    @Resource
    private PersonalInfoMapper personalInfoMapper;

    @Test
    public void changePassword() {
        PlatformUserVO platformUserVO = platformUserService.detailByUsername("M13007");
        Boolean aBoolean = platformUserService.changePassword(platformUserVO.getUserId(), "M130072023@");
//        Boolean aBoolean1 = platformUserService.changePassword(3L, "123456");
//        Boolean aBoolean2 = platformUserService.changePassword(4L, "123456");
        log.info("修改密码 " + aBoolean);
    }

    /**
     * 添加超级管理员
     */
    @Test
    public void addSuperAdmin() {
        log.info("生成超级管理员");

        List<PlatformUserRO> platformUserROList = new ArrayList<>();
        PlatformUserRO platformUserRO = new PlatformUserRO();
        platformUserRO.setUsername("scnuXueliAdmin");
        platformUserRO.setPassword("scnuXueliAdmin2023@");
        platformUserRO.setRoleId(8L);
        platformUserROList.add(platformUserRO);
        List<PlatformUserVO> platformUserVOS = platformUserService.batchCreateUser(platformUserROList);
        log.info("生成超级管理员  \n" + platformUserVOS);
    }

    /**
     * 添加学历教育部管理员
     */
    @Test
    public void addManager() {
        log.info("生成学历教育部管理员账号");

        List<PlatformUserRO> platformUserROList = new ArrayList<>();
        PlatformUserRO platformUserRO = new PlatformUserRO();
        platformUserRO.setUsername("xuelijiaoyuTest1");
        platformUserRO.setPassword("xuelijiaoyuTest12023@");
        platformUserRO.setRoleId(3L);
        platformUserROList.add(platformUserRO);
        platformUserService.batchCreateUser(platformUserROList);
    }

    /**
     * 添加教学点管理员
     */
    @Test
    public void addTeachingPointManager() {

        List<PlatformUserRO> platformUserROList = new ArrayList<>();
        PlatformUserRO platformUserRO = new PlatformUserRO();
        platformUserRO.setUsername("guangzhoudadeTest1");
        platformUserRO.setPassword("guangzhoudadeTest12023@");
        platformUserRO.setRoleId(7L);
        platformUserROList.add(platformUserRO);
        platformUserService.batchCreateUser(platformUserROList);
    }

    /**
     * 添加二级学院管理员
     */
//    @Test
//    public void addCollegeManager() {
//
//        List<PlatformUserRO> platformUserROList = new ArrayList<>();
//        PlatformUserRO platformUserRO = new PlatformUserRO();
//        platformUserRO.setUsername("guangzhoudadeTest1");
//        platformUserRO.setPassword("guangzhoudadeTest12023@");
//        platformUserRO.setRoleId(7L);
//        platformUserROList.add(platformUserRO);
//        platformUserService.batchCreateUser(platformUserROList);
//    }

    /**
     * 添加老师
     */
    @Test
    public void addTeacher() {

        List<PlatformUserRO> platformUserROList = new ArrayList<>();
        PlatformUserRO platformUserRO = new PlatformUserRO();
        platformUserRO.setUsername("T2022020896");
        platformUserRO.setPassword("020896");
        platformUserRO.setRoleId(2L);
        platformUserROList.add(platformUserRO);
        platformUserService.batchCreateUser(platformUserROList);
    }

    /**
     * 生成教学点的教务员账号
     */
    @Test
    public void addTeachingPointAdmin() {
        List<PlatformUserRO> platformUserROList = new ArrayList<>();
        List<TeachingPointAdminInformationPO> teachingPointAdminInformationPOS = teachingPointAdminInformationMapper.selectList(null);
        for (TeachingPointAdminInformationPO teachingPointAdminInformationPO : teachingPointAdminInformationPOS) {
            PlatformUserPO platformUserPO = platformUserService.getBaseMapper().selectOne(new LambdaQueryWrapper<PlatformUserPO>()
                    .eq(PlatformUserPO::getUsername, "M" + teachingPointAdminInformationPO.getIdCardNumber()));
            if (platformUserPO == null) {
                String username = "M" + teachingPointAdminInformationPO.getIdCardNumber();
                PlatformUserRO platformUserRO = new PlatformUserRO();
                platformUserRO.setUsername(username);
                platformUserRO.setPassword(username.substring(username.length() - 6));
                platformUserRO.setRoleId(7L);
                platformUserROList.add(platformUserRO);
            }
        }
        platformUserService.batchCreateUser(platformUserROList);
    }

    /**
     * 指定学生群体创建登录账号
     */
    @Test
    public void addStudentLoginAccount() {
        List<StudentStatusPO> studentStatusPOS = studentStatusMapper.selectStudentsByGradeCollege("2023", "教育科学学院");
        log.info(studentStatusPOS.toString());

        List<PlatformUserRO> platformUserROList = new ArrayList<>();

        for (StudentStatusPO studentStatusPO : studentStatusPOS) {
            PlatformUserRO platformUserRO = new PlatformUserRO();
            String username = studentStatusPO.getIdNumber();
            platformUserRO.setUsername(username);
            if (username.length() >= 6) {
                platformUserRO.setPassword(username.substring(username.length() - 6));
            } else {
                platformUserRO.setPassword(username); // 如果 account 长度小于6，则直接使用 account 作为密码
            }
            platformUserRO.setRoleId(1L);
            platformUserROList.add(platformUserRO);
        }

        platformUserService.batchCreateUser(platformUserROList);
        log.info("共计生成账号 " + studentStatusPOS.size() + " 个");
    }

    /**
     * 删除指定学生群体的登录账号
     */
    @Test
    public void deleteStudentLoginAccount() {
        List<PlatformUserPO> usersToDelete = platformUserService.getBaseMapper().selectUsersByCollegeAndGrade("2023", "计算机学院");
        int deletedCount = platformUserService.getBaseMapper().deleteUsersByCollegeAndGrade("2023", "计算机学院");

        if (deletedCount == usersToDelete.size()) {
            // 所有查询到的用户都被成功删除
            log.info("查询到所有用户都被删除了");
        } else {
            // 可能有一些用户没有被删除，你可以进一步处理或记录这些用户
            log.error("存在部分用户没有被删除");
        }

    }

    @Test
    public void addCollegeManager() {
        String idNumber = "450722200008016522";
        String username = "M" + idNumber;
        String password = "016522";
        String name = "刘舒婷";
        String collegeName = "文学院";
        String phoneNumber = "15362954650";
        PlatformUserPO platformUserPO = new PlatformUserPO();
        platformUserPO.setUsername(username);
        platformUserPO.setPassword(password);
        platformUserPO.setRoleId(6L);
        LambdaQueryWrapper<PlatformUserPO> wrapper = Wrappers.<PlatformUserPO>lambdaQuery()
                .eq(PlatformUserPO::getUsername, username);
        Integer count = platformUserService.getBaseMapper().selectCount(wrapper);
        if (count == 0) {
            int insert = platformUserService.getBaseMapper().insert(platformUserPO);
            log.info("生成新账号 " + insert);
            // 给二级学院生成账号后 再录入其个人信息
            CollegeInformationPO collegeInformationPO = collegeInformationService.getBaseMapper().selectOne(new LambdaQueryWrapper<CollegeInformationPO>()
                    .eq(CollegeInformationPO::getCollegeName, collegeName));
            if(collegeInformationPO == null){
                throw new IllegalArgumentException("该学院信息为空 " + collegeName);
            }

            CollegeAdminInformationPO collegeAdminInformationPO = new CollegeAdminInformationPO();
            collegeAdminInformationPO.setUserId(String.valueOf(platformUserPO.getUserId()));
            collegeAdminInformationPO.setCollegeId(collegeInformationPO.getCollegeId());
            collegeAdminInformationPO.setIdNumber(idNumber);
            collegeAdminInformationPO.setPhone(phoneNumber);
            collegeAdminInformationPO.setName(name);
            int insert1 = collegeAdminInformationService.getBaseMapper().insert(collegeAdminInformationPO);
            if(insert1 > 0){
                log.info("更新新二级学院教务员成功");
            }
        }
    }

    @Test
    public void addCollegeManager2() {
        String username = "Mjiaokeyuan001";
        long userId = -1L;
        PlatformUserVO platformUserVO = platformUserService.detailByUsername(username);
        if (Objects.nonNull(platformUserVO)) {
            userId = platformUserVO.getUserId();
        }
        // 设置其为某个学院的教务员
        CollegeAdminInformationPO collegeAdminInformationPO = new CollegeAdminInformationPO();
        collegeAdminInformationPO.setUserId(String.valueOf(userId));
        collegeAdminInformationPO.setCollegeId("07");
        collegeAdminInformationPO.setName("教育科学学院测试教务员1");
        log.info(collegeAdminInformationPO.toString());
        int insert = collegeAdminInformationService.getBaseMapper().insert(collegeAdminInformationPO);
        log.info("插入教务员账号 " + insert);
    }

    /**
     * 根据年级来添加用户账号
     *
     * @param grade 年级
     */
    private void generateStudentAccount(String grade) {
        List<StudentStatusPO> studentStatusPOS = studentStatusMapper.selectStudentsByGrade(grade);
//        log.info(studentStatusPOS.toString());

        List<PlatformUserRO> platformUserROList = new ArrayList<>();

        for (StudentStatusPO studentStatusPO : studentStatusPOS) {
            // 获取学生的姓名
            PersonalInfoPO personalInfoPO = personalInfoMapper.selectOne(new LambdaQueryWrapper<PersonalInfoPO>().
                    eq(PersonalInfoPO::getGrade, studentStatusPO.getGrade()).
                    eq(PersonalInfoPO::getIdNumber, studentStatusPO.getIdNumber()));
            PlatformUserRO platformUserRO = new PlatformUserRO();
            String username = studentStatusPO.getIdNumber();
            platformUserRO.setUsername(username);
            platformUserRO.setName(personalInfoPO.getName());
            if (username.length() >= 6) {
                platformUserRO.setPassword(username.substring(username.length() - 6));
            } else {
                platformUserRO.setPassword(username); // 如果 account 长度小于6，则直接使用 account 作为密码
            }
            platformUserRO.setRoleId(1L);
            // 检测是否该用户存在
            PlatformUserVO platformUserVO = platformUserService.detailByUsername(username);
            if (Objects.nonNull(platformUserVO)) {
                // 存在账号，如果姓名为空 则更新姓名信息 以最新年份的个人信息为准
                log.info("该用户已存在平台账户 " + platformUserRO);
            } else {
                platformUserROList.add(platformUserRO);
            }
        }

        platformUserService.batchCreateUser(platformUserROList);
        log.info("共计生成账号 " + studentStatusPOS.size() + " 个");
    }


    /**
     * 指定一个年级的学生群体创建登录账号
     */
    @Test
    public void addStudentLoginAccountByGrade() {
        /**
         * 2023 - 2019 都有了
         */
        int gradeStart = 2018;
        int gradeEnd = 2015;
        for (int i = gradeStart; i >= gradeEnd; i--) {
            generateStudentAccount("" + i);
        }
    }


    private void deleteStudentAccountByGrade(String grade) {
        // 获取指定年级的所有学生的账号
        List<StudentStatusPO> studentStatusPOS = studentStatusMapper.selectStudentsByGrade(grade);

        if (studentStatusPOS.isEmpty()) {
            log.info("没有找到年级为 " + grade + " 的用户账号");
            return;
        }

        // 删除这些账号
        for (StudentStatusPO studentStatusPO : studentStatusPOS) {
            LambdaQueryWrapper<PlatformUserPO> wrapper = Wrappers.<PlatformUserPO>lambdaQuery()
                    .eq(PlatformUserPO::getUsername, studentStatusPO.getIdNumber());
            platformUserService.getBaseMapper().delete(wrapper);
            log.info("成功删除了用户账号：" + studentStatusPO.getIdNumber());
        }
    }

    @Test
    public void testDeleteStudentAccountByGrade() {
//        String gradeToDelete = "2023";  // 你可以根据需要更改这个值
//        deleteStudentAccountByGrade(gradeToDelete);
        String username = "";
        LambdaQueryWrapper<PlatformUserPO> wrapper = Wrappers.<PlatformUserPO>lambdaQuery()
                .eq(PlatformUserPO::getUsername, username);
        platformUserService.getBaseMapper().delete(wrapper);
        log.info("成功删除了用户账号：" + username);
    }


    /**
     * 为华为云服务器的数据库添加用户
     */
    @Test
    public void addStudentLoginAccountByGrade1() {
        int gradeStart = 2023;
        int gradeEnd = 2023;
        for (int i = gradeStart; i >= gradeEnd; i--) {
            generateStudentAccount("" + i);
        }
    }


    /**
     * 更新二级学院管理员名字
     */
    @Test
    public void updateCollegeAdminNames() {
        List<CollegeAdminInformationPO> collegeAdminInformationPOS = collegeAdminInformationService.getBaseMapper().selectList(null);
        for (CollegeAdminInformationPO collegeAdminInformationPO : collegeAdminInformationPOS) {
            String name = collegeAdminInformationPO.getName();
            String userId = collegeAdminInformationPO.getUserId();
            PlatformUserPO platformUserPO = platformUserService.getBaseMapper().selectOne(new LambdaQueryWrapper<PlatformUserPO>()
                    .eq(PlatformUserPO::getUserId, Long.parseLong(userId)));
            if (platformUserPO == null) {
                throw new IllegalArgumentException("不存在账号 " + collegeAdminInformationPO);
            } else {
                // 更新名字
                UpdateWrapper<PlatformUserPO> updateWrapper = new UpdateWrapper<>();
                updateWrapper.set("name", name)
                        .eq("user_id", platformUserPO.getUserId());

                int i = platformUserService.getBaseMapper().update(null, updateWrapper);
                if (i > 0) {
                    log.info("更新姓名成功 " + i + " " + collegeAdminInformationPO);
                }
            }
        }
    }

}
