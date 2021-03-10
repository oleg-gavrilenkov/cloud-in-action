package com.java.cloud.training.service.utils;

import org.springframework.data.domain.Sort;

import static org.apache.commons.lang3.StringUtils.countMatches;
import static org.apache.commons.lang3.StringUtils.isEmpty;

public class SortUtils {

    public static Sort parseSort(String sortStr, String fallbackSort) {
        if (isEmpty(sortStr) || countMatches(sortStr, ',') != 1) {
            sortStr = fallbackSort;
        }
        String[] sortPart = sortStr.split(",");
        Sort.Direction direction = Sort.Direction.fromString(sortPart[1]);
        Sort.Order order = new Sort.Order(direction, sortPart[0]);
        return Sort.by(order);
    }
}
