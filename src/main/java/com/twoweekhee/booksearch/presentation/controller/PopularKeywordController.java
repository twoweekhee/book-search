package com.twoweekhee.booksearch.presentation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.twoweekhee.booksearch.application.port.in.PopularKeywordUseCase;
import com.twoweekhee.booksearch.presentation.dto.KeywordsResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/popular-keyword")
@RequiredArgsConstructor
public class PopularKeywordController {

	private final PopularKeywordUseCase popularKeywordUseCase;

	@GetMapping
	public ResponseEntity<KeywordsResponse> getTop10Keywords() {

		return ResponseEntity.ok(popularKeywordUseCase.getTop10Keywords());
	}
}
