package com.scnujxjy.backendpoint.service.admission_information;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.constant.enums.DownloadFileNameEnum;
import com.scnujxjy.backendpoint.constant.enums.MinioBucketEnum;
import com.scnujxjy.backendpoint.constant.enums.PermissionEnum;
import com.scnujxjy.backendpoint.constant.enums.SystemEnum;
import com.scnujxjy.backendpoint.dao.entity.admission_information.AdmissionInformationPO;
import com.scnujxjy.backendpoint.dao.entity.basic.GlobalConfigPO;
import com.scnujxjy.backendpoint.dao.entity.college.CollegeInformationPO;
import com.scnujxjy.backendpoint.dao.entity.platform_message.DownloadMessagePO;
import com.scnujxjy.backendpoint.dao.entity.platform_message.PlatformMessagePO;
import com.scnujxjy.backendpoint.dao.entity.teaching_point.TeachingPointInformationPO;
import com.scnujxjy.backendpoint.dao.mapper.admission_information.AdmissionInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.basic.GlobalConfigMapper;
import com.scnujxjy.backendpoint.dao.mapper.basic.PlatformUserMapper;
import com.scnujxjy.backendpoint.dao.mapper.platform_message.DownloadMessageMapper;
import com.scnujxjy.backendpoint.dao.mapper.platform_message.PlatformMessageMapper;
import com.scnujxjy.backendpoint.inverter.admission_information.AdmissionInformationInverter;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.admission_information.AdmissionInformationRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.admission_information.AdmissionInformationVO;
import com.scnujxjy.backendpoint.model.vo.admission_information.AdmissionSelectArgs;
import com.scnujxjy.backendpoint.model.vo.admission_information.AdmissionStudentsInfoVO;
import com.scnujxjy.backendpoint.service.minio.MinioService;
import com.scnujxjy.backendpoint.util.ApplicationContextProvider;
import com.scnujxjy.backendpoint.util.MessageSender;
import com.scnujxjy.backendpoint.util.ResultCode;
import com.scnujxjy.backendpoint.util.filter.*;
import com.scnujxjy.backendpoint.util.tool.ScnuXueliTools;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

import static com.scnujxjy.backendpoint.constant.enums.RoleEnum.*;

/**
 * <p>
 * 录取学生信息表 服务实现类
 * </p>
 *
 * @author leopard
 * @since 2023-08-02
 */
@Service
@Slf4j
public class AdmissionInformationService extends ServiceImpl<AdmissionInformationMapper, AdmissionInformationPO> implements IService<AdmissionInformationPO> {

    @Resource
    private AdmissionInformationInverter admissionInformationInverter;

    @Resource
    private ScnuXueliTools scnuXueliTools;

    @Resource
    private CollegeAdminFilter collegeAdminFilter;

    @Resource
    private TeacherFilter teacherFilter;

    @Resource
    private ManagerFilter managerFilter;

    @Resource
    private TeachingPointFilter teachingPointFilter;

    @Resource
    private MessageSender messageSender;

    /**
    * @Version：1.0.0
    * @Description：添加录取学生信息
    * @Author：3304393868@qq.com
    * @Date：2023/12/7-16:00
    */
    public Integer insterAdmissionInformation(AdmissionInformationRO admissionInformationRO){
        AdmissionInformationPO admissionInformationPO =  admissionInformationInverter.ro2PO(admissionInformationRO);
        return   baseMapper.insert(admissionInformationPO);
    }


    /**
     * 根据id查询录取学生信息表
     *
     * @param id 录取学生信息id
     * @return 录取学生信息
     */
    public AdmissionInformationVO detailById(Long id) {
        // 参数校验
        if (Objects.isNull(id)) {
            log.error("参数缺失");
            return null;
        }
        // 查找数据
        AdmissionInformationPO admissionInformationPO = baseMapper.selectById(id);
        // 返回结果
        return admissionInformationInverter.po2VO(admissionInformationPO);
    }
    public PageVO<AdmissionInformationVO> getAdmissionInformationByAllRoles(PageRO<AdmissionInformationRO> admissionInformationROPageRO) {
        List<String> roleList = StpUtil.getRoleList();
        List<String> permissionList = StpUtil.getPermissionList();

        if (roleList.contains(SECOND_COLLEGE_ADMIN.getRoleName())) {
            // 查询继续教育管理员权限范围内的教学计划
            CollegeInformationPO userBelongCollege = scnuXueliTools.getUserBelongCollege();
            // 采用超级管理员的筛选器 在里面添加一个条件 即该二级学院管理员的学院
            admissionInformationROPageRO.getEntity().setCollege(userBelongCollege.getCollegeName());
            return managerFilter.getAdmissionInformationByAllRoles(admissionInformationROPageRO);
        }else if (roleList.contains(TEACHING_POINT_ADMIN.getRoleName())) {
            // 采用超级管理员的筛选器 在里面添加一个条件 即该教学点教务员所属教学点的信息
            TeachingPointInformationPO userBelongTeachingPoint = scnuXueliTools.getUserBelongTeachingPoint();
            admissionInformationROPageRO.getEntity().setTeachingPoint(userBelongTeachingPoint.getTeachingPointName());
            return managerFilter.getAdmissionInformationByAllRoles(admissionInformationROPageRO);
        }
        else if (permissionList.contains(PermissionEnum.VIEW_NEW_STUDENT_INFORMATION.getPermission())) {
            // 查询继续教育管理员权限范围内的教学计划
            return managerFilter.getAdmissionInformationByAllRoles(admissionInformationROPageRO);

        }
        return null;
    }

    /**
     * 分页查询学生录取信息
     *
     * @param admissionInformationROPageRO 录取学生信息分页查询参数
     * @return 录取学生分页信息
     */
    public PageVO<AdmissionInformationVO> pageQueryAdmissionInformation(PageRO<AdmissionInformationRO> admissionInformationROPageRO) {
        // 参数校验
        if (Objects.isNull(admissionInformationROPageRO)) {
            log.error("参数缺失");
            return null;
        }
        AdmissionInformationRO entity = admissionInformationROPageRO.getEntity();
        if (Objects.isNull(entity)) {
            entity = new AdmissionInformationRO();
        }
        // 构建查询分页查询语句
        // 分页查询 或 列表查询 最后返回结果
        LambdaQueryWrapper<AdmissionInformationPO> wrapper = Wrappers.<AdmissionInformationPO>lambdaQuery()
                .eq(Objects.nonNull(entity.getId()), AdmissionInformationPO::getId, entity.getId())
                .eq(StrUtil.isNotBlank(entity.getName()), AdmissionInformationPO::getName, entity.getName())
                .eq(StrUtil.isNotBlank(entity.getGender()), AdmissionInformationPO::getGender, entity.getGender())
                .eq(Objects.nonNull(entity.getTotalScore()), AdmissionInformationPO::getTotalScore, entity.getTotalScore())
                .eq(StrUtil.isNotBlank(entity.getMajorCode()), AdmissionInformationPO::getMajorCode, entity.getMajorCode())
                .like(StrUtil.isNotBlank(entity.getMajorName()), AdmissionInformationPO::getMajorName, entity.getMajorName())
                .eq(StrUtil.isNotBlank(entity.getLevel()), AdmissionInformationPO::getLevel, entity.getLevel())
                .eq(StrUtil.isNotBlank(entity.getStudyForm()), AdmissionInformationPO::getStudyForm, entity.getStudyForm())
                .eq(StrUtil.isNotBlank(entity.getOriginalEducation()), AdmissionInformationPO::getOriginalEducation, entity.getOriginalEducation())
                .eq(StrUtil.isNotBlank(entity.getGraduationSchool()), AdmissionInformationPO::getGraduationSchool, entity.getGraduationSchool())
                .eq(Objects.nonNull(entity.getGraduationDate()), AdmissionInformationPO::getGraduationDate, entity.getGraduationDate())
                .eq(StrUtil.isNotBlank(entity.getPhoneNumber()), AdmissionInformationPO::getPhoneNumber, entity.getPhoneNumber())
                .eq(StrUtil.isNotBlank(entity.getIdCardNumber()), AdmissionInformationPO::getIdCardNumber, entity.getIdCardNumber())
                .eq(Objects.nonNull(entity.getBirthDate()), AdmissionInformationPO::getBirthDate, entity.getBirthDate())
                .like(StrUtil.isNotBlank(entity.getAddress()), AdmissionInformationPO::getAddress, entity.getAddress())
                .like(StrUtil.isNotBlank(entity.getPostalCode()), AdmissionInformationPO::getPostalCode, entity.getPostalCode())
                .eq(StrUtil.isNotBlank(entity.getEthnicity()), AdmissionInformationPO::getEthnicity, entity.getEthnicity())
                .eq(StrUtil.isNotBlank(entity.getPoliticalStatus()), AdmissionInformationPO::getPoliticalStatus, entity.getPoliticalStatus())
                .eq(StrUtil.isNotBlank(entity.getAdmissionNumber()), AdmissionInformationPO::getAdmissionNumber, entity.getAdmissionNumber())
                .eq(StrUtil.isNotBlank(entity.getShortStudentNumber()), AdmissionInformationPO::getShortStudentNumber, entity.getShortStudentNumber())
                .eq(StrUtil.isNotBlank(entity.getCollege()), AdmissionInformationPO::getCollege, entity.getCollege())
                .eq(StrUtil.isNotBlank(entity.getTeachingPoint()), AdmissionInformationPO::getTeachingPoint, entity.getTeachingPoint())
                .like(StrUtil.isNotBlank(entity.getReportLocation()), AdmissionInformationPO::getReportLocation, entity.getReportLocation())
                .eq(StrUtil.isNotBlank(entity.getEntrancePhotoUrl()), AdmissionInformationPO::getEntrancePhotoUrl, entity.getEntrancePhotoUrl())
                .eq(StrUtil.isNotBlank(entity.getGrade()), AdmissionInformationPO::getGrade, entity.getGrade())
                .last(StrUtil.isNotBlank(admissionInformationROPageRO.getOrderBy()), admissionInformationROPageRO.lastOrderSql());
        // 分页查询 或 列表查询 最后返回结果
        if (Objects.equals(true, admissionInformationROPageRO.getIsAll())) {
            List<AdmissionInformationPO> admissionInformationPOS = baseMapper.selectList(wrapper);
            return new PageVO<>(admissionInformationInverter.po2VO(admissionInformationPOS));
        } else {
            Page<AdmissionInformationPO> admissionInformationPOPage = baseMapper.selectPage(admissionInformationROPageRO.getPage(), wrapper);
            return new PageVO<>(admissionInformationPOPage, admissionInformationInverter.po2VO(admissionInformationPOPage.getRecords()));
        }
    }

    /**
     * 根据id更新录取学生信息
     *
     * @param admissionInformationRO 更新的学生信息
     * @return 更新后的录取学生信息
     */
    public AdmissionInformationVO editById(AdmissionInformationRO admissionInformationRO) {
        // 参数校验
        if (Objects.isNull(admissionInformationRO) || Objects.isNull(admissionInformationRO.getId())) {
            log.error("参数缺失");
            return null;
        }
        // 更新
        AdmissionInformationPO admissionInformationPO = admissionInformationInverter.ro2PO(admissionInformationRO);
        int count = baseMapper.updateById(admissionInformationPO);
        // 更新校验
        if (count <= 0) {
            log.error("更新失败，Admission information：{}", admissionInformationPO);
            return null;
        }
        // 返回数据
        return detailById(admissionInformationRO.getId());
    }

    /**
     * 根据id删除录取学生信息
     *
     * @param id 录取学生信息id
     * @return 删除的数量
     */
    public Integer deleteById(Long id) {
        // 参数校验
        if (Objects.isNull(id)) {
            log.error("参数缺失");
            return null;
        }
        // 删除数据
        int count = baseMapper.deleteById(id);
        // 校验操作
        if (count <= 0) {
            log.error("删除失败，id：{}", id);
            return null;
        }
        // 返回删除数量
        return count;
    }

    /**
     * 获取新生录取信息查询的筛选参数
     * @param admissionInformationRO
     * @return
     */
    public AdmissionSelectArgs getAdmissionArgsByAllRoles(AdmissionInformationRO admissionInformationRO) {
        List<String> roleList = StpUtil.getRoleList();
        List<String> permissionList = StpUtil.getPermissionList();

        if (roleList.contains(SECOND_COLLEGE_ADMIN.getRoleName())) {
            // 查询继续教育管理员权限范围内的教学计划
            CollegeInformationPO userBelongCollege = scnuXueliTools.getUserBelongCollege();
            // 采用超级管理员的筛选器 在里面添加一个条件 即该二级学院管理员的学院
            admissionInformationRO.setCollege(userBelongCollege.getCollegeName());
            return managerFilter.getAdmissionArgsByAllRoles(admissionInformationRO);
        }else if (roleList.contains(TEACHING_POINT_ADMIN.getRoleName())) {
            // 采用超级管理员的筛选器 在里面添加一个条件 即该教学点教务员所属教学点的信息
            TeachingPointInformationPO userBelongTeachingPoint = scnuXueliTools.getUserBelongTeachingPoint();
            admissionInformationRO.setTeachingPoint(userBelongTeachingPoint.getTeachingPointName());
            return managerFilter.getAdmissionArgsByAllRoles(admissionInformationRO);
        }
        else if (permissionList.contains(PermissionEnum.VIEW_NEW_STUDENT_INFORMATION.getPermission())) {
            // 查询继续教育管理员权限范围内的教学计划
            return managerFilter.getAdmissionArgsByAllRoles(admissionInformationRO);

        }
        return null;
    }

    /**
     * 批量导出新生录取数据
     * @param admissionInformationRO
     * @return
     */
    public Boolean batchExportAdmissionInformationByAllRoles(AdmissionInformationRO admissionInformationRO) {
        List<String> roleList = StpUtil.getRoleList();
        String userId = StpUtil.getLoginIdAsString();
        if (roleList.isEmpty()) {
            log.error(ResultCode.ROLE_INFO_FAIL1.getMessage());
            return false;
        } else {
            if (roleList.contains(SECOND_COLLEGE_ADMIN.getRoleName())) {
                // 二级学院管理员
                CollegeInformationPO userBelongCollege = scnuXueliTools.getUserBelongCollege();
                PageRO<AdmissionInformationRO> pageVO = new PageRO<>();
                admissionInformationRO.setCollege(userBelongCollege.getCollegeName());
                pageVO.setEntity(admissionInformationRO);
                boolean send = messageSender.sendExportMsg(pageVO, managerFilter, userId);
                if (send) {
                    return true;
                }
            } else if (roleList.contains(XUELIJIAOYUBU_ADMIN.getRoleName()) ||
                    roleList.contains(CAIWUBU_ADMIN.getRoleName()) ||
                    roleList.contains(ADMISSION_ADMIN.getRoleName())
            ) {
                // 继续教育学院管理员
                PageRO<AdmissionInformationRO> pageVO = new PageRO<>();
                pageVO.setEntity(admissionInformationRO);
                boolean send = messageSender.sendExportMsg(pageVO, managerFilter, userId);
                if (send) {
                    return true;
                }
            }
            else if (roleList.contains(TEACHING_POINT_ADMIN.getRoleName())) {
                // 继续教育学院管理员
                PageRO<AdmissionInformationRO> pageVO = new PageRO<>();
                admissionInformationRO.setTeachingPoint(scnuXueliTools.getUserBelongTeachingPoint().getTeachingPointName());
                pageVO.setEntity(admissionInformationRO);
                boolean send = messageSender.sendExportMsg(pageVO, managerFilter, userId);
                if (send) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 批量导出新生录取信息
     * @param pageRO
     * @param filter
     * @param username
     * @param platformMessagePO
     */
    public void generateBatchAdmissionData(PageRO<AdmissionInformationRO> pageRO, AbstractFilter filter, String username, PlatformMessagePO platformMessagePO) {
        log.info("异步下载新生录取信息, 筛选参数为 " + pageRO.getEntity());
        AdmissionInformationRO entity = pageRO.getEntity();
        log.info("机考名单筛选参数" + entity);
        ApplicationContext ctx = ApplicationContextProvider.getApplicationContext();
        AdmissionInformationMapper admissionInformationMapper1 = ctx.getBean(AdmissionInformationMapper.class);
        MinioService minioService1 = ctx.getBean(MinioService.class);
        GlobalConfigMapper globalConfigMapper1 = ctx.getBean(GlobalConfigMapper.class);
        PlatformMessageMapper platformMessageMapper = ctx.getBean(PlatformMessageMapper.class);
        DownloadMessageMapper downloadMessageMapper = ctx.getBean(DownloadMessageMapper.class);
        PlatformUserMapper platformUserMapper1 = ctx.getBean(PlatformUserMapper.class);

        // 获取数据
        List<AdmissionStudentsInfoVO> admissionStudentsInfoVOS = new ArrayList<>();
        @Data
        class DateInfo {
            private String year;
            private String month;
            private String day;

            // 构造器、getters 和 setters
        }

        // 获取当前日期
        LocalDate now = LocalDate.now();

        // 使用 LocalDate 获取年、月、日
        String year = String.valueOf(now.getYear());
        String month = String.format("%02d", now.getMonthValue()); // 保证月份是两位数字
        String day = String.format("%02d", now.getDayOfMonth()); // 保证天数是两位数字

        // 设置填表时间到 DateInfo 对象
        DateInfo dateInfo = new DateInfo();
        dateInfo.setYear(year);
        dateInfo.setMonth(month);
        dateInfo.setDay(day);

        // 或者使用 Map
        Map<String, Object> dateInfoMap = new HashMap<>();
        dateInfoMap.put("year", year);
        dateInfoMap.put("month", month);
        dateInfoMap.put("day", day);

        List<AdmissionInformationVO> admissionInformationVOS = admissionInformationMapper1.batchSelectData(entity);
        int count = 1;
        for (AdmissionInformationVO admissionInformationVO : admissionInformationVOS) {
            AdmissionStudentsInfoVO admissionStudentsInfoVO = new AdmissionStudentsInfoVO();
            BeanUtils.copyProperties(admissionInformationVO, admissionStudentsInfoVO);
            admissionStudentsInfoVO.setIndex(count);


            admissionStudentsInfoVOS.add(admissionStudentsInfoVO);

        }


        log.info("导出了 " + admissionStudentsInfoVOS.size() + " 条新生录取信息数据");

        // 为每个StudentStatusAllVO对象设置序号
        for (int i = 0; i < admissionStudentsInfoVOS.size(); i++) {
            admissionStudentsInfoVOS.get(i).setIndex(i + 1);
        }
        GlobalConfigPO globalConfigPO = globalConfigMapper1.selectOne(new LambdaQueryWrapper<GlobalConfigPO>()
                .eq(GlobalConfigPO::getConfigKey, "新生录取信息导出模板"));
        InputStream fileInputStreamFromMinio = minioService1.getFileInputStreamFromMinio(globalConfigPO.getConfigValue());

        // 使用 ByteArrayOutputStream 将数据写入到流中
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        // 配置 Excel 写入操作
        ExcelWriter excelWriter = null;
        try {

            excelWriter = EasyExcel.write(outputStream, AdmissionInformationVO.class)
                    .withTemplate(fileInputStreamFromMinio)
                    .build();

            WriteSheet writeSheet = EasyExcel.writerSheet().build();
            FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.TRUE).build();
            excelWriter.fill(admissionStudentsInfoVOS, fillConfig, writeSheet);
            // 填充填表时间
            excelWriter.fill(dateInfoMap, writeSheet);  // 如果使用 Map
            excelWriter.finish();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (excelWriter != null) {
                excelWriter.finish();
            }
        }
        // 获取文件大小
        int fileSize = outputStream.size();
        // 获取桶名和子目录
        String bucketName = MinioBucketEnum.DATA_DOWNLOAD_ADMISSION_STUDENT.getBucketName();
        String subDirectory = MinioBucketEnum.DATA_DOWNLOAD_ADMISSION_STUDENT.getSubDirectory();

        // 使用当前日期和时间作为文件名的一部分
        Date generateData = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS");
        String currentDateTime = sdf.format(generateData);

        // 构建文件名
        String fileName = subDirectory + "/" + username + "_" + currentDateTime + "_新生录取信息.xlsx";

        // 上传到 Minio
        // 将流转换为 ByteArrayInputStream
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        boolean b = minioService1.uploadStreamToMinio(inputStream, fileName, bucketName);

        // 如果上传成功了 则修改数据库中的用户下载消息
        if (b) {
            DownloadMessagePO downloadMessagePO = new DownloadMessagePO();
            downloadMessagePO.setCreatedAt(generateData);
            downloadMessagePO.setFileName(DownloadFileNameEnum.ADMISSION_STUDENTS_EXPORT_FILE.getFilename());
            downloadMessagePO.setFileMinioUrl(bucketName + "/" + fileName);
            downloadMessagePO.setFileSize((long) fileSize);
            int insert = downloadMessageMapper.insert(downloadMessagePO);
            log.info("下载新生录取信息数据、下载文件消息插入 " + insert);

            // 获取自增ID
            Long generatedId = downloadMessagePO.getId();

            platformMessagePO.setRelatedMessageId(generatedId);

            int insert1 = platformMessageMapper.updateById(platformMessagePO);
            log.info("用户下载消息插入结果 " + insert1);
        }
    }

    /**
     * 学生获取自己的录取信息
     * @return
     */
    public AdmissionInformationVO getAdmission_info() {
        String loginIdAsString = StpUtil.getLoginIdAsString();
        String admissionYear = SystemEnum.NOW_NEW_STUDENT_GRADE.getSystemArg();
        return getBaseMapper().selectSingleAdmissionInfo(loginIdAsString, admissionYear);
    }
}
