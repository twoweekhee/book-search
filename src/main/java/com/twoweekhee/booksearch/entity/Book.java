package com.twoweekhee.booksearch.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "book", uniqueConstraints = {
	@UniqueConstraint(name = "uk_book_isbn", columnNames = "isbn")
})
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Book {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "title", nullable = false, length = 500)
	private String title;

	@Column(name = "subtitle", length = 500)
	private String subtitle;

	@Column(name = "author", nullable = false, length = 200)
	private String author;

	@Column(name = "isbn", unique = true, nullable = false, length = 20)
	private String isbn;

	@Column(name = "publisher", length = 200)
	private String publisher;

	@Column(name = "published")
	private LocalDate published;
}
