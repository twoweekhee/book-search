package com.twoweekhee.booksearch.presentation.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

import com.twoweekhee.booksearch.application.port.in.PopularKeywordUseCase;
import com.twoweekhee.booksearch.presentation.dto.KeywordsResponse;

@AutoConfigureMockMvc
@AutoConfigureRestDocs
@WebMvcTest(PopularKeywordController.class)
class PopularKeywordControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private PopularKeywordUseCase popularKeywordUseCase;

	@Test
	@DisplayName("인기 검색어 TOP 10을 조회한다")
	void getTop10Keywords_Success() throws Exception {
		// given
		KeywordsResponse keywordsResponse = KeywordsResponse.builder()
			.keywords(List.of("Java", "Spring", "TDD", "Go", "JavaScript", "Python", "React", "Node", "Docker", "Kubernetes"))
			.build();

		given(popularKeywordUseCase.getTop10Keywords()).willReturn(keywordsResponse);

		// when & then
		mockMvc.perform(get("/api/popular-keyword")
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.keywords").isArray())
			.andExpect(jsonPath("$.keywords.length()").value(10))
			.andExpect(jsonPath("$.keywords[0]").value("Java"))
			.andExpect(jsonPath("$.keywords[1]").value("Spring"))
			.andExpect(jsonPath("$.keywords[2]").value("TDD"))
			.andDo(document("popular-keywords-get",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				responseFields(
					fieldWithPath("keywords").description("인기 검색어 TOP 10 목록")
				)
			));
	}
}