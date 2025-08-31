package com.twoweekhee.booksearch.common.exception;

import java.io.Serializable;

public interface ExceptionCode extends Serializable {
	String getCode();
	String getMessage();
}
