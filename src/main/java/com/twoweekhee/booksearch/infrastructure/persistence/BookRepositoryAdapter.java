package com.twoweekhee.booksearch.infrastructure.persistence;

import static com.twoweekhee.booksearch.common.exception.ResourceNotFoundException.ResourceNotFoundExceptionCode.*;

import java.time.Duration;
import java.time.Instant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.twoweekhee.booksearch.application.dto.SearchResult;
import com.twoweekhee.booksearch.application.port.out.BookRepositoryPort;
import com.twoweekhee.booksearch.common.exception.ResourceNotFoundException;
import com.twoweekhee.booksearch.entity.Book;
import com.twoweekhee.booksearch.presentation.dto.SearchMetadata;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class BookRepositoryAdapter implements BookRepositoryPort {

	private final BookJpaRepository bookJpaRepository;

	@Override
	public Book getBook(Long id) {
		return bookJpaRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(BOOK_NOT_FOUND));
	}

	@Override
	public Page<Book> findAll(Pageable pageable) {
		return bookJpaRepository.findAll(pageable);
	}

	@Override
	public SearchResult<Book> findByOrKeywords(String keyword1, String keyword2, Pageable pageable) {
		Instant startTime = Instant.now();
		Page<Book> bookPage = bookJpaRepository.findByOrKeywords(keyword1, keyword2, pageable);
		long executionTime = Duration.between(startTime, Instant.now()).toMillis();

		return SearchResult.from(bookPage, executionTime, SearchMetadata.Strategy.OR_OPERATION);
	}

	@Override
	public SearchResult<Book> findByNotKeywords(String keyword1, String keyword2, Pageable pageable) {
		Instant startTime = Instant.now();
		Page<Book> bookPage = bookJpaRepository.findByNotKeywords(keyword1, keyword2, pageable);
		long executionTime = Duration.between(startTime, Instant.now()).toMillis();

		return SearchResult.from(bookPage, executionTime, SearchMetadata.Strategy.NOT_OPERATION);
	}

	@Override
	public SearchResult<Book> findByKeyword(String keyword, Pageable pageable) {

		Instant startTime = Instant.now();
		Page<Book> bookPage = bookJpaRepository.findByKeyword(keyword, pageable);
		long executionTime = Duration.between(startTime, Instant.now()).toMillis();

		return SearchResult.from(bookPage, executionTime, SearchMetadata.Strategy.SIMPLE_SEARCH);
	}
}
