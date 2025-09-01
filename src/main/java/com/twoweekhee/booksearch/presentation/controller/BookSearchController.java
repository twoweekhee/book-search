package com.twoweekhee.booksearch.presentation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.twoweekhee.booksearch.application.port.in.BookSearchUseCase;
import com.twoweekhee.booksearch.presentation.dto.BookSearchResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/books/search")
@RequiredArgsConstructor
public class BookSearchController {

	private final BookSearchUseCase bookSearchUseCase;

	@GetMapping
	public ResponseEntity<BookSearchResponse> searchBooks(
		@RequestParam String keyword,
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "20") int size) {

		return ResponseEntity.ok(bookSearchUseCase.searchBooks(keyword, page, size));
	}
}
