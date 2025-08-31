package com.twoweekhee.booksearch.presentation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.twoweekhee.booksearch.application.port.in.BookUseCase;
import com.twoweekhee.booksearch.presentation.dto.BookListResponse;
import com.twoweekhee.booksearch.presentation.dto.BookResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

	private final BookUseCase bookUseCase;

	@GetMapping
	public ResponseEntity<BookListResponse> getBookList(
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "20") int size) {

		BookListResponse response = bookUseCase.getBooks(page, size);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/{id}")
	public ResponseEntity<BookResponse> getBookDetail(@PathVariable Long id) {
		return ResponseEntity.ok(bookUseCase.getBook(id));
	}
}
