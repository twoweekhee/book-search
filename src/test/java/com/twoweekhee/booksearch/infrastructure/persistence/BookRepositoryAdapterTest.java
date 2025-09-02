package com.twoweekhee.booksearch.infrastructure.persistence;

import static com.twoweekhee.booksearch.common.exception.ResourceNotFoundException.ResourceNotFoundExceptionCode.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
import com.twoweekhee.booksearch.common.exception.ResourceNotFoundException;
import com.twoweekhee.booksearch.entity.Book;
import com.twoweekhee.booksearch.presentation.dto.SearchMetadata;

@ExtendWith(MockitoExtension.class)
class BookRepositoryAdapterTest {

	@Mock
	private BookJpaRepository bookJpaRepository;

	@InjectMocks
	private BookRepositoryAdapter bookRepositoryAdapter;

	@Test
	@DisplayName("ID로 도서를 조회한다")
	void getBook_Success() {
		// given
		Long bookId = 1L;
		Book book = Book.builder()
			.id(1L)
			.title("Everything about Go Lang")
			.subtitle("version 3.0")
			.author("twoweekhee")
			.isbn("9781617291609")
			.publisher("Lees House")
			.published(LocalDate.of(2016, 3, 1))
			.build();

		given(bookJpaRepository.findById(bookId)).willReturn(Optional.of(book));

		// when
		Book result = bookRepositoryAdapter.getBook(bookId);

		// then
		assertEquals(book.getId(), result.getId());
		assertEquals(book.getTitle(), result.getTitle());
		assertEquals(book.getAuthor(), result.getAuthor());
		assertEquals(book.getIsbn(), result.getIsbn());
		assertEquals(book.getPublisher(), result.getPublisher());
		assertEquals(book.getPublished(), result.getPublished());
	}

	@Test
	@DisplayName("존재하지 않는 ID로 조회 시 예외가 발생한다")
	void getBook_NotFound() {
		// given
		Long nonExistentId = 999L;
		given(bookJpaRepository.findById(nonExistentId)).willReturn(Optional.empty());

		// when & then
		ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
			() -> bookRepositoryAdapter.getBook(nonExistentId));

		assertEquals(BOOK_NOT_FOUND.getCode(), exception.getCode());
		assertEquals(BOOK_NOT_FOUND.getMessage(), exception.getMessage());
	}

	@Test
	@DisplayName("모든 도서를 페이징하여 조회한다")
	void findAll_Success() {
		// given
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

		Page<Book> expectedPage = new PageImpl<>(List.of(book), pageable, 1L);

		given(bookJpaRepository.findAll(pageable)).willReturn(expectedPage);

		// when
		Page<Book> result = bookRepositoryAdapter.findAll(pageable);

		// then
		assertNotNull(result);
		assertEquals(1, result.getContent().size());
		assertEquals(book, result.getContent().get(0));
		assertEquals(0, result.getNumber());
		assertEquals(20, result.getSize());
		assertEquals(1L, result.getTotalElements());

		verify(bookJpaRepository).findAll(pageable);
	}
	@Test
	@DisplayName("OR 키워드 검색 테스트")
	void findByOrKeywords_Success() {
		// given
		String keyword1 = "Go";
		String keyword2 = "JavaScript";
		Pageable pageable = PageRequest.of(0, 10);

		Book book1 = Book.builder()
			.title("Everything about Go Lang")
			.subtitle("version 3.0")
			.author("twoweekhee")
			.isbn("9781617291609")
			.publisher("Lees House")
			.published(LocalDate.of(2016, 3, 1))
			.build();

		Book book2 = Book.builder()
			.title("JavaScript Programming")
			.subtitle("Advanced Concepts")
			.author("John Doe")
			.isbn("9781234567890")
			.publisher("Tech Press")
			.published(LocalDate.of(2020, 1, 1))
			.build();

		Page<Book> mockPage = new PageImpl<>(List.of(book1, book2), pageable, 2);
		given(bookJpaRepository.findByOrKeywords(keyword1, keyword2, pageable)).willReturn(mockPage);

		// when
		SearchResult<Book> result = bookRepositoryAdapter.findByOrKeywords(keyword1, keyword2, pageable);

		// then
		assertEquals(2, result.getPage().getContent().size());
		assertTrue(result.getExecutionTimeMs() >= 0);
		assertEquals(SearchMetadata.Strategy.OR_OPERATION, result.getStrategy());
		verify(bookJpaRepository).findByOrKeywords(keyword1, keyword2, pageable);
	}

	@Test
	@DisplayName("NOT 키워드 검색 테스트")
	void findByNotKeywords_Success() {
		// given
		String keyword1 = "Programming";
		String keyword2 = "JavaScript";
		Pageable pageable = PageRequest.of(0, 10);

		Page<Book> mockPage = new PageImpl<>(List.of(), pageable, 0);
		given(bookJpaRepository.findByNotKeywords(keyword1, keyword2, pageable)).willReturn(mockPage);

		// when
		SearchResult<Book> result = bookRepositoryAdapter.findByNotKeywords(keyword1, keyword2, pageable);

		// then
		assertEquals(0, result.getPage().getContent().size());
		assertTrue(result.getExecutionTimeMs() >= 0);
		assertEquals(SearchMetadata.Strategy.NOT_OPERATION, result.getStrategy());
		verify(bookJpaRepository).findByNotKeywords(keyword1, keyword2, pageable);
	}

	@Test
	@DisplayName("단순 키워드 검색 테스트")
	void findByKeyword_Success() {
		// given
		String keyword = "TDD";
		Pageable pageable = PageRequest.of(0, 10);

		Book book = Book.builder()
			.title("TDD in Practice")
			.subtitle("Test Driven Development")
			.author("Jane Smith")
			.isbn("9780987654321")
			.publisher("Dev Books")
			.published(LocalDate.of(2021, 5, 15))
			.build();

		Page<Book> mockPage = new PageImpl<>(List.of(book), pageable, 1);
		given(bookJpaRepository.findByKeyword(keyword, pageable)).willReturn(mockPage);

		// when
		SearchResult<Book> result = bookRepositoryAdapter.findByKeyword(keyword, pageable);

		// then
		assertEquals(1, result.getPage().getContent().size());
		assertEquals("TDD in Practice", result.getPage().getContent().get(0).getTitle());
		assertTrue(result.getExecutionTimeMs() >= 0);
		assertEquals(SearchMetadata.Strategy.SIMPLE_SEARCH, result.getStrategy());
		verify(bookJpaRepository).findByKeyword(keyword, pageable);
	}

	@Test
	@DisplayName("검색 결과가 없는 경우")
	void findByKeyword_NoResults() {
		// given
		String keyword = "NonExistent";
		Pageable pageable = PageRequest.of(0, 10);

		Page<Book> mockPage = new PageImpl<>(List.of(), pageable, 0);
		given(bookJpaRepository.findByKeyword(keyword, pageable)).willReturn(mockPage);

		// when
		SearchResult<Book> result = bookRepositoryAdapter.findByKeyword(keyword, pageable);

		// then
		assertEquals(0, result.getPage().getContent().size());
		assertTrue(result.getExecutionTimeMs() >= 0);
		assertEquals(SearchMetadata.Strategy.SIMPLE_SEARCH, result.getStrategy());
		verify(bookJpaRepository).findByKeyword(keyword, pageable);
	}
}