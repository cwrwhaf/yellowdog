package com.yellowdog.listener;

import com.yellowdog.dto.Aggregation;
import com.yellowdog.dto.CommodityDataDto;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CommodityDataStreamListenerTest {

    @Value("${yellowdog.raw.topic}")
    private String inTopic;

    @Value("${yellowdog.aggregated.topic}")
    private String outTopic;

    @Autowired
    private CommodityDataStreamListener commodityDataStreamListener;

    @Autowired
    private JsonSerializer<CommodityDataDto> commodityDataDtoJsonSerializer;

    @Autowired
    private JsonDeserializer<Aggregation> aggregationJsonDeserializer;

    @Test
    void givenInputMessages_whenProcessed_thenAggregationIsProduced() {
        StreamsBuilder streamsBuilder = new StreamsBuilder();
        commodityDataStreamListener.kStream(streamsBuilder);
        Topology topology = streamsBuilder.build();

        try (TopologyTestDriver topologyTestDriver = new TopologyTestDriver(topology, new Properties())) {

            TestInputTopic<String, CommodityDataDto> inputTopic = topologyTestDriver
                    .createInputTopic(inTopic, new StringSerializer(), commodityDataDtoJsonSerializer);

            TestOutputTopic<String, Aggregation> outputTopic = topologyTestDriver
                    .createOutputTopic(outTopic, new StringDeserializer(), aggregationJsonDeserializer);

            inputTopic.pipeInput(CommodityDataDto.builder().countryOrArea("UK").flow("Import").tradeUsd(1l).build());
            inputTopic.pipeInput(CommodityDataDto.builder().countryOrArea("UK").flow("Import").tradeUsd(1l).build());

            assertThat(outputTopic.readKeyValuesToList())
                    .contains(KeyValue.pair("UK : Import",Aggregation.builder().countryOrArea("UK : Import").aggregatedTradeUsd(2l).build()));

        }
    }
}
