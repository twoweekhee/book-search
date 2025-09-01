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

import com.twoweekhee.booksearch.application.dto.SearchResult;
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
	@DisplayName("OR 연산자로 도서 검색")
	void searchBooks_OrOperation() {
		// given
		String query = "Go|JavaScript";
		int page = 1;
		int size = 10;
		Pageable pageable = PageRequest.of(0, 10);

		Book book = Book.builder()
			.id(1L)
			.title("Everything about Go Lang")
			.subtitle("version 3.0")
			.author("twoweekhee")
			.isbn("9781617291609")
			.publisher("Lees House")
			.published(LocalDate.of(2016, 3, 1))
			.build();

		Page<Book> bookPage = new PageImpl<>(List.of(book), pageable, 1);
		SearchResult<Book> searchResult = SearchResult.from(bookPage, 50L, SearchMetadata.Strategy.OR_OPERATION);

		given(bookRepositoryPort.findByOrKeywords("Go", "JavaScript", pageable))
			.willReturn(searchResult);

		// when
		BookSearchResponse response = bookSearchService.searchBooks(query, page, size);

		// then
		assertEquals("Go|JavaScript", response.getSearchQuery());
		assertEquals(1, response.getBooks().size());
		assertEquals(SearchMetadata.Strategy.OR_OPERATION, response.getSearchMetadata().getStrategy());
		assertEquals(50L, response.getSearchMetadata().getExecutionTime());
		verify(bookRepositoryPort).findByOrKeywords("Go", "JavaScript", pageable);
	}

	@Test
	@DisplayName("NOT 연산자로 도서 검색")
	void searchBooks_NotOperation() {
		// given
		String query = "Programming-JavaScript";
		int page = 1;
		int size = 10;
		Pageable pageable = PageRequest.of(0, 10);

		Book book = Book.builder()
			.id(1L)
			.title("Everything about Go Lang")
			.subtitle("version 3.0")
			.author("twoweekhee")
			.isbn("9781617291609")
			.publisher("Lees House")
			.published(LocalDate.of(2016, 3, 1))
			.build();

		Page<Book> bookPage = new PageImpl<>(List.of(book), pageable, 1);
		SearchResult<Book> searchResult = SearchResult.from(bookPage, 30L, SearchMetadata.Strategy.NOT_OPERATION);

		given(bookRepositoryPort.findByNotKeywords("Programming", "JavaScript", pageable))
			.willReturn(searchResult);

		// when
		BookSearchResponse response = bookSearchService.searchBooks(query, page, size);

		// then
		assertEquals("Programming-JavaScript", response.getSearchQuery());
		assertEquals(1, response.getBooks().size());
		assertEquals(SearchMetadata.Strategy.NOT_OPERATION, response.getSearchMetadata().getStrategy());
		assertEquals(30L, response.getSearchMetadata().getExecutionTime());
		verify(bookRepositoryPort).findByNotKeywords("Programming", "JavaScript", pageable);
	}

	@Test
	@DisplayName("단순 키워드로 도서 검색")
	void searchBooks_SimpleSearch() {
		// given
		String query = "TDD";
		int page = 1;
		int size = 10;
		Pageable pageable = PageRequest.of(0, 10);

		Book book = Book.builder()
			.id(1L)
			.title("Everything about Go Lang")
			.subtitle("version 3.0")
			.author("twoweekhee")
			.isbn("9781617291609")
			.publisher("Lees House")
			.published(LocalDate.of(2016, 3, 1))
			.build();

		Page<Book> bookPage = new PageImpl<>(List.of(book), pageable, 1);
		SearchResult<Book> searchResult = SearchResult.from(bookPage, 25L, SearchMetadata.Strategy.SIMPLE_SEARCH);

		given(bookRepositoryPort.findByKeyword("TDD", pageable))
			.willReturn(searchResult);

		// when
		BookSearchResponse response = bookSearchService.searchBooks(query, page, size);

		// then
		assertEquals("TDD", response.getSearchQuery());
		assertEquals(1, response.getBooks().size());
		assertEquals(SearchMetadata.Strategy.SIMPLE_SEARCH, response.getSearchMetadata().getStrategy());
		assertEquals(25L, response.getSearchMetadata().getExecutionTime());
		verify(bookRepositoryPort).findByKeyword("TDD", pageable);
	}

	@Test
	@DisplayName("공백이 포함된 키워드 검색 시 trim 처리")
	void searchBooks_TrimKeywords() {
		// given
		String query = " Go | JavaScript ";
		int page = 1;
		int size = 10;
		Pageable pageable = PageRequest.of(0, 10);

		Page<Book> bookPage = new PageImpl<>(List.of(), pageable, 0);
		SearchResult<Book> searchResult = SearchResult.from(bookPage, 10L, SearchMetadata.Strategy.OR_OPERATION);

		given(bookRepositoryPort.findByOrKeywords("Go", "JavaScript", pageable))
			.willReturn(searchResult);

		// when
		bookSearchService.searchBooks(query, page, size);

		// then
		verify(bookRepositoryPort).findByOrKeywords("Go", "JavaScript", pageable);
	}

	@Test
	@DisplayName("페이지 번호가 올바르게 변환됨 (1-based to 0-based)")
	void searchBooks_PageConversion() {
		// given
		String query = "test";
		int page = 3;
		int size = 20;
		Pageable expectedPageable = PageRequest.of(2, 20); // page-1

		Page<Book> bookPage = new PageImpl<>(List.of(), expectedPageable, 0);
		SearchResult<Book> searchResult = SearchResult.from(bookPage, 10L, SearchMetadata.Strategy.SIMPLE_SEARCH);

		given(bookRepositoryPort.findByKeyword("test", expectedPageable))
			.willReturn(searchResult);

		// when
		bookSearchService.searchBooks(query, page, size);

		// then
		verify(bookRepositoryPort).findByKeyword("test", expectedPageable);
	}
}