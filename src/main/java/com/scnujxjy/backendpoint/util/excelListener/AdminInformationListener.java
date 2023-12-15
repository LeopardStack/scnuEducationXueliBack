package com.scnujxjy.backendpoint.util.excelListener;

import cn.hutool.crypto.digest.SM3;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scnujxjy.backendpoint.dao.entity.basic.AdminInfoPO;
import com.scnujxjy.backendpoint.dao.entity.basic.PlatformRolePO;
import com.scnujxjy.backendpoint.dao.entity.basic.PlatformUserPO;
import com.scnujxjy.backendpoint.dao.mapper.admission_information.AdmissionInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.basic.AdminInfoMapper;
import com.scnujxjy.backendpoint.dao.mapper.basic.PlatformRoleMapper;
import com.scnujxjy.backendpoint.dao.mapper.basic.PlatformUserMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.PersonalInfoMapper;
import com.scnujxjy.backendpoint.model.ro.admission_information.AdmissionInformationRO;
import com.scnujxjy.backendpoint.model.ro.basic.AdminInfoExcelOutputVO;
import com.scnujxjy.backendpoint.model.ro.basic.AdminInformationRO;
import com.scnujxjy.backendpoint.model.vo.admission_information.AdmissionInformationExcelOutputVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.utils.StringUtils;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminInformationListener  extends AnalysisEventListener<AdminInformationRO> {
    private final SM3 sm3 = new SM3();

    private AdminInfoMapper adminInfoMapper;

    private PlatformUserMapper platformUserMapper;

    private PlatformRoleMapper platformRoleMapper;


    private List<AdminInfoExcelOutputVO> outputDataList = new ArrayList<>();

    public AdminInformationListener(AdminInfoMapper adminInfoMapper,
                                    PlatformRoleMapper platformRoleMapper,
                                        PlatformUserMapper platformUserMapper){
        this.adminInfoMapper = adminInfoMapper;
        this.platformRoleMapper = platformRoleMapper;
        this.platformUserMapper = platformUserMapper;
    }

    @Override
    public void invoke(AdminInformationRO adminInformationRO, AnalysisContext analysisContext) {
        AdminInfoExcelOutputVO outputData = new AdminInfoExcelOutputVO();
        log.info("管理人员数据一条 " + adminInformationRO);
        try {
            List<PlatformRolePO> platformRolePOS = platformRoleMapper.selectList(null);
            // 获取部门名称
            String department = adminInformationRO.getDepartment();

            // 在platformRolePOS列表中查找第一个其roleName包含department的PlatformRolePO对象
            PlatformRolePO matchingRole = platformRolePOS.stream()
                    .filter(role -> role.getRoleName().contains(department))
                    .findFirst()
                    .orElse(null); // 如果没有找到符合条件的对象，则返回null
            if(matchingRole == null){
                throw new IllegalArgumentException("该管理人员找不到它所属的部门");
            }else{
                // 先根据公告查看该管理人员是否账号 如果有 则直接退出
                String wordNumber = adminInformationRO.getWordNumber();
                String idNumber = adminInformationRO.getIdNumber();
                if(!StringUtils.isBlank(wordNumber)){
                    PlatformUserPO platformUserPO = platformUserMapper.selectOne(new LambdaQueryWrapper<PlatformUserPO>()
                            .eq(PlatformUserPO::getUsername, "M" + adminInformationRO.getWordNumber()));
                    if(platformUserPO != null){
                        throw new IllegalArgumentException("该管理人员已经有账号(工号)了 " + platformUserPO.getUsername());
                    }else{
                        // 此时创建账号
                        String userName = "M" + adminInformationRO.getWordNumber();
                        PlatformUserPO platformUserPO1 = new PlatformUserPO();
                        platformUserPO1.setUsername(userName);
                        String encryptedPassword = sm3.digestHex(userName.substring(userName.length() - 6));
                        platformUserPO1.setPassword(encryptedPassword);
                        platformUserPO1.setRoleId(matchingRole.getRoleId());
                        platformUserPO1.setName(adminInformationRO.getName());
                        int insert = platformUserMapper.insert(platformUserPO1);
                        if(insert > 0){
                            // 更新管理人员信息
                            AdminInfoPO adminInfoPO = new AdminInfoPO();
                            adminInfoPO.setName(adminInformationRO.getName());
                            adminInfoPO.setIdNumber(adminInformationRO.getIdNumber());
                            adminInfoPO.setUserId(String.valueOf(platformUserPO1.getUserId()));
                            adminInfoPO.setRoleId(matchingRole.getRoleId());
                            adminInfoPO.setDepartment(adminInformationRO.getDepartment());
                            adminInfoPO.setWorkNumber(adminInformationRO.getWordNumber());
                            adminInfoPO.setPrivatePhone(adminInformationRO.getPrivatePhone());
                            adminInfoMapper.insert(adminInfoPO);
                            throw new IllegalArgumentException("插入成功");
                        }else{
                            throw new IllegalArgumentException("插入失败");
                        }
                    }
                }else if(!StringUtils.isBlank(idNumber)){
                    PlatformUserPO platformUserPO = platformUserMapper.selectOne(new LambdaQueryWrapper<PlatformUserPO>()
                            .eq(PlatformUserPO::getUsername, "M" + adminInformationRO.getIdNumber()));
                    if(platformUserPO != null){
                        throw new IllegalArgumentException("该管理人员已经有账号(身份证号码)了 " + platformUserPO.getUsername());
                    }else{
                        String userName = "M" + adminInformationRO.getIdNumber();
                        PlatformUserPO platformUserPO1 = new PlatformUserPO();
                        platformUserPO1.setUsername(userName);
                        String encryptedPassword = sm3.digestHex(userName.substring(userName.length() - 6));
                        platformUserPO1.setPassword(encryptedPassword);
                        platformUserPO1.setRoleId(matchingRole.getRoleId());
                        platformUserPO1.setName(adminInformationRO.getName());
                        int insert = platformUserMapper.insert(platformUserPO1);
                        if(insert > 0){
                            // 更新管理人员信息
                            AdminInfoPO adminInfoPO = new AdminInfoPO();
                            adminInfoPO.setName(adminInformationRO.getName());
                            adminInfoPO.setIdNumber(adminInformationRO.getIdNumber());
                            adminInfoPO.setUserId(String.valueOf(platformUserPO1.getUserId()));
                            adminInfoPO.setRoleId(matchingRole.getRoleId());
                            adminInfoPO.setDepartment(adminInformationRO.getDepartment());
                            adminInfoPO.setWorkNumber(adminInformationRO.getWordNumber());
                            adminInfoPO.setPrivatePhone(adminInformationRO.getPrivatePhone());
                            adminInfoMapper.insert(adminInfoPO);
                            throw new IllegalArgumentException("插入成功");
                        }else{
                            throw new IllegalArgumentException("插入失败");
                        }
                    }
                }else{
                    throw new IllegalArgumentException("该管理人员未提供符合规范的工号或者身份证号码");
                }

            }
        }catch (Exception e){
            outputData.setErrorMessage(e.getMessage()); // 设置错误信息
            outputDataList.add(outputData); // 将输出数据添加到列表中
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
// 使用EasyExcel写入数据到新的Excel文件中
        if(outputDataList.size() > 0) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").withZone(ZoneId.of("Asia/Shanghai"));

            String currentDateTime = LocalDateTime.now().format(formatter);
            String relativePath = "data_import_error_excel/adminInformationData";
            String errorFileName = currentDateTime + "_管理人员数据导入失败数据.xlsx";
            // 检查目录是否存在，如果不存在则创建它
            File directory = new File(relativePath);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // 写入Excel文件
            EasyExcel.write(relativePath + "/" + errorFileName, AdminInfoExcelOutputVO.class)
                    .sheet("Sheet1").doWrite(outputDataList);

            log.info("管理人员数据存在错误记录，已写入 " + errorFileName);
        }else{
            log.info("管理人员数据没有任何错误");
        }
    }
}
