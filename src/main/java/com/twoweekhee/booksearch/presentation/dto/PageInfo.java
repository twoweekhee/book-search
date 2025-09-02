package com.twoweekhee.booksearch.presentation.dto;

import java.util.List;

import org.springframework.data.domain.Page;

import com.twoweekhee.booksearch.entity.Book;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PageInfo {
	private int currentPage;
	private int pageSize;
	private int totalPages;
	private Long totalElements;

	public static PageInfo from(Page<Book> book, int page, int size) {
		return PageInfo.builder()
			.currentPage(page)
			.pageSize(size)
			.totalPages(book.getTotalPages())
			.totalElements(book.getTotalElements())
			.build();
	}
}
