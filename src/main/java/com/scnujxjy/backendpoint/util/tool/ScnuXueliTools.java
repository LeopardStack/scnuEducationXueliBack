package com.scnujxjy.backendpoint.util.tool;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.linuxense.javadbf.DBFDataType;
import com.linuxense.javadbf.DBFField;
import com.scnujxjy.backendpoint.constant.enums.MessageEnum;
import com.scnujxjy.backendpoint.dao.entity.basic.PlatformUserPO;
import com.scnujxjy.backendpoint.dao.entity.college.CollegeAdminInformationPO;
import com.scnujxjy.backendpoint.dao.entity.college.CollegeInformationPO;
import com.scnujxjy.backendpoint.dao.entity.platform_message.PlatformMessagePO;
import com.scnujxjy.backendpoint.dao.entity.teaching_point.TeachingPointAdminInformationPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_point.TeachingPointInformationPO;
import com.scnujxjy.backendpoint.dao.mapper.basic.PlatformUserMapper;
import com.scnujxjy.backendpoint.dao.mapper.college.CollegeAdminInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.college.CollegeInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.platform_message.PlatformMessageMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.ClassInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_point.TeachingPointAdminInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_point.TeachingPointInformationMapper;
import com.scnujxjy.backendpoint.exception.BusinessException;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.ClassInformationRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseScheduleFilterRO;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.ClassInformationVO;
import com.scnujxjy.backendpoint.service.basic.PlatformUserService;
import com.scnujxjy.backendpoint.service.platform_message.PlatformMessageService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.index.qual.SameLen;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

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
    @Resource
    private PlatformUserService platformUserService;
    @Resource
    private PlatformMessageMapper platformMessageMapper;

    @Resource
    private TeachingPointAdminInformationMapper teachingPointAdminInformationMapper;

    @Resource
    private TeachingPointInformationMapper teachingPointInformationMapper;

    @Resource
    private ClassInformationMapper classInformationMapper;

    public PlatformMessagePO generateMessage(String username) {
        PlatformMessagePO platformMessagePO = new PlatformMessagePO();
        Date generateData = new Date();
        platformMessagePO.setCreatedAt(generateData);
        platformMessagePO.setUserId(String.valueOf(platformUserService.getUserIdByUsername(username)));
        platformMessagePO.setRelatedMessageId(null);
        platformMessagePO.setIsRead(false);
        platformMessagePO.setMessageType(MessageEnum.DOWNLOAD_MSG.getMessageName());
        int insert1 = platformMessageMapper.insert(platformMessagePO);
        log.info("接收到用户下载消息，正在处理下载内容... " + insert1);
        return platformMessagePO;
    }


    /**
     * 根据loginId获取教学点班级信息
     *
     * @return
     */
    public Set<String> getTeachingPointClassNameSet() {
        // 通过username查询userId，再查询对应教学点
        String loginId = StpUtil.getLoginIdAsString();
        if (StrUtil.isBlank(loginId)) {
            throw new BusinessException("获取用户id失败");
        }
        PlatformUserPO platformUserPO = platformUserMapper.selectOne(Wrappers.<PlatformUserPO>lambdaQuery()
                .eq(PlatformUserPO::getUsername, loginId));
        if (Objects.isNull(platformUserPO)) {
            throw new BusinessException("获取用户信息失败");
        }
        Long userId = platformUserPO.getUserId();
        // 获取教学点教务员的 teaching_id 进而他所管理的教学点的简称
        Set<String> classNameSet = new HashSet<>();
        List<TeachingPointAdminInformationPO> teachingPointAdminInformationPOS = teachingPointAdminInformationMapper.selectList(Wrappers.<TeachingPointAdminInformationPO>lambdaQuery()
                .eq(TeachingPointAdminInformationPO::getUserId, userId));
        for (TeachingPointAdminInformationPO teachingPointAdminInformationPO : teachingPointAdminInformationPOS) {
            String teachingPointId = teachingPointAdminInformationPO.getTeachingPointId();
            TeachingPointInformationPO teachingPointInformationPO = teachingPointInformationMapper.selectOne(Wrappers.<TeachingPointInformationPO>lambdaQuery()
                    .eq(TeachingPointInformationPO::getTeachingPointId, teachingPointId));
            String alias = teachingPointInformationPO.getAlias();
            classNameSet.add(alias);
        }
        if (CollUtil.isEmpty(classNameSet)) {
            throw new BusinessException("查询班级集合为空，查询失败");
        }
        return classNameSet;
    }


    /**
     * 根据 loginId 获取教学点班级标识信息
     *
     * @return
     */
    public Set<String> getTeachingPointClassIdetifierSet() {
        // 通过username查询userId，再查询对应教学点
        String loginId = StpUtil.getLoginIdAsString();
        if (StrUtil.isBlank(loginId)) {
            throw new BusinessException("获取用户id失败");
        }
        PlatformUserPO platformUserPO = platformUserMapper.selectOne(Wrappers.<PlatformUserPO>lambdaQuery()
                .eq(PlatformUserPO::getUsername, loginId));
        if (Objects.isNull(platformUserPO)) {
            throw new BusinessException("获取用户信息失败");
        }
        Long userId = platformUserPO.getUserId();
        // 获取教学点教务员的 teaching_id 进而他所管理的教学点的简称
        Set<String> classNameSet = new HashSet<>();
        List<TeachingPointAdminInformationPO> teachingPointAdminInformationPOS = teachingPointAdminInformationMapper.selectList(Wrappers.<TeachingPointAdminInformationPO>lambdaQuery()
                .eq(TeachingPointAdminInformationPO::getUserId, userId));
        for (TeachingPointAdminInformationPO teachingPointAdminInformationPO : teachingPointAdminInformationPOS) {
            String teachingPointId = teachingPointAdminInformationPO.getTeachingPointId();
            TeachingPointInformationPO teachingPointInformationPO = teachingPointInformationMapper.selectOne(Wrappers.<TeachingPointInformationPO>lambdaQuery()
                    .eq(TeachingPointInformationPO::getTeachingPointId, teachingPointId));
            String alias = teachingPointInformationPO.getAlias();
            classNameSet.add(alias);
        }

        List<ClassInformationVO> classInformationVOList = classInformationMapper.
                selectClassInfoData(new ClassInformationRO().setClassNames(new ArrayList<>(classNameSet)));
        classInformationVOList.get(0).getClassIdentifier();
        // 使用 Stream API 提取 classIdentifier 并存储为 Set
        Set<String> classIdentifierSet = classInformationVOList.stream()  // 将 List 转换为 Stream
                .map(ClassInformationVO::getClassIdentifier)  // 提取每个 ClassInformationVO 的 classIdentifier
                .collect(Collectors.toSet());  // 收集结果到 Set

        if (CollUtil.isEmpty(classIdentifierSet)) {
            throw new BusinessException("查询班级标识符集合为空，查询失败");
        }
        return classIdentifierSet;
    }


    /**
     * 根据 loginId 获取教学点班级标识信息
     *
     * @return
     */
    public Set<String> getTeachingPointClassIdetifierSet(String username) {

        PlatformUserPO platformUserPO = platformUserMapper.selectOne(Wrappers.<PlatformUserPO>lambdaQuery()
                .eq(PlatformUserPO::getUsername, username));
        if (Objects.isNull(platformUserPO)) {
            throw new BusinessException("获取用户信息失败");
        }
        Long userId = platformUserPO.getUserId();
        // 获取教学点教务员的 teaching_id 进而他所管理的教学点的简称
        Set<String> classNameSet = new HashSet<>();
        List<TeachingPointAdminInformationPO> teachingPointAdminInformationPOS = teachingPointAdminInformationMapper.selectList(Wrappers.<TeachingPointAdminInformationPO>lambdaQuery()
                .eq(TeachingPointAdminInformationPO::getUserId, userId));
        for (TeachingPointAdminInformationPO teachingPointAdminInformationPO : teachingPointAdminInformationPOS) {
            String teachingPointId = teachingPointAdminInformationPO.getTeachingPointId();
            TeachingPointInformationPO teachingPointInformationPO = teachingPointInformationMapper.selectOne(Wrappers.<TeachingPointInformationPO>lambdaQuery()
                    .eq(TeachingPointInformationPO::getTeachingPointId, teachingPointId));
            String alias = teachingPointInformationPO.getAlias();
            classNameSet.add(alias);
        }

        List<ClassInformationVO> classInformationVOList = classInformationMapper.
                selectClassInfoData(new ClassInformationRO().setClassNames(new ArrayList<>(classNameSet)));
        classInformationVOList.get(0).getClassIdentifier();
        // 使用 Stream API 提取 classIdentifier 并存储为 Set
        Set<String> classIdentifierSet = classInformationVOList.stream()  // 将 List 转换为 Stream
                .map(ClassInformationVO::getClassIdentifier)  // 提取每个 ClassInformationVO 的 classIdentifier
                .collect(Collectors.toSet());  // 收集结果到 Set

        if (CollUtil.isEmpty(classIdentifierSet)) {
            throw new BusinessException("查询班级标识符集合为空，查询失败");
        }
        return classIdentifierSet;
    }

    public TeachingPointInformationPO getUserBelongTeachingPoint(){
        try{
            String loginId = (String) StpUtil.getLoginId();
            if (StrUtil.isBlank(loginId)) {
                return null;
            }
            PlatformUserPO platformUserPO = platformUserMapper.selectOne(Wrappers.<PlatformUserPO>lambdaQuery().eq(PlatformUserPO::getUsername, loginId));
            if (Objects.isNull(platformUserPO)) {
                return null;
            }
            // 创建查询包装器实例
            LambdaQueryWrapper<TeachingPointAdminInformationPO> queryWrapper = new LambdaQueryWrapper<>();
            // 设置查询条件
            queryWrapper.eq(TeachingPointAdminInformationPO::getUserId, platformUserPO.getUserId());
            // 执行查询
            TeachingPointAdminInformationPO teachingPointAdminInformationPO = teachingPointAdminInformationMapper.selectOne(queryWrapper);

            if (Objects.isNull(teachingPointAdminInformationPO)) {
                return null;
            }
            TeachingPointInformationPO teachingPointInformationPO = teachingPointInformationMapper.
                    selectById(teachingPointAdminInformationPO.getTeachingPointId());
            if (Objects.isNull(teachingPointInformationPO)) {
                return null;
            }
            return teachingPointInformationPO;
        }catch (Exception e){
            log.error("获取用户所属教学点信息失败 " + e.toString());
        }
        return null;
    }

    public List<TeachingPointInformationPO> getUserBelongTeachingPoints(){
        try{
            String loginId = (String) StpUtil.getLoginId();
            if (StrUtil.isBlank(loginId)) {
                return null;
            }
            PlatformUserPO platformUserPO = platformUserMapper.selectOne(Wrappers.<PlatformUserPO>lambdaQuery().eq(PlatformUserPO::getUsername, loginId));
            if (Objects.isNull(platformUserPO)) {
                return null;
            }
            // 创建查询包装器实例
            LambdaQueryWrapper<TeachingPointAdminInformationPO> queryWrapper = new LambdaQueryWrapper<>();
            // 设置查询条件
            queryWrapper.eq(TeachingPointAdminInformationPO::getUserId, platformUserPO.getUserId());
            // 执行查询
            List<TeachingPointAdminInformationPO> teachingPointAdminInformationPOS = teachingPointAdminInformationMapper.selectList(queryWrapper);
            List<TeachingPointInformationPO> teachingPointAdminInformationPOList = new ArrayList<>();
            for(TeachingPointAdminInformationPO teachingPointAdminInformationPO: teachingPointAdminInformationPOS){
                TeachingPointInformationPO teachingPointInformationPO = teachingPointInformationMapper.selectById(teachingPointAdminInformationPO.getTeachingPointId());
                teachingPointAdminInformationPOList.add(teachingPointInformationPO);
            }

            return teachingPointAdminInformationPOList;

        }catch (Exception e){
            log.error("获取用户所属教学点信息失败 " + e.toString());
        }
        return null;
    }

    public Integer getUserBelongTeachingPointCount(){
        try{
            String loginId = (String) StpUtil.getLoginId();
            if (StrUtil.isBlank(loginId)) {
                return null;
            }
            PlatformUserPO platformUserPO = platformUserMapper.selectOne(Wrappers.<PlatformUserPO>lambdaQuery().eq(PlatformUserPO::getUsername, loginId));
            if (Objects.isNull(platformUserPO)) {
                return null;
            }
            // 创建查询包装器实例
            LambdaQueryWrapper<TeachingPointAdminInformationPO> queryWrapper = new LambdaQueryWrapper<>();
            // 设置查询条件
            queryWrapper.eq(TeachingPointAdminInformationPO::getUserId, platformUserPO.getUserId());
            // 执行查询
            return teachingPointAdminInformationMapper.selectCount(queryWrapper);


        }catch (Exception e){
            log.error("获取用户所属教学点信息失败 " + e.toString());
        }
        return null;
    }

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

    public CollegeInformationPO getUserBelongCollegeByLoginId(String loginId){
        try{
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


    /**
     * 将指定对象的所有字符串属性 只要其值为空字符串 将其置为 null
     * @param obj
     */
    public void convertEmptyStringsToNull(Object obj) {
        if (obj == null) {
            return;
        }

        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                // 确保可以访问私有字段
                field.setAccessible(true);

                // 检查字段是否为字符串类型
                if (field.getType().equals(String.class)) {
                    String value = (String) field.get(obj);

                    // 如果字符串为空，则将其设置为 null
                    if (value != null && value.isEmpty()) {
                        field.set(obj, null);
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public static DBFField createField(String name, DBFDataType type, int length) {
        DBFField field = new DBFField();
        field.setName(name);
        field.setType(type);
        field.setLength(length);
        return field;
    }

    public static String getFileExtension(MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            String fileName = file.getOriginalFilename();
            if (fileName != null && fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
                return fileName.substring(fileName.lastIndexOf(".") + 1);
            }
        }
        return "未知文件";
    }

}
