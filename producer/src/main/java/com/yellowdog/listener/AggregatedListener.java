package com.yellowdog.listener;

import com.yellowdog.dto.Aggregation;
import com.yellowdog.dto.CommodityDataDto;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = "#{'${yellowdog.aggregated.topic}'}")
public class AggregatedListener {
//    @KafkaListener(topics = "general-task-topic")
//    public void consume(String taskStatus) {
//        System.out.println("recieveddd " + taskStatus);
//    }


    //String
    //errorHandler
    //Set an KafkaListenerErrorHandler bean name to invoke if the listener method throws an exception.

//    @KafkaHandler(isDefault = true)
//    public void handleMessage(Aggregation message) {
//
//        System.out.println(" recieved:   ");
//        System.out.println("              " + message);
//    }

    @KafkaHandler(isDefault = true)
    public void handleMessage(Aggregation record) {

        System.out.println(" recieved:   ");
        System.out.println("              " + record);
    }



}
