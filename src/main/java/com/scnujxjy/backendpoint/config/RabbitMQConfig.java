package com.scnujxjy.backendpoint.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Slf4j
public class RabbitMQConfig {
    private final AmqpAdmin amqpAdmin;

    @Value("${spring.rabbitmq.queue1}")
    private String queue1;

    @Value("${spring.rabbitmq.queue2}")
    private String queue2;

    @Autowired
    public RabbitMQConfig(AmqpAdmin amqpAdmin) {
        this.amqpAdmin = amqpAdmin;
    }

    @PostConstruct
    public void declareQueue() {
        Queue queue = new Queue(queue1);
        log.info("成功初始化队列 " + queue1);
        amqpAdmin.declareQueue(queue);
    }

    @PostConstruct
    public void declareQueue2() {
        Queue queue = new Queue(queue2);
        log.info("成功初始化队列 " + queue2);
        amqpAdmin.declareQueue(queue);
    }
}

