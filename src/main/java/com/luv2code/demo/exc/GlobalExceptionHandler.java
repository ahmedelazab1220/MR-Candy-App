package com.luv2code.demo.exc;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.luv2code.demo.exc.custom.NotFoundException;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	private ErrorResponse buildErrorResponse(int statusCode, String errorType, String message, Object details,
			WebRequest request) {
		return new ErrorResponse(statusCode, errorType, message, details, request.getDescription(false).substring(4),
				System.currentTimeMillis());
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ErrorResponse handleBadCredentialsException(BadCredentialsException ex, WebRequest request) {
		return buildErrorResponse(StatusCode.UNAUTHORIZED, "BadCredentialsException", "Email or password is incorrect.",
				ex.getMessage(), request);
	}

	@ExceptionHandler(InternalAuthenticationServiceException.class)
	public ErrorResponse handleInternalAuthenticationServiceException(InternalAuthenticationServiceException ex, WebRequest request) {
		return buildErrorResponse(StatusCode.UNAUTHORIZED, "InternalAuthenticationServiceException", "User Not Found!",
				ex.getMessage(), request);
	}
	
	@ExceptionHandler(NotFoundException.class)
	public ErrorResponse handleNotFoundException(NotFoundException ex, WebRequest request) {
		return buildErrorResponse(StatusCode.NOT_FOUND, "NotFoundException", ex.getMessage(),
				ex.getClass(), request);
	}
	
	@ExceptionHandler(IOException.class)
	public ErrorResponse handleIOException(IOException ex, WebRequest request) {
		return buildErrorResponse(StatusCode.INTERNAL_SERVER_ERROR, "IOException", "File upload failed", ex.getMessage(),
				 request);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ErrorResponse handleDataIntegrityViolationException(DataIntegrityViolationException ex, WebRequest request) {
		
		Pattern pattern = Pattern.compile("\\[(.*?)\\]");
	    Matcher matcher = pattern.matcher(ex.getMessage());
	    
	    String message = ex.getMessage();
	    if (matcher.find()) {
            message = matcher.group(1);
        }
		
		return buildErrorResponse(StatusCode.Conflict, "DataIntegrityViolationException", message,
				 ex.getClass(),request);
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest request) {
		
		String message = ex.getBindingResult().getFieldError().getField(); 
	    String defaultMessage = ex.getBindingResult().getFieldError().getDefaultMessage();

	    log.info("on Field - " + message);

	    String customMessage = "Validation failed on field '" + message + "': " + defaultMessage;

	    return buildErrorResponse(StatusCode.Conflict, "MethodArgumentNotValidException", customMessage,
	            ex.getClass(), request);
	}
	
	@ExceptionHandler(SignatureException.class)
	public ErrorResponse handleSignatureException(SignatureException ex, WebRequest request) {
		return buildErrorResponse(StatusCode.FORBIDDEN, "SignatureException", "The JWT signature is invalid.",
				ex.getMessage(), request);	
	}
	
	@ExceptionHandler(MalformedJwtException.class)
	public ErrorResponse handleMalformedJwtException(MalformedJwtException ex, WebRequest request) {
		return buildErrorResponse(StatusCode.UNAUTHORIZED, "MalformedJwtException", "Invalid Jwt token.",
				ex.getMessage(), request);
	}
	
	@ExceptionHandler(ExpiredJwtException.class)
	public ErrorResponse handleExpiredJwtxception(ExpiredJwtException ex, WebRequest request) {
		return buildErrorResponse(StatusCode.FORBIDDEN, "ExpiredJwtException", "The JWT token has expired.",
				ex.getMessage(), request);
	}
	
	@ExceptionHandler(AccessDeniedException.class)
	ErrorResponse handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
		return buildErrorResponse(StatusCode.FORBIDDEN, "AccessDeniedException", "No Permission.", ex.getMessage(),
				request);
	}
	
	@ExceptionHandler(InsufficientAuthenticationException.class)
	public ErrorResponse handleInsufficientAuthenticationException(InsufficientAuthenticationException ex,
			WebRequest request) {
		return buildErrorResponse(StatusCode.INTERNAL_SERVER_ERROR, "InsufficientAuthenticationException",
				"Login credentials are missing.", ex.getMessage(), request);
	}
	
	
}
