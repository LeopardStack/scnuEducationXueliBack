package com.scnujxjy.backendpoint.util;

import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.scnujxjy.backendpoint.constant.enums.MessageEnum;
import com.scnujxjy.backendpoint.constant.enums.RoleEnum;
import com.scnujxjy.backendpoint.dao.entity.college.CollegeInformationPO;
import com.scnujxjy.backendpoint.dao.entity.platform_message.PlatformMessagePO;
import com.scnujxjy.backendpoint.dao.entity.platform_message.UserUploadsPO;
import com.scnujxjy.backendpoint.dao.mapper.basic.PlatformUserMapper;
import com.scnujxjy.backendpoint.dao.mapper.core_data.TeacherInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.platform_message.PlatformMessageMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseScheduleMapper;
import com.scnujxjy.backendpoint.model.bo.cdn_file_manage.FileCommuBO;
import com.scnujxjy.backendpoint.model.bo.teaching_process.CourseScheduleStudentExcelBO;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.admission_information.AdmissionInformationRO;
import com.scnujxjy.backendpoint.model.ro.core_data.PaymentInfoFilterRO;
import com.scnujxjy.backendpoint.model.ro.exam.BatchSetTeachersInfoRO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.ClassInformationFilterRO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.StudentStatusFilterRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.ScoreInformationFilterRO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.CourseScheduleExcelImportVO;
import com.scnujxjy.backendpoint.service.InterBase.OldDataSynchronize;
import com.scnujxjy.backendpoint.service.admission_information.AdmissionInformationService;
import com.scnujxjy.backendpoint.service.basic.PlatformUserService;
import com.scnujxjy.backendpoint.service.core_data.PaymentInfoService;
import com.scnujxjy.backendpoint.service.exam.CourseExamInfoService;
import com.scnujxjy.backendpoint.service.minio.MinioService;
import com.scnujxjy.backendpoint.service.platform_message.UserUploadsService;
import com.scnujxjy.backendpoint.service.registration_record_card.ClassInformationService;
import com.scnujxjy.backendpoint.service.registration_record_card.StudentStatusService;
import com.scnujxjy.backendpoint.service.video_stream.SingleLivingService;
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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class MessageReceiver {
    @Value("${spring.rabbitmq.queue1}")
    private String queue1;

    @Value("${minio.importBucketName}")
    private String importBucketName;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

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
    private CourseExamInfoService courseExamInfoService;

    @Resource
    private AdmissionInformationService admissionInformationService ;

    @Resource
    private PlatformMessageMapper platformMessageMapper;

    @Resource
    private PlatformUserMapper platformUserMapper;

    @Resource
    private CourseInformationMapper courseInformationMapper;

    @Resource
    private TeacherInformationMapper teacherInformationMapper;

    @Resource
    private CourseScheduleMapper courseScheduleMapper;

    @Resource
    private PlatformUserService platformUserService;

    @Resource
    private SingleLivingService singleLivingService;

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

                PlatformMessagePO platformMessagePO = scnuXueliTools.generateMessage(userId);
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
                log.info("拿到缴费数据筛选条件 " + pageRO);
                PlatformMessagePO platformMessagePO = scnuXueliTools.generateMessage(userId);
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
            } else if ("com.scnujxjy.backendpoint.model.ro.exam.BatchSetTeachersInfoRO".equals(type)) {
                PageRO<BatchSetTeachersInfoRO> pageRO = JSON.parseObject(message.getString("data"),
                        new TypeReference<PageRO<BatchSetTeachersInfoRO>>() {
                        });
                String loginId = message.getString("userId");
                String dataType = message.getString("dataType");
                List<String> roleList = StpUtil.getRoleList(loginId);
                if (roleList.contains(RoleEnum.XUELIJIAOYUBU_ADMIN.getRoleName())) {
                    // 学历教育部管理员
                    AbstractFilter managerFilter = JSON.parseObject(message.getString("filter"), new TypeReference<ManagerFilter>() {
                    });

                    log.info("接收到批量导出考试信息消息，开始准备数据 ");
                    if(dataType.equals("机考名单")){
                        managerFilter.exportExamStudentsInfo(pageRO.getEntity(), loginId);
                    }else{
                        managerFilter.exportExamTeachersInfo(pageRO.getEntity(), loginId);
                    }

                } else if (roleList.contains(RoleEnum.SECOND_COLLEGE_ADMIN.getRoleName())) {
                    // 二级学院管理员
                    AbstractFilter collegeAdminFilter = JSON.parseObject(message.getString("filter"), new TypeReference<CollegeAdminFilter>() {
                    });
                    log.info("接收到批量导出考试信息消息，开始准备数据 ");
                    if(dataType.equals("机考名单")){
                        collegeAdminFilter.exportExamStudentsInfo(pageRO.getEntity(), loginId);
                    }else{
                        collegeAdminFilter.exportExamTeachersInfo(pageRO.getEntity(), loginId);
                    }
                }

            }else if ("com.scnujxjy.backendpoint.model.ro.admission_information.AdmissionInformationRO".equals(type)) {
                // 下载新生录取数据，获取筛选参数
                PageRO<AdmissionInformationRO> pageRO = JSON.parseObject(message.getString("data"),
                        new TypeReference<PageRO<AdmissionInformationRO>>() {
                        });
                AbstractFilter filter = JSON.parseObject(message.getString("filter"), new TypeReference<ManagerFilter>() {
                });
                String userId = message.getString("userId");
                log.info("拿到新生数据导出筛选条件 ");
                PlatformMessagePO platformMessagePO = scnuXueliTools.generateMessage(userId);
                // 处理pageRO
                admissionInformationService.generateBatchAdmissionData(pageRO, filter, userId, platformMessagePO);

            }else if("java.lang.Long".equals(type)){
                Long sectionId = message.getLong("data");
                singleLivingService.exportStudentSituation(sectionId, null);

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


    @RabbitListener(
            queuesToDeclare = {
                    @Queue("${spring.rabbitmq.queue5}")
            }
    )
    @RabbitHandler
    public void processSystemMsg(String messageContent, Channel channel, Message msg) {
        log.info("收到系统消息，正在处理 ...");
        try {
            JSONObject message = JSON.parseObject(messageContent);
            String systemMsgType = message.getString("systemMsgType");
            String type = message.getString("type");

            if ("com.scnujxjy.backendpoint.model.ro.exam.BatchSetTeachersInfoRO".equals(type)) {
                BatchSetTeachersInfoRO batchSetTeachersInfoRO = JSON.parseObject(message.getString("data"),
                        new TypeReference<BatchSetTeachersInfoRO>() {
                        });

                String loginId = message.getString("userId");
                log.info("拿到需要批量设置命题人和阅卷人的筛选参数 " + batchSetTeachersInfoRO);
                // 处理pageRO
                // 单独处理二级学院
                List<String> roleList = StpUtil.getRoleList(loginId);
                if (roleList.contains(RoleEnum.SECOND_COLLEGE_ADMIN.getRoleName())) {
                    CollegeInformationPO userBelongCollegeByLoginId = scnuXueliTools.getUserBelongCollegeByLoginId(loginId);
                    batchSetTeachersInfoRO.setCollege(userBelongCollegeByLoginId.getCollegeName());
                }

                boolean b = courseExamInfoService.batchSetTeachers(batchSetTeachersInfoRO);
            }

            // 手动确认消息
            channel.basicAck(msg.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.error("处理系统消息时出现异常: ", e);
            try {
                /**
                 * 根据需要，拒绝消息并选择是否重新入队
                 * deliveryTag: 这是一个由服务器分配的标记，用于标识通道上接收的消息。
                 * multiple: 这个布尔值指定是否一次拒绝多条消息。如果设置为 true，服务器会拒绝所有消息，直到给定的 deliveryTag。
                 * requeue: 这个布尔值指定消息是否应该被重新放入队列。如果为 true，消息将被重新入队。
                 */
                channel.basicNack(msg.getMessageProperties().getDeliveryTag(), false, false);
            } catch (IOException ioException) {
                log.error("确认系统消息时出现异常: ", ioException);
            }
        }
    }


    /**
     * 处理 CDN 的响应消息
     * @param msg
     * @param channel
     * @param message
     */
    @RabbitListener(queuesToDeclare = @Queue("${spring.rabbitmq.cdn_queue2}"), ackMode = "MANUAL")
    public void processCDNMsg(String msg, Channel channel, Message message) {
        try {
            log.info("接收到消息 " + msg);
            // 使用 Jackson ObjectMapper 解析 JSON
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(msg);

            // 从 JSON 中提取并转换 protocolInfo
            String protocolInfoJson = jsonNode.get("protocolInfo").asText();
            FileCommuBO fileCommuBO = mapper.readValue(protocolInfoJson, FileCommuBO.class);
            String ipAddr = jsonNode.get("ipAddr").asText();

            log.info("收到 IP 地址为 " + ipAddr + " 的CDN 服务的反馈信息 " + fileCommuBO);
            redisTemplate.opsForValue().set("FileCommuBO:" + fileCommuBO.getSerialNumber(), fileCommuBO, 1, TimeUnit.MINUTES);

            // 手动确认消息
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }catch (JsonProcessingException e) {
            log.error("JSON 解析错误: ", e);
            // ... 错误处理 ...
        }  catch (Exception e) {
            log.error("处理消息时出现异常: ", e);
            try {
                // 根据需要，拒绝消息并选择是否重新入队
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
            } catch (IOException ioException) {
                log.error("确认消息时出现异常: ", ioException);
            }
        }
    }

}

