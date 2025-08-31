package com.twoweekhee.booksearch.infrastructure.persistence;

import static com.twoweekhee.booksearch.common.exception.ResourceNotFoundException.ResourceNotFoundExceptionCode.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.twoweekhee.booksearch.application.port.out.BookRepositoryPort;
import com.twoweekhee.booksearch.common.exception.ResourceNotFoundException;
import com.twoweekhee.booksearch.entity.Book;

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
}
