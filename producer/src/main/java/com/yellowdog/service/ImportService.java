package com.yellowdog.service;

import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBeanBuilder;
import com.yellowdog.controller.ImportController;
import com.yellowdog.dto.CommodityDataDto;
import com.yellowdog.error.FileNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class ImportService {// TODO add generics to this class?
    private static final Logger log = LoggerFactory.getLogger(ImportController.class);

    private final KafkaProducerService kafkaProducerService;

    public ImportService(KafkaProducerService kafkaProducerService) {
        this.kafkaProducerService = kafkaProducerService;
    }

    public String importFile(Path path, String topic) throws IOException {
        try (Reader reader = Files.newBufferedReader(path)) {
            try (CSVReader csvReader = new CSVReader(reader)) {

                //TODO this is the recomended way of parsing a list of beans but takes ages as it reads the entire file
                // kafka connect?
                new CsvToBeanBuilder<CommodityDataDto>(csvReader).withType(CommodityDataDto.class)
                        .build()
                        .forEach(data -> kafkaProducerService.send(topic, data));

                return "done";
            }
        } catch (IOException e) {
            log.error("File {} not found ", path, e);
            throw new FileNotFoundException(path.toString());
        }
    }
}
