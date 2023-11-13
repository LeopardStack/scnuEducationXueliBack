package com.scnujxjy.backendpoint.TeachingPointInfoImport;

import cn.hutool.crypto.digest.SM3;
import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scnujxjy.backendpoint.dao.entity.basic.PlatformUserPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_point.TeachingPointAdminInformationPO;
import com.scnujxjy.backendpoint.dao.mapper.basic.PlatformUserMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_point.TeachingPointAdminInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_point.TeachingPointInformationMapper;
import com.scnujxjy.backendpoint.mapperTest.AdminDataListener;
import com.scnujxjy.backendpoint.mapperTest.ExcelAdminData;
import com.scnujxjy.backendpoint.model.ro.teaching_point.TeachingPointAdminInformationExcelImportRO;
import com.scnujxjy.backendpoint.util.excelListener.TeachingPointAdminInformationListener;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

/**
 * 导入教学点信息 并且生成账号
 */

@SpringBootTest
@Slf4j
public class Test1 {

    @Resource
    private TeachingPointInformationMapper teachingPointInformationMapper;
    @Resource
    private TeachingPointAdminInformationMapper teachingPointAdminInformationMapper;
    @Resource
    private PlatformUserMapper platformUserMapper;

    /**
     * 导入教学点教务人员
     * 已经存在的就说明一下 不存在的则导入进去 并且把 user_id 更新 user_id 为 它在 platformUser 的 id
     */
    @Test
    public void test1(){
        String excelPath = "D:\\MyProject\\xueliJYPlatform2Update20231009\\xueliBackEnd\\src\\main\\resources\\data\\教学点教务员导入\\教学点教务员信息导入.xlsx";
        TeachingPointAdminInformationListener listener = new TeachingPointAdminInformationListener(teachingPointInformationMapper, teachingPointAdminInformationMapper,
                platformUserMapper);
        EasyExcel.read(excelPath, TeachingPointAdminInformationExcelImportRO.class, listener).sheet().headRowNumber(1).doRead();
    }

    @Test
    public void test2(){
        // 将现有的教务员的 user_id 刷新为它 platformUser 表中的 userId
        List<TeachingPointAdminInformationPO> teachingPointAdminInformationPOS = teachingPointAdminInformationMapper.selectList(null);
        for(TeachingPointAdminInformationPO teachingPointAdminInformationPO: teachingPointAdminInformationPOS){
            String idCardNumber = teachingPointAdminInformationPO.getIdCardNumber();
            String userName = "M" + idCardNumber;
            PlatformUserPO platformUserPO = platformUserMapper.selectOne(new LambdaQueryWrapper<PlatformUserPO>().eq(PlatformUserPO::getUsername, userName));
            if(platformUserPO != null){
                // 账号已经存在了
                teachingPointAdminInformationPO.setUserId(String.valueOf(platformUserPO.getUserId()));
                int i = teachingPointAdminInformationMapper.update(teachingPointAdminInformationPO, new LambdaQueryWrapper<TeachingPointAdminInformationPO>()
                        .eq(TeachingPointAdminInformationPO::getIdCardNumber, idCardNumber));
                log.info("刷新成功 userId " + teachingPointAdminInformationPO + "\n 刷新结果 " + i);
            }else{
                // 不存在 创建账号
                log.info("该教务员没有账号 " + teachingPointAdminInformationPO);
            }
            // 创建后 直接将 userId 刷新到 TeachingPointAdminInformationPO

        }
    }
}
