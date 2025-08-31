package com.twoweekhee.booksearch.application.port.in;

import com.twoweekhee.booksearch.presentation.dto.BookListResponse;
import com.twoweekhee.booksearch.presentation.dto.BookResponse;

public interface BookUseCase {
	BookResponse getBook(Long id);
	BookListResponse getBooks(int page, int size);
}
