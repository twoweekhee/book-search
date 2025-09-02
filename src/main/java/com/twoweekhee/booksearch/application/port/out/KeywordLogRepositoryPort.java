package com.twoweekhee.booksearch.application.port.out;

import java.time.LocalDateTime;
import java.util.List;

public interface KeywordLogRepositoryPort {
	List<String> getTop10Keywords(LocalDateTime since);
	void saveKeyword(String keyword);
}
