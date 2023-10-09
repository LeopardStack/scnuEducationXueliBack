package com.scnujxjy.backendpoint.mqTest;

import com.scnujxjy.backendpoint.util.MessageSender;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
public class RabbitMQTest1 {

    @Value("${spring.rabbitmq.queue1}")
    private String queue1;

    @Value("${spring.rabbitmq.queue2}")
    private String queue2;

    @Autowired(required = false)
    private MessageSender messageSender;

    @Test
    public void test1(){
        messageSender.send();
    }

    @Test
    public void test2(){
//        String context = "hello " + new Date();
//        System.out.println("Sender : " + context + " 测试消息堆积");
//        messageSender.send(queue2, context);
        messageSender.send(queue1, "数据同步");
    }

    @Test
    public void test3(){
        for(int i = 0; i < 10; i++) {
            String context = "hello " + new Date();
            System.out.println("Sender " + i + " : " + context + " 测试消息堆积");
            messageSender.send(queue2, context);
        }
    }

    /**
     * 取消息
     */
    @Test
    @RabbitListener(queuesToDeclare = @Queue("${spring.rabbitmq.queue2}"))
    @RabbitHandler
    public void test4(String msg){
        System.out.println("Receiver  : " + msg);
    }
}
