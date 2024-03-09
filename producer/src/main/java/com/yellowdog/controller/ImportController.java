package com.yellowdog.controller;

import com.yellowdog.service.ImportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Path;

// TODO chose not to do this on startup in case there was an error with the file
// should be recoverable not stop the application from starting up

@RestController
public class ImportController {

    private final ImportService importService;

    public ImportController(ImportService importService) {
        this.importService = importService;
    }

    @GetMapping(value = "/import", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public String importKaggle(@RequestParam Path path, @RequestParam String topic) throws IOException {
        System.out.println(System.getProperty("user.dir"));
        return importService.importFile(path, topic);
    }
}
