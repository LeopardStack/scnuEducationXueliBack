package com.scnujxjy.backendpoint.util;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class MessageSender {

    private final RabbitTemplate rabbitTemplate;

    @Value("${spring.rabbitmq.queue1}")
    private String queue1;

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

    public void send(String queue, String msg) {
        System.out.println("Sender " + queue + " : " + msg);
        this.rabbitTemplate.convertAndSend(queue, msg);
    }
}

