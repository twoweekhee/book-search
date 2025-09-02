package com.twoweekhee.booksearch.application.service;

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

import com.twoweekhee.booksearch.application.port.out.KeywordLogRepositoryPort;
import com.twoweekhee.booksearch.presentation.dto.KeywordsResponse;

@ExtendWith(MockitoExtension.class)
class PopularKeywordServiceTest {

	@Mock
	private KeywordLogRepositoryPort keywordLogRepositoryPort;

	@InjectMocks
	private PopularKeywordService popularKeywordService;

	@Test
	@DisplayName("최근 3시간 내 인기 검색어 TOP 10을 조회한다")
	void getTop10Keywords_Success() {
		// given
		List<String> expectedKeywords = List.of("Java", "Spring", "TDD", "Go", "JavaScript", "Python", "React", "Node", "Docker", "Kubernetes");

		given(keywordLogRepositoryPort.getTop10Keywords(any(LocalDateTime.class)))
			.willReturn(expectedKeywords);

		// when
		KeywordsResponse result = popularKeywordService.getTop10Keywords();

		// then
		assertEquals(10, result.getKeywords().size());
		assertEquals("Java", result.getKeywords().get(0));
		assertEquals("Spring", result.getKeywords().get(1));
		assertEquals("TDD", result.getKeywords().get(2));

		verify(keywordLogRepositoryPort).getTop10Keywords(argThat(since ->
			since.isBefore(LocalDateTime.now()) &&
				since.isAfter(LocalDateTime.now().minusHours(4))
		));
	}

	@Test
	@DisplayName("검색어가 없을 때 빈 리스트를 반환한다")
	void getTop10Keywords_EmptyResult() {
		// given
		List<String> emptyKeywords = List.of();

		given(keywordLogRepositoryPort.getTop10Keywords(any(LocalDateTime.class)))
			.willReturn(emptyKeywords);

		// when
		KeywordsResponse result = popularKeywordService.getTop10Keywords();

		// then
		assertEquals(0, result.getKeywords().size());
		verify(keywordLogRepositoryPort).getTop10Keywords(any(LocalDateTime.class));
	}

	@Test
	@DisplayName("10개 미만의 키워드가 있을 때 정상 처리한다")
	void getTop10Keywords_LessThan10Keywords() {
		// given
		List<String> keywords = List.of("Java", "Spring", "TDD");

		given(keywordLogRepositoryPort.getTop10Keywords(any(LocalDateTime.class)))
			.willReturn(keywords);

		// when
		KeywordsResponse result = popularKeywordService.getTop10Keywords();

		// then
		assertEquals(3, result.getKeywords().size());
		assertEquals("Java", result.getKeywords().get(0));
		assertEquals("Spring", result.getKeywords().get(1));
		assertEquals("TDD", result.getKeywords().get(2));
		verify(keywordLogRepositoryPort).getTop10Keywords(any(LocalDateTime.class));
	}
}