package com.yellowdog.controller;

import com.yellowdog.ProducerApplication;
import com.yellowdog.error.FileNotFoundException;
import com.yellowdog.service.ImportService;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.IOException;
import java.nio.file.Path;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(classes = ProducerApplication.class)
@AutoConfigureMockMvc
public class ImportControllerTest {

    public static final Path NOT_FOUND_PATH = Path.of("notFoundFile.csv");
    public static final String TOPIC = "topic";
    @Mock
    private ImportService importService;

    @Autowired
    private MockMvc mockMvc;


    @Test
    public void onIOException_EndpointReturns404() throws Exception {
      mockMvc.perform(MockMvcRequestBuilders.get("/import?path=notFoundFile.csv&topic=topic"))
               // .andDo(print())
                .andExpect(status().isNotFound());
    }
}
