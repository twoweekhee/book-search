package com.twoweekhee.booksearch.application.port.out;

import com.twoweekhee.booksearch.entity.Book;

public interface BookRepositoryPort {
	Book getBook(Long id);
}
