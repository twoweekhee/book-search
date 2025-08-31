package com.twoweekhee.booksearch.presentation.dto;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.twoweekhee.booksearch.entity.Book;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BookListResponse {
	private PageInfo pageInfo;
	private List<BookResponse> books;
}
