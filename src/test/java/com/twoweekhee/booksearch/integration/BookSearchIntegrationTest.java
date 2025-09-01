package com.twoweekhee.booksearch.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.twoweekhee.booksearch.entity.Book;
import com.twoweekhee.booksearch.infrastructure.persistence.BookJpaRepository;
import com.twoweekhee.booksearch.presentation.dto.BookSearchResponse;
import com.twoweekhee.booksearch.support.BaseIntegrationTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookSearchIntegrationTest extends BaseIntegrationTest {

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private BookJpaRepository bookJpaRepository;

	@AfterEach
	void cleanup() {
		bookJpaRepository.deleteAll();
	}

	@Test
	@DisplayName("키워드로 도서 검색 통합테스트 - 성공")
	void searchBooks_Success() {
		// setup
		Book book1 = Book.builder()
			.title("Everything about Go Lang")
			.subtitle("version 3.0")
			.author("twoweekhee")
			.isbn("9781617291609")
			.publisher("Lees House")
			.published(LocalDate.of(2016, 3, 1))
			.build();

		Book book2 = Book.builder()
			.title("Java Programming")
			.subtitle("Complete Guide")
			.author("John Doe")
			.isbn("9781234567890")
			.publisher("Tech Press")
			.published(LocalDate.of(2020, 1, 1))
			.build();

		bookJpaRepository.save(book1);
		Book save = bookJpaRepository.save(book2);

		System.out.println("save.getDocument() : " + save.getDocument());

		// given & when
		ResponseEntity<BookSearchResponse> response = restTemplate.getForEntity(
			"/api/books/search?keyword=Go&page=1&size=20", BookSearchResponse.class);

		// then
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());

		BookSearchResponse searchResponse = response.getBody();
		assertEquals("Go", searchResponse.getSearchQuery());
		assertEquals(1, searchResponse.getPageInfo().getCurrentPage());
		assertEquals(20, searchResponse.getPageInfo().getPageSize());
		assertTrue(searchResponse.getBooks().size() > 0);
		assertTrue(searchResponse.getSearchMetadata().getExecutionTime() >= 0);
	}

	@Test
	@DisplayName("기본값으로 도서 검색 통합테스트")
	void searchBooks_WithDefaults() {
		// setup
		Book book = Book.builder()
			.title("Everything about Go Lang")
			.subtitle("version 3.0")
			.author("twoweekhee")
			.isbn("9781617291609")
			.publisher("Lees House")
			.published(LocalDate.of(2016, 3, 1))
			.build();

		bookJpaRepository.save(book);

		// given & when
		ResponseEntity<BookSearchResponse> response = restTemplate.getForEntity(
			"/api/books/search?keyword=Go", BookSearchResponse.class);

		// then
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());

		BookSearchResponse searchResponse = response.getBody();
		assertEquals("Go", searchResponse.getSearchQuery());
		assertEquals(1, searchResponse.getPageInfo().getCurrentPage());
		assertEquals(20, searchResponse.getPageInfo().getPageSize());
	}

	@Test
	@DisplayName("검색 결과가 없을 때 빈 결과 반환 통합테스트")
	void searchBooks_EmptyResult() {
		// given & when
		ResponseEntity<BookSearchResponse> response = restTemplate.getForEntity(
			"/api/books/search?keyword=NonExistentKeyword", BookSearchResponse.class);

		// then
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());

		BookSearchResponse searchResponse = response.getBody();
		assertEquals("NonExistentKeyword", searchResponse.getSearchQuery());
		assertTrue(searchResponse.getBooks().isEmpty());
		assertEquals(0L, searchResponse.getPageInfo().getTotalElements());
		assertEquals(0, searchResponse.getPageInfo().getTotalPages());
	}

	@Test
	@DisplayName("페이징 처리 통합테스트")
	void searchBooks_WithPaging() {
		// setup - 여러 개의 책 생성
		for (int i = 1; i <= 25; i++) {
			Book book = Book.builder()
				.title("Java Book " + i)
				.author("Author " + i)
				.isbn("ISBN" + String.format("%010d", i))
				.build();
			bookJpaRepository.save(book);
		}

		// given & when
		ResponseEntity<BookSearchResponse> response = restTemplate.getForEntity(
			"/api/books/search?keyword=Java&page=2&size=10", BookSearchResponse.class);

		// then
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());

		BookSearchResponse searchResponse = response.getBody();
		assertEquals("Java", searchResponse.getSearchQuery());
		assertEquals(2, searchResponse.getPageInfo().getCurrentPage());
		assertEquals(10, searchResponse.getPageInfo().getPageSize());
		assertEquals(25L, searchResponse.getPageInfo().getTotalElements());
		assertEquals(3, searchResponse.getPageInfo().getTotalPages());
		assertEquals(10, searchResponse.getBooks().size());
	}

	@Test
	@DisplayName("필수 파라미터 누락 시 400 에러 반환")
	void searchBooks_MissingKeyword() {
		// given & when
		ResponseEntity<String> response = restTemplate.getForEntity(
			"/api/books/search?page=1&size=20", String.class);

		// then
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
}