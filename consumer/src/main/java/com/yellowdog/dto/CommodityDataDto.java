package com.yellowdog.dto;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CommodityDataDto {

    @CsvBindByName(column = "country_or_area")
    private String countryOrArea;

    @CsvBindByName(column = "year")
    private String year;

    @CsvBindByName(column = "comm_code")
    private String commCode;

    @CsvBindByName(column = "commodity")
    private String commodity;

    @CsvBindByName(column = "flow")
    private String flow;

    @CsvBindByName(column = "trade_usd")
    private Long tradeUsd;

    @CsvBindByName(column = "weight_kg")
    private String weightKg;

    @CsvBindByName(column = "quantity_name")
    private String quantityName;

    @CsvBindByName(column = "quantity")
    private String quantity;

    @CsvBindByName(column = "category")
    private String category;


}
