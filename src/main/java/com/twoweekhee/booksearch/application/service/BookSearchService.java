package com.twoweekhee.booksearch.application.service;

import java.time.Duration;
import java.time.Instant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.twoweekhee.booksearch.application.port.in.BookSearchUseCase;
import com.twoweekhee.booksearch.application.port.out.BookRepositoryPort;
import com.twoweekhee.booksearch.entity.Book;
import com.twoweekhee.booksearch.presentation.dto.BookSearchResponse;
import com.twoweekhee.booksearch.presentation.dto.PageInfo;
import com.twoweekhee.booksearch.presentation.dto.SearchMetadata;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookSearchService implements BookSearchUseCase {

	private final BookRepositoryPort bookRepositoryPort;

	@Override
	public BookSearchResponse searchBooks(String keyword, int page, int size) {

		Pageable pageable = PageRequest.of(page - 1, size);

		Instant startTime = Instant.now();

		Page<Book> bookPage = bookRepositoryPort.findByKeyword(keyword, pageable);

		long executionTime = Duration.between(startTime, Instant.now()).toMillis();

		PageInfo pageInfo = PageInfo.from(bookPage, page, size);

		return BookSearchResponse.from(keyword, bookPage, pageInfo, SearchMetadata.Strategy.SIMPLE_SEARCH, executionTime);
	}
}
