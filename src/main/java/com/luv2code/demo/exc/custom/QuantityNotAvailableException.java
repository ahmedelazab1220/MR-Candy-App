package com.luv2code.demo.exc.custom;

public class QuantityNotAvailableException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public QuantityNotAvailableException(String message, Throwable cause) {
		super(message, cause);
	}

	public QuantityNotAvailableException(String message) {
		super(message);
	}

	public QuantityNotAvailableException(Throwable cause) {
		super(cause);
	}

}
