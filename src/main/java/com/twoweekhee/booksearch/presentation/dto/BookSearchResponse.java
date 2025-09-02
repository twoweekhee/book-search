package com.twoweekhee.booksearch.presentation.dto;

import java.util.List;

import org.springframework.data.domain.Page;

import com.twoweekhee.booksearch.entity.Book;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BookSearchResponse {
	private String searchQuery;
	private PageInfo pageInfo;
	private List<BookResponse> books;
	private SearchMetadata searchMetadata;

	public static BookSearchResponse from(String searchQuery, Page<Book> bookPage, PageInfo pageInfo, SearchMetadata.Strategy strategy, long executionTime) {
		return BookSearchResponse.builder()
			.searchQuery(searchQuery)
			.pageInfo(pageInfo)
			.books(bookPage.getContent().stream()
				.map(BookResponse::from)
				.toList())
			.searchMetadata(SearchMetadata.builder()
				.executionTime(executionTime)
				.strategy(strategy)
				.build())
			.build();
	}
}
