package com.scnujxjy.backendpoint.service.TeacherInformation;

import com.scnujxjy.backendpoint.dao.entity.basic.PlatformUserPO;
import com.scnujxjy.backendpoint.dao.entity.core_data.TeacherInformationPO;
import com.scnujxjy.backendpoint.dao.mapper.basic.PlatformUserMapper;
import com.scnujxjy.backendpoint.model.ro.basic.PlatformRoleRO;
import com.scnujxjy.backendpoint.model.ro.basic.PlatformUserRO;
import com.scnujxjy.backendpoint.service.basic.PlatformUserService;
import com.scnujxjy.backendpoint.service.core_data.TeacherInformationService;
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
    private PlatformUserService platformUserService;

    /**
     * 批量为一些老师生成登录账户
     */
    @Test
    public void test1(){

        PlatformUserMapper platformUserMapper = platformUserService.getBaseMapper();

        List<TeacherInformationPO> teacherInformationPOS = teacherInformationService.getBaseMapper().selectList(null);
        int countSuccess = 0;
        int countFail = 0;

        ArrayList<String>  accountGenerateAll = new ArrayList<>();

        for(TeacherInformationPO teacherInformationPO: teacherInformationPOS){
            String word_number = teacherInformationPO.getWorkNumber();
            String id_number = teacherInformationPO.getIdCardNumber();
            String phone = teacherInformationPO.getPhone();
            HashMap<String, String> accountGenerate = new HashMap<>();
            String generateUserName = null;

            if (word_number != null && word_number.trim().length() > 0) {
                String account = "T" + word_number;
                List<PlatformUserPO> platformUserPOS = platformUserMapper.selectPlatformUsers1(account);
                if(platformUserPOS.size() == 0){
//                    log.info("创建成功!");
                    accountGenerate.put("工号创建的账号", account);
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
                    if(accountGenerate.size() == 0){
                        accountGenerate.put("身份证号码创建的账号", account);
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
                    if(accountGenerate.size() == 0){
                        accountGenerate.put("手机号码创建的账号", account);
                        generateUserName = account;
                    }
                }else{
                    log.error("生成账号失败 " + teacherInformationPO.toString() + "\n 该账号 " + account + "  平台已存在");
                }
            }

            if(accountGenerate.size() > 0){
                log.info("成功创建账号 " + accountGenerate.toString());
                countSuccess += 1;

                accountGenerateAll.add(generateUserName);
            }else{
                log.error("没能成功创建账号 " + teacherInformationPO.toString());
                countFail += 1;
            }
        }
        log.info("总共系统内 " + teacherInformationPOS.size() + " 位老师 成功创建 " + countSuccess + " 个账号 未能创建 " + countFail + " 个账号");
        List<PlatformUserRO> generateNewAccounts = new ArrayList<>();
        for(String account: accountGenerateAll){
            PlatformUserRO platformUserRO = new PlatformUserRO();
            platformUserRO.setUsername(account);
            // 获取 account 的后六位作为密码
            if(account.length() >= 6) {
                platformUserRO.setPassword(account.substring(account.length() - 6));
            } else {
                platformUserRO.setPassword(account); // 如果 account 长度小于6，则直接使用 account 作为密码
            }
            platformUserRO.setRoleId(2L);
            generateNewAccounts.add(platformUserRO);
        }

        platformUserService.batchCreateUser(generateNewAccounts);
    }
}
