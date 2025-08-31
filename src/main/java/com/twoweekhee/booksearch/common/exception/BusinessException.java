package com.twoweekhee.booksearch.common.exception;

public class BusinessException extends RuntimeException {
	private final ExceptionCode exceptionCode;

	public BusinessException(ExceptionCode exceptionCode) {
		super(exceptionCode.getMessage());
		this.exceptionCode = exceptionCode;
	}

	public BusinessException(ExceptionCode exceptionCode, Throwable cause) {
		super(exceptionCode.getMessage(), cause);
		this.exceptionCode = exceptionCode;
	}

	public String getCode() {
		return exceptionCode.getCode();
	}
}
