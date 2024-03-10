package com.yellowdog.service;

import com.yellowdog.dto.CommodityDataDto;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.*;

public class KafkaProducerServiceTest {

    @Test
    void testSendCommodityDataDto() {
        KafkaTemplate<String, CommodityDataDto> templateMock = mock(org.springframework.kafka.core.KafkaTemplate.class);
        KafkaProducerService producer = new KafkaProducerService(templateMock);
        producer.send("topic", CommodityDataDto.builder().build());

        verify(templateMock, times(1).description("kafka template should have been called"));

    }

}
