package com.scnujxjy.backendpoint.util.tool;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.scnujxjy.backendpoint.dao.entity.basic.PlatformUserPO;
import com.scnujxjy.backendpoint.dao.entity.college.CollegeAdminInformationPO;
import com.scnujxjy.backendpoint.dao.entity.college.CollegeInformationPO;
import com.scnujxjy.backendpoint.dao.mapper.basic.PlatformUserMapper;
import com.scnujxjy.backendpoint.dao.mapper.college.CollegeAdminInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.college.CollegeInformationMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.index.qual.SameLen;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

/**
 * @author hp
 */
@Component
@Slf4j
public class ScnuXueliTools {
    @Resource
    private PlatformUserMapper platformUserMapper;

    @Resource
    private CollegeAdminInformationMapper collegeAdminInformationMapper;

    @Resource
    private CollegeInformationMapper collegeInformationMapper;


    public CollegeInformationPO getUserBelongCollege(){
        try{
            String loginId = (String) StpUtil.getLoginId();
            if (StrUtil.isBlank(loginId)) {
                return null;
            }
            PlatformUserPO platformUserPO = platformUserMapper.selectOne(Wrappers.<PlatformUserPO>lambdaQuery().eq(PlatformUserPO::getUsername, loginId));
            if (Objects.isNull(platformUserPO)) {
                return null;
            }
            CollegeAdminInformationPO collegeAdminInformationPO = collegeAdminInformationMapper.selectById(platformUserPO.getUserId());
            if (Objects.isNull(collegeAdminInformationPO)) {
                return null;
            }
            CollegeInformationPO collegeInformationPO = collegeInformationMapper.selectById(collegeAdminInformationPO.getCollegeId());
            if (Objects.isNull(collegeInformationPO)) {
                return null;
            }
            return collegeInformationPO;
        }catch (Exception e){
            log.error("获取用户所属学院信息失败 " + e.toString());
        }
        return null;
    }

    public ScnuTimeInterval getTimeInterval(Date teachingDate, String teachingTime){
        ScnuTimeInterval scnuTimeInterval = new ScnuTimeInterval();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(teachingDate);

        // 全角转半角
        teachingTime = teachingTime.replace("：", ":").replace("－", "-").replace("—", "-");


        String[] timeParts = teachingTime.split("[:-]");
        // 这会把 "8:30-11:30" 分为 "8", "30", "11", "30"
        // 开始时间
        int startHour = Integer.parseInt(timeParts[0].trim());
        int startMinute = Integer.parseInt(timeParts[1].trim());
        calendar.set(Calendar.HOUR_OF_DAY, startHour);
        calendar.set(Calendar.MINUTE, startMinute);
        Date startDateTime = calendar.getTime();
        scnuTimeInterval.setStart(startDateTime);
        // 结束时间
        int endHour = Integer.parseInt(timeParts[2].trim());
        int endMinute = Integer.parseInt(timeParts[3].trim());
        calendar.set(Calendar.HOUR_OF_DAY, endHour);
        calendar.set(Calendar.MINUTE, endMinute);
        Date endDateTime = calendar.getTime();
        scnuTimeInterval.setEnd(endDateTime);

        return scnuTimeInterval;
    }

    public boolean areAllFieldsNull(Object obj) {
        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);  // You might want to set modifier to public first.
            try {
                if (field.get(obj) != null) {
                    return false;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
}
