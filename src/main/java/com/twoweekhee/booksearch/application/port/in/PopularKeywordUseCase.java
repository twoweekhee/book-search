package com.twoweekhee.booksearch.application.port.in;

import com.twoweekhee.booksearch.presentation.dto.KeywordsResponse;

public interface PopularKeywordUseCase {
	KeywordsResponse getTop10Keywords();
}
