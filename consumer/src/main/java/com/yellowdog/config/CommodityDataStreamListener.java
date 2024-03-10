package com.yellowdog.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yellowdog.dto.Aggregation;
import com.yellowdog.dto.CommodityDataDto;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
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


//    @Bean(name = DEFAULT_STREAMS_CONFIG_BEAN_NAME)
//    public KafkaStreamsConfiguration kafkaStreamsConfig() {
//        var props = new HashMap<String, Object>();
//        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "kafka-stream-item");
//        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
//        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG,
//                Serdes.String().getClass().getName());
//        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG,
//                Serdes.String().getClass().getName());
//        return new KafkaStreamsConfiguration(props);
//    }


    @Autowired
    public void kStream(StreamsBuilder streamsBuilder) {

        JsonDeserializer<CommodityDataDto> jsonDeserializer = new JsonDeserializer<>();
        jsonDeserializer.addTrustedPackages("com.yellowdog.dto");

        JsonSerializer<CommodityDataDto> jsonSerializer = new JsonSerializer();

        Serde<CommodityDataDto> jsonSerde = Serdes.serdeFrom(jsonSerializer, jsonDeserializer);

        KStream<String, CommodityDataDto> commodityDataKStream = streamsBuilder.stream(inTopic, Consumed.with(Serdes.String(), jsonSerde));

        KGroupedStream<String, Long> dataByCountry = commodityDataKStream
                .map((key, data) -> new KeyValue<>(data.getCountryOrArea() + " : "+data.getFlow(), data.getTradeUsd()))
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
