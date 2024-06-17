package com.scnujxjy.backendpoint.enrollmentPlanTest;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.scnujxjy.backendpoint.dao.entity.teaching_point.TeachingPointInformationPO;
import com.scnujxjy.backendpoint.dao.mapper.teaching_point.TeachingPointInformationMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Data
public class TeachingPointInformationListener  extends AnalysisEventListener<TeachingPointDataRO> {

    @Resource
    private TeachingPointInformationMapper teachingPointInformationMapper;

    public TeachingPointInformationListener(TeachingPointInformationMapper teachingPointInformationMapper){
        this.teachingPointInformationMapper = teachingPointInformationMapper;
    }

    @Override
    public void invoke(TeachingPointDataRO teachingPointDataRO, AnalysisContext analysisContext) {
        log.info("读取到一行数据" + teachingPointDataRO);
        if(StringUtils.isBlank(teachingPointDataRO.getTeachingPointAddress()) || StringUtils.isBlank(teachingPointDataRO.getTeachingPointContactNumbers())){
            log.error("不在本次招生计划里面的教学点" + teachingPointDataRO);
        }else{
            log.info("处理后的电话号码列表: {}", teachingPointDataRO.getContactNumbersList());

            TeachingPointInformationPO teachingPointInformationPO = teachingPointInformationMapper.selectOne(new LambdaQueryWrapper<TeachingPointInformationPO>()
                    .eq(TeachingPointInformationPO::getTeachingPointName, teachingPointDataRO.getTeachingPointName()));
            if(teachingPointInformationPO == null){
                log.error("该教学点不在数据库中 " + teachingPointDataRO);
            }else{
                String address = teachingPointInformationPO.getAddress();
                String teachingPointAddress = teachingPointDataRO.getTeachingPointAddress();
                if(!address.equals(teachingPointAddress)){
                    teachingPointInformationPO.setAddress(teachingPointDataRO.getTeachingPointAddress());
                    log.info("更新地址 " +teachingPointInformationPO);
                }

                String phone = teachingPointInformationPO.getPhone();
                List<String> contactNumbersList = teachingPointDataRO.getContactNumbersList();
                boolean ident = true;
                for(String phoneNumber : contactNumbersList){
                    if(!phone.contains(phoneNumber)){
                        ident = false;
                    }
                }


                // 如果存在联系方式不在教学点联系方式库中 则更新
                if (!ident) {
                    String joinedNumbers = String.join(", ", contactNumbersList);
                    teachingPointInformationPO.setPhone(joinedNumbers); // 假设 setPhone 接受字符串类型
                    log.info("更新联系方式 " + teachingPointInformationPO);
                }


                String enrollmentArea = teachingPointDataRO.getEnrollmentArea();
                String enrollmentArea1 = teachingPointInformationPO.getEnrollmentArea();

                // 更新招生区域
                if((enrollmentArea != null && enrollmentArea1 == null) || !enrollmentArea1.equals(enrollmentArea)){
                    teachingPointInformationPO.setEnrollmentArea(enrollmentArea);
                }

                // 更新资格
                teachingPointInformationPO.setQualification(true);

                int i = teachingPointInformationMapper.updateById(teachingPointInformationPO);
                if(i <= 0){
                    log.error("更新失败, 数据库更新失败" + teachingPointDataRO);
                }
            }


        }

        log.info("*********************************************************************************************" );

    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
