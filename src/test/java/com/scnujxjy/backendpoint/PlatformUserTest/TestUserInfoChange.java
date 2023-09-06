package com.scnujxjy.backendpoint.PlatformUserTest;

import com.scnujxjy.backendpoint.dao.entity.registration_record_card.StudentStatusPO;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.StudentStatusMapper;
import com.scnujxjy.backendpoint.model.ro.basic.PlatformUserRO;
import com.scnujxjy.backendpoint.service.basic.PlatformUserService;
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
    private StudentStatusMapper studentStatusMapper;

    @Test
    public void changePassword(){
        Boolean aBoolean = platformUserService.changePassword(2L, "123456");
        Boolean aBoolean1 = platformUserService.changePassword(3L, "123456");
        Boolean aBoolean2 = platformUserService.changePassword(4L, "123456");
        log.info("修改密码 " + aBoolean);
    }

    @Test
    public void addManager(){
        List<PlatformUserRO> platformUserROList = new ArrayList<>();
        PlatformUserRO platformUserRO = new PlatformUserRO();
        platformUserRO.setUsername("M001");
        platformUserRO.setPassword("123456");
        platformUserRO.setRoleId(6L);
        platformUserROList.add(platformUserRO);
        platformUserService.batchCreateUser(platformUserROList);
    }

    /**
     * 指定学生群体创建登录账号
     */
    @Test
    public void addStudentLoginAccount(){
        List<StudentStatusPO> studentStatusPOS = studentStatusMapper.selectStudentsByGradeCollege("2023", "计算机学院");
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
    }
}
