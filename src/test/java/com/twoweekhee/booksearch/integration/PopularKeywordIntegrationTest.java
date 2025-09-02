package com.twoweekhee.booksearch.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.twoweekhee.booksearch.entity.KeywordLog;
import com.twoweekhee.booksearch.infrastructure.persistence.KeywordLogJpaRepository;
import com.twoweekhee.booksearch.presentation.dto.KeywordsResponse;
import com.twoweekhee.booksearch.support.BaseIntegrationTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PopularKeywordIntegrationTest extends BaseIntegrationTest {

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private KeywordLogJpaRepository keywordLogJpaRepository;

	@Test
	@DisplayName("인기 검색어 TOP 10 조회 통합테스트 - 성공")
	void getTop10Keywords_Success() {
		// setup
		LocalDateTime now = LocalDateTime.now();
		keywordLogJpaRepository.save(KeywordLog.builder().keyword("Java").searchedAt(now.minusHours(1)).build());
		keywordLogJpaRepository.save(KeywordLog.builder().keyword("Spring").searchedAt(now.minusHours(2)).build());
		keywordLogJpaRepository.save(KeywordLog.builder().keyword("TDD").searchedAt(now.minusMinutes(30)).build());

		// when
		ResponseEntity<KeywordsResponse> response = restTemplate.getForEntity(
			"/api/popular-keyword", KeywordsResponse.class);

		// then
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertTrue(response.getBody().getKeywords().size() <= 10);

		// cleanup
		keywordLogJpaRepository.deleteAll();
	}

	@Test
	@DisplayName("검색어가 없을 때 빈 리스트 반환 (통합 테스트)")
	void getTop10Keywords_EmptyResult() {
		// setup
		keywordLogJpaRepository.deleteAll();

		// when
		ResponseEntity<KeywordsResponse> response = restTemplate.getForEntity(
			"/api/popular-keyword", KeywordsResponse.class);

		// then
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(0, response.getBody().getKeywords().size());
	}
}