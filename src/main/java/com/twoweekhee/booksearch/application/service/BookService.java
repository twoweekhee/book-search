package com.twoweekhee.booksearch.application.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.twoweekhee.booksearch.application.port.in.BookUseCase;
import com.twoweekhee.booksearch.application.port.out.BookRepositoryPort;
import com.twoweekhee.booksearch.entity.Book;
import com.twoweekhee.booksearch.presentation.dto.BookListResponse;
import com.twoweekhee.booksearch.presentation.dto.BookResponse;
import com.twoweekhee.booksearch.presentation.dto.PageInfo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService implements BookUseCase {

	private final BookRepositoryPort bookRepositoryPort;

	@Override
	public BookResponse getBook(Long id) {
		return BookResponse.from(bookRepositoryPort.getBook(id));
	}

	@Override
	public BookListResponse getBooks(int page, int size) {
		Pageable pageable = PageRequest.of(page - 1, size);
		Page<Book> bookPage = bookRepositoryPort.findAll(pageable);

		PageInfo pageInfo = PageInfo.from(bookPage, page, size);

		return BookListResponse.from(bookPage, pageInfo);
	}
}
