package com.twoweekhee.booksearch.presentation.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.twoweekhee.booksearch.common.exception.BusinessException;
import com.twoweekhee.booksearch.presentation.dto.ErrorResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class CustomExceptionHandler {

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ErrorResponse> handleResourceNotFoundException(BusinessException exception) {
		ErrorResponse errorResponse = logExceptionAndBuildResponse(exception);
		return ResponseEntity.ok(errorResponse);
	}

	private ErrorResponse logExceptionAndBuildResponse(BusinessException exception) {
		log.info("[BusinessException] code={}, message={}, exceptionClass={}",
			exception.getCode(),
			exception.getMessage(),
			exception.getClass().getSimpleName());

		return ErrorResponse.builder()
			.code(exception.getCode())
			.message(exception.getMessage()).build();
	}
}
