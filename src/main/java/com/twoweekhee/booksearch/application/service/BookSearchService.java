package com.twoweekhee.booksearch.application.service;

import java.time.Duration;
import java.time.Instant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.twoweekhee.booksearch.application.dto.SearchResult;
import com.twoweekhee.booksearch.application.port.in.BookSearchUseCase;
import com.twoweekhee.booksearch.application.port.out.BookRepositoryPort;
import com.twoweekhee.booksearch.entity.Book;
import com.twoweekhee.booksearch.presentation.dto.BookSearchResponse;
import com.twoweekhee.booksearch.presentation.dto.PageInfo;
import com.twoweekhee.booksearch.presentation.dto.SearchMetadata;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookSearchService implements BookSearchUseCase {

	private final BookRepositoryPort bookRepositoryPort;

	@Override
	public BookSearchResponse searchBooks(String query, int page, int size) {

		Pageable pageable = PageRequest.of(page - 1, size);
		SearchResult<Book> searchResult;

		if (query.contains("|")) {
			String[] keywords = query.split("\\|");
			searchResult = bookRepositoryPort.findByOrKeywords(keywords[0].trim(), keywords[1].trim(), pageable);
		} else if (query.contains("-")) {
			String[] keywords = query.split("-");
			searchResult = bookRepositoryPort.findByNotKeywords(keywords[0].trim(), keywords[1].trim(), pageable);
		} else {
			searchResult = bookRepositoryPort.findByKeyword(query, pageable);
		}

		PageInfo pageInfo = PageInfo.from(searchResult.getPage(), page, size);

		return BookSearchResponse.from(query, searchResult.getPage(), pageInfo, searchResult.getStrategy(), searchResult.getExecutionTimeMs());
	}
}
