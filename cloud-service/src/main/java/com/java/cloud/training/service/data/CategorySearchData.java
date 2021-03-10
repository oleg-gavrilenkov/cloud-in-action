package com.java.cloud.training.service.data;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CategorySearchData {

    private String name;

    private String sort;
}
