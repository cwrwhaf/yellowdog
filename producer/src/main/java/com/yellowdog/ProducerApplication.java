package com.yellowdog;

import com.yellowdog.dto.CommodityDataDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.kafka.core.KafkaTemplate;

@SpringBootApplication
public class ProducerApplication implements CommandLineRunner {
    @Autowired
    KafkaTemplate<String, CommodityDataDto> kafkaTemplate;
    @Autowired
    KafkaProperties kafkaProperties;

    public static void main(String[] args) {
        SpringApplication.run(ProducerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
    // tester on startup
        CommodityDataDto commodityDataDto = CommodityDataDto.builder()

                .build();
    //    kafkaTemplate.send("general-task-topic", commodityData);
    }
}