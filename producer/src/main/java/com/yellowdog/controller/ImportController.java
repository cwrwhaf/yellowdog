package com.yellowdog.controller;


import com.yellowdog.service.ImportService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Path;

// TODO proper error handler here

@RestController
public class ImportController {

    private static final Logger log = LoggerFactory.getLogger(ImportController.class);

    private final ImportService importService;

    public ImportController(ImportService importService) {
        this.importService = importService;
    }

    @GetMapping(value = "/import", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public String importKaggle(@RequestParam Path path, @RequestParam String topic) throws IOException {
        log.info("Ingesting {}", path);
        return importService.importFile(path, topic);
    }

}
