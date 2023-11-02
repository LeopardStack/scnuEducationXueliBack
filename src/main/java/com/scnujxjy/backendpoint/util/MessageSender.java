package com.scnujxjy.backendpoint.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.util.filter.AbstractFilter;
import com.scnujxjy.backendpoint.util.filter.ManagerFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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

    @Value("${spring.rabbitmq.queue6}")
    private String queue6;

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
            message.put("userId", userId);

            this.rabbitTemplate.convertAndSend(queue4, message.toJSONString());
            log.info("成功发送导出文件处理消息 ");
            return true;
        } catch (AmqpException e) {
            log.error("Error sending message: " + e.getMessage());
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

}

