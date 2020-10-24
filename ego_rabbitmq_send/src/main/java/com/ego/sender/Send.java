package com.ego.sender;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Send {

    @Autowired
    private AmqpTemplate amqpTemplate;

    public void send(String queue,Object obj){
        amqpTemplate.convertAndSend(queue,obj);
    }
}
