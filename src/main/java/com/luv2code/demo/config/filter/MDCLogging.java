package com.luv2code.demo.config.filter;

import java.io.IOException;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MDCLogging implements Filter {

	private final String CORRELATION_ID = "X-Correlation-Id";

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
        MDC.put(CORRELATION_ID, httpRequest.getHeader(CORRELATION_ID));
        log.info("Intercept coming request and set MDC context information");
        chain.doFilter(request, response);
		
	}
	
}
