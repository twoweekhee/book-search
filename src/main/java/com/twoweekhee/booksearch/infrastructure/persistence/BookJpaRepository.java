package com.twoweekhee.booksearch.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.twoweekhee.booksearch.entity.Book;

public interface BookJpaRepository extends JpaRepository<Book, Long> {
}
