package com.yellowdog.service;

import com.yellowdog.dto.CommodityDataDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, CommodityDataDto> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, CommodityDataDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(String topic, CommodityDataDto data) {
        kafkaTemplate.send(topic, data);
    }
}