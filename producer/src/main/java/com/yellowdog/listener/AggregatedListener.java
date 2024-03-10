package com.yellowdog.listener;

import com.yellowdog.controller.ImportController;
import com.yellowdog.dto.Aggregation;
import com.yellowdog.dto.CommodityDataDto;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = "#{'${yellowdog.aggregated.topic}'}")
public class AggregatedListener {

    private static final Logger log = LoggerFactory.getLogger(AggregatedListener.class);

    //String
    //errorHandler
    //Set an KafkaListenerErrorHandler bean name to invoke if the listener method throws an exception.

    @KafkaHandler(isDefault = true)
    public void handleMessage(@Payload Aggregation record, @Header(KafkaHeaders.RECEIVED_KEY) String key) {

      log.info("Country: [{}] Aggregated Trade: [{}]", record.getCountryOrArea(), record.getAggregatedTradeUsd() );

    }



}
