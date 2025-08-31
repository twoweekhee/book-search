package com.twoweekhee.booksearch.application.service;

import static com.twoweekhee.booksearch.common.exception.ResourceNotFoundException.ResourceNotFoundExceptionCode.*;
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
import com.twoweekhee.booksearch.common.exception.ResourceNotFoundException;
import com.twoweekhee.booksearch.entity.Book;
import com.twoweekhee.booksearch.presentation.dto.BookListResponse;
import com.twoweekhee.booksearch.presentation.dto.BookResponse;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

	@Mock
	private BookRepositoryPort bookRepositoryPort;

	@InjectMocks
	private BookService bookService;

	@Test
	@DisplayName("도서를 조회한다")
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

		given(bookRepositoryPort.getBook(bookId)).willReturn(book);

		// when
		BookResponse result = bookService.getBook(bookId);

		// then
		assertEquals(book.getId(), result.getId());
		assertEquals(book.getTitle(), result.getTitle());
		assertEquals(book.getAuthor(), result.getAuthor());
		assertEquals(book.getIsbn(), result.getIsbn());
		assertEquals(book.getPublisher(), result.getPublisher());
		assertEquals(book.getPublished(), result.getPublished());
	}

	@Test
	@DisplayName("존재하지 않는 도서 조회 시 예외가 발생한다")
	void getBook_NotFound() {
		// given
		Long nonExistentId = 999L;
		given(bookRepositoryPort.getBook(nonExistentId))
			.willThrow(new ResourceNotFoundException(BOOK_NOT_FOUND));

		// when & then
		ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
			() -> bookService.getBook(nonExistentId));

		assertEquals(BOOK_NOT_FOUND.getCode(), exception.getCode());
	}

	@Test
	@DisplayName("도서 목록을 페이징하여 조회한다")
	void getBooks_Success() {
		// given
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

		Page<Book> bookPage = new PageImpl<>(List.of(book), pageable, 100L);

		given(bookRepositoryPort.findAll(pageable)).willReturn(bookPage);

		// when
		BookListResponse result = bookService.getBooks(page, size);

		// then
		assertNotNull(result);
		assertEquals(1, result.getBooks().size());
		assertEquals(1L, result.getBooks().get(0).getId());
		assertEquals("Everything about Go Lang", result.getBooks().get(0).getTitle());
		assertEquals(1, result.getPageInfo().getCurrentPage());
		assertEquals(20, result.getPageInfo().getPageSize());
		assertEquals(5, result.getPageInfo().getTotalPages());
		assertEquals(100L, result.getPageInfo().getTotalElements());

		verify(bookRepositoryPort).findAll(pageable);
	}

	@Test
	@DisplayName("빈 결과를 페이징하여 조회한다")
	void getBooks_EmptyResult() {
		// given
		int page = 1;
		int size = 20;
		Pageable pageable = PageRequest.of(0, 20);

		Page<Book> emptyPage = new PageImpl<>(List.of(), pageable, 0L);

		given(bookRepositoryPort.findAll(pageable)).willReturn(emptyPage);

		// when
		BookListResponse result = bookService.getBooks(page, size);

		// then
		assertNotNull(result);
		assertTrue(result.getBooks().isEmpty());
		assertEquals(1, result.getPageInfo().getCurrentPage());
		assertEquals(20, result.getPageInfo().getPageSize());
		assertEquals(0, result.getPageInfo().getTotalPages());
		assertEquals(0L, result.getPageInfo().getTotalElements());

		verify(bookRepositoryPort).findAll(pageable);
	}
}