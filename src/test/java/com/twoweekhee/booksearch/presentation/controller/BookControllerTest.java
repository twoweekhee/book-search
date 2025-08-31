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
import com.twoweekhee.booksearch.presentation.dto.BookResponse;

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
}