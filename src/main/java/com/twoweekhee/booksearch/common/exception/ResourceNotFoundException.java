package com.twoweekhee.booksearch.common.exception;

import lombok.Getter;

public class ResourceNotFoundException extends BusinessException {

	@Getter
	public enum ResourceNotFoundExceptionCode implements ExceptionCode {
		BOOK_NOT_FOUND("BOOK_404", "책을 찾을 수 없습니다."),
		;

		private final String code;
		private final String message;

		ResourceNotFoundExceptionCode(String code, String message) {
			this.code = code;
			this.message = message;
		}
	}

	public ResourceNotFoundException(ResourceNotFoundExceptionCode code) {
		super(code);
	}
}
