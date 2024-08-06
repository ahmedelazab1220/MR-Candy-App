package com.luv2code.demo.exc;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {

    private int status;

    private String title;

    private String message;

    private Object detail;

    private String path;

    private long timeStamp;

}
