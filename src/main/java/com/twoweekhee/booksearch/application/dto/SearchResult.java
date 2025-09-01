package com.twoweekhee.booksearch.application.dto;

import org.springframework.data.domain.Page;

import com.twoweekhee.booksearch.presentation.dto.SearchMetadata;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SearchResult<T> {
	private final Page<T> page;
	private final long executionTimeMs;
	private final SearchMetadata.Strategy strategy;

	public static <T> SearchResult<T> from(Page<T> page, long executionTimeMs, SearchMetadata.Strategy strategy) {
		return SearchResult.<T>builder()
			.page(page)
			.executionTimeMs(executionTimeMs)
			.strategy(strategy)
			.build();
	}
}
