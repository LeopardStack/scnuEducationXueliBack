package com.scnujxjy.backendpoint.util;

import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MessageReceiver {
    @Value("${spring.rabbitmq.queue1}")
    private String queue1;

    @RabbitListener(queuesToDeclare = @Queue("${spring.rabbitmq.queue1}"))
    @RabbitHandler
    public void process(String hello) {
        System.out.println("Receiver  : " + hello);
    }
}
