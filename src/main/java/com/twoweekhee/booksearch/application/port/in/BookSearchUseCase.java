package com.twoweekhee.booksearch.application.port.in;

import com.twoweekhee.booksearch.presentation.dto.BookSearchResponse;

public interface BookSearchUseCase {
	BookSearchResponse searchBooks(String query, int page, int size);
}
