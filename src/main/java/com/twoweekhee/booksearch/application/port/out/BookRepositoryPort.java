package com.twoweekhee.booksearch.application.port.out;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.twoweekhee.booksearch.application.dto.SearchResult;
import com.twoweekhee.booksearch.entity.Book;

public interface BookRepositoryPort {
	Book getBook(Long id);
	Page<Book> findAll(Pageable pageable);
	SearchResult<Book> findByOrKeywords(String keyword1, String keyword2, Pageable pageable);
	SearchResult<Book> findByNotKeywords(String keyword1, String keyword2, Pageable pageable);
	SearchResult<Book> findByKeyword(String keyword, Pageable pageable);
}
