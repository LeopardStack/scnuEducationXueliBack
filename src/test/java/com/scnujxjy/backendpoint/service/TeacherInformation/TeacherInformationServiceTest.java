package com.scnujxjy.backendpoint.service.TeacherInformation;

import cn.hutool.core.lang.hash.Hash;
import com.scnujxjy.backendpoint.dao.entity.basic.PlatformUserPO;
import com.scnujxjy.backendpoint.dao.entity.core_data.TeacherInformationPO;
import com.scnujxjy.backendpoint.dao.mapper.basic.PlatformUserMapper;
import com.scnujxjy.backendpoint.model.ro.basic.PlatformRoleRO;
import com.scnujxjy.backendpoint.model.ro.basic.PlatformUserRO;
import com.scnujxjy.backendpoint.service.basic.PlatformUserService;
import com.scnujxjy.backendpoint.service.core_data.TeacherInformationService;
import com.scnujxjy.backendpoint.service.teaching_process.CourseScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SpringBootTest
@Slf4j
public class TeacherInformationServiceTest {
    @Resource
    private TeacherInformationService teacherInformationService;

    @Resource
    private CourseScheduleService courseScheduleService;

    @Resource
    private PlatformUserService platformUserService;

    /**
     * 判断一个老师的姓名在数据库中是否唯一
     * @param teacherName 老师的姓名
     * @return 如果唯一返回true，否则返回false
     */
    public boolean isTeacherNameUnique(String teacherName) {
        List<TeacherInformationPO> teachers = teacherInformationService.getBaseMapper().selectList(null);
        long count = teachers.stream().filter(teacher -> teacher.getName().equals(teacherName)).count();
        return count == 1;
    }


    public void updateTeacherInformationCourseSchedule(List<TeacherInformationPO> teacherInformationPOS1, TeacherAccount account){
        // 更新教师表
        TeacherInformationPO teacherInformationPO = teacherInformationPOS1.get(0);
//        log.info("Updating teacher with ID: " + teacherInformationPO.toString() + ", teacher_username: " + account.getAccountName());
        teacherInformationPO.setTeacher_username(account.getAccountName());
        teacherInformationService.getBaseMapper().updateTeacherUsernameByUserId(teacherInformationPO.getUserId(), account.getAccountName());

        // 更新排课表
        if(isTeacherNameUnique(teacherInformationPO.getName())){
            courseScheduleService.getBaseMapper().updateTeacherPlatformAccount(teacherInformationPO.getName(), null, null,
                    account.getAccountName());
        }else{
            courseScheduleService.getBaseMapper().updateTeacherPlatformAccount(teacherInformationPO.getName(), teacherInformationPO.getWorkNumber(),
                    teacherInformationPO.getIdCardNumber(),
                    account.getAccountName());
        }
    }

    /**
     * 批量为一些老师生成登录账户
     */
    @Test
    public void test1(){

        PlatformUserMapper platformUserMapper = platformUserService.getBaseMapper();

        List<TeacherInformationPO> teacherInformationPOS = teacherInformationService.getBaseMapper().selectList(null);
        int countSuccess = 0;
        int countFail = 0;

        // 存儲教師創建的平台用戶賬號 可能是工號、身份證號碼或者是手機號碼
        ArrayList<TeacherAccount>  accountGenerateAll = new ArrayList<>();

        for(TeacherInformationPO teacherInformationPO: teacherInformationPOS){
            String word_number = teacherInformationPO.getWorkNumber();
            String id_number = teacherInformationPO.getIdCardNumber();
            String phone = teacherInformationPO.getPhone();
            TeacherAccount accountGenerate = null;
            String generateUserName = null;

            if (word_number != null && word_number.trim().length() > 0) {
                String account = "T" + word_number;
                List<PlatformUserPO> platformUserPOS = platformUserMapper.selectPlatformUsers1(account);
                if(platformUserPOS.size() == 0){
//                    log.info("创建成功!");
                    accountGenerate = new TeacherAccount("工号", account);
                    generateUserName = account;
                }else{
                    log.error("生成账号失败 " + teacherInformationPO.toString() + "\n 该账号 " + account + "  平台已存在");
                }
            }
            if(id_number != null && id_number.trim().length() > 0){
                String account = "T" + id_number;
                List<PlatformUserPO> platformUserPOS = platformUserMapper.selectPlatformUsers1(account);
                if(platformUserPOS.size() == 0){
//                    log.info("创建成功!");
                    if(accountGenerate == null){
                        accountGenerate = new TeacherAccount("身份证号码", account);
                        generateUserName = account;
                    }
                }else{
                    log.error("生成账号失败 " + teacherInformationPO.toString() + "\n 该账号 " + account + "  平台已存在");
                }
            }

            if (phone != null && phone.trim().length() > 0) {
                String account = "T" + phone;
                List<PlatformUserPO> platformUserPOS = platformUserMapper.selectPlatformUsers1(account);
                if(platformUserPOS.size() == 0){
//                    log.info("创建成功!");
                    if(accountGenerate == null){
                        accountGenerate = new TeacherAccount("手机号码", account);
                        generateUserName = account;
                    }
                }else{
                    log.error("生成账号失败 " + teacherInformationPO.toString() + "\n 该账号 " + account + "  平台已存在");
                }
            }

            if(accountGenerate != null){
//                log.info("成功创建账号 " + accountGenerate.toString());
                countSuccess += 1;

                accountGenerateAll.add(accountGenerate);
            }else{
                log.error("没能成功创建账号 " + teacherInformationPO.toString());
                countFail += 1;
            }
        }
        log.info("总共系统内 " + teacherInformationPOS.size() + " 位老师 成功创建 " + countSuccess + " 个账号 未能创建 " + countFail + " 个账号");
        List<PlatformUserRO> generateNewAccounts = new ArrayList<>();
        for(TeacherAccount account: accountGenerateAll){
            String teacherNumber = account.getAccountName().replace("T", "");
            // 根據账号类型 比如工号、身份证号码、手机号码去匹配排课表、教师信息表中的信息
            // 更新教师信息表
            if(account.getAccountType().equals("工号")){
                List<TeacherInformationPO> teacherInformationPOS1 = teacherInformationService.getBaseMapper().selectByWorkNumber(teacherNumber);

                if(teacherInformationPOS1.size() == 0){
                    log.error("不存在该工号的教师 " + account);
                } else if (teacherInformationPOS1.size() == 1) {
                    // 更新教师表 和排课表中的教师平台账号
                    updateTeacherInformationCourseSchedule(teacherInformationPOS1, account);

                }else{
                    log.error("该工号匹配到了多名教师 " + account);
                }



            }else if(account.getAccountType().equals("身份证号码")){
                List<TeacherInformationPO> teacherInformationPOS1 = teacherInformationService.getBaseMapper().selectByIdCardNumber(teacherNumber);

                if(teacherInformationPOS1.size() == 0){
                    log.error("不存在该身份证号码的教师 " + account);
                } else if (teacherInformationPOS1.size() == 1) {
                    // 更新教师表 和排课表中的教师平台账号
                    updateTeacherInformationCourseSchedule(teacherInformationPOS1, account);
                }else{
                    log.error("该身份证号码匹配到了多名教师 " + account);
                }

            }else if(account.getAccountType().equals("手机号码")){
                List<TeacherInformationPO> teacherInformationPOS1 = teacherInformationService.getBaseMapper().selectByPhone(teacherNumber);

                if(teacherInformationPOS1.size() == 0){
                    log.error("不存在该手机号码的教师 " + account);
                } else if (teacherInformationPOS1.size() == 1) {
                    // 更新教师表 和排课表中的教师平台账号
                    updateTeacherInformationCourseSchedule(teacherInformationPOS1, account);
                }else{
                    log.error("该手机号码匹配到了多名教师 " + account + "\n" + teacherInformationPOS1.toString());
                }

            }else{
                log.error("异常的账户类型 " + account.toString());
            }


            PlatformUserRO platformUserRO = new PlatformUserRO();
            platformUserRO.setUsername(account.getAccountName());
            // 获取 account 的后六位作为密码
            if(account.getAccountName().length() >= 6) {
                platformUserRO.setPassword(account.getAccountName().substring(account.getAccountName().length() - 6));
            } else {
                platformUserRO.setPassword(account.getAccountName()); // 如果 account 长度小于6，则直接使用 account 作为密码
            }
            platformUserRO.setRoleId(2L);
            generateNewAccounts.add(platformUserRO);
        }

        platformUserService.batchCreateUser(generateNewAccounts);
    }
}
