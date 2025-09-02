package com.twoweekhee.booksearch.presentation.dto;

import java.util.List;

import org.springframework.data.domain.Page;

import com.twoweekhee.booksearch.entity.Book;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BookListResponse {
	private PageInfo pageInfo;
	private List<BookResponse> books;

	public static BookListResponse from(Page<Book> books, PageInfo pageInfo) {
		return BookListResponse.builder()
			.pageInfo(pageInfo)
			.books(books.getContent().stream()
				.map(BookResponse::from)
				.toList())
			.build();
	}
}
