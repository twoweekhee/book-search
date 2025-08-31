package com.twoweekhee.booksearch.presentation.controller;

import static com.twoweekhee.booksearch.common.exception.ResourceNotFoundException.ResourceNotFoundExceptionCode.*;
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

import com.twoweekhee.booksearch.application.port.in.BookUseCase;
import com.twoweekhee.booksearch.common.exception.ResourceNotFoundException;
import com.twoweekhee.booksearch.presentation.dto.BookListResponse;
import com.twoweekhee.booksearch.presentation.dto.BookResponse;
import com.twoweekhee.booksearch.presentation.dto.PageInfo;

@AutoConfigureMockMvc
@AutoConfigureRestDocs
@WebMvcTest(BookController.class)
class BookControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private BookUseCase bookUseCase;

	@Test
	@DisplayName("ID로 도서를 조회한다")
	void getBookDetail_Success() throws Exception {
		// given
		Long bookId = 1L;
		BookResponse bookResponse = BookResponse.builder()
			.id(1L)
			.title("Everything about Go Lang")
			.subtitle("version 3.0")
			.author("twoweekhee")
			.isbn("9781617291609")
			.publisher("Lees House")
			.published(LocalDate.of(2016, 3, 1))
			.build();

		given(bookUseCase.getBook(bookId)).willReturn(bookResponse);

		// when & then
		mockMvc.perform(get("/api/books/{id}", bookId)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(1L))
			.andExpect(jsonPath("$.title").value("Everything about Go Lang"))
			.andExpect(jsonPath("$.subtitle").value("version 3.0"))
			.andExpect(jsonPath("$.author").value("twoweekhee"))
			.andExpect(jsonPath("$.isbn").value("9781617291609"))
			.andExpect(jsonPath("$.publisher").value("Lees House"))
			.andExpect(jsonPath("$.published").value("2016-03-01"))
			.andDo(document("book-get",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				pathParameters(
					parameterWithName("id").description("조회할 도서의 ID")
				),
				responseFields(
					fieldWithPath("id").description("도서 식별자 (ISBN)"),
					fieldWithPath("title").description("도서 제목"),
					fieldWithPath("subtitle").description("도서 부제목").optional(),
					fieldWithPath("author").description("저자"),
					fieldWithPath("isbn").description("ISBN"),
					fieldWithPath("publisher").description("출판사").optional(),
					fieldWithPath("published").description("출판일").optional()
				)
			));
	}

	@Test
	@DisplayName("존재하지 않는 도서 ID로 조회 시 예외가 발생한다")
	void getBookDetail_NotFound() throws Exception {
		// given
		Long nonExistentId = 999L;
		given(bookUseCase.getBook(nonExistentId))
			.willThrow(new ResourceNotFoundException(BOOK_NOT_FOUND));

		// when & then
		mockMvc.perform(get("/api/books/{id}", nonExistentId)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value(BOOK_NOT_FOUND.getCode()))
			.andExpect(jsonPath("$.message").value(BOOK_NOT_FOUND.getMessage()))
			.andDo(document("book-not-found",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				pathParameters(
					parameterWithName("id").description("조회할 도서의 ID")
				),
				responseFields(
					fieldWithPath("code").description("에러 코드"),
					fieldWithPath("message").description("에러 메시지")
				)
			));
	}

	@Test
	@DisplayName("도서 목록을 조회한다")
	void getBookList_Success() throws Exception {
		// given
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

		BookListResponse response = BookListResponse.builder()
			.books(List.of(bookResponse))
			.pageInfo(pageInfo)
			.build();

		given(bookUseCase.getBooks(1, 20)).willReturn(response);

		// when & then
		mockMvc.perform(get("/api/books")
				.param("page", "1")
				.param("size", "20")
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.books").isArray())
			.andExpect(jsonPath("$.books[0].id").value(1L))
			.andExpect(jsonPath("$.books[0].title").value("Everything about Go Lang"))
			.andExpect(jsonPath("$.pageInfo.currentPage").value(1))
			.andExpect(jsonPath("$.pageInfo.pageSize").value(20))
			.andExpect(jsonPath("$.pageInfo.totalPages").value(5))
			.andExpect(jsonPath("$.pageInfo.totalElements").value(100))
			.andDo(document("book-list",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				queryParameters(
					parameterWithName("page").description("페이지 번호 (기본값: 1)").optional(),
					parameterWithName("size").description("페이지 크기 (기본값: 20)").optional()
				),
				responseFields(
					fieldWithPath("books").description("도서 목록"),
					fieldWithPath("books[].id").description("도서 식별자"),
					fieldWithPath("books[].title").description("도서 제목"),
					fieldWithPath("books[].subtitle").description("도서 부제목").optional(),
					fieldWithPath("books[].author").description("저자"),
					fieldWithPath("books[].isbn").description("ISBN"),
					fieldWithPath("books[].publisher").description("출판사").optional(),
					fieldWithPath("books[].published").description("출판일").optional(),
					fieldWithPath("pageInfo").description("페이징 정보"),
					fieldWithPath("pageInfo.currentPage").description("현재 페이지"),
					fieldWithPath("pageInfo.pageSize").description("페이지 크기"),
					fieldWithPath("pageInfo.totalPages").description("총 페이지 수"),
					fieldWithPath("pageInfo.totalElements").description("총 요소 수")
				)
			));
	}

	@Test
	@DisplayName("기본 파라미터로 도서 목록을 조회한다")
	void getBookList_DefaultParams() throws Exception {
		// given
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

		BookListResponse response = BookListResponse.builder()
			.books(List.of(bookResponse))
			.pageInfo(pageInfo)
			.build();

		given(bookUseCase.getBooks(1, 20)).willReturn(response);

		// when & then
		mockMvc.perform(get("/api/books")
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.books").isArray())
			.andExpect(jsonPath("$.pageInfo.currentPage").value(1))
			.andExpect(jsonPath("$.pageInfo.pageSize").value(20));
	}
}