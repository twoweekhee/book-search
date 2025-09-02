package com.twoweekhee.booksearch.infrastructure.persistence;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.twoweekhee.booksearch.application.port.out.KeywordLogRepositoryPort;
import com.twoweekhee.booksearch.entity.KeywordLog;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class KeywordLogRepositoryAdapter implements KeywordLogRepositoryPort {

	private final KeywordLogJpaRepository keywordLogJpaRepository;

	@Override
	public List<String> getTop10Keywords(LocalDateTime since) {
		return keywordLogJpaRepository.findTop10KeywordsSince(since);
	}
	@Override
	public void saveKeyword(String keyword) {
		keywordLogJpaRepository.save(KeywordLog.builder()
			.keyword(keyword)
			.build());
	}
}
