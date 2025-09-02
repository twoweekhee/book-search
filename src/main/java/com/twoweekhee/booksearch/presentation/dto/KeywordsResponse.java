package com.twoweekhee.booksearch.presentation.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class KeywordsResponse {
	private List<String> keywords;

	public static KeywordsResponse from(List<String> keywords) {
		return KeywordsResponse.builder()
			.keywords(keywords)
			.build();
	}
}
