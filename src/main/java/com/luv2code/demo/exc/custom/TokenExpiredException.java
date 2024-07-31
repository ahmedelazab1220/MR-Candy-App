package com.luv2code.demo.exc.custom;

public class TokenExpiredException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public TokenExpiredException(String message, Throwable cause) {
		super(message, cause);
	}

	public TokenExpiredException(String message) {
		super(message);
	}

	public TokenExpiredException(Throwable cause) {
		super(cause);
	}

}
