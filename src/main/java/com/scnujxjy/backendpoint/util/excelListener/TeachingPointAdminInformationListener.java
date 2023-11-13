package com.scnujxjy.backendpoint.util.excelListener;

import cn.hutool.crypto.digest.SM3;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scnujxjy.backendpoint.dao.entity.basic.PlatformUserPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_point.TeachingPointAdminInformationPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_point.TeachingPointInformationPO;
import com.scnujxjy.backendpoint.dao.mapper.basic.PlatformUserMapper;
import com.scnujxjy.backendpoint.dao.mapper.college.CollegeAdminInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.college.CollegeInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_point.TeachingPointAdminInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_point.TeachingPointInformationMapper;
import com.scnujxjy.backendpoint.model.ro.basic.PlatformUserRO;
import com.scnujxjy.backendpoint.model.ro.teaching_point.TeachingPointAdminInformationExcelImportRO;
import com.scnujxjy.backendpoint.model.ro.teaching_point.TeachingPointAdminInformationImportError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class TeachingPointAdminInformationListener implements ReadListener<TeachingPointAdminInformationExcelImportRO> {
    private final TeachingPointInformationMapper teachingPointInformationMapper;
    private final TeachingPointAdminInformationMapper teachingPointAdminInformationMapper;

    private final PlatformUserMapper platformUserMapper;

    private final SM3 sm3 = new SM3();

    private List<TeachingPointAdminInformationImportError> outputDataList = new ArrayList<>();

    @Override
    public void invoke(TeachingPointAdminInformationExcelImportRO data, AnalysisContext context) {
        TeachingPointAdminInformationImportError teachingPointAdminInformationImportError = new TeachingPointAdminInformationImportError();
        BeanUtils.copyProperties(data, teachingPointAdminInformationImportError);
        try{
            log.info("发现一条教务员记录 " + data);
            TeachingPointAdminInformationPO teachingPointAdminInformationPO = new TeachingPointAdminInformationPO();
            BeanUtils.copyProperties(data, teachingPointAdminInformationPO);
            String teachingPointName = data.getTeachingPointName();
            TeachingPointInformationPO teachingPointInformationPO = null;
            try {
                teachingPointInformationPO = teachingPointInformationMapper.selectOne(new LambdaQueryWrapper<TeachingPointInformationPO>()
                        .eq(TeachingPointInformationPO::getTeachingPointName, teachingPointName));
                String teachingPointId = teachingPointInformationPO.getTeachingPointId();
            }catch (Exception e){
            throw new IllegalArgumentException("获取教学点信息失败 " + data);
        }

                String idCardNumber = teachingPointAdminInformationPO.getIdCardNumber();
                String userName = "M" + idCardNumber;
                PlatformUserPO platformUserPO = platformUserMapper.selectOne(new LambdaQueryWrapper<PlatformUserPO>().eq(PlatformUserPO::getUsername, userName));
                if(platformUserPO != null){
                    // 账号已经存在了

                }else{
                    // 账号不存在直接创建
                    platformUserPO = new PlatformUserPO();
                    platformUserPO.setUsername(userName);
                    platformUserPO.setPassword(sm3.digestHex(userName.substring(userName.length() - 6)));
                    platformUserPO.setRoleId(7L);
                    platformUserPO.setName(data.getName());
                    int insert = platformUserMapper.insert(platformUserPO);
                    log.info(platformUserPO.getName() + " " + teachingPointInformationPO.getTeachingPointName() + " 插入成功 \n" +
                            "插入结果 " + insert);
                }

                // 校验该用户是否存在了
                TeachingPointAdminInformationPO teachingPointAdminInformationPO1 = teachingPointAdminInformationMapper.selectOne(new LambdaQueryWrapper<TeachingPointAdminInformationPO>()
                        .eq(TeachingPointAdminInformationPO::getIdCardNumber, data.getIdCardNumber()));
                if(teachingPointAdminInformationPO1 != null){
                    // 已经存在 无需创建
                    throw new IllegalArgumentException("该教务员已经存在 " + data.getName() + " 教学点为 " + data.getTeachingPointName());

                }

                teachingPointAdminInformationPO.setUserId(String.valueOf(platformUserPO.getUserId()));
                teachingPointAdminInformationPO.setTeachingPointId(teachingPointInformationPO.getTeachingPointId());
                int i = teachingPointAdminInformationMapper.insert(teachingPointAdminInformationPO);
                log.info("创建成功 userId " + teachingPointAdminInformationPO + "\n 刷新结果 " + i);



        }catch (IllegalArgumentException e){
            teachingPointAdminInformationImportError.setErrorMsg(e.toString());
            // 将输出数据添加到列表中
            outputDataList.add(teachingPointAdminInformationImportError);
        }
        catch (Exception e){
            teachingPointAdminInformationImportError.setErrorMsg("教学点教务员导入失败 " + e.toString());
            // 将输出数据添加到列表中
            outputDataList.add(teachingPointAdminInformationImportError);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {

        if(outputDataList.size() > 0) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            String currentDateTime = LocalDateTime.now().format(formatter);
            String relativePath = "data_import_error_excel/teachingPointData";
            String errorFileName = currentDateTime + "_教学点教务员导入.xlsx";

            // 检查目录是否存在，如果不存在，则创建
            Path path = Paths.get(relativePath);
            if (!Files.exists(path)) {
                try {
                    Files.createDirectories(path); // 创建目录及所有必需的父目录
                } catch (IOException e) {
                    e.printStackTrace();
                    // 在这里处理异常，例如记录日志或通知用户
                }
            }

            // 写入 Excel 文件
            EasyExcel.write(relativePath + "/" + errorFileName,
                    TeachingPointAdminInformationImportError.class).sheet("Sheet1").doWrite(outputDataList);
        } else {
            log.info("全部成功导入了！！！");
        }
    }
}
