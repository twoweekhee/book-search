package com.twoweekhee.booksearch.infrastructure.persistence;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.twoweekhee.booksearch.entity.KeywordLog;

@ExtendWith(MockitoExtension.class)
class KeywordLogRepositoryAdapterTest {

	@Mock
	private KeywordLogJpaRepository keywordLogJpaRepository;

	@InjectMocks
	private KeywordLogRepositoryAdapter keywordLogRepositoryAdapter;

	@Test
	@DisplayName("특정 시점 이후의 TOP 10 키워드를 조회한다")
	void getTop10Keywords_Success() {
		// given
		LocalDateTime since = LocalDateTime.of(2024, 1, 1, 0, 0);
		List<String> expectedKeywords = List.of("Java", "Spring", "TDD", "Go", "JavaScript", "Python", "React", "Node", "Docker", "Kubernetes");

		given(keywordLogJpaRepository.findTop10KeywordsSince(since))
			.willReturn(expectedKeywords);

		// when
		List<String> result = keywordLogRepositoryAdapter.getTop10Keywords(since);

		// then
		assertEquals(10, result.size());
		assertEquals("Java", result.get(0));
		assertEquals("Spring", result.get(1));
		assertEquals("TDD", result.get(2));
		verify(keywordLogJpaRepository).findTop10KeywordsSince(since);
	}

	@Test
	@DisplayName("키워드를 저장한다")
	void saveKeyword_Success() {
		// given
		String keyword = "Java";
		KeywordLog expectedKeywordLog = KeywordLog.builder()
			.keyword(keyword)
			.build();

		given(keywordLogJpaRepository.save(any(KeywordLog.class)))
			.willReturn(expectedKeywordLog);

		// when
		keywordLogRepositoryAdapter.saveKeyword(keyword);

		// then
		verify(keywordLogJpaRepository).save(argThat(keywordLog ->
			keyword.equals(keywordLog.getKeyword())
		));
	}

	@Test
	@DisplayName("빈 결과가 반환되어도 정상 처리한다")
	void getTop10Keywords_EmptyResult() {
		// given
		LocalDateTime since = LocalDateTime.of(2024, 1, 1, 0, 0);
		List<String> emptyKeywords = List.of();

		given(keywordLogJpaRepository.findTop10KeywordsSince(since))
			.willReturn(emptyKeywords);

		// when
		List<String> result = keywordLogRepositoryAdapter.getTop10Keywords(since);

		// then
		assertEquals(0, result.size());
		verify(keywordLogJpaRepository).findTop10KeywordsSince(since);
	}
}