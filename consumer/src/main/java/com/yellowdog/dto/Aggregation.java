package com.yellowdog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Aggregation {

    private String countryOrArea;
    private Long aggregatedTradeUsd;
}
