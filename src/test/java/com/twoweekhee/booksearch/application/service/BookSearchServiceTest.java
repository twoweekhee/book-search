package com.twoweekhee.booksearch.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.twoweekhee.booksearch.application.port.out.BookRepositoryPort;
import com.twoweekhee.booksearch.entity.Book;
import com.twoweekhee.booksearch.presentation.dto.BookSearchResponse;
import com.twoweekhee.booksearch.presentation.dto.SearchMetadata;

@ExtendWith(MockitoExtension.class)
class BookSearchServiceTest {

	@Mock
	private BookRepositoryPort bookRepositoryPort;

	@InjectMocks
	private BookSearchService bookSearchService;

	@Test
	@DisplayName("키워드로 도서를 검색한다")
	void searchBooks_Success() {
		// given
		String keyword = "java";
		int page = 1;
		int size = 20;
		Pageable pageable = PageRequest.of(0, 20);

		Book book = Book.builder()
			.id(1L)
			.title("Everything about Go Lang")
			.subtitle("version 3.0")
			.author("twoweekhee")
			.isbn("9781617291609")
			.publisher("Lees House")
			.published(LocalDate.of(2016, 3, 1))
			.build();

		Page<Book> bookPage = new PageImpl<>(List.of(book), pageable, 100);

		given(bookRepositoryPort.findByKeyword(keyword, pageable)).willReturn(bookPage);

		// when
		BookSearchResponse result = bookSearchService.searchBooks(keyword, page, size);

		// then
		assertNotNull(result);
		assertEquals(keyword, result.getSearchQuery());
		assertEquals(1, result.getPageInfo().getCurrentPage());
		assertEquals(20, result.getPageInfo().getPageSize());
		assertEquals(5, result.getPageInfo().getTotalPages());
		assertEquals(100L, result.getPageInfo().getTotalElements());
		assertEquals(1, result.getBooks().size());
		assertEquals("Everything about Go Lang", result.getBooks().get(0).getTitle());
		assertEquals(SearchMetadata.Strategy.SIMPLE_SEARCH, result.getSearchMetadata().getStrategy());
		assertTrue(result.getSearchMetadata().getExecutionTime() >= 0);

		verify(bookRepositoryPort).findByKeyword(keyword, pageable);
	}

	@Test
	@DisplayName("검색 결과가 없을 때 빈 리스트를 반환한다")
	void searchBooks_EmptyResult() {
		// given
		String keyword = "nonexistent";
		int page = 1;
		int size = 20;
		Pageable pageable = PageRequest.of(0, 20);

		Page<Book> emptyBookPage = new PageImpl<>(List.of(), pageable, 0);

		given(bookRepositoryPort.findByKeyword(keyword, pageable)).willReturn(emptyBookPage);

		// when
		BookSearchResponse result = bookSearchService.searchBooks(keyword, page, size);

		// then
		assertNotNull(result);
		assertEquals(keyword, result.getSearchQuery());
		assertEquals(1, result.getPageInfo().getCurrentPage());
		assertEquals(20, result.getPageInfo().getPageSize());
		assertEquals(0, result.getPageInfo().getTotalPages());
		assertEquals(0L, result.getPageInfo().getTotalElements());
		assertTrue(result.getBooks().isEmpty());
		assertEquals(SearchMetadata.Strategy.SIMPLE_SEARCH, result.getSearchMetadata().getStrategy());
		assertTrue(result.getSearchMetadata().getExecutionTime() >= 0);

		verify(bookRepositoryPort).findByKeyword(keyword, pageable);
	}

	@Test
	@DisplayName("페이지 정보가 올바르게 변환된다")
	void searchBooks_PageInfoConversion() {
		// given
		String keyword = "test";
		int page = 2;
		int size = 10;
		Pageable pageable = PageRequest.of(1, 10);

		Book book = Book.builder()
			.id(1L)
			.title("Test Book")
			.author("Test Author")
			.isbn("1234567890")
			.build();

		Page<Book> bookPage = new PageImpl<>(List.of(book), pageable, 25);

		given(bookRepositoryPort.findByKeyword(keyword, pageable)).willReturn(bookPage);

		// when
		BookSearchResponse result = bookSearchService.searchBooks(keyword, page, size);

		// then
		assertEquals(2, result.getPageInfo().getCurrentPage());
		assertEquals(10, result.getPageInfo().getPageSize());
		assertEquals(3, result.getPageInfo().getTotalPages());
		assertEquals(25L, result.getPageInfo().getTotalElements());

		verify(bookRepositoryPort).findByKeyword(keyword, PageRequest.of(1, 10));
	}

	@Test
	@DisplayName("실행 시간이 측정된다")
	void searchBooks_ExecutionTimeTracking() {
		// given
		String keyword = "performance";
		int page = 1;
		int size = 20;
		Pageable pageable = PageRequest.of(0, 20);

		Page<Book> bookPage = new PageImpl<>(List.of(), pageable, 0);

		given(bookRepositoryPort.findByKeyword(keyword, pageable)).willReturn(bookPage);

		// when
		BookSearchResponse result = bookSearchService.searchBooks(keyword, page, size);

		// then
		assertNotNull(result.getSearchMetadata());
		assertTrue(result.getSearchMetadata().getExecutionTime() >= 0);
		assertEquals(SearchMetadata.Strategy.SIMPLE_SEARCH, result.getSearchMetadata().getStrategy());
	}
}