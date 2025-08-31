package com.twoweekhee.booksearch.presentation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.twoweekhee.booksearch.application.port.in.BookUseCase;
import com.twoweekhee.booksearch.presentation.dto.BookResponse;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

	private final BookUseCase bookUseCase;

	@GetMapping("/{id}")
	public ResponseEntity<BookResponse> getBookDetail(@PathVariable Long id) {
		return ResponseEntity.ok(bookUseCase.getBook(id));
	}
}
