//package com.scnujxjy.backendpoint.util;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.alibaba.fastjson.TypeReference;
//import com.scnujxjy.backendpoint.constant.enums.MessageEnum;
//import com.scnujxjy.backendpoint.dao.entity.platform_message.PlatformMessagePO;
//import com.scnujxjy.backendpoint.dao.mapper.platform_message.PlatformMessageMapper;
//import com.scnujxjy.backendpoint.model.ro.PageRO;
//import com.scnujxjy.backendpoint.model.ro.registration_record_card.ClassInformationFilterRO;
//import com.scnujxjy.backendpoint.model.ro.registration_record_card.StudentStatusFilterRO;
//import com.scnujxjy.backendpoint.model.ro.teaching_process.ScoreInformationFilterRO;
//import com.scnujxjy.backendpoint.service.InterBase.OldDataSynchronize;
//import com.scnujxjy.backendpoint.service.registration_record_card.ClassInformationService;
//import com.scnujxjy.backendpoint.service.registration_record_card.StudentStatusService;
//import com.scnujxjy.backendpoint.util.filter.AbstractFilter;
//import com.scnujxjy.backendpoint.util.filter.ManagerFilter;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.amqp.rabbit.annotation.Queue;
//import org.springframework.amqp.rabbit.annotation.RabbitHandler;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//import org.springframework.stereotype.Service;
//
//import javax.annotation.Resource;
//import java.text.SimpleDateFormat;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.ArrayList;
//import java.util.Date;
//
//@Component
//@Slf4j
//public class MessageReceiver {
//    @Value("${spring.rabbitmq.queue1}")
//    private String queue1;
//
//    @Resource
//    private StudentStatusService studentStatusService;
//
//    @Resource
//    private ClassInformationService classInformationService;
//
//    @Resource
//    private OldDataSynchronize oldDataSynchronize;
//
//    @Resource
//    private PlatformMessageMapper platformMessageMapper;
//
//    @RabbitListener(queuesToDeclare = @Queue("${spring.rabbitmq.queue1}"))
//    @RabbitHandler
//    public void process(String rsg) {
//        log.info("接收到消息 " + rsg);
//        if("数据同步".equals(rsg)){
////            oldDataSynchronize.synchronizeAllData();
//            log.info("接收到消息");
//        }else{
//            log.info("其他消息，不予处理 " + rsg);
//        }
//    }
//
//    @RabbitListener(
////            queuesToDeclare = {
////                    @Queue("${spring.rabbitmq.queue3}"),
////                    @Queue("${spring.rabbitmq.queue4}")
////            }
//    )
//    @RabbitHandler
//    public void processExportData(String messageContent) {
//        log.info("收到消息，正在处理 ...");
//        try {
//            JSONObject message = JSON.parseObject(messageContent);
//            String type = message.getString("type");
//
//            if ("com.scnujxjy.backendpoint.model.ro.registration_record_card.StudentStatusFilterRO".equals(type)) {
//                PageRO<StudentStatusFilterRO> pageRO = JSON.parseObject(message.getString("data"),
//                        new TypeReference<PageRO<StudentStatusFilterRO>>() {});
//                AbstractFilter filter = JSON.parseObject(message.getString("filter"), new TypeReference<ManagerFilter>() {});
//                String userId = message.getString("userId");
//                log.info("拿到学籍数据筛选条件 " + pageRO.getEntity().toString());
//                // 处理pageRO
//                studentStatusService.generateBatchStudentStatusData(pageRO, filter, userId);
//            }else if("com.scnujxjy.backendpoint.model.ro.teaching_process.ScoreInformationFilterRO".equals(type)){
//                // 下载成绩数据，获取成绩筛选参数
//                PageRO<ScoreInformationFilterRO> pageRO = JSON.parseObject(message.getString("data"),
//                        new TypeReference<PageRO<ScoreInformationFilterRO>>() {});
//                AbstractFilter filter = JSON.parseObject(message.getString("filter"), new TypeReference<ManagerFilter>() {});
//                String userId = message.getString("userId");
//                log.info("拿到成绩数据筛选条件 " + pageRO.getEntity().toString());
//                // 处理pageRO
//                studentStatusService.generateBatchScoreInformationData(pageRO, filter, userId);
//
//            }else if("com.scnujxjy.backendpoint.model.ro.registration_record_card.ClassInformationFilterRO".equals(type)){
//            // 下载成绩数据，获取成绩筛选参数
//            PageRO<ClassInformationFilterRO> pageRO = JSON.parseObject(message.getString("data"),
//                    new TypeReference<PageRO<ClassInformationFilterRO>>() {});
//            AbstractFilter filter = JSON.parseObject(message.getString("filter"), new TypeReference<ManagerFilter>() {});
//            String userId = message.getString("userId");
//            log.info("拿到班级数据筛选条件 ");
//
//            PlatformMessagePO platformMessagePO = new PlatformMessagePO();
//            Date generateData = new Date();
//            platformMessagePO.setCreatedAt(generateData);
//            platformMessagePO.setUserId(userId);
//            platformMessagePO.setRelatedMessageId(null);
//            platformMessagePO.setIsRead(false);
//            platformMessagePO.setMessageType(MessageEnum.DOWNLOAD_MSG.getMessage_name());
//            int insert1 = platformMessageMapper.insert(platformMessagePO);
//            log.info("接收到用户下载消息，正在处理下载内容... "+ insert1);
//            // 处理pageRO
//            classInformationService.generateBatchClassInformationData(pageRO, filter, userId, platformMessagePO);
//
//        }
//            // 添加其他类型的处理逻辑
//
//        } catch (Exception e) {
//            log.error("Error processing message: " + messageContent, e);
//        }
//    }
//
//}

