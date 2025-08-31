package com.twoweekhee.booksearch.application.service;

import static com.twoweekhee.booksearch.common.exception.ResourceNotFoundException.ResourceNotFoundExceptionCode.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.twoweekhee.booksearch.application.port.out.BookRepositoryPort;
import com.twoweekhee.booksearch.common.exception.ResourceNotFoundException;
import com.twoweekhee.booksearch.entity.Book;
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
}