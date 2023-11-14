package com.scnujxjy.backendpoint.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rabbitmq.client.Channel;
import com.scnujxjy.backendpoint.constant.enums.MessageEnum;
import com.scnujxjy.backendpoint.dao.entity.college.CollegeInformationPO;
import com.scnujxjy.backendpoint.dao.entity.platform_message.PlatformMessagePO;
import com.scnujxjy.backendpoint.dao.entity.platform_message.UserUploadsPO;
import com.scnujxjy.backendpoint.dao.mapper.core_data.TeacherInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.platform_message.PlatformMessageMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseScheduleMapper;
import com.scnujxjy.backendpoint.model.bo.teaching_process.CourseScheduleStudentExcelBO;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.core_data.PaymentInfoFilterRO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.ClassInformationFilterRO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.StudentStatusFilterRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.ScoreInformationFilterRO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.CourseScheduleExcelImportVO;
import com.scnujxjy.backendpoint.service.InterBase.OldDataSynchronize;
import com.scnujxjy.backendpoint.service.core_data.PaymentInfoService;
import com.scnujxjy.backendpoint.service.minio.MinioService;
import com.scnujxjy.backendpoint.service.platform_message.UserUploadsService;
import com.scnujxjy.backendpoint.service.registration_record_card.ClassInformationService;
import com.scnujxjy.backendpoint.service.registration_record_card.StudentStatusService;
import com.scnujxjy.backendpoint.util.excelListener.CourseScheduleListener;
import com.scnujxjy.backendpoint.util.excelListener.CustomDateConverter;
import com.scnujxjy.backendpoint.util.filter.AbstractFilter;
import com.scnujxjy.backendpoint.util.filter.CollegeAdminFilter;
import com.scnujxjy.backendpoint.util.filter.CourseScheduleFilter;
import com.scnujxjy.backendpoint.util.filter.ManagerFilter;
import com.scnujxjy.backendpoint.util.tool.ScnuXueliTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

@Component
@Slf4j
public class MessageReceiver {
    @Value("${spring.rabbitmq.queue1}")
    private String queue1;

    @Value("${minio.importBucketName}")
    private String importBucketName;

    @Resource
    private StudentStatusService studentStatusService;

    @Resource
    private ClassInformationService classInformationService;

    @Resource
    private PaymentInfoService paymentInfoService;

    @Resource
    private UserUploadsService userUploadsService;

    @Resource
    private ScnuXueliTools scnuXueliTools;

    @Resource
    private MinioService minioService;

    @Resource
    private OldDataSynchronize oldDataSynchronize;

    @Resource
    private PlatformMessageMapper platformMessageMapper;

    @Resource
    private CourseInformationMapper courseInformationMapper;

    @Resource
    private TeacherInformationMapper teacherInformationMapper;

    @Resource
    private CourseScheduleMapper courseScheduleMapper;

    @RabbitListener(queuesToDeclare = @Queue("${spring.rabbitmq.queue1}"))
    public void process(String msg, Channel channel, Message message) {
        try {
            log.info("接收到消息 " + msg);
            if ("数据同步".equals(msg)) {
                oldDataSynchronize.synchronizeAllData();
                log.info("接收到消息");
            } else {
                log.info("其他消息，不予处理 " + msg);
            }

            // 手动确认消息
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.error("处理消息时出现异常: ", e);
            try {
                // 根据需要，拒绝消息并选择是否重新入队
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
            } catch (IOException ioException) {
                log.error("确认消息时出现异常: ", ioException);
            }
        }
    }

    @RabbitListener(
            queuesToDeclare = {
                    @Queue("${spring.rabbitmq.queue3}"),
//                    @Queue("${spring.rabbitmq.queue5}"),
                    @Queue("${spring.rabbitmq.queue4}")
            }
    )
    @RabbitHandler
    public void processExportData(String messageContent, Channel channel, Message msg) {
        log.info("收到消息，正在处理 ...");
        try {
            JSONObject message = JSON.parseObject(messageContent);
            String type = message.getString("type");

            if ("com.scnujxjy.backendpoint.model.ro.registration_record_card.StudentStatusFilterRO".equals(type)) {
                PageRO<StudentStatusFilterRO> pageRO = JSON.parseObject(message.getString("data"),
                        new TypeReference<PageRO<StudentStatusFilterRO>>() {
                        });
                AbstractFilter filter = JSON.parseObject(message.getString("filter"), new TypeReference<ManagerFilter>() {
                });
                String userId = message.getString("userId");
                log.info("拿到学籍数据筛选条件 " + pageRO.getEntity().toString());
                // 处理pageRO
                studentStatusService.generateBatchStudentStatusData(pageRO, filter, userId);
            } else if ("com.scnujxjy.backendpoint.model.ro.teaching_process.ScoreInformationFilterRO".equals(type)) {
                // 下载成绩数据，获取成绩筛选参数
                PageRO<ScoreInformationFilterRO> pageRO = JSON.parseObject(message.getString("data"),
                        new TypeReference<PageRO<ScoreInformationFilterRO>>() {
                        });
                AbstractFilter filter = JSON.parseObject(message.getString("filter"), new TypeReference<ManagerFilter>() {
                });
                String userId = message.getString("userId");
                log.info("拿到成绩数据筛选条件 " + pageRO.getEntity().toString());
                // 处理pageRO
                studentStatusService.generateBatchScoreInformationData(pageRO, filter, userId);

            } else if ("com.scnujxjy.backendpoint.model.ro.registration_record_card.ClassInformationFilterRO".equals(type)) {
                // 下载成绩数据，获取成绩筛选参数
                PageRO<ClassInformationFilterRO> pageRO = JSON.parseObject(message.getString("data"),
                        new TypeReference<PageRO<ClassInformationFilterRO>>() {
                        });
                AbstractFilter filter = JSON.parseObject(message.getString("filter"), new TypeReference<ManagerFilter>() {
                });
                String userId = message.getString("userId");
                log.info("拿到班级数据筛选条件 ");

                PlatformMessagePO platformMessagePO = generateMessage(userId);
                // 处理pageRO
                classInformationService.generateBatchClassInformationData(pageRO, filter, userId, platformMessagePO);

            } else if ("com.scnujxjy.backendpoint.model.ro.core_data.PaymentInfoFilterRO".equals(type)) {
                // 下载成绩数据，获取成绩筛选参数
                PageRO<PaymentInfoFilterRO> pageRO = JSON.parseObject(message.getString("data"),
                        new TypeReference<PageRO<PaymentInfoFilterRO>>() {
                        });
                AbstractFilter filter = JSON.parseObject(message.getString("filter"), new TypeReference<ManagerFilter>() {
                });
                String userId = message.getString("userId");
                log.info("拿到缴费数据筛选条件 ");
                PlatformMessagePO platformMessagePO = generateMessage(userId);
                // 处理pageRO
                paymentInfoService.generateBatchPaymentData(pageRO, filter, userId, platformMessagePO);

            } else if ("com.scnujxjy.backendpoint.model.bo.teaching_process.CourseScheduleStudentExcelBO".equals(type)) {
                PageRO<CourseScheduleStudentExcelBO> pageRO = JSON.parseObject(message.getString("data"),
                        new TypeReference<PageRO<CourseScheduleStudentExcelBO>>() {
                        });
                AbstractFilter courseScheduleFilter = JSON.parseObject(message.getString("filter"), new TypeReference<CourseScheduleFilter>() {
                });
                String userId = message.getString("userId");
                log.info("接收到根据批次id: {} 导出学生信息消息，正在下载内容");
                courseScheduleFilter.exportStudentInformationBatchIndex(pageRO, userId);
            }
            // 添加其他类型的处理逻辑

            // 手动确认消息
            channel.basicAck(msg.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.error("处理消息时出现异常: ", e);
            try {
                // 根据需要，拒绝消息并选择是否重新入队
                channel.basicNack(msg.getMessageProperties().getDeliveryTag(), false, false);
            } catch (IOException ioException) {
                log.error("确认消息时出现异常: ", ioException);
            }
        }
    }

    private PlatformMessagePO generateMessage(String userId) {
        PlatformMessagePO platformMessagePO = new PlatformMessagePO();
        Date generateData = new Date();
        platformMessagePO.setCreatedAt(generateData);
        platformMessagePO.setUserId(userId);
        platformMessagePO.setRelatedMessageId(null);
        platformMessagePO.setIsRead(false);
        platformMessagePO.setMessageType(MessageEnum.DOWNLOAD_MSG.getMessage_name());
        int insert1 = platformMessageMapper.insert(platformMessagePO);
        log.info("接收到用户下载消息，正在处理下载内容... " + insert1);
        return platformMessagePO;
    }


    @RabbitListener(
            queuesToDeclare = {
                    @Queue("${spring.rabbitmq.queue6}")
//                    @Queue("${spring.rabbitmq.queue5}"),
            }
    )
    @RabbitHandler
    public void processImportData(String messageContent, Channel channel, Message msg) {
        log.info("收到文件上传消息，正在处理 ...");
        try {
            JSONObject message = JSON.parseObject(messageContent);
            String uploadId = message.getString("uploadId");
            String userId = message.getString("userId");

            String filterClassFullName = message.getString("filterClass");  // 获取类全名

            try {
                Class<?> receivedClass = Class.forName(filterClassFullName);  // 获取从消息中解析出来的 Class 对象
                Class<?> superReceivedClass = receivedClass.getSuperclass();  // 获取 receivedClass 的超类

                if (ManagerFilter.class.equals(superReceivedClass)) {  // 比较两个 Class 对象是否相同
                    ManagerFilter filter = JSON.parseObject(
                            message.getString("filter"),
                            ManagerFilter.class  // 直接使用 ManagerFilter 类型反序列化
                    );

                    // 处理 ManagerFilter 对象
                    log.info("处理继续教育学院教务员 " + userId + " 上传的文件 " + uploadId);
                    UserUploadsPO userUploadsPO = userUploadsService.getBaseMapper().selectOne(new LambdaQueryWrapper<UserUploadsPO>().
                            eq(UserUploadsPO::getId, uploadId));
                    String minioURL = importBucketName + "/" + userUploadsPO.getFileUrl();
                    log.info("上传的文件地址为 " + minioURL);
                    InputStream fileInputStreamFromMinio = minioService.getFileInputStreamFromMinio(minioURL);
                    if (fileInputStreamFromMinio != null) {
                        log.info("成功获取上传文件的文件流，开始进行处理 ");
                    }
                    processExcelFile(fileInputStreamFromMinio, null, userUploadsPO);

                    // 解析完导入 excel 后 开始改变上传消息状态


                } else if (CollegeAdminFilter.class.equals(superReceivedClass)) {
                    CollegeAdminFilter filter = JSON.parseObject(
                            message.getString("filter"),
                            CollegeAdminFilter.class  // 直接使用 CollegeAdminFilter 类型反序列化
                    );

                    CollegeInformationPO userBelongCollege = scnuXueliTools.getUserBelongCollege();
                    // 处理 ManagerFilter 对象
                    log.info("处理二级学院教务员 " + userId + " 上传的文件 " + uploadId);
                    UserUploadsPO userUploadsPO = userUploadsService.getBaseMapper().selectOne(new LambdaQueryWrapper<UserUploadsPO>().
                            eq(UserUploadsPO::getId, uploadId));
                    String minioURL = importBucketName + "/" + userUploadsPO.getFileUrl();
                    log.info("上传的文件地址为 " + minioURL);
                    InputStream fileInputStreamFromMinio = minioService.getFileInputStreamFromMinio(minioURL);
                    if (fileInputStreamFromMinio != null) {
                        log.info("成功获取上传文件的文件流，开始进行处理 ");
                    }
                    processExcelFile(fileInputStreamFromMinio, userBelongCollege.getCollegeName(), userUploadsPO);
                } else {
                    log.error("找不到转换的类型 " + uploadId + " 用户 " + userId + " 类名" + filterClassFullName);
                }
            } catch (ClassNotFoundException e) {
                log.error("Class not found: ", e);
                return;
            }

            // 添加其他类型的处理逻辑

            // 手动确认消息
            channel.basicAck(msg.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.error("处理消息时出现异常: ", e);
            try {
                // 根据需要，拒绝消息并选择是否重新入队
                channel.basicNack(msg.getMessageProperties().getDeliveryTag(), false, false);
            } catch (IOException ioException) {
                log.error("确认消息时出现异常: ", ioException);
            }
        }
    }

    /**
     * 从 Minio 获取 excel 文件的输入流 然后通过 easyExcel 来解析
     *
     * @param fileInputStreamFromMinio
     * @param collegeName
     */
    public void processExcelFile(InputStream fileInputStreamFromMinio, String collegeName, UserUploadsPO userUploadsPO) {
        int headRowNumber = 1;  // 根据你的 Excel 调整这个值

        // 创建一个监听器实例
        CourseScheduleListener courseScheduleListener = new CourseScheduleListener(
                courseScheduleMapper, classInformationService.getBaseMapper(), teacherInformationMapper,
                studentStatusService.getBaseMapper(), courseInformationMapper, userUploadsService.getBaseMapper(), minioService,
                collegeName, userUploadsPO
        );
        if (collegeName == null) {
            // 管理员导入
            courseScheduleListener.manger = true;
        }
        courseScheduleListener.setUpdate(true);

        // 使用ExcelReaderBuilder注册自定义的日期转换器
        ExcelReaderBuilder readerBuilder = EasyExcel.read(
                fileInputStreamFromMinio,  // 使用 InputStream 代替文件名
                CourseScheduleExcelImportVO.class,
                courseScheduleListener
        );
        readerBuilder.registerConverter(new CustomDateConverter());

        // 继续你的读取操作
        readerBuilder.sheet().headRowNumber(headRowNumber).doRead();
    }


}

