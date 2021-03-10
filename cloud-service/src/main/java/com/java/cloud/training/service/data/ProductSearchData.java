package com.java.cloud.training.service.data;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class ProductSearchData {

    private String name;

    private BigDecimal priceLow;
    private BigDecimal priceHigh;

    private String categoryCode;

    private String sort;

}
