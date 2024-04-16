package com.scnujxjy.backendpoint.scnuXueliToolsTest;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scnujxjy.backendpoint.dao.entity.basic.PlatformUserPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.ClassInformationPO;
import com.scnujxjy.backendpoint.model.ro.basic.PlatformUserRO;
import com.scnujxjy.backendpoint.service.basic.PlatformUserService;
import com.scnujxjy.backendpoint.service.registration_record_card.ClassInformationService;
import com.scnujxjy.backendpoint.util.tool.ScnuXueliTools;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Set;

@SpringBootTest
@Slf4j
public class Test1 {
    @Resource
    private ScnuXueliTools scnuXueliTools;

    @Resource
    private PlatformUserService platformUserService;

    @Resource
    private ClassInformationService classInformationService;

    @Test
    public void test1(){
        PlatformUserRO platformUserRO = new PlatformUserRO()
                .setUsername("M15915910399")
                .setPassword("910399")
                ;
        platformUserService.userLogin(platformUserRO);
        StpUtil.login("M15915910399");

        Set<String> teachingPointClassIdetifierSet = scnuXueliTools.getTeachingPointClassIdetifierSet();
        Set<String> teachingPointClassNameSet = scnuXueliTools.getTeachingPointClassNameSet();
        log.info("\n 该教学的班级有这些 " + teachingPointClassNameSet);
        log.info("\n 该教学的班级标识有这些 " + teachingPointClassIdetifierSet);

        for(String classIdentifier : teachingPointClassIdetifierSet){
            ClassInformationPO classInformationPO = classInformationService.getBaseMapper().selectOne(new LambdaQueryWrapper<ClassInformationPO>()
                    .eq(ClassInformationPO::getClassIdentifier, classIdentifier));
            log.info("班级名称 " + classInformationPO.getClassName());
        }
    }
}
