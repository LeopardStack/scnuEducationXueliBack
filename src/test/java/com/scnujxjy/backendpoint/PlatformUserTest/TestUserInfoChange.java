package com.scnujxjy.backendpoint.PlatformUserTest;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scnujxjy.backendpoint.dao.entity.basic.PlatformUserPO;
import com.scnujxjy.backendpoint.dao.entity.college.CollegeAdminInformationPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.PersonalInfoPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.StudentStatusPO;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.PersonalInfoMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.StudentStatusMapper;
import com.scnujxjy.backendpoint.model.ro.basic.PlatformUserRO;
import com.scnujxjy.backendpoint.model.vo.basic.PlatformUserVO;
import com.scnujxjy.backendpoint.service.basic.PlatformUserService;
import com.scnujxjy.backendpoint.service.college.CollegeAdminInformationService;
import com.scnujxjy.backendpoint.service.college.CollegeInformationService;
import com.scnujxjy.backendpoint.service.registration_record_card.StudentStatusService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@Slf4j
public class TestUserInfoChange {

    @Resource
    private PlatformUserService platformUserService;

    @Resource
    private CollegeAdminInformationService collegeAdminInformationService;

    @Resource
    private StudentStatusMapper studentStatusMapper;

    @Resource
    private PersonalInfoMapper personalInfoMapper;

    @Test
    public void changePassword(){
        PlatformUserVO platformUserVO = platformUserService.detailByuserName("M50129");
        Boolean aBoolean = platformUserService.changePassword(platformUserVO.getUserId(), "M501292023@");
//        Boolean aBoolean1 = platformUserService.changePassword(3L, "123456");
//        Boolean aBoolean2 = platformUserService.changePassword(4L, "123456");
        log.info("修改密码 " + aBoolean);
    }

    /**
     * 添加学历教育部管理员
     */
    @Test
    public void addManager(){
        log.info("生成学历教育部管理员账号");

        List<PlatformUserRO> platformUserROList = new ArrayList<>();
        PlatformUserRO platformUserRO = new PlatformUserRO();
        platformUserRO.setUsername("xuelijiaoyuTest");
        platformUserRO.setPassword("xuelijiaoyuTest2023@");
        platformUserRO.setRoleId(3L);
        platformUserROList.add(platformUserRO);
        platformUserService.batchCreateUser(platformUserROList);
    }

    /**
     * 添加教学点管理员
     */
    @Test
    public void addTeachingPointManager(){

        List<PlatformUserRO> platformUserROList = new ArrayList<>();
        PlatformUserRO platformUserRO = new PlatformUserRO();
        platformUserRO.setUsername("guangzhoudadeTest1");
        platformUserRO.setPassword("guangzhoudadeTest12023@");
        platformUserRO.setRoleId(7L);
        platformUserROList.add(platformUserRO);
        platformUserService.batchCreateUser(platformUserROList);
    }

    /**
     * 指定学生群体创建登录账号
     */
    @Test
    public void addStudentLoginAccount(){
        List<StudentStatusPO> studentStatusPOS = studentStatusMapper.selectStudentsByGradeCollege("2023", "教育科学学院");
        log.info(studentStatusPOS.toString());

        List<PlatformUserRO> platformUserROList = new ArrayList<>();

        for(StudentStatusPO studentStatusPO: studentStatusPOS){
            PlatformUserRO platformUserRO = new PlatformUserRO();
            String userName = studentStatusPO.getIdNumber();
            platformUserRO.setUsername(userName);
            if(userName.length() >= 6) {
                platformUserRO.setPassword(userName.substring(userName.length() - 6));
            } else {
                platformUserRO.setPassword(userName); // 如果 account 长度小于6，则直接使用 account 作为密码
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
    public void deleteStudentLoginAccount(){
        List<PlatformUserPO> usersToDelete = platformUserService.getBaseMapper().selectUsersByCollegeAndGrade("2023", "计算机学院");
        int deletedCount = platformUserService.getBaseMapper().deleteUsersByCollegeAndGrade("2023", "计算机学院");

        if(deletedCount == usersToDelete.size()) {
            // 所有查询到的用户都被成功删除
            log.info("查询到所有用户都被删除了");
        } else {
            // 可能有一些用户没有被删除，你可以进一步处理或记录这些用户
            log.error("存在部分用户没有被删除");
        }

    }

    @Test
    public void addCollegeManager(){
        String userName = "Mjiaokeyuan001";
        String password = "test001";
        PlatformUserPO platformUserPO = new PlatformUserPO();
        platformUserPO.setUsername(userName);
        platformUserPO.setPassword(password);
        platformUserPO.setRoleId(6L);
        if(!platformUserService.getBaseMapper().existsByUsername(userName)){
            int insert = platformUserService.getBaseMapper().insert(platformUserPO);
            log.info("生成新账号 " + insert);
        }
    }

    @Test
    public void addCollegeManager2(){
        String userName = "Mjiaokeyuan001";
        long userIdByUsername = -1L;
        userIdByUsername = platformUserService.getBaseMapper().getUserIdByUsername(userName);
        // 设置其为某个学院的教务员
        CollegeAdminInformationPO collegeAdminInformationPO = new CollegeAdminInformationPO();
        collegeAdminInformationPO.setUserId(String.valueOf(userIdByUsername));
        collegeAdminInformationPO.setCollegeId("07");
        collegeAdminInformationPO.setName("教育科学学院测试教务员1");
        log.info(collegeAdminInformationPO.toString());
        int insert = collegeAdminInformationService.getBaseMapper().insert(collegeAdminInformationPO);
        log.info("插入教务员账号 " + insert);
    }

    /**
     * 根据年级来添加用户账号
     * @param grade 年级
     */
    private void generateStudentAccount(String grade){
        List<StudentStatusPO> studentStatusPOS = studentStatusMapper.selectStudentsByGrade(grade);
//        log.info(studentStatusPOS.toString());

        List<PlatformUserRO> platformUserROList = new ArrayList<>();

        for(StudentStatusPO studentStatusPO: studentStatusPOS){
            // 获取学生的姓名
            PersonalInfoPO personalInfoPO = personalInfoMapper.selectOne(new LambdaQueryWrapper<PersonalInfoPO>().
                    eq(PersonalInfoPO::getGrade, studentStatusPO.getGrade()).
                    eq(PersonalInfoPO::getIdNumber, studentStatusPO.getIdNumber()));
            PlatformUserRO platformUserRO = new PlatformUserRO();
            String userName = studentStatusPO.getIdNumber();
            platformUserRO.setUsername(userName);
            platformUserRO.setName(personalInfoPO.getName());
            if(userName.length() >= 6) {
                platformUserRO.setPassword(userName.substring(userName.length() - 6));
            } else {
                platformUserRO.setPassword(userName); // 如果 account 长度小于6，则直接使用 account 作为密码
            }
            platformUserRO.setRoleId(1L);
            // 检测是否该用户存在
            if(platformUserService.getBaseMapper().selectPlatformUsers1(userName).size() > 0){
                // 存在账号，如果姓名为空 则更新姓名信息 以最新年份的个人信息为准
                log.info("该用户已存在平台账户 " + platformUserRO.toString());
            }else{
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
    public void addStudentLoginAccountByGrade(){
        /**
         * 2023 - 2019 都有了
         */
        int gradeStart = 2018;
        int gradeEnd = 2015;
        for(int i = gradeStart; i >= gradeEnd; i--){
            generateStudentAccount("" + i);
        }
    }


    private void deleteStudentAccountByGrade(String grade) {
        // 获取指定年级的所有学生的账号
        List<StudentStatusPO> studentStatusPOS = studentStatusMapper.selectStudentsByGrade(grade);

        if(studentStatusPOS.isEmpty()) {
            log.info("没有找到年级为 " + grade + " 的用户账号");
            return;
        }

        // 删除这些账号
        for(StudentStatusPO studentStatusPO : studentStatusPOS) {
            platformUserService.getBaseMapper().deleteStudentByIdNumber(studentStatusPO.getIdNumber());
            log.info("成功删除了用户账号：" + studentStatusPO.getIdNumber());
        }
    }

    @Test
    public void testDeleteStudentAccountByGrade() {
//        String gradeToDelete = "2023";  // 你可以根据需要更改这个值
//        deleteStudentAccountByGrade(gradeToDelete);
        String userName = "";
        platformUserService.getBaseMapper().deleteStudentByIdNumber(userName);
        log.info("成功删除了用户账号：" + userName);
    }


    /**
     * 为华为云服务器的数据库添加用户
     */
    @Test
    public void addStudentLoginAccountByGrade1(){
        int gradeStart = 2023;
        int gradeEnd = 2023;
        for(int i = gradeStart; i >= gradeEnd; i--){
            generateStudentAccount("" + i);
        }
    }

}
