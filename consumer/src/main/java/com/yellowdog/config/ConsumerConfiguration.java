package com.yellowdog.config;

import com.yellowdog.dto.Aggregation;
import com.yellowdog.dto.CommodityDataDto;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
public class ConsumerConfiguration {

    @Bean
    public JsonSerializer<CommodityDataDto> commodityDataDtoJsonSerializer() {
        return new JsonSerializer<>();

    }

    @Bean
    public JsonDeserializer<CommodityDataDto> commodityDataDtoJsonDeserializer() {
        JsonDeserializer<CommodityDataDto> jsonDeserializer = new JsonDeserializer<>();
        jsonDeserializer.addTrustedPackages("com.yellowdog.dto");
        return jsonDeserializer;
    }

    @Bean
    public Serde<CommodityDataDto> commodityDataJsonSerde() {

        Serde<CommodityDataDto> commodityDataJsonSerde = Serdes.serdeFrom(commodityDataDtoJsonSerializer(), commodityDataDtoJsonDeserializer());
        return commodityDataJsonSerde;
    }

    @Bean
    public JsonDeserializer<Aggregation> aggregationJsonDeserializer() {
        JsonDeserializer<Aggregation> aggregationJsonDeserializer = new JsonDeserializer<>();
        aggregationJsonDeserializer.addTrustedPackages("com.yellowdog.dto");
        return aggregationJsonDeserializer;
    }

}
