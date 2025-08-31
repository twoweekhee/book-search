package com.twoweekhee.booksearch.application.port.out;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.twoweekhee.booksearch.entity.Book;

public interface BookRepositoryPort {
	Book getBook(Long id);
	Page<Book> findAll(Pageable pageable);
}
