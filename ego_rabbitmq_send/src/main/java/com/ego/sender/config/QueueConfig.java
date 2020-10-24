package com.ego.sender.config;


import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueueConfig {

    @Value("${ego.rabbitmq.content.queueName}")
    private String queueName;

    /**
     * Content的queue
     * @return
     */
    @Bean
    public Queue queue(){
        return new Queue(queueName);
    }
    @Bean
    public DirectExchange directExchange(){
        return new DirectExchange("amq.direct");
    }
    @Bean
    public Binding binding(Queue queue,DirectExchange directExchange){
        return BindingBuilder.bind(queue).to(directExchange).withQueueName();
    }

    /**
     * ItemInsertQueue 队列
     * @return
     */
    @Value("${ego.rabbitmq.item.insertName}")
    private String insertName;
    @Bean
    public Queue queueItemInsert(){
        return new Queue(insertName);
    }

    @Bean
    public Binding binding2(Queue queueItemInsert,DirectExchange directExchange){
        return BindingBuilder.bind(queueItemInsert).to(directExchange).withQueueName();
    }
    /**
     * ItemDeleteQueue 队列
     * @return
     */
    @Value("${ego.rabbitmq.item.updateName}")
    private String deleteName;
    @Bean
    public Queue queueItemDelete(){
        return new Queue(deleteName);
    }

    @Bean
    public Binding binding3(Queue queueItemDelete,DirectExchange directExchange){
        return BindingBuilder.bind(queueItemDelete).to(directExchange).withQueueName();
    }





}
