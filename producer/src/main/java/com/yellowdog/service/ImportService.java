package com.yellowdog.service;

import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBeanBuilder;
import com.yellowdog.dto.CommodityDataDto;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class ImportService {// TODO add generics to this class?
    private final KafkaTemplate<String, CommodityDataDto> kafkaTemplate;

    public ImportService(KafkaTemplate<String, CommodityDataDto> kafkaTemplate){
        this.kafkaTemplate = kafkaTemplate;
    }

    public String importFile(Path path, String topic) throws IOException {
        try (Reader reader = Files.newBufferedReader(path)) {
            try (CSVReader csvReader = new CSVReader(reader)) {

                //TODO this is the recomended way of parsing a list of beans but takes ages as it reads the entire file
                // kafka connect?
                new CsvToBeanBuilder<CommodityDataDto>(csvReader).withType(CommodityDataDto.class)
                        .build()
                        .forEach(data -> kafkaTemplate.send(topic, data));

            } catch (IOException e) {
                // TODO Error handling here
                System.out.println("oops");
                throw new RuntimeException(e);
            }

            return "done";
        }
    }
}
