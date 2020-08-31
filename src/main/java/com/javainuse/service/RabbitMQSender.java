package com.javainuse.service;

import com.javainuse.model.Employee;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Set;

@Service
public class RabbitMQSender {

    @Autowired
    private AmqpTemplate rabbitTemplate;

    @Value("${javainuse.rabbitmq.exchange}")
    private String exchange;

    @Autowired
    private AmqpAdmin admin;

    public void send(Employee employee, String queueName) {
        Queue queue = new Queue(queueName, true, false, false);
        Binding binding = new Binding(queueName, Binding.DestinationType.QUEUE, exchange, queueName, null);
        admin.declareQueue(queue);
        admin.declareBinding(binding);
        rabbitTemplate.convertAndSend(exchange, queueName, employee, message -> {
            message.getMessageProperties().getHeaders().put("queueName", queueName);
            return message;
        });
//        System.out.println("Send msg = " + company);
    }

    public void send(Employee employee, Set<String> queueName) {
        for(String queue: queueName) {
            send(employee, queue);
        }
    }
}