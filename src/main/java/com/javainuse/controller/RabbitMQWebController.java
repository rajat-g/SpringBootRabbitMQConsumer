package com.javainuse.controller;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.javainuse.model.Employee;
import com.javainuse.service.RabbitMQSender;

import java.util.*;

@RestController
@RequestMapping(value = "/javainuse-rabbitmq")
public class RabbitMQWebController {

    @Autowired
    RabbitListenerEndpointRegistry rabbitListenerEndpointRegistry;

    @Value("${javainuse.rabbitmq.queue}")
    private String queueName;

    @Autowired
    RabbitMQSender rabbitMQSender;

    @GetMapping(value = "/consumer")
    public void consumer() {
        Set<String> queue = new HashSet<>(Arrays.asList("Test queue1", "Test queue2", "Test queue3"));
//        for (int i = 0; i < 10; i++) {
            addQueueToListener("Test container1", queue);
//        }
    }

    @GetMapping(value = "/producer")
    public void producer(@RequestParam(name = "count", required = false, defaultValue = "20000") int count) {
        Set<String> queue = new HashSet<>(Arrays.asList("Test queue1", "Test queue2", "Test queue3"));
        for (int i = 0; i < count; i++) {
            Employee emp = new Employee();
            emp.setEmpId(String.valueOf(i));
            emp.setEmpName(RandomStringUtils.randomAlphabetic(50));
            rabbitMQSender.send(emp, queue);
        }
        System.out.println("Message sent to the RabbitMQ JavaInUse Successfully");
    }

    public void addQueueToListener(String containerId, String queueName) {
        SimpleMessageListenerContainer listener = (SimpleMessageListenerContainer)
                rabbitListenerEndpointRegistry.getListenerContainer(containerId);
        if (Objects.nonNull(listener)) {
            String[] queueNames = listener.getQueueNames();
            System.out.println("Queue Names: " + Arrays.toString(queueNames) + " going to Add: " + queueName);
            System.out.println("search result for queue name: " + queueName + "     -->" + Arrays.binarySearch(queueNames, queueName));
            //do not add same queue name to listener
            if (Arrays.binarySearch(queueNames, queueName) < 0) {
                System.out.println("Adding queue: " + queueName);
                listener.addQueueNames(queueName);
                listener.setExclusive(true);
                System.out.println("added queue: " + queueName);
            }
        }
    }

    public void addQueueToListener(String containerId, Set<String> queueName) {
        for (String queue : queueName) {
            addQueueToListener(containerId, queue);
        }
    }
}