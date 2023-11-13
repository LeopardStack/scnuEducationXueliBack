package com.scnujxjy.backendpoint.util.filter;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.alibaba.excel.EasyExcel;
import com.scnujxjy.backendpoint.constant.enums.DownloadFileNameEnum;
import com.scnujxjy.backendpoint.constant.enums.MessageEnum;
import com.scnujxjy.backendpoint.dao.entity.platform_message.DownloadMessagePO;
import com.scnujxjy.backendpoint.dao.entity.platform_message.PlatformMessagePO;
import com.scnujxjy.backendpoint.dao.mapper.platform_message.DownloadMessageMapper;
import com.scnujxjy.backendpoint.dao.mapper.platform_message.PlatformMessageMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseScheduleMapper;
import com.scnujxjy.backendpoint.model.bo.teaching_process.CourseScheduleStudentExcelBO;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.service.minio.MinioService;
import com.scnujxjy.backendpoint.util.ApplicationContextProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.scnujxjy.backendpoint.constant.enums.MinioBucketEnum.DATA_DOWNLOAD_SCORE_INFORMATION;

@Component
@Slf4j
public class CourseScheduleFilter extends AbstractFilter {
    @Override
    public void exportStudentInformationBatchIndex(PageRO<CourseScheduleStudentExcelBO> courseScheduleStudentExcelBOPageRO, String userId) {
        if (Objects.isNull(courseScheduleStudentExcelBOPageRO)
                || Objects.isNull(courseScheduleStudentExcelBOPageRO.getEntity())
                || Objects.isNull(courseScheduleStudentExcelBOPageRO.getEntity().getBatchIndex())) {
            return;
        }
        CourseScheduleStudentExcelBO courseScheduleStudentExcelBO = courseScheduleStudentExcelBOPageRO.getEntity();
        Long batchIndex = courseScheduleStudentExcelBO.getBatchIndex();
        Set<String> includeColumnFiledNames = courseScheduleStudentExcelBO.getIncludeColumnFiledNames();
        CourseScheduleMapper courseScheduleMapper = ApplicationContextProvider.getApplicationContext().getBean(CourseScheduleMapper.class);
        List<CourseScheduleStudentExcelBO> studentInformation = courseScheduleMapper.getStudentInformationBatchIndex(batchIndex);
        if (CollUtil.isEmpty(studentInformation)) {
            return;
        }
        HashSet<CourseScheduleStudentExcelBO> studentInformationSet = new HashSet<>(studentInformation);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        EasyExcel.write(outputStream, CourseScheduleStudentExcelBO.class)
                .includeColumnFieldNames(includeColumnFiledNames)
                .sheet()
                .doWrite(studentInformationSet);
        MinioService minioService = ApplicationContextProvider.getApplicationContext().getBean(MinioService.class);
        PlatformMessageMapper platformMessageMapper = ApplicationContextProvider.getApplicationContext().getBean(PlatformMessageMapper.class);
        DownloadMessageMapper downloadMessageMapper = ApplicationContextProvider.getApplicationContext().getBean(DownloadMessageMapper.class);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        // 获取文件大小
        int size = outputStream.size();
        // 获取桶名和子目录
        String bucketName = DATA_DOWNLOAD_SCORE_INFORMATION.getBucketName();
        String subDirectory = DATA_DOWNLOAD_SCORE_INFORMATION.getSubDirectory();
        // 使用当前时间作为文件名前缀
        DateTime currentDate = DateUtil.date();
        String date = DateUtil.format(currentDate, "yyyyMMdd_HHmmss_SSS");
        String filename = subDirectory + "/" + userId + "_" + date + "_CourseScheduleStudentInformationDate.xlsx";
        // 上传到minio
        boolean isUpload = minioService.uploadStreamToMinio(inputStream, filename, bucketName);
        if (isUpload) {
            DownloadMessagePO downloadMessagePO = DownloadMessagePO.builder()
                    .createdAt(currentDate)
                    .fileName(DownloadFileNameEnum.STUDENT_SCORE_INFORMATION_EXPORT_FILE.getFilename())
                    .fileMinioUrl(bucketName + "/" + filename)
                    .fileSize((long) size)
                    .build();
            int count = downloadMessageMapper.insert(downloadMessagePO);
            log.info("根据批次id: {} 下载学生数据消息插入 {} 条", batchIndex, count);

            if (count == 0) {
                return;
            }
            // 获取自增id
            Long id = downloadMessagePO.getId();
            PlatformMessagePO platformMessagePO = PlatformMessagePO.builder()
                    .createdAt(currentDate)
                    .userId(userId)
                    .isRead(false)
                    .relatedMessageId(id)
                    .messageType(MessageEnum.DOWNLOAD_MSG.getMessage_name())
                    .build();
            count = platformMessageMapper.insert(platformMessagePO);
            log.info("用户下载消息插入 {} 条", count);
        }
    }
}
