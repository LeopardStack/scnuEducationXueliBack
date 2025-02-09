package com.scnujxjy.backendpoint.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.scnujxjy.backendpoint.model.bo.cdn_file_manage.FileCommuBO;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.util.filter.AbstractFilter;
import com.scnujxjy.backendpoint.util.filter.ManagerFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

@Component
@Slf4j
public class MessageSender {

    private final RabbitTemplate rabbitTemplate;

    @Value("${spring.rabbitmq.queue1}")
    private String queue1;

    @Value("${spring.rabbitmq.queue3}")
    private String queue3;

    @Value("${spring.rabbitmq.queue4}")
    private String queue4;

    @Value("${spring.rabbitmq.queue5}")
    private String queue5;

    @Value("${spring.rabbitmq.queue6}")
    private String queue6;

    @Value("${spring.rabbitmq.cdn_queue1}")
    private String cdn_queue1;

    @Value("${spring.rabbitmq.cdn_queue2}")
    private String cdn_queue2;

//    @Value("${spring.rabbitmq.queue5}")
//    private String queue5;

    @Autowired
    public MessageSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void send() {
        String context = "hello " + new Date();
        System.out.println("Sender : " + context);
        this.rabbitTemplate.convertAndSend(queue1, context);
    }

    public void send(int i) {
        String context = "hello " + i + " "  + new Date();
        System.out.println("Sender" + i + " : " + context);
        this.rabbitTemplate.convertAndSend(queue1, context);
    }

    public boolean send(PageRO<?> pageRO, AbstractFilter filter, String userId){
        try {
            // 创建一个包含数据和类型信息的JSON对象
            JSONObject message = new JSONObject();
            message.put("type", pageRO.getEntity().getClass().getName());
            message.put("data", JSON.toJSONString(pageRO));
            message.put("filter", JSON.toJSONString(filter));
            message.put("userId", userId);

            this.rabbitTemplate.convertAndSend(queue1, message.toJSONString());
            log.info("成功发送消息 ");
            return true;
        } catch (AmqpException e) {
            log.error("Error sending message: " + e.getMessage());
            return false;
        }
    }

    /**
     * 往消息队列中发送导出消息 后台异步处理导出任务
     * @param pageRO
     * @param filter
     * @param userId
     * @return
     */
    public boolean sendExportMsg(PageRO<?> pageRO, AbstractFilter filter, String userId){
        try {
            // 创建一个包含数据和类型信息的JSON对象
            JSONObject message = new JSONObject();
            message.put("type", pageRO.getEntity().getClass().getName());
            message.put("data", JSON.toJSONString(pageRO));
            message.put("filter", JSON.toJSONString(filter));
            message.put("dataType", "普通消息");
            message.put("userId", userId);

            this.rabbitTemplate.convertAndSend(queue4, message.toJSONString());
            log.info("成功发送导出文件处理消息 ");
            return true;
        } catch (AmqpException e) {
            log.error("发送导出文件处理消息失败: " + e.getMessage());
            return false;
        }
    }

    /**
     * 往消息队列中发送导出消息 后台异步处理导出任务
     * @param pageRO
     * @param filter
     * @param userId
     * @return
     */
    public boolean sendExportExamStudents(PageRO<?> pageRO, AbstractFilter filter, String userId){
        try {
            // 创建一个包含数据和类型信息的JSON对象
            JSONObject message = new JSONObject();
            message.put("type", pageRO.getEntity().getClass().getName());
            message.put("data", JSON.toJSONString(pageRO));
            message.put("filter", JSON.toJSONString(filter));
            message.put("dataType", "机考名单");
            message.put("userId", userId);

            this.rabbitTemplate.convertAndSend(queue4, message.toJSONString());
            log.info("成功发送导出文件处理消息 ");
            return true;
        } catch (AmqpException e) {
            log.error("发送导出文件处理消息失败: " + e.getMessage());
            return false;
        }
    }

    /**
     * 往消息队列中发送导出消息 后台异步处理导出任务
     * @param systemMsgType
     * @param filter
     * @param userId
     * @return
     */
    public <T> boolean sendSystemMsg(T filter, String userId, String systemMsgType){
        try {
            // 创建一个包含数据和类型信息的JSON对象
            JSONObject message = new JSONObject();
            message.put("type", filter.getClass().getName());
            message.put("systemMsgType", JSON.toJSONString(systemMsgType));
            message.put("data", JSON.toJSONString(filter));
            message.put("userId", userId);

            this.rabbitTemplate.convertAndSend(queue5, message.toJSONString());
            log.info("成功发送系统消息处理 ");
            return true;
        } catch (AmqpException e) {
            log.error("发送系统消息处理失败: " + e.getMessage());
            return false;
        }
    }

    public boolean sendExportStudentSituation(Long sectionId,String loginId,Integer exportType){
        try {
            // 创建一个包含数据和类型信息的JSON对象
            JSONObject message = new JSONObject();
            message.put("type", sectionId.getClass().getName());
            message.put("data", sectionId);
            message.put("dataType", "考勤表信息");
            message.put("loginId", loginId);
            //type为1单节点，2整门课
            message.put("exportType",exportType);
            this.rabbitTemplate.convertAndSend(queue4, message.toJSONString());
            log.info(loginId+"成功发送导出文件处理消息 ");
            return true;
        } catch (AmqpException e) {
            log.error(loginId+"发送导出文件处理消息失败: " + e.getMessage());
            return false;
        }
    }

    public void send(String queue, String msg) {
        System.out.println(msg);
        this.rabbitTemplate.convertAndSend(queue, msg);
    }

    /**
     * 处理导入消息
     * @param uploadId
     * @param filter
     * @param userId
     * @return
     */
    public boolean sendImportMsg(long uploadId, AbstractFilter filter, String userId) {
        try {
            // 创建一个包含数据和类型信息的JSON对象
            JSONObject message = new JSONObject();
            message.put("uploadId", uploadId);
            message.put("filter", JSON.toJSONString(filter));
            message.put("filterClass", filter.getClass().getName());  // 添加类型信息
            message.put("userId", userId);

            this.rabbitTemplate.convertAndSend(queue6, message.toJSONString());
            log.info("成功发送上传文件处理消息 ");
            return true;
        } catch (AmqpException e) {
            log.error("Error sending message: " + e.getMessage());
            return false;
        }
    }


    /**
     * CDN 操作方需要发送 操作消息
     * @param fileCommuBO
     * @return
     */
    public boolean send(FileCommuBO fileCommuBO){
        try {
            // 创建一个包含数据和类型信息的JSON对象
            JSONObject message = new JSONObject();
            message.put("protocolInfo", JSON.toJSONString(fileCommuBO));
            message.put("ipAddr", JSON.toJSONString(getLocalIpAddress()));

            this.rabbitTemplate.convertAndSend(cdn_queue1, message.toJSONString());
            log.info("成功发送操作消息 ");
            return true;
        } catch (AmqpException e) {
            log.error("Error sending message: " + e.getMessage());
            return false;
        }
    }

    /**
     * CDN 服务方发送响应消息 操作方负责监听即可
     * @param fileCommuBO
     * @return
     */
    public boolean sendAnc(FileCommuBO fileCommuBO){
        try {
            // 创建一个包含数据和类型信息的JSON对象
            JSONObject message = new JSONObject();
            message.put("protocolInfo", JSON.toJSONString(fileCommuBO));
            message.put("ipAddr", JSON.toJSONString(getLocalIpAddress()));

            this.rabbitTemplate.convertAndSend(cdn_queue2, message.toJSONString());
            log.info("成功发送应答消息 ");
            return true;
        } catch (AmqpException e) {
            log.error("Error sending message: " + e.getMessage());
            return false;
        }
    }


    private static String getLocalIpAddress() {
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            return inetAddress.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return "Unable to find IP address";
        }
    }

}

