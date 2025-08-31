package com.twoweekhee.booksearch.application.service;

import org.springframework.stereotype.Service;

import com.twoweekhee.booksearch.application.port.in.BookUseCase;
import com.twoweekhee.booksearch.application.port.out.BookRepositoryPort;
import com.twoweekhee.booksearch.presentation.dto.BookResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookService implements BookUseCase {

	private final BookRepositoryPort bookRepositoryPort;

	@Override
	public BookResponse getBook(Long id) {
		return BookResponse.from(bookRepositoryPort.getBook(id));
	}
}
