package com.yellowdog.listener;

import com.yellowdog.dto.Aggregation;
import com.yellowdog.dto.CommodityDataDto;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
//

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.stereotype.Component;

import static org.apache.kafka.common.serialization.Serdes.Long;
import static org.apache.kafka.common.serialization.Serdes.String;
import static org.apache.kafka.streams.kstream.Grouped.with;

@Component
@EnableKafka
@EnableKafkaStreams
public class CommodityDataStreamListener {

    @Value("${yellowdog.raw.topic}")
    private String inTopic;

    @Value("${yellowdog.aggregated.topic}")
    private String outTopic;

    private final Serde<CommodityDataDto> commodityDataJsonSerde;

    public CommodityDataStreamListener(Serde<CommodityDataDto> commodityDataJsonSerde) {
        this.commodityDataJsonSerde = commodityDataJsonSerde;
    }

    @Autowired
    public void kStream(StreamsBuilder streamsBuilder) {

        KStream<String, CommodityDataDto> commodityDataKStream = streamsBuilder.stream(inTopic, Consumed.with(Serdes.String(), commodityDataJsonSerde));

        KGroupedStream<String, Long> dataByCountry = commodityDataKStream
                .map((key, data) -> new KeyValue<>(data.getCountryOrArea() + " : " + data.getFlow(), data.getTradeUsd()))
                .groupByKey(with(String(), Long()));

        KTable<String, Long> dataAggregated = dataByCountry.aggregate(() -> 0l,
                (key, value, aggregate) -> value + aggregate,
                Materialized.with(String(), Long()));


        dataAggregated
                .mapValues(((key, value) -> new Aggregation(key, value)))
                .toStream()
                .to(outTopic, Produced.with(
                        Serdes.String(),
                        Serdes.serdeFrom(new JsonSerializer<Aggregation>(), new JsonDeserializer<Aggregation>()))
                );

    }

}
