package com.luv2code.demo.helper.impl;

import org.springframework.stereotype.Component;

import com.luv2code.demo.helper.IPaginationHelper;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PaginationHelper implements IPaginationHelper {

    @Override
    public void validatePageParameters(Integer page, Integer size) {

        if (page < 0 || size < 0) {
            log.warn("Invalid page index or page size: page={}, size={}", page, size);
            throw new IllegalArgumentException("Page index and page size must be non-negative integers.");
        }

    }

}
