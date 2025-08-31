package com.twoweekhee.booksearch.presentation.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.twoweekhee.booksearch.entity.Book;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BookResponse {
	private Long id;
	private String title;
	private String subtitle;
	private String author;
	private String isbn;
	private String publisher;

	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate published;

	public static BookResponse from(Book book) {
		return BookResponse.builder()
			.id(book.getId())
			.title(book.getTitle())
			.subtitle(book.getSubtitle())
			.author(book.getAuthor())
			.isbn(book.getIsbn())
			.publisher(book.getPublisher())
			.published(book.getPublished())
			.build();
	}
}
