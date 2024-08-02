package com.luv2code.demo.exc.custom;

public class ExpiredException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ExpiredException(String message, Throwable cause) {
		super(message, cause);
	}

	public ExpiredException(String message) {
		super(message);
	}

	public ExpiredException(Throwable cause) {
		super(cause);
	}

}
