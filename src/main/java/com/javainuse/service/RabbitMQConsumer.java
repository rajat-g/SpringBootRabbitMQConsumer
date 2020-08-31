package com.javainuse.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javainuse.model.Employee;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class RabbitMQConsumer {

    //	@RabbitListener(id = "Test container1", queues = "#{rabbitMQConsumer.getEntriesQueueName()}", containerFactory = "consumerSimpleContainerFactory",
//			exclusive = true)
    public void receiveMessage(Message message) throws InterruptedException, IOException {
        Thread.sleep(30000);
//		ObjectMapper jsonObjectMapper = new ObjectMapper();
//		Employee employee = jsonObjectMapper.readValue(Arrays.toString(message.getBody()), Employee.class);
        String queueName = (String) message.getMessageProperties().getHeaders().get("queueName");
        System.out.println("RabbitMQConsumer.receiveMessage QueueName:" + queueName);
//		System.out.println("Recieved Message From RabbitMQ: " + employee + "      QueueName:"+ queueName);
    }

    @RabbitListener(id = "Test container1", queues = "#{rabbitMQConsumer.getEntriesQueueName()}", containerFactory = "consumerBatchContainerFactory")
    public void receiveMessageBatch(List<Message> messages) throws InterruptedException, IOException {
        for (Message message : messages) {
            Thread.sleep(30000);
//		ObjectMapper jsonObjectMapper = new ObjectMapper();
//		Employee employee = jsonObjectMapper.readValue(Arrays.toString(message.getBody()), Employee.class);
            String queueName = (String) message.getMessageProperties().getHeaders().get("queueName");
            System.out.println("RabbitMQConsumer.receiveMessage QueueName:" + queueName);
//		System.out.println("Recieved Message From RabbitMQ: " + employee + "      QueueName:"+ queueName);
        }
    }

    public List<String> getEntriesQueueName() {
    	return Collections.emptyList();
//        return Arrays.asList("Test queue1", "Test queue2", "Test queue3");
    }
}