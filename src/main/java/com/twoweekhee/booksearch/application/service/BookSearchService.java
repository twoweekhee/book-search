package com.twoweekhee.booksearch.application.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.twoweekhee.booksearch.application.dto.SearchResult;
import com.twoweekhee.booksearch.application.port.in.BookSearchUseCase;
import com.twoweekhee.booksearch.application.port.out.BookRepositoryPort;
import com.twoweekhee.booksearch.application.port.out.KeywordLogRepositoryPort;
import com.twoweekhee.booksearch.entity.Book;
import com.twoweekhee.booksearch.presentation.dto.BookSearchResponse;
import com.twoweekhee.booksearch.presentation.dto.PageInfo;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class BookSearchService implements BookSearchUseCase {

	private final BookRepositoryPort bookRepositoryPort;
	private final KeywordLogRepositoryPort keywordLogRepository;

	@Override
	public BookSearchResponse searchBooks(String query, int page, int size) {

		Pageable pageable = PageRequest.of(page - 1, size);
		SearchResult<Book> searchResult;

		if (query.contains("|")) {
			String[] keywords = query.split("\\|");
			String keyword1 = keywords[0].trim();
			String keyword2 = keywords[1].trim();

			keywordLogRepository.saveKeyword(keyword1);
			keywordLogRepository.saveKeyword(keyword2);

			searchResult = bookRepositoryPort.findByOrKeywords(keyword1, keyword2, pageable);
		} else if (query.contains("-")) {
			String[] keywords = query.split("-");
			String keyword1 = keywords[0].trim();

			keywordLogRepository.saveKeyword(keyword1);

			searchResult = bookRepositoryPort.findByNotKeywords(keyword1, keywords[1].trim(), pageable);
		} else {
			keywordLogRepository.saveKeyword(query.trim());
			searchResult = bookRepositoryPort.findByKeyword(query, pageable);
		}

		PageInfo pageInfo = PageInfo.from(searchResult.getPage(), page, size);
		return BookSearchResponse.from(query, searchResult.getPage(), pageInfo, searchResult.getStrategy(), searchResult.getExecutionTimeMs());
	}
}
