package com.scnujxjy.backendpoint.util.filter;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.scnujxjy.backendpoint.constant.enums.DownloadFileNameEnum;
import com.scnujxjy.backendpoint.constant.enums.MessageEnum;
import com.scnujxjy.backendpoint.constant.enums.MinioBucketEnum;
import com.scnujxjy.backendpoint.dao.entity.basic.PlatformUserPO;
import com.scnujxjy.backendpoint.dao.entity.platform_message.DownloadMessagePO;
import com.scnujxjy.backendpoint.dao.entity.platform_message.PlatformMessagePO;
import com.scnujxjy.backendpoint.dao.entity.video_stream.VideoStreamRecordPO;
import com.scnujxjy.backendpoint.dao.mapper.core_data.PaymentInfoMapper;
import com.scnujxjy.backendpoint.dao.mapper.platform_message.DownloadMessageMapper;
import com.scnujxjy.backendpoint.dao.mapper.platform_message.PlatformMessageMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.ClassInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.StudentStatusMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.ScoreInformationMapper;
import com.scnujxjy.backendpoint.model.bo.teaching_process.ScheduleCoursesInformationBO;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.core_data.PaymentInfoFilterRO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.ClassInformationFilterRO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.StudentStatusFilterRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseInformationRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseScheduleFilterRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.ScoreInformationFilterRO;
import com.scnujxjy.backendpoint.model.vo.core_data.PaymentInfoAllVO;
import com.scnujxjy.backendpoint.model.vo.core_data.PaymentInfoVO;
import com.scnujxjy.backendpoint.model.vo.core_data.PaymentInformationSelectArgs;
import com.scnujxjy.backendpoint.model.vo.core_data.TeacherInformationVO;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.ClassInformationSelectArgs;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.ClassInformationVO;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.StudentStatusSelectArgs;
import com.scnujxjy.backendpoint.model.vo.teaching_process.*;
import com.scnujxjy.backendpoint.service.minio.MinioService;
import com.scnujxjy.backendpoint.util.ApplicationContextProvider;
import com.scnujxjy.backendpoint.util.tool.LogExecutionTime;
import com.scnujxjy.backendpoint.util.tool.ScnuTimeInterval;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 继续教育学院各个部门的管理员的筛选器
 */
@Component
@Slf4j
@Data
public class ManagerFilter extends AbstractFilter {
    /**
     * 标识
     * 0 表示学历教育部
     * 1 表示招生部
     * 2 表示财务部
     * -1 表示超级管理员
     */
    private int identifier = 0;

    /**
     * 筛选教学计划
     *
     * @param courseInformationFilter 获取的教学计划筛选数据
     * @return
     */
    @Override
    public FilterDataVO filterCourseInformation(PageRO<CourseInformationRO> courseInformationFilter) {
        FilterDataVO<CourseInformationManagerZeroVO> courseInformationFilterDataVO = new FilterDataVO<>();
        String loginId = (String) StpUtil.getLoginId();
        if (StrUtil.isBlank(loginId)) {
            return null;
        }
        PlatformUserPO platformUserPO = platformUserMapper.selectOne(Wrappers.<PlatformUserPO>lambdaQuery().eq(PlatformUserPO::getUsername, loginId));
        if (Objects.isNull(platformUserPO)) {
            return null;
        }


        log.info("查询参数 " + courseInformationFilter.getEntity());
        // 使用 courseInformationMapper 获取数据
        List<CourseInformationManagerZeroVO> courseInformationVOS = courseInformationMapper.selectByFilterAndPageByManager0(courseInformationFilter.getEntity(),
                courseInformationFilter.getPageSize(),
                courseInformationFilter.getPageSize() * (courseInformationFilter.getPageNumber() - 1));
        long total = courseInformationMapper.getCountByFilterAndPageManager0(courseInformationFilter.getEntity());
        courseInformationFilterDataVO.setData(courseInformationVOS);
        courseInformationFilterDataVO.setTotal(total);

        return courseInformationFilterDataVO;
    }

    /**
     * 获取二级学院教学计划筛选参数
     *
     * @return
     */
    @Override
    public CourseInformationSelectArgs filterCourseInformationSelectArgs() {
        CourseInformationSelectArgsManagerZero courseInformationSelectArgs = new CourseInformationSelectArgsManagerZero();

        String loginId = (String) StpUtil.getLoginId();
        if (StrUtil.isBlank(loginId)) {
            return null;
        }
        PlatformUserPO platformUserPO = platformUserMapper.selectOne(Wrappers.<PlatformUserPO>lambdaQuery().eq(PlatformUserPO::getUsername, loginId));
        if (Objects.isNull(platformUserPO)) {
            return null;
        }


        List<String> grades = courseInformationMapper.selectDistinctGrades(null, null);
        List<String> majorNames = courseInformationMapper.selectDistinctMajorNames(null, null);
        List<String> levels = courseInformationMapper.selectDistinctLevels(null, null);
        List<String> courseNames = courseInformationMapper.selectDistinctCourseNames(null, null);
        List<String> studyForms = courseInformationMapper.selectDistinctStudyForms(null, null);
        List<String> classNames = courseInformationMapper.selectDistinctClassNames(null, null);
        List<String> collegeNames = courseInformationMapper.selectDistinctCollegeNames(null);
        courseInformationSelectArgs.setGrades(grades);
        courseInformationSelectArgs.setMajorNames(majorNames);
        courseInformationSelectArgs.setLevels(levels);
        courseInformationSelectArgs.setCourseNames(courseNames);
        courseInformationSelectArgs.setStudyForms(studyForms);
        courseInformationSelectArgs.setClassNames(classNames);
        courseInformationSelectArgs.setCollegeNames(collegeNames);
        return courseInformationSelectArgs;
    }

    /**
     * 批量导出教学计划
     *
     * @param courseInformationROPageRO
     * @return
     */
    @Override
    public byte[] downloadTeachingPlans(PageRO<CourseInformationRO> courseInformationROPageRO) {

        List<CourseInformationVO> courseInformationVOS = courseInformationMapper.selectByFilterAndPage(courseInformationROPageRO.getEntity(),
                null, null);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            // 将数据写入到 ByteArrayOutputStream
            EasyExcel.write(outputStream, CourseInformationVO.class).sheet("Sheet1").doWrite(courseInformationVOS);
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return outputStream.toByteArray();
    }

    /**
     * 筛选学籍数据
     *
     * @param studentStatusFilter 获取学籍数据的筛选数据
     * @return
     */
    @Override
    @LogExecutionTime
    public FilterDataVO filterStudentStatus(PageRO<StudentStatusFilterRO> studentStatusFilter) {
        FilterDataVO<StudentStatusAllVO> studentStatusVOFilterDataVO = new FilterDataVO<>();
        String loginId = (String) StpUtil.getLoginId();
        if (StrUtil.isBlank(loginId)) {
            return null;
        }
        PlatformUserPO platformUserPO = platformUserMapper.selectOne(Wrappers.<PlatformUserPO>lambdaQuery().eq(PlatformUserPO::getUsername, loginId));
        if (Objects.isNull(platformUserPO)) {
            return null;
        }


        log.info("学籍数据查询参数 " + studentStatusFilter.getEntity());
        // 使用 courseInformationMapper 获取数据
        List<StudentStatusAllVO> studentStatusVOS = studentStatusMapper.selectByFilterAndPageByManager0(studentStatusFilter.getEntity(),
                studentStatusFilter.getPageSize(),
                studentStatusFilter.getPageSize() * (studentStatusFilter.getPageNumber() - 1));
        long total = studentStatusMapper.getCountByFilterAndPageManager0(studentStatusFilter.getEntity());
        studentStatusVOFilterDataVO.setData(studentStatusVOS);
        studentStatusVOFilterDataVO.setTotal(total);

        return studentStatusVOFilterDataVO;
    }

    /**
     * 导出学籍数据到指定继续教育学院用户 的消息中
     *
     * @param studentStatusFilter
     * @param userId
     */
    @Override
    @Transactional
    public void exportStudentStatusData(PageRO<StudentStatusFilterRO> studentStatusFilter, String userId) {
        ApplicationContext ctx = ApplicationContextProvider.getApplicationContext();
        StudentStatusMapper studentStatusMapper1 = ctx.getBean(StudentStatusMapper.class);
        MinioService minioService = ctx.getBean(MinioService.class);
        PlatformMessageMapper platformMessageMapper = ctx.getBean(PlatformMessageMapper.class);
        DownloadMessageMapper downloadMessageMapper = ctx.getBean(DownloadMessageMapper.class);

        // 获取数据
        List<StudentStatusAllVO> studentStatusVOS = studentStatusMapper1.
                downloadStudentStatusDataByManager0(studentStatusFilter.getEntity());

        // 使用流API和forEach操作来更新每个对象的毕业日期，将毕业日期、入学日期的日 统一改为 10 号
        studentStatusVOS.stream().forEach(student -> {
            Calendar calendar = Calendar.getInstance();

            // 更新毕业日期
            calendar.setTime(student.getGraduationDate());
            calendar.set(Calendar.DAY_OF_MONTH, 10);
            student.setGraduationDate(calendar.getTime());

            // 更新入学日期
            calendar.setTime(student.getEnrollmentDate());
            calendar.set(Calendar.DAY_OF_MONTH, 10);
            student.setEnrollmentDate(calendar.getTime());
        });

        log.info("导出了 " + studentStatusVOS.size() + " 条学籍数据");

        // 为每个StudentStatusAllVO对象设置序号
        for (int i = 0; i < studentStatusVOS.size(); i++) {
            studentStatusVOS.get(i).setIndex(i + 1);
        }

        // 获取所有带有@ExcelProperty注解的字段
        List<String> includeColumnFiledNames = Arrays.stream(StudentStatusAllVO.class.getDeclaredFields())
                .filter(field -> field.getAnnotation(ExcelProperty.class) != null)
                .map(Field::getName)
                .collect(Collectors.toList());

        // 使用 ByteArrayOutputStream 将数据写入到流中
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        EasyExcel.write(outputStream, StudentStatusAllVO.class)
                .includeColumnFiledNames(includeColumnFiledNames)  // 只导出带有@ExcelProperty注解的字段
                .sheet("学籍数据")
                .doWrite(studentStatusVOS);

        // 将流转换为 ByteArrayInputStream
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        // 获取文件大小
        int fileSize = outputStream.size();
        // 获取桶名和子目录
        String bucketName = MinioBucketEnum.DATA_DOWNLOAD_STUDENT_STATUS.getBucketName();
        String subDirectory = MinioBucketEnum.DATA_DOWNLOAD_STUDENT_STATUS.getSubDirectory();

        // 使用当前日期和时间作为文件名的一部分
        Date generateData = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS");
        String currentDateTime = sdf.format(generateData);

        // 构建文件名
        String fileName = subDirectory + "/" + userId + "_" + currentDateTime + "_studentStatusData.xlsx";

        // 上传到 Minio
        boolean b = minioService.uploadStreamToMinio(inputStream, fileName, bucketName);

        // 如果上传成功了 则修改数据库中的用户下载消息
        if (b) {
            DownloadMessagePO downloadMessagePO = new DownloadMessagePO();
            downloadMessagePO.setCreatedAt(generateData);
            downloadMessagePO.setFileName(DownloadFileNameEnum.STUDENT_STATUS_EXPORT_FILE.getFilename());
            downloadMessagePO.setFileMinioUrl(bucketName + "/" + fileName);
            downloadMessagePO.setFileSize((long) fileSize);
            int insert = downloadMessageMapper.insert(downloadMessagePO);
            log.info("下载学籍数据、下载文件消息插入 " + insert);

            // 获取自增ID
            Long generatedId = downloadMessagePO.getId();
            PlatformMessagePO platformMessagePO = new PlatformMessagePO();
            platformMessagePO.setCreatedAt(generateData);
            platformMessagePO.setUserId(userId);
            platformMessagePO.setIsRead(false);
            platformMessagePO.setRelatedMessageId(generatedId);
            platformMessagePO.setMessageType(MessageEnum.DOWNLOAD_MSG.getMessage_name());
            int insert1 = platformMessageMapper.insert(platformMessagePO);
            log.info("用户下载消息插入结果 " + insert1);
        }
    }

    /**
     * 获取学籍数据筛选参数
     *
     * @return
     */
    @Override
    @LogExecutionTime
    public StudentStatusSelectArgs filterStudentStatusSelectArgs() {
        StudentStatusSelectArgs studentStatusSelectArgs = new StudentStatusSelectArgs();
        List<String> distinctGrades = studentStatusMapper.getDistinctGrades(new StudentStatusFilterRO());
        List<String> colleges = studentStatusMapper.getDistinctColleges(new StudentStatusFilterRO());
        List<String> majorNames = studentStatusMapper.getDistinctMajorNames(new StudentStatusFilterRO());
        List<String> levels = studentStatusMapper.getDistinctLevels(new StudentStatusFilterRO());
        List<String> studyForms = studentStatusMapper.getDistinctStudyForms(new StudentStatusFilterRO());
        List<String> classNames = studentStatusMapper.getDistinctClassNames(new StudentStatusFilterRO());
        List<String> studyDurations = studentStatusMapper.getDistinctStudyDurations(new StudentStatusFilterRO());
        List<String> academicStatuss = studentStatusMapper.getDistinctAcademicStatuss(new StudentStatusFilterRO());

        studentStatusSelectArgs.setGrades(distinctGrades);
        studentStatusSelectArgs.setClassNames(classNames);
        studentStatusSelectArgs.setCollegeNames(colleges);
        studentStatusSelectArgs.setMajorNames(majorNames);
        studentStatusSelectArgs.setLevels(levels);
        studentStatusSelectArgs.setStudyForms(studyForms);
        studentStatusSelectArgs.setStudyDurations(studyDurations);
        studentStatusSelectArgs.setAcademicStatus(academicStatuss);

        return studentStatusSelectArgs;
    }

    /**
     * 为继续教育学院学历教育部获取缴费信息
     *
     * @param paymentInfoFilterROPageRO 缴费筛选参数
     * @return
     */
    @Override
    public FilterDataVO filterPayInfo(PageRO<PaymentInfoFilterRO> paymentInfoFilterROPageRO) {
        FilterDataVO<PaymentInfoVO> studentStatusVOFilterDataVO = new FilterDataVO<>();
        log.info("用户缴费筛选参数" + paymentInfoFilterROPageRO);
        List<PaymentInfoVO> paymentInfoVOList = paymentInfoMapper.getStudentPayInfoByFilter(
                paymentInfoFilterROPageRO.getEntity(),
                paymentInfoFilterROPageRO.getPageSize(),
                (paymentInfoFilterROPageRO.getPageNumber() - 1) * paymentInfoFilterROPageRO.getPageSize()
        );
        long countStudentPayInfoByFilter = paymentInfoMapper.getCountStudentPayInfoByFilter(paymentInfoFilterROPageRO.getEntity());
//        long countStudentPayInfoByFilter = 100L;
        studentStatusVOFilterDataVO.setTotal(countStudentPayInfoByFilter);
        studentStatusVOFilterDataVO.setData(paymentInfoVOList);

        return studentStatusVOFilterDataVO;
    }


    /**
     * 为继续教育学院学历教育部获取成绩信息
     *
     * @param scoreInformationFilterROPageRO 成绩筛选参数
     * @return
     */
    @Override
    public FilterDataVO filterGradeInfo(PageRO<ScoreInformationFilterRO> scoreInformationFilterROPageRO) {
        FilterDataVO<ScoreInformationVO> studentStatusVOFilterDataVO = new FilterDataVO<>();
        log.info(StpUtil.getLoginId() + " 查询成绩的参数是 " + scoreInformationFilterROPageRO);
        List<ScoreInformationVO> paymentInfoVOList = scoreInformationMapper.getStudentGradeInfoByFilter(
                scoreInformationFilterROPageRO.getEntity(),
                scoreInformationFilterROPageRO.getPageSize(),
                (scoreInformationFilterROPageRO.getPageNumber() - 1) * scoreInformationFilterROPageRO.getPageSize()
        );
        long countStudentPayInfoByFilter = scoreInformationMapper.getCountStudentGradeInfoByFilter(scoreInformationFilterROPageRO.getEntity());
//        long countStudentPayInfoByFilter = 100L;
        studentStatusVOFilterDataVO.setTotal(countStudentPayInfoByFilter);
        studentStatusVOFilterDataVO.setData(paymentInfoVOList);

        return studentStatusVOFilterDataVO;
    }

    /**
     * 采用线程池技术，提高 SQL 查询筛选参数效率
     * 获取学籍数据筛选参数
     *
     * @return
     */
    @Override
    @LogExecutionTime
    public ScoreInformationSelectArgs filterScoreInformationSelectArgs() {
        ScoreInformationSelectArgs scoreInformationSelectArgs = new ScoreInformationSelectArgs();
        ScoreInformationFilterRO filter = new ScoreInformationFilterRO();

        ExecutorService executor = Executors.newFixedThreadPool(8); // 8 代表你有8个查询

        Future<List<String>> distinctGradesFuture = executor.submit(() -> scoreInformationMapper.getDistinctGrades(filter));
        Future<List<String>> collegesFuture = executor.submit(() -> scoreInformationMapper.getDistinctCollegeNames(filter));
        Future<List<String>> majorNamesFuture = executor.submit(() -> scoreInformationMapper.getDistinctMajorNames(filter));
        Future<List<String>> levelsFuture = executor.submit(() -> scoreInformationMapper.getDistinctLevels(filter));
        Future<List<String>> studyFormsFuture = executor.submit(() -> scoreInformationMapper.getDistinctStudyForms(filter));
        Future<List<String>> classNamesFuture = executor.submit(() -> scoreInformationMapper.getDistinctClassNames(filter));
        Future<List<String>> courseNamesFuture = executor.submit(() -> scoreInformationMapper.getDistinctCourseNames(filter));
        Future<List<String>> statusesFuture = executor.submit(() -> scoreInformationMapper.getDistinctStatus(filter));

        try {
            scoreInformationSelectArgs.setGrades(distinctGradesFuture.get());
            scoreInformationSelectArgs.setCollegeNames(collegesFuture.get());
            scoreInformationSelectArgs.setMajorNames(majorNamesFuture.get());
            scoreInformationSelectArgs.setLevels(levelsFuture.get());
            scoreInformationSelectArgs.setStudyForms(studyFormsFuture.get());
            scoreInformationSelectArgs.setClassNames(classNamesFuture.get());
            scoreInformationSelectArgs.setCourseNames(courseNamesFuture.get());
            scoreInformationSelectArgs.setStatuses(statusesFuture.get());
        } catch (Exception e) {
            // Handle exceptions like InterruptedException or ExecutionException
            e.printStackTrace();
        } finally {
            executor.shutdown(); // Always remember to shutdown the executor after usage
        }

        return scoreInformationSelectArgs;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void exportScoreInformationData(PageRO<ScoreInformationFilterRO> scoreInformationFilterROPageRO, String userId) {
        ApplicationContext ctx = ApplicationContextProvider.getApplicationContext();
        ScoreInformationMapper scoreInformationMapper1 = ctx.getBean(ScoreInformationMapper.class);
        MinioService minioService = ctx.getBean(MinioService.class);
        PlatformMessageMapper platformMessageMapper = ctx.getBean(PlatformMessageMapper.class);
        DownloadMessageMapper downloadMessageMapper = ctx.getBean(DownloadMessageMapper.class);

        // 获取数据
        List<ScoreInformationDownloadVO> scoreInformationDownloadVOS = scoreInformationMapper1.
                downloadScoreInformationDataByManager0(scoreInformationFilterROPageRO.getEntity());

        log.info("导出了 " + scoreInformationDownloadVOS.size() + " 条成绩数据");

        // 为每个StudentStatusAllVO对象设置序号
        for (int i = 0; i < scoreInformationDownloadVOS.size(); i++) {
            scoreInformationDownloadVOS.get(i).setIndex(i + 1);
        }

        // 获取所有带有@ExcelProperty注解的字段
        List<String> includeColumnFiledNames = Arrays.stream(ScoreInformationDownloadVO.class.getDeclaredFields())
                .filter(field -> field.getAnnotation(ExcelProperty.class) != null)
                .map(Field::getName)
                .collect(Collectors.toList());

        // 使用 ByteArrayOutputStream 将数据写入到流中
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // 根据数据量计算需要的sheet数量
        final int MAX_ROWS = 1000000;  // 10W
        int totalSize = scoreInformationDownloadVOS.size();
        int sheetCount = (totalSize / MAX_ROWS) + (totalSize % MAX_ROWS == 0 ? 0 : 1);

        ExcelWriter excelWriter = EasyExcel.write(outputStream, ScoreInformationDownloadVO.class)
                .includeColumnFiledNames(includeColumnFiledNames)
                .build();

        for (int i = 0; i < sheetCount; i++) {
            int startIndex = i * MAX_ROWS;
            int endIndex = Math.min((i + 1) * MAX_ROWS, totalSize);
            List<ScoreInformationDownloadVO> currentSheetData = scoreInformationDownloadVOS.subList(startIndex, endIndex);

            WriteSheet writeSheet = EasyExcel.writerSheet(i, "成绩数据_" + (i + 1)).build();
            excelWriter.write(currentSheetData, writeSheet);
        }

        excelWriter.finish();

        // 将流转换为 ByteArrayInputStream
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        // 获取文件大小
        int fileSize = outputStream.size();
        // 获取桶名和子目录
        String bucketName = MinioBucketEnum.DATA_DOWNLOAD_SCORE_INFORMATION.getBucketName();
        String subDirectory = MinioBucketEnum.DATA_DOWNLOAD_SCORE_INFORMATION.getSubDirectory();

        // 使用当前日期和时间作为文件名的一部分
        Date generateData = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS");
        String currentDateTime = sdf.format(generateData);

        // 构建文件名
        String fileName = subDirectory + "/" + userId + "_" + currentDateTime + "_scoreInformationData.xlsx";

        // 上传到 Minio
        boolean b = minioService.uploadStreamToMinio(inputStream, fileName, bucketName);

        // 如果上传成功了 则修改数据库中的用户下载消息
        if (b) {
            DownloadMessagePO downloadMessagePO = new DownloadMessagePO();
            downloadMessagePO.setCreatedAt(generateData);
            downloadMessagePO.setFileName(DownloadFileNameEnum.STUDENT_SCORE_INFORMATION_EXPORT_FILE.getFilename());
            downloadMessagePO.setFileMinioUrl(bucketName + "/" + fileName);
            downloadMessagePO.setFileSize((long) fileSize);
            int insert = downloadMessageMapper.insert(downloadMessagePO);
            log.info("下载成绩数据、下载文件消息插入 " + insert);

            // 获取自增ID
            Long generatedId = downloadMessagePO.getId();
            PlatformMessagePO platformMessagePO = new PlatformMessagePO();
            platformMessagePO.setCreatedAt(generateData);
            platformMessagePO.setUserId(userId);
            platformMessagePO.setIsRead(false);
            platformMessagePO.setRelatedMessageId(generatedId);
            platformMessagePO.setMessageType(MessageEnum.DOWNLOAD_MSG.getMessage_name());
            int insert1 = platformMessageMapper.insert(platformMessagePO);
            log.info("用户下载消息插入结果 " + insert1);
        }
    }

    /**
     * 采用线程池技术，提高 SQL 查询筛选参数效率
     * 获取缴费数据筛选参数
     *
     * @return
     */
    @Override
    @LogExecutionTime
    public PaymentInformationSelectArgs filterPaymentInformationSelectArgs() {
        PaymentInformationSelectArgs paymentInformationSelectArgs = new PaymentInformationSelectArgs();
        PaymentInfoFilterRO filter = new PaymentInfoFilterRO();

        ExecutorService executor = Executors.newFixedThreadPool(7); // 8 代表你有8个查询

        Future<List<String>> distinctGradesFuture = executor.submit(() -> paymentInfoMapper.getDistinctGrades(filter));
        Future<List<String>> distinctLevelsFuture = executor.submit(() -> paymentInfoMapper.getDistinctLevels(filter));
        Future<List<String>> distinctStudyFormsFuture = executor.submit(() -> paymentInfoMapper.getDistinctStudyForms(filter));
        Future<List<String>> distinctClassNamesFuture = executor.submit(() -> paymentInfoMapper.getDistinctClassNames(filter));
        Future<List<String>> distinctTeachingPointsFuture = executor.submit(() -> paymentInfoMapper.getDistinctTeachingPoints(filter));
        Future<List<String>> distinctCollegeNamesFuture = executor.submit(() -> paymentInfoMapper.getDistinctCollegeNames(filter));
        Future<List<String>> distinctAcademicYearsFuture = executor.submit(() -> paymentInfoMapper.getDistinctAcademicYears(filter));

        try {
            paymentInformationSelectArgs.setGrades(distinctGradesFuture.get());
            paymentInformationSelectArgs.setLevels(distinctLevelsFuture.get());
            paymentInformationSelectArgs.setStudyForms(distinctStudyFormsFuture.get());
            paymentInformationSelectArgs.setClassNames(distinctClassNamesFuture.get());
            paymentInformationSelectArgs.setTeachingPoints(distinctTeachingPointsFuture.get());
            paymentInformationSelectArgs.setCollegeNames(distinctCollegeNamesFuture.get());
            paymentInformationSelectArgs.setAcademicYears(distinctAcademicYearsFuture.get());

        } catch (Exception e) {
            // Handle exceptions like InterruptedException or ExecutionException
            e.printStackTrace();
        } finally {
            executor.shutdown(); // Always remember to shutdown the executor after usage
        }

        return paymentInformationSelectArgs;
    }

    /**
     * 为继续教育学院学历教育部获取班级信息
     *
     * @param classInformationFilterROPageRO 班级筛选参数
     * @return
     */
    @Override
    public FilterDataVO filterClassInfo(PageRO<ClassInformationFilterRO> classInformationFilterROPageRO) {
        FilterDataVO<ClassInformationVO> classInformationVOFilterDataVO = new FilterDataVO<>();
        log.info(StpUtil.getLoginId() + " 查询班级的参数是 " + classInformationFilterROPageRO);
        List<ClassInformationVO> classInformationVOList = classInformationMapper.getClassInfoByFilter(
                classInformationFilterROPageRO.getEntity(),
                classInformationFilterROPageRO.getPageSize(),
                (classInformationFilterROPageRO.getPageNumber() - 1) * classInformationFilterROPageRO.getPageSize()
        );
        long countStudentPayInfoByFilter = classInformationMapper.getCountClassInfoByFilter(classInformationFilterROPageRO.getEntity());
//        long countStudentPayInfoByFilter = 100L;
        classInformationVOFilterDataVO.setTotal(countStudentPayInfoByFilter);
        classInformationVOFilterDataVO.setData(classInformationVOList);

        return classInformationVOFilterDataVO;
    }


    /**
     * 采用线程池技术，提高 SQL 查询筛选参数效率
     * 获取班级数据筛选参数
     *
     * @return
     */
    @Override
    @LogExecutionTime
    public ClassInformationSelectArgs filterClassInformationSelectArgs() {
        ClassInformationSelectArgs classInformationSelectArgs = new ClassInformationSelectArgs();
        ClassInformationFilterRO filter = new ClassInformationFilterRO();

        ExecutorService executor = Executors.newFixedThreadPool(8); // 8 代表你有8个查询

        Future<List<String>> distinctGradesFuture = executor.submit(() -> classInformationMapper.getDistinctGrades(filter));
        Future<List<String>> distinctLevelsFuture = executor.submit(() -> classInformationMapper.getDistinctLevels(filter));
        Future<List<String>> distinctStudyFormsFuture = executor.submit(() -> classInformationMapper.getDistinctStudyForms(filter));
        Future<List<String>> distinctClassNamesFuture = executor.submit(() -> classInformationMapper.getDistinctClassNames(filter));
        Future<List<String>> distinctTeachingPointsFuture = executor.submit(() -> classInformationMapper.getDistinctTeachingPoints(filter));
        Future<List<String>> distinctCollegeNamesFuture = executor.submit(() -> classInformationMapper.getDistinctCollegeNames(filter));
        Future<List<String>> distinctMajorNamesFuture = executor.submit(() -> classInformationMapper.getDistinctMajorNames(filter));
        Future<List<String>> distinctStudyPeriodsFuture = executor.submit(() -> classInformationMapper.getDistinctStudyPeriods(filter));

        try {
            classInformationSelectArgs.setGrades(distinctGradesFuture.get());

            classInformationSelectArgs.setLevels(distinctLevelsFuture.get());
            classInformationSelectArgs.setStudyForms(distinctStudyFormsFuture.get());
            classInformationSelectArgs.setClassNames(distinctClassNamesFuture.get());
            classInformationSelectArgs.setTeachingPoints(distinctTeachingPointsFuture.get());
            classInformationSelectArgs.setCollegeNames(distinctCollegeNamesFuture.get());
            classInformationSelectArgs.setMajorNames(distinctMajorNamesFuture.get());
            classInformationSelectArgs.setStudyDurations(distinctStudyPeriodsFuture.get());

        } catch (Exception e) {
            // Handle exceptions like InterruptedException or ExecutionException
            e.printStackTrace();
        } finally {
            executor.shutdown(); // Always remember to shutdown the executor after usage
        }

        return classInformationSelectArgs;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void exportClassInformationData(PageRO<ClassInformationFilterRO> classInformationFilterROPageRO, String userId,
                                           PlatformMessagePO platformMessagePO) {
        ApplicationContext ctx = ApplicationContextProvider.getApplicationContext();
        ClassInformationMapper classInformationMapper1 = ctx.getBean(ClassInformationMapper.class);
        MinioService minioService = ctx.getBean(MinioService.class);
        PlatformMessageMapper platformMessageMapper = ctx.getBean(PlatformMessageMapper.class);
        DownloadMessageMapper downloadMessageMapper = ctx.getBean(DownloadMessageMapper.class);

        // 获取数据
        List<ClassInformationDownloadVO> classInformationDownloadVOS = classInformationMapper1.
                downloadClassInformationDataByManager0(classInformationFilterROPageRO.getEntity());

        log.info("导出了 " + classInformationDownloadVOS.size() + " 条成绩数据");

        // 为每个StudentStatusAllVO对象设置序号
        for (int i = 0; i < classInformationDownloadVOS.size(); i++) {
            classInformationDownloadVOS.get(i).setIndex(i + 1);
        }

        // 获取所有带有@ExcelProperty注解的字段
        List<String> includeColumnFiledNames = Arrays.stream(ClassInformationDownloadVO.class.getDeclaredFields())
                .filter(field -> field.getAnnotation(ExcelProperty.class) != null)
                .map(Field::getName)
                .collect(Collectors.toList());

        // 使用 ByteArrayOutputStream 将数据写入到流中
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // 根据数据量计算需要的sheet数量
        final int MAX_ROWS = 1000000;  // 10W
        int totalSize = classInformationDownloadVOS.size();
        int sheetCount = (totalSize / MAX_ROWS) + (totalSize % MAX_ROWS == 0 ? 0 : 1);

        ExcelWriter excelWriter = EasyExcel.write(outputStream, ClassInformationDownloadVO.class)
                .includeColumnFiledNames(includeColumnFiledNames)
                .build();

        for (int i = 0; i < sheetCount; i++) {
            int startIndex = i * MAX_ROWS;
            int endIndex = Math.min((i + 1) * MAX_ROWS, totalSize);
            List<ClassInformationDownloadVO> currentSheetData = classInformationDownloadVOS.subList(startIndex, endIndex);

            WriteSheet writeSheet = EasyExcel.writerSheet(i, "班级数据_" + (i + 1)).build();
            excelWriter.write(currentSheetData, writeSheet);
        }

        excelWriter.finish();

        // 将流转换为 ByteArrayInputStream
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        // 获取文件大小
        int fileSize = outputStream.size();
        // 获取桶名和子目录
        String bucketName = MinioBucketEnum.DATA_DOWNLOAD_CLASS_INFORMATIONS.getBucketName();
        String subDirectory = MinioBucketEnum.DATA_DOWNLOAD_CLASS_INFORMATIONS.getSubDirectory();

        // 使用当前日期和时间作为文件名的一部分
        Date generateData = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS");
        String currentDateTime = sdf.format(generateData);

        // 构建文件名
        String fileName = subDirectory + "/" + userId + "_" + currentDateTime + "_classInformationData.xlsx";

        // 上传到 Minio
        boolean b = minioService.uploadStreamToMinio(inputStream, fileName, bucketName);

        // 如果上传成功了 则修改数据库中的用户下载消息
        if (b) {
            DownloadMessagePO downloadMessagePO = new DownloadMessagePO();
            downloadMessagePO.setCreatedAt(generateData);
            downloadMessagePO.setFileName(DownloadFileNameEnum.CLASS_INFORMATION_EXPORT_FILE.getFilename());
            downloadMessagePO.setFileMinioUrl(bucketName + "/" + fileName);
            downloadMessagePO.setFileSize((long) fileSize);
            int insert = downloadMessageMapper.insert(downloadMessagePO);

            platformMessagePO.setRelatedMessageId(downloadMessagePO.getId());
            platformMessageMapper.update(platformMessagePO, new LambdaQueryWrapper<PlatformMessagePO>().
                    eq(PlatformMessagePO::getId, platformMessagePO.getId()));

            log.info("下载班级信息数据完成 " + insert);
        }
    }


    /**
     * 获取排课表的课程信息
     *
     * @param courseScheduleFilterROPageRO
     * @return
     */
    @Override
    public FilterDataVO filterScheduleCoursesInformation(PageRO<CourseScheduleFilterRO> courseScheduleFilterROPageRO) {


        // 获取教学计划
        CourseInformationRO courseInformationRO = new CourseInformationRO();
        BeanUtils.copyProperties(courseScheduleFilterROPageRO.getEntity(), courseInformationRO);

        List<CourseInformationVO> courseInformationVOS = courseInformationMapper.selectByFilterAndPage(courseInformationRO,
                courseScheduleFilterROPageRO.getPageSize(),
                (courseScheduleFilterROPageRO.getPageNumber() - 1) * courseScheduleFilterROPageRO.getPageSize());

        List<CourseInformationScheduleVO> courseInformationScheduleVOS = new ArrayList<>();
        // 首先获取教学计划中与排课表相对应的课程
        for (CourseInformationVO courseInformationVO : courseInformationVOS) {
            CourseInformationScheduleVO courseInformationScheduleVO = new CourseInformationScheduleVO();
            BeanUtils.copyProperties(courseInformationVO, courseInformationScheduleVO);
            courseInformationScheduleVO.setMainTeachers(new ArrayList<>());
            courseInformationScheduleVO.setTutors(new ArrayList<>());

            // 比较年级、专业、学习形式、层次、班级名称、课程名称
            CourseScheduleFilterRO courseScheduleFilterRO = new CourseScheduleFilterRO();
            courseScheduleFilterRO.setGrade(courseInformationVO.getGrade());
            courseScheduleFilterRO.setMajorName(courseInformationVO.getMajorName());
            courseScheduleFilterRO.setLevel(courseInformationVO.getLevel());
            courseScheduleFilterRO.setStudyForm(courseInformationVO.getStudyForm());
            courseScheduleFilterRO.setAdminClassName(courseInformationVO.getClassName());
            courseScheduleFilterRO.setCourseName(courseInformationVO.getCourseName());
            List<ScheduleCourseInformationVO> scheduleCourseInformationVOS = courseScheduleMapper.selectCoursesInformationWithoutPage(courseScheduleFilterRO);

            for (ScheduleCourseInformationVO scheduleCourseInformationVO : scheduleCourseInformationVOS) {
//                courseInformationScheduleVO.setTeachingMethod(scheduleCourseInformationVO.getTeachingMethod());
                // 获取主讲教师
                if (scheduleCourseInformationVO.getTeacherUsername() != null) {
                    TeacherInformationVO mainTeacher = new TeacherInformationVO();
                    mainTeacher.setTeacherUsername(scheduleCourseInformationVO.getTeacherUsername());
                    mainTeacher.setName(scheduleCourseInformationVO.getMainTeacherName());
                    mainTeacher.setIdCardNumber(scheduleCourseInformationVO.getMainTeacherIdentity());
                    mainTeacher.setWorkNumber(scheduleCourseInformationVO.getMainTeacherId());
                    courseInformationScheduleVO.getMainTeachers().add(mainTeacher);
                }

                if (scheduleCourseInformationVO.getTeachingAssistantUsername() != null) {
                    TeacherInformationVO tutor = new TeacherInformationVO();
                    tutor.setTeacherUsername(scheduleCourseInformationVO.getTeachingAssistantUsername());
                    tutor.setName(scheduleCourseInformationVO.getTutorName());
                    tutor.setIdCardNumber(scheduleCourseInformationVO.getTutorIdentity());
                    tutor.setWorkNumber(scheduleCourseInformationVO.getTutorId());
                    courseInformationScheduleVO.getTutors().add(tutor);
                }
            }

            if (courseScheduleFilterROPageRO.getEntity().getTeachingMethod() == null) {
                courseInformationScheduleVOS.add(courseInformationScheduleVO);
            } else if (courseScheduleFilterROPageRO.getEntity().getTeachingMethod() != null &&
                    (!"线下".equals(courseScheduleFilterROPageRO.getEntity().getTeachingMethod()))) {
                courseInformationScheduleVOS.add(courseInformationScheduleVO);
            } else {

            }


        }

        FilterDataVO<CourseInformationScheduleVO> filterDataVO = new FilterDataVO<>();
        log.info(StpUtil.getLoginId() + " 查询排课表课程信息的参数是 " + courseScheduleFilterROPageRO);

        long l = courseInformationMapper.getCountByFilterAndPage(courseInformationRO);
        filterDataVO.setTotal(l);
        filterDataVO.setData(courseInformationScheduleVOS);

        return filterDataVO;
    }

    /**
     * 采用线程池技术，提高 SQL 查询筛选参数效率
     * 获取排课表筛选参数
     *
     * @return
     */
    @Override
    @LogExecutionTime
    public ScheduleCourseInformationSelectArgs filterScheduleCourseInformationSelectArgs() {

        ScheduleCourseInformationSelectArgs scheduleCourseInformationSelectArgs = new ScheduleCourseInformationSelectArgs();
        CourseScheduleFilterRO filter = new CourseScheduleFilterRO();

        ExecutorService executor = Executors.newFixedThreadPool(8); // 8 代表你有8个查询

        Future<List<String>> distinctGradesFuture = executor.submit(() -> courseScheduleMapper.getDistinctGrades(filter));
        Future<List<String>> distinctCollegeNamesFuture = executor.submit(() -> courseScheduleMapper.getDistinctCollegeNames(filter));
        Future<List<String>> distinctStudyFormsFuture = executor.submit(() -> courseScheduleMapper.getDistinctStudyForms(filter));
        Future<List<String>> distinctClassNamesFuture = executor.submit(() -> courseScheduleMapper.getDistinctClassNames(filter));
        Future<List<String>> distinctLevelsFuture = executor.submit(() -> courseScheduleMapper.getDistinctLevels(filter));
        Future<List<String>> distinctMajorNamesFuture = executor.submit(() -> courseScheduleMapper.getDistinctMajorNames(filter));
        Future<List<String>> distinctTeachingClassesFuture = executor.submit(() -> courseScheduleMapper.getDistinctTeachingClasses(filter));
        Future<List<String>> distinctCourseNamesFuture = executor.submit(() -> courseScheduleMapper.getDistinctCourseNames(filter));

        try {
            scheduleCourseInformationSelectArgs.setGrades(distinctGradesFuture.get());

            scheduleCourseInformationSelectArgs.setLevels(distinctLevelsFuture.get());
            scheduleCourseInformationSelectArgs.setStudyForms(distinctStudyFormsFuture.get());
            scheduleCourseInformationSelectArgs.setAdminClassNames(distinctClassNamesFuture.get());
            scheduleCourseInformationSelectArgs.setLevels(distinctLevelsFuture.get());
            scheduleCourseInformationSelectArgs.setCollegeNames(distinctCollegeNamesFuture.get());
            scheduleCourseInformationSelectArgs.setMajorNames(distinctMajorNamesFuture.get());
            scheduleCourseInformationSelectArgs.setTeachingClasses(distinctTeachingClassesFuture.get());
            scheduleCourseInformationSelectArgs.setCourseNames(distinctCourseNamesFuture.get());

        } catch (Exception e) {
            // Handle exceptions like InterruptedException or ExecutionException
            e.printStackTrace();
        } finally {
            executor.shutdown(); // Always remember to shutdown the executor after usage
        }

        return scheduleCourseInformationSelectArgs;
    }


    /**
     * 采用线程池技术，提高 SQL 查询筛选参数效率
     * 获取排课表课程数据筛选参数
     *
     * @return
     */
    @Override
    @LogExecutionTime
    public ScheduleCourseInformationSelectArgs getCoursesArgs(CourseScheduleFilterRO filter) {
        ScheduleCourseInformationSelectArgs scheduleCourseInformationSelectArgs = new ScheduleCourseInformationSelectArgs();

        ExecutorService executor = Executors.newFixedThreadPool(8); // 8 代表你有8个查询

        Future<List<String>> distinctGradesFuture = executor.submit(() -> courseInformationMapper.getDistinctGrades(filter));
        Future<List<String>> distinctCollegeNamesFuture = executor.submit(() -> courseInformationMapper.getDistinctCollegeNames(filter));
        Future<List<String>> distinctStudyFormsFuture = executor.submit(() -> courseInformationMapper.getDistinctStudyForms(filter));
        Future<List<String>> distinctClassNamesFuture = executor.submit(() -> courseInformationMapper.getDistinctClassNames(filter));
        Future<List<String>> distinctLevelsFuture = executor.submit(() -> courseInformationMapper.getDistinctLevels(filter));
        Future<List<String>> distinctMajorNamesFuture = executor.submit(() -> courseInformationMapper.getDistinctMajorNames(filter));
        Future<List<String>> distinctSemastersFuture = executor.submit(() -> courseInformationMapper.getDistinctSemasters(filter));
        Future<List<String>> distinctCourseNamesFuture = executor.submit(() -> courseInformationMapper.getDistinctCourseNames(filter));

        try {
            scheduleCourseInformationSelectArgs.setGrades(distinctGradesFuture.get());

            scheduleCourseInformationSelectArgs.setLevels(distinctLevelsFuture.get());
            scheduleCourseInformationSelectArgs.setStudyForms(distinctStudyFormsFuture.get());
            scheduleCourseInformationSelectArgs.setAdminClassNames(distinctClassNamesFuture.get());
            scheduleCourseInformationSelectArgs.setLevels(distinctLevelsFuture.get());
            scheduleCourseInformationSelectArgs.setCollegeNames(distinctCollegeNamesFuture.get());
            scheduleCourseInformationSelectArgs.setMajorNames(distinctMajorNamesFuture.get());
            scheduleCourseInformationSelectArgs.setSemesters(distinctSemastersFuture.get());
            scheduleCourseInformationSelectArgs.setCourseNames(distinctCourseNamesFuture.get());

        } catch (Exception e) {
            // Handle exceptions like InterruptedException or ExecutionException
            e.printStackTrace();
        } finally {
            executor.shutdown(); // Always remember to shutdown the executor after usage
        }

        return scheduleCourseInformationSelectArgs;
    }


    /**
     * 获取排课表详细信息
     *
     * @return
     */
    public FilterDataVO filterSchedulesInformation(PageRO<CourseScheduleFilterRO> courseScheduleFilterROPageRO) {
        List<SchedulesVO> schedulesVOS = courseScheduleMapper.selectSchedulesInformation(
                courseScheduleFilterROPageRO.getEntity(),
                courseScheduleFilterROPageRO.getPageSize(),
                (courseScheduleFilterROPageRO.getPageNumber() - 1) * courseScheduleFilterROPageRO.getPageSize());

        for (SchedulesVO schedulesVO : schedulesVOS) {
            String onlinePlatform = schedulesVO.getOnlinePlatform();

            if (onlinePlatform != null) {
                VideoStreamRecordPO videoStreamRecordPO = videoStreamRecordsMapper.selectOne(
                        new LambdaQueryWrapper<VideoStreamRecordPO>().eq(VideoStreamRecordPO::getId, onlinePlatform));

                if (videoStreamRecordPO != null) {
                    // 此处你只检查了videoStreamRecordPO是否为null，但没使用它的其他属性。
                    // 假设你只想检查它是否存在，并据此设置onlinePlatform
                    // 设置直播状态
                    schedulesVO.setLivingStatus(videoStreamRecordPO.getWatchStatus());
                    schedulesVO.setChannelId(videoStreamRecordPO.getChannelId());
                }
            } else {
                schedulesVO.setLivingStatus("未开播");
            }
        }


        FilterDataVO<SchedulesVO> filterDataVO = new FilterDataVO<>();
        log.info(StpUtil.getLoginId() + " 查询排课表课程信息的参数是 " + courseScheduleFilterROPageRO);

        long l = courseScheduleMapper.selectCoursesInformationCount(courseScheduleFilterROPageRO.getEntity());
        filterDataVO.setTotal(l);
        filterDataVO.setData(schedulesVOS);

        return filterDataVO;
    }


    /**
     * 导出学籍数据到指定继续教育学院用户 的消息中
     *
     * @param paymentInfoFilterROPageRO
     * @param userId
     */
    @Override
    @Transactional
    public void exportPaymentInfoData(PageRO<PaymentInfoFilterRO> paymentInfoFilterROPageRO, String userId, PlatformMessagePO platformMessagePO) {
        log.info("用户批量导出缴费筛选参数" + paymentInfoFilterROPageRO);
        ApplicationContext ctx = ApplicationContextProvider.getApplicationContext();
        PaymentInfoMapper paymentInfoMapper1 = ctx.getBean(PaymentInfoMapper.class);
        MinioService minioService = ctx.getBean(MinioService.class);
        PlatformMessageMapper platformMessageMapper = ctx.getBean(PlatformMessageMapper.class);
        DownloadMessageMapper downloadMessageMapper = ctx.getBean(DownloadMessageMapper.class);

        // 获取数据
        List<PaymentInfoAllVO> paymentInfoAllVOS = paymentInfoMapper1.
                downloadPaymentInfoDataByManager0(paymentInfoFilterROPageRO.getEntity());

        log.info("导出了 " + paymentInfoAllVOS.size() + " 条缴费数据");

        // 为每个StudentStatusAllVO对象设置序号
        for (int i = 0; i < paymentInfoAllVOS.size(); i++) {
            paymentInfoAllVOS.get(i).setIndex(i + 1);
        }

        // 获取所有带有@ExcelProperty注解的字段
        List<String> includeColumnFiledNames = Arrays.stream(PaymentInfoAllVO.class.getDeclaredFields())
                .filter(field -> field.getAnnotation(ExcelProperty.class) != null)
                .map(Field::getName)
                .collect(Collectors.toList());

        // 使用 ByteArrayOutputStream 将数据写入到流中
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        EasyExcel.write(outputStream, PaymentInfoAllVO.class)
                .includeColumnFiledNames(includeColumnFiledNames)  // 只导出带有@ExcelProperty注解的字段
                .sheet("缴费数据")
                .doWrite(paymentInfoAllVOS);

        // 将流转换为 ByteArrayInputStream
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        // 获取文件大小
        int fileSize = outputStream.size();
        // 获取桶名和子目录
        String bucketName = MinioBucketEnum.DATA_DOWNLOAD_STUDENT_FEES.getBucketName();
        String subDirectory = MinioBucketEnum.DATA_DOWNLOAD_STUDENT_FEES.getSubDirectory();

        // 使用当前日期和时间作为文件名的一部分
        Date generateData = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS");
        String currentDateTime = sdf.format(generateData);

        // 构建文件名
        String fileName = subDirectory + "/" + userId + "_" + currentDateTime + "_paymentInfoData.xlsx";

        // 上传到 Minio
        boolean b = minioService.uploadStreamToMinio(inputStream, fileName, bucketName);

        // 如果上传成功了 则修改数据库中的用户下载消息
        if (b) {
            DownloadMessagePO downloadMessagePO = new DownloadMessagePO();
            downloadMessagePO.setCreatedAt(generateData);
            downloadMessagePO.setFileName(DownloadFileNameEnum.STUDENT_FEES_EXPORT_FILE.getFilename());
            downloadMessagePO.setFileMinioUrl(bucketName + "/" + fileName);
            downloadMessagePO.setFileSize((long) fileSize);
            int insert = downloadMessageMapper.insert(downloadMessagePO);
            log.info("下载缴费数据、下载文件消息插入 " + insert);

            // 获取自增ID
            platformMessagePO.setRelatedMessageId(downloadMessagePO.getId());
            platformMessageMapper.update(platformMessagePO, new LambdaQueryWrapper<PlatformMessagePO>().
                    eq(PlatformMessagePO::getId, platformMessagePO.getId()));

            log.info("下载缴费信息数据完成 " + insert);
        }
    }


    /**
     * 获取排课表课程管理信息
     *
     * @return
     */
    public FilterDataVO getScheduleCourses(PageRO<CourseScheduleFilterRO> courseScheduleFilterROPageRO) {
        log.info(StpUtil.getLoginId() + " 查询排课表课程信息的参数是 " + courseScheduleFilterROPageRO);
        // 展示给前端的排课课程管理信息
        List<ScheduleCoursesInformationVO> scheduleCoursesInformationVOS = new ArrayList<>();

        // 获取指定条件的排课表表课程信息 但是还需要做二次处理 比如 去掉同一批次的重复信息 把时间和班级 还有直播间信息 单独摘出来
        String redisKey = "getScheduleCoursesInformation:" + courseScheduleFilterROPageRO.getEntity().toString();
        ValueOperations<String, Object> valueOps1 = redisTemplate.opsForValue();
        List<ScheduleCoursesInformationBO> schedulesVOS;

        // Check if data is present in Redis cache
        if (Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))) {
            schedulesVOS = (List<ScheduleCoursesInformationBO>) valueOps1.get(redisKey);
        } else {
            // If not present in cache, retrieve data from the database
            schedulesVOS = courseScheduleMapper.getScheduleCoursesInformation(courseScheduleFilterROPageRO.getEntity());
            // Store the data in cache with a timeout of 30 minutes
            valueOps1.set(redisKey, schedulesVOS, 30, TimeUnit.MINUTES);
        }

        List<ScheduleCoursesInformationVO> scheduleCoursesInformationVOList = new ArrayList<>();

        List<String> errorCourses = new ArrayList<>();

        // 去重 把同一批次的拿到 再去根据时间排序
        for (ScheduleCoursesInformationBO schedulesVO : schedulesVOS) {
            // 使用流来处理 这个 ScheduleCoursesInformationVO 对象，它如果发现这个 List 不存在，则新建
            ScheduleCoursesInformationVO scheduleCoursesInformationVO = scheduleCoursesInformationVOList.stream()
                    .filter(vo -> vo.getBatchIndex().equals(schedulesVO.getBatchIndex()))
                    .findFirst()
                    .orElseGet(() -> {
                        ScheduleCoursesInformationVO newVO = new ScheduleCoursesInformationVO(schedulesVO.getBatchIndex());
                        scheduleCoursesInformationVOList.add(newVO);
                        newVO.setClassName(new ArrayList<>());
                        return newVO;
                    });

            // 接下来根据每个批次里的排课日期和排课时间 拿到具体现在最近的 并且拿到它的直播状态 和 channelI

            scheduleCoursesInformationVO.setMainTeacherName(schedulesVO.getMainTeacherName());
            scheduleCoursesInformationVO.setTeacherUsername(schedulesVO.getTeacherUsername());
            scheduleCoursesInformationVO.setCourseName(schedulesVO.getCourseName());

            if (scheduleCoursesInformationVO.getTeachingDate() == null) {
                scheduleCoursesInformationVO.setTeachingDate(schedulesVO.getTeachingDate());
                scheduleCoursesInformationVO.setTeachingTime(schedulesVO.getTeachingTime());
            } else {
                ScnuTimeInterval timeInterval = scnuXueliTools.getTimeInterval(schedulesVO.getTeachingDate(), schedulesVO.getTeachingTime());
                Date newStart = timeInterval.getStart();
                Date now = new Date();
                Date currentTeachingDate = scnuXueliTools.getTimeInterval(scheduleCoursesInformationVO.getTeachingDate(),
                        scheduleCoursesInformationVO.getTeachingTime()).getStart();


                // 比较时间差
                long diffNew = newStart.getTime() - now.getTime();
                long diffCurrent = currentTeachingDate.getTime() - now.getTime();

                // 如果新的开始时间比现在时间晚，并且与现在的时间差比当前记录的时间差小
                if (diffCurrent > 0 && diffNew < 0) {
                    // 当前记录的排课的上课日期和上课时间 比此时此刻的大 而新的排课的上课日期和上课时间比现在小 那么就啥也不做
                } else if (diffCurrent > 0) {
                    if (Math.abs(diffNew) < Math.abs(diffCurrent)) {
                        // 选最近的
                        scheduleCoursesInformationVO.setTeachingDate(schedulesVO.getTeachingDate());
                        scheduleCoursesInformationVO.setTeachingTime(schedulesVO.getTeachingTime());
                        scheduleCoursesInformationVO.setOnlinePlatform(schedulesVO.getOnlinePlatform());
                    }

                } else {
                    // 目前拿到的上课时间 比当下的时间 大
                    if (diffNew > 0) {
                        scheduleCoursesInformationVO.setTeachingDate(schedulesVO.getTeachingDate());
                        scheduleCoursesInformationVO.setTeachingTime(schedulesVO.getTeachingTime());
                        scheduleCoursesInformationVO.setOnlinePlatform(schedulesVO.getOnlinePlatform());

                    } else {
                        if (Math.abs(diffNew) < Math.abs(diffCurrent)) {
                            // 选最近的
                            scheduleCoursesInformationVO.setTeachingDate(schedulesVO.getTeachingDate());
                            scheduleCoursesInformationVO.setTeachingTime(schedulesVO.getTeachingTime());
                            scheduleCoursesInformationVO.setOnlinePlatform(schedulesVO.getOnlinePlatform());
                        }
                    }
                }

            }

        }


        // 将拿到的每个批次 进行时间的升序排列 与现在相比比现在大的排在前面 比现在小的排在后面
        // 分为两组后 组内 按照 teachingDate 和 teachingTime 升序排列

        // 创建一个固定大小的线程池
        ExecutorService executorService = Executors.newFixedThreadPool(200);

        // 使用CompletableFuture来异步处理每个scheduleCoursesInformationVO
        List<CompletableFuture<Void>> futures = scheduleCoursesInformationVOList.stream()
                .map(scheduleCoursesInformationVO -> CompletableFuture.runAsync(() -> {
                    super.processScheduleCoursesInformationVO(
                            scheduleCoursesInformationVO,
                            errorCourses
                    );
                }, executorService))
                .collect(Collectors.toList());

        // 等待所有的future完成
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        // 关闭线程池，不再接受新任务，等待所有任务完成
        executorService.shutdown();

        // 等待所有的future完成
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        // 关闭线程池，不再接受新任务，等待所有任务完成
        executorService.shutdown();

        Long pageNumber = courseScheduleFilterROPageRO.getPageNumber();
        Long pageSize = courseScheduleFilterROPageRO.getPageSize();

        // 计算开始索引
        long startIndex = (pageNumber - 1) * pageSize;

        // 计算结束索引
        long endIndex = startIndex + pageSize;

        endIndex = endIndex > scheduleCoursesInformationVOList.size() ? scheduleCoursesInformationVOList.size() : endIndex;

        List<ScheduleCoursesInformationVO> pageData = scheduleCoursesInformationVOList.subList((int) startIndex, (int) endIndex);

        FilterDataVO<ScheduleCoursesInformationVO> filterDataVO = new FilterDataVO<>();

        long total = scheduleCoursesInformationVOList.size();
        filterDataVO.setTotal(total);
        filterDataVO.setData(pageData);

        log.info("所有的排课表信息出现错误的记录 \n" + errorCourses);

        return filterDataVO;
    }


}
