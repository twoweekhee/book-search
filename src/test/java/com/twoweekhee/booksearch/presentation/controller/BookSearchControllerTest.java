package com.twoweekhee.booksearch.presentation.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.twoweekhee.booksearch.application.port.in.BookSearchUseCase;
import com.twoweekhee.booksearch.presentation.dto.BookResponse;
import com.twoweekhee.booksearch.presentation.dto.BookSearchResponse;
import com.twoweekhee.booksearch.presentation.dto.PageInfo;
import com.twoweekhee.booksearch.presentation.dto.SearchMetadata;

@AutoConfigureMockMvc
@AutoConfigureRestDocs
@WebMvcTest(BookSearchController.class)
class BookSearchControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private BookSearchUseCase bookSearchUseCase;

	@Test
	@DisplayName("키워드로 도서를 검색한다")
	void searchBooks_Success() throws Exception {
		// given
		String keyword = "tdd";
		int page = 1;
		int size = 20;

		BookResponse bookResponse = BookResponse.builder()
			.id(1L)
			.title("Everything about Go Lang")
			.subtitle("version 3.0")
			.author("twoweekhee")
			.isbn("9781617291609")
			.publisher("Lees House")
			.published(LocalDate.of(2016, 3, 1))
			.build();

		PageInfo pageInfo = PageInfo.builder()
			.currentPage(1)
			.pageSize(20)
			.totalPages(5)
			.totalElements(100L)
			.build();

		SearchMetadata searchMetadata = SearchMetadata.builder()
			.executionTime(23L)
			.strategy(SearchMetadata.Strategy.SIMPLE_SEARCH)
			.build();

		BookSearchResponse searchResponse = BookSearchResponse.builder()
			.searchQuery(keyword)
			.pageInfo(pageInfo)
			.books(List.of(bookResponse))
			.searchMetadata(searchMetadata)
			.build();

		given(bookSearchUseCase.searchBooks(keyword, page, size)).willReturn(searchResponse);

		// when & then
		mockMvc.perform(get("/api/books/search")
				.param("keyword", keyword)
				.param("page", String.valueOf(page))
				.param("size", String.valueOf(size))
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.searchQuery").value("tdd"))
			.andExpect(jsonPath("$.pageInfo.currentPage").value(1))
			.andExpect(jsonPath("$.pageInfo.pageSize").value(20))
			.andExpect(jsonPath("$.pageInfo.totalPages").value(5))
			.andExpect(jsonPath("$.pageInfo.totalElements").value(100))
			.andExpect(jsonPath("$.books[0].id").value(1L))
			.andExpect(jsonPath("$.books[0].title").value("Everything about Go Lang"))
			.andExpect(jsonPath("$.books[0].subtitle").value("version 3.0"))
			.andExpect(jsonPath("$.books[0].author").value("twoweekhee"))
			.andExpect(jsonPath("$.books[0].isbn").value("9781617291609"))
			.andExpect(jsonPath("$.books[0].publisher").value("Lees House"))
			.andExpect(jsonPath("$.books[0].published").value("2016-03-01"))
			.andExpect(jsonPath("$.searchMetadata.executionTime").value(23))
			.andExpect(jsonPath("$.searchMetadata.strategy").value("SIMPLE_SEARCH"))
			.andDo(document("book-search",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				queryParameters(
					parameterWithName("keyword").description("검색 키워드"),
					parameterWithName("page").description("페이지 번호 (기본값: 1)").optional(),
					parameterWithName("size").description("페이지 크기 (기본값: 20)").optional()
				),
				responseFields(
					fieldWithPath("searchQuery").description("검색 쿼리"),
					fieldWithPath("pageInfo.currentPage").description("현재 페이지"),
					fieldWithPath("pageInfo.pageSize").description("페이지 크기"),
					fieldWithPath("pageInfo.totalPages").description("전체 페이지 수"),
					fieldWithPath("pageInfo.totalElements").description("전체 검색 결과 수"),
					fieldWithPath("books[].id").description("도서 식별자"),
					fieldWithPath("books[].title").description("도서 제목"),
					fieldWithPath("books[].subtitle").description("도서 부제목").optional(),
					fieldWithPath("books[].author").description("저자"),
					fieldWithPath("books[].isbn").description("ISBN"),
					fieldWithPath("books[].publisher").description("출판사").optional(),
					fieldWithPath("books[].published").description("출판일").optional(),
					fieldWithPath("searchMetadata.executionTime").description("검색 실행 시간 (ms)"),
					fieldWithPath("searchMetadata.strategy").description("검색 전략")
				)
			));
	}

	@Test
	@DisplayName("키워드로 도서를 검색한다 - page 기본값으로")
	void searchBooks_WithDefaults() throws Exception {
		// given
		String keyword = "java";

		BookResponse bookResponse = BookResponse.builder()
			.id(1L)
			.title("Everything about Go Lang")
			.subtitle("version 3.0")
			.author("twoweekhee")
			.isbn("9781617291609")
			.publisher("Lees House")
			.published(LocalDate.of(2016, 3, 1))
			.build();

		PageInfo pageInfo = PageInfo.builder()
			.currentPage(1)
			.pageSize(20)
			.totalPages(1)
			.totalElements(1L)
			.build();

		SearchMetadata searchMetadata = SearchMetadata.builder()
			.executionTime(12L)
			.strategy(SearchMetadata.Strategy.SIMPLE_SEARCH)
			.build();

		BookSearchResponse searchResponse = BookSearchResponse.builder()
			.searchQuery(keyword)
			.pageInfo(pageInfo)
			.books(List.of(bookResponse))
			.searchMetadata(searchMetadata)
			.build();

		given(bookSearchUseCase.searchBooks(keyword, 1, 20)).willReturn(searchResponse);

		// when & then
		mockMvc.perform(get("/api/books/search")
				.param("keyword", keyword)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.pageInfo.currentPage").value(1))
			.andExpect(jsonPath("$.pageInfo.pageSize").value(20));

		verify(bookSearchUseCase).searchBooks(keyword, 1, 20);
	}
}