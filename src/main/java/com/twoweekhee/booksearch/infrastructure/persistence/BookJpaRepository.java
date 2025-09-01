package com.twoweekhee.booksearch.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.twoweekhee.booksearch.entity.Book;

public interface BookJpaRepository extends JpaRepository<Book, Long> {

	@Query(
		value = "SELECT * FROM book WHERE document @@ plainto_tsquery(:keyword)",
		countQuery = "SELECT count(*) FROM book WHERE document @@ plainto_tsquery(:keyword)",
		nativeQuery = true
	)
	Page<Book> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

	@Query(
		value = "SELECT * FROM book WHERE document @@ to_tsquery(:keyword1 || ' | ' || :keyword2)",
		countQuery = "SELECT count(*) FROM book WHERE document @@ to_tsquery(:keyword1 || ' | ' || :keyword2)",
		nativeQuery = true
	)
	Page<Book> findByOrKeywords(@Param("keyword1") String keyword1,
		@Param("keyword2") String keyword2,
		Pageable pageable);

	@Query(
		value = "SELECT * FROM book WHERE document @@ to_tsquery(:keyword1 || ' & ! ' || :keyword2)",
		countQuery = "SELECT count(*) FROM book WHERE document @@ to_tsquery(:keyword1 || ' & ! ' || :keyword2)",
		nativeQuery = true
	)
	Page<Book> findByNotKeywords(@Param("keyword1") String keyword1,
		@Param("keyword2") String keyword2,
		Pageable pageable);
}
