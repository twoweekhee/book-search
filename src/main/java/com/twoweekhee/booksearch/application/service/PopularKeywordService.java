package com.twoweekhee.booksearch.application.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.twoweekhee.booksearch.application.port.in.PopularKeywordUseCase;
import com.twoweekhee.booksearch.application.port.out.KeywordLogRepositoryPort;
import com.twoweekhee.booksearch.presentation.dto.KeywordsResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PopularKeywordService implements PopularKeywordUseCase {

	private final KeywordLogRepositoryPort keywordLogRepositoryPort;

	private static final int POPULAR_KEYWORD_PERIOD_HOURS = 3;

	@Override
	public KeywordsResponse getTop10Keywords() {
		LocalDateTime since = LocalDateTime.now().minusHours(POPULAR_KEYWORD_PERIOD_HOURS);

		return KeywordsResponse.from(keywordLogRepositoryPort.getTop10Keywords(since));
	}
}
