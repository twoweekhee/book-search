package com.twoweekhee.booksearch.presentation.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SearchMetadata {
	private long executionTime;
	private Strategy strategy;

	public enum Strategy {
		OR_OPERATION,
		NOT_OPERATION,
		SIMPLE_SEARCH
	}
}
