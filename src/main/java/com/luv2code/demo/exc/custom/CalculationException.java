package com.luv2code.demo.exc.custom;

public class CalculationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CalculationException(String message, Throwable cause) {
        super(message, cause);
    }

    public CalculationException(String message) {
        super(message);
    }

    public CalculationException(Throwable cause) {
        super(cause);
    }

}
