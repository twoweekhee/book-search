package com.twoweekhee.booksearch.infrastructure.persistence;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.twoweekhee.booksearch.entity.KeywordLog;

public interface KeywordLogJpaRepository extends JpaRepository<KeywordLog, Long> {

	@Query("SELECT k.keyword as count FROM KeywordLog k " +
		"WHERE k.searchedAt >= :since " +
		"GROUP BY k.keyword " +
		"ORDER BY count DESC " +
		"LIMIT 10")
	List<String> findTop10KeywordsSince(@Param("since") LocalDateTime since);
}
