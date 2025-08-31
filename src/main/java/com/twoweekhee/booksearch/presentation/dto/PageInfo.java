package com.twoweekhee.booksearch.presentation.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PageInfo {
	private int currentPage;
	private int pageSize;
	private int totalPages;
	private Long totalElements;
}
