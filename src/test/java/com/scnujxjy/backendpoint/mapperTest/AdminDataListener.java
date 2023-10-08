package com.scnujxjy.backendpoint.mapperTest;

import cn.hutool.crypto.digest.SM3;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scnujxjy.backendpoint.dao.entity.basic.PlatformUserPO;
import com.scnujxjy.backendpoint.dao.entity.college.CollegeAdminInformationPO;
import com.scnujxjy.backendpoint.dao.entity.college.CollegeInformationPO;
import com.scnujxjy.backendpoint.dao.mapper.basic.PlatformUserMapper;
import com.scnujxjy.backendpoint.dao.mapper.college.CollegeAdminInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.college.CollegeInformationMapper;
import com.scnujxjy.backendpoint.model.vo.teaching_process.CourseInformationExcelOutputVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class AdminDataListener implements ReadListener<ExcelAdminData> {
    private final CollegeAdminInformationMapper collegeAdminInformationMapper;
    private final CollegeInformationMapper collegeInformationMapper;

    private final PlatformUserMapper platformUserMapper;

    private final SM3 sm3;

    private List<AdminInfoImportError> outputDataList = new ArrayList<>();

    @Override
    public void invoke(ExcelAdminData data, AnalysisContext context) {
        AdminInfoImportError adminInfoImportError = new AdminInfoImportError();
        BeanUtils.copyProperties(data, adminInfoImportError);
        try {

            // Convert ExcelAdminData to CollegeAdminInformationPO

            CollegeAdminInformationPO po = new CollegeAdminInformationPO();
            po.setName(data.getName());
            po.setPhone(data.getPhone());
            po.setWorkNumber(data.getWorkNumber());
            po.setIdNumber(data.getIdNumber());
            String collegeName = data.getCollegeName();


            // 教务员信息一旦插入，立刻生成它的 userName 、角色和 默认密码
            PlatformUserPO platformUserPO = new PlatformUserPO();

            if(po.getWorkNumber() != null && po.getWorkNumber().trim().length() > 0){
                platformUserPO.setUsername("M" + po.getWorkNumber());
                List<PlatformUserPO> platformUserPOS = platformUserMapper.selectList(new LambdaQueryWrapper<PlatformUserPO>().eq(PlatformUserPO::getUsername,
                        "M" + po.getWorkNumber()));
                if(platformUserPOS.size() == 1){
                    log.info("该教务员已经在平台内拥有账号 " + platformUserPOS);
                }else if(platformUserPOS.size() == 0){
                    platformUserPO.setRoleId(6L);
                    String username = platformUserPO.getUsername();
                    String newPassword = username + "2023A@";
                    if (username.length() > 6) {
                        newPassword = username.substring(username.length() - 6);
                    }
                    // 密码加密
                    String encryptedPassword = sm3.digestHex(newPassword);
                    platformUserPO.setPassword(encryptedPassword);

                    platformUserMapper.insert(platformUserPO);
                }else{
                    throw new RuntimeException("该教务员在平台内拥有多个账号 " + platformUserPOS);
                }
            }else if(po.getIdNumber() != null && po.getIdNumber().trim().length() > 0){
                platformUserPO.setUsername("M" + po.getIdNumber());
                List<PlatformUserPO> platformUserPOS = platformUserMapper.selectList(new LambdaQueryWrapper<PlatformUserPO>().eq(PlatformUserPO::getUsername,
                        "M" + po.getIdNumber()));
                if(platformUserPOS.size() == 1){
                    log.info("该教务员已经在平台内拥有账号 " + platformUserPOS);
                }else if(platformUserPOS.size() == 0){
                    platformUserPO.setRoleId(6L);
                    String username = platformUserPO.getUsername();
                    String newPassword = username + "2023A@";
                    if (username.length() > 6) {
                        newPassword = username.substring(username.length() - 6);
                    }
                    // 密码加密
                    String encryptedPassword = sm3.digestHex(newPassword);
                    platformUserPO.setPassword(encryptedPassword);

                    platformUserMapper.insert(platformUserPO);
                }else{
                    throw new RuntimeException("该教务员在平台内拥有多个账号 " + platformUserPOS);
                }
            }else{
                throw new RuntimeException("提供的二级学院教务员账号必须有工号/身份证信息，否则无法产生账号 ");
            }

            List<CollegeInformationPO> collegeInformationPOS = collegeInformationMapper.selectByCollegeName(collegeName);
            if (collegeInformationPOS.size() == 1) {
                PlatformUserPO userPO = platformUserMapper.selectOne(new LambdaQueryWrapper<PlatformUserPO>().eq(
                        PlatformUserPO::getUsername, platformUserPO.getUsername()
                ));
                String userId = String.valueOf(userPO.getUserId());
                List<CollegeAdminInformationPO> collegeAdminInformationPOS = collegeAdminInformationMapper.
                        selectList(new LambdaQueryWrapper<CollegeAdminInformationPO>().eq(
                        CollegeAdminInformationPO::getUserId, userId));
                if(collegeAdminInformationPOS.size() > 0){
                    throw new RuntimeException("该教务员信息已经在数据库中了 ");
                }

                CollegeInformationPO collegeInformationPO = collegeInformationPOS.get(0);
                String collegeId = collegeInformationPO.getCollegeId();
                po.setCollegeId(collegeId);

                po.setUserId("" + userId);
                log.info("读取到的原始教务员信息 " + data + "\n插入数据库中的信息 " + po);
                int insert = collegeAdminInformationMapper.insert(po);
                log.info("二级学院教务员插入结果 " + insert);

            }else if(collegeInformationPOS.size() > 1){
                throw new RuntimeException("该学院在数据库中存在多条信息 " + collegeInformationPOS.toString());
            }else{
                throw new RuntimeException("该学院在数据库中不存在 ");
            }

            // Save to DB
//        mapper.insert(po);
        }catch (Exception e){
            adminInfoImportError.setErrorMsg("二级学院教务员信息插入失败 " + e.toString());
            outputDataList.add(adminInfoImportError); // 将输出数据添加到列表中
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        if(outputDataList.size() > 0) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            String currentDateTime = LocalDateTime.now().format(formatter);
            String relativePath = "data_import_error_excel/collegeAdminData";
            String errorFileName = currentDateTime + "_二级学院教务员导入失败数据.xlsx";
            EasyExcel.write(relativePath + "/" + errorFileName,
                    AdminInfoImportError.class).sheet("Sheet1").doWrite(outputDataList);
        }else{
            log.info("全部成功导入了！！！");
        }
    }
}

