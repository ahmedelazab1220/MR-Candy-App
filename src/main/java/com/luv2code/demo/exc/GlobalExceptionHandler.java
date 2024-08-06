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
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.luv2code.demo.exc.custom.NotFoundException;
import com.luv2code.demo.exc.custom.QuantityNotAvailableException;
import com.luv2code.demo.exc.custom.ExpiredException;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.validation.UnexpectedTypeException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ErrorResponse buildErrorResponse(int statusCode, String errorType, String message, Object details,
            WebRequest request) {
        return new ErrorResponse(statusCode, errorType, message, details, request.getDescription(false).substring(4),
                System.currentTimeMillis());
    }

    /**
     * Handles the BadCredentialsException by building an ErrorResponse object
     * with the appropriate status code, error type, message, details, and
     * request information.
     *
     * @param ex the BadCredentialsException that was thrown
     * @param request the WebRequest object containing information about the
     * request
     * @return the ErrorResponse object with the appropriate information
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ErrorResponse handleBadCredentialsException(BadCredentialsException ex, WebRequest request) {
        return buildErrorResponse(StatusCode.UNAUTHORIZED, "BadCredentialsException", "Email or password is incorrect.",
                ex.getMessage(), request);
    }

    /**
     * Handles the InternalAuthenticationServiceException by building an
     * ErrorResponse object with the appropriate status code, error type,
     * message, details, and request information.
     *
     * @param ex the InternalAuthenticationServiceException that was thrown
     * @param request the WebRequest object containing information about the
     * request
     * @return the ErrorResponse object with the appropriate information
     */
    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ErrorResponse handleInternalAuthenticationServiceException(InternalAuthenticationServiceException ex,
            WebRequest request) {
        return buildErrorResponse(StatusCode.UNAUTHORIZED, "InternalAuthenticationServiceException", "User Not Found!",
                ex.getMessage(), request);
    }

    /**
     * Handles the NotFoundException by building an ErrorResponse object with
     * the appropriate status code, error type, message, details, and request
     * information.
     *
     * @param ex the NotFoundException that was thrown
     * @param request the WebRequest object containing information about the
     * request
     * @return the ErrorResponse object with the appropriate information
     */
    @ExceptionHandler(NotFoundException.class)
    public ErrorResponse handleNotFoundException(NotFoundException ex, WebRequest request) {
        return buildErrorResponse(StatusCode.NOT_FOUND, "NotFoundException", ex.getMessage(), ex.getClass(), request);
    }

    /**
     * Handles the IOException by building an ErrorResponse object with the
     * appropriate status code, error type, message, details, and request
     * information.
     *
     * @param ex the IOException that was thrown
     * @param request the WebRequest object containing information about the
     * request
     * @return the ErrorResponse object with the appropriate information
     */
    @ExceptionHandler(IOException.class)
    public ErrorResponse handleIOException(IOException ex, WebRequest request) {
        return buildErrorResponse(StatusCode.INTERNAL_SERVER_ERROR, "IOException", "File upload failed",
                ex.getMessage(), request);
    }

    /**
     * Handles the DataIntegrityViolationException by extracting the relevant
     * message from the exception and building an ErrorResponse object with the
     * appropriate status code, error type, message, details, and request
     * information.
     *
     * @param ex the DataIntegrityViolationException that was thrown
     * @param request the WebRequest object containing information about the
     * request
     * @return the ErrorResponse object with the appropriate information
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ErrorResponse handleDataIntegrityViolationException(DataIntegrityViolationException ex, WebRequest request) {

        Pattern pattern = Pattern.compile("\\[(.*?)\\]");
        Matcher matcher = pattern.matcher(ex.getMessage());

        String message = ex.getMessage();
        if (matcher.find()) {
            message = matcher.group(1);
        }

        return buildErrorResponse(StatusCode.CONFLICT, "DataIntegrityViolationException", message, ex.getClass(),
                request);
    }

    /**
     * Handles the MethodArgumentNotValidException by extracting the field name
     * and default message from the exception, logging the field name, and
     * building an ErrorResponse object with the appropriate status code, error
     * type, custom message, exception class, and request information.
     *
     * @param ex the MethodArgumentNotValidException that was thrown
     * @param request the WebRequest object containing information about the
     * request
     * @return the ErrorResponse object with the appropriate information
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest request) {

        String message = ex.getBindingResult().getFieldError().getField();
        String defaultMessage = ex.getBindingResult().getFieldError().getDefaultMessage();

        String customMessage = "Validation failed on field '" + message + "': " + defaultMessage;

        return buildErrorResponse(StatusCode.CONFLICT, "MethodArgumentNotValidException", customMessage, ex.getClass(),
                request);
    }

    /**
     * Handles the SignatureException by building an ErrorResponse object with
     * the appropriate status code, error type, message, details, and request
     * information.
     *
     * @param ex the SignatureException that was thrown
     * @param request the WebRequest object containing information about the
     * request
     * @return the ErrorResponse object with the appropriate information
     */
    @ExceptionHandler(SignatureException.class)
    public ErrorResponse handleSignatureException(SignatureException ex, WebRequest request) {
        return buildErrorResponse(StatusCode.FORBIDDEN, "SignatureException", "The JWT signature is invalid.",
                ex.getMessage(), request);
    }

    /**
     * Handles the MalformedJwtException by building an ErrorResponse object
     * with the appropriate status code, error type, message, details, and
     * request information.
     *
     * @param ex the MalformedJwtException that was thrown
     * @param request the WebRequest object containing information about the
     * request
     * @return the ErrorResponse object with the appropriate information
     */
    @ExceptionHandler(MalformedJwtException.class)
    public ErrorResponse handleMalformedJwtException(MalformedJwtException ex, WebRequest request) {
        return buildErrorResponse(StatusCode.UNAUTHORIZED, "MalformedJwtException", "Invalid Jwt token.",
                ex.getMessage(), request);
    }

    /**
     * Handles the ExpiredJwtException by building an ErrorResponse object with
     * the appropriate status code, error type, message, details, and request
     * information.
     *
     * @param ex the ExpiredJwtException that was thrown
     * @param request the WebRequest object containing information about the
     * request
     * @return the ErrorResponse object with the appropriate information
     */
    @ExceptionHandler(ExpiredJwtException.class)
    public ErrorResponse handleExpiredJwtxception(ExpiredJwtException ex, WebRequest request) {
        return buildErrorResponse(StatusCode.FORBIDDEN, "ExpiredJwtException", "The JWT token has expired.",
                ex.getMessage(), request);
    }

    /**
     * Handles the AccessDeniedException by building an ErrorResponse object
     * with the appropriate status code, error type, message, details, and
     * request information.
     *
     * @param ex the AccessDeniedException that was thrown
     * @param request the WebRequest object containing information about the
     * request
     * @return the ErrorResponse object with the appropriate information
     */
    @ExceptionHandler(AccessDeniedException.class)
    ErrorResponse handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        return buildErrorResponse(StatusCode.FORBIDDEN, "AccessDeniedException", "No Permission.", ex.getMessage(),
                request);
    }

    /**
     * Handles the InsufficientAuthenticationException by building an
     * ErrorResponse object with the appropriate status code, error type,
     * message, details, and request information.
     *
     * @param ex the InsufficientAuthenticationException that was thrown
     * @param request the WebRequest object containing information about the
     * request
     * @return the ErrorResponse object with the appropriate information
     */
    @ExceptionHandler(InsufficientAuthenticationException.class)
    public ErrorResponse handleInsufficientAuthenticationException(InsufficientAuthenticationException ex,
            WebRequest request) {
        return buildErrorResponse(StatusCode.INTERNAL_SERVER_ERROR, "InsufficientAuthenticationException",
                "Login credentials are missing.", ex.getMessage(), request);
    }

    /**
     * Handles the IllegalArgumentException by building an ErrorResponse object
     * with the appropriate status code, error type, message, details, and
     * request information.
     *
     * @param ex	the IllegalArgumentException that was thrown
     * @param request	the WebRequest object containing information about the
     * request
     * @return the ErrorResponse object with the appropriate information
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        return buildErrorResponse(StatusCode.INVALID_ARGUMENT, "IllegalArgumentException", ex.getMessage(), ex.getClass(), request);
    }

    /**
     * Handles the MissingServletRequestParameterException by building an
     * ErrorResponse object with the appropriate status code, error type,
     * message, details, and request information.
     *
     * @param ex the MissingServletRequestParameterException that was thrown
     * @param request the WebRequest object containing information about the
     * request
     * @return the ErrorResponse object with the appropriate information
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ErrorResponse handleMissingServletRequestParameterExceptio(MissingServletRequestParameterException ex, WebRequest request) {
        return buildErrorResponse(StatusCode.INVALID_ARGUMENT, "MissingServletRequestParameterException", ex.getMessage(), ex.getClass(), request);
    }

    /**
     * Handles the QuantityNotAvailableException by building an ErrorResponse
     * object with the appropriate status code, error type, message, details,
     * and request information.
     *
     * @param ex the QuantityNotAvailableException that was thrown
     * @param request the WebRequest object containing information about the
     * request
     * @return the ErrorResponse object with the appropriate information
     */
    @ExceptionHandler(QuantityNotAvailableException.class)
    public ErrorResponse handleMissingServletRequestParameterExceptio(QuantityNotAvailableException ex, WebRequest request) {
        return buildErrorResponse(StatusCode.INVALID_ARGUMENT, "QuantityNotAvailableException", ex.getMessage(), ex.getClass(), request);
    }

    /**
     * Handles the ExpiredException by building an ErrorResponse object with the
     * appropriate status code, error type, message, and request information.
     *
     * @param ex the ExpiredException that was thrown
     * @param request the WebRequest object containing information about the
     * request
     * @return the ErrorResponse object with the appropriate information
     */
    @ExceptionHandler(ExpiredException.class)
    public ErrorResponse handleTokenExpiredException(ExpiredException ex, WebRequest request) {
        return buildErrorResponse(StatusCode.FORBIDDEN, "ExpiredException", ex.getMessage(), ex.getClass(), request);
    }

    /**
     * Handles the UnexpectedTypeException by building an ErrorResponse object
     * with the appropriate status code, error type, message, and request
     * information.
     *
     * @param ex the UnexpectedTypeException that was thrown
     * @param request the WebRequest object containing information about the
     * request
     * @return the ErrorResponse object with the appropriate information
     */
    @ExceptionHandler(UnexpectedTypeException.class)
    public ErrorResponse handleUnexpectedTypeException(UnexpectedTypeException ex, WebRequest request) {
        return buildErrorResponse(StatusCode.FORBIDDEN, "UnexpectedTypeException", ex.getMessage(), ex.getClass(), request);
    }

    /**
     * Handles the Exception by building an ErrorResponse object with the
     * appropriate status code, error type, message, and request information.
     *
     * @param ex the Exception that was thrown
     * @param request the WebRequest object containing information about the
     * request
     * @return the ErrorResponse object with the appropriate information
     */
    @ExceptionHandler(Exception.class)
    public ErrorResponse handleException(Exception ex, WebRequest request) {
        return buildErrorResponse(StatusCode.INTERNAL_SERVER_ERROR, "Exception", "An unknown error occurred.",
                ex.getMessage(), request);
    }

}
