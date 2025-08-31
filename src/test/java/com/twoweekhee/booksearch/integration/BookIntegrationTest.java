package com.twoweekhee.booksearch.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import com.twoweekhee.booksearch.entity.Book;
import com.twoweekhee.booksearch.infrastructure.persistence.BookJpaRepository;
import com.twoweekhee.booksearch.presentation.dto.BookListResponse;
import com.twoweekhee.booksearch.presentation.dto.BookResponse;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private BookJpaRepository bookJpaRepository;

    @Test
    @DisplayName("도서 상세 조회 통합테스트 - 성공")
    void getBookDetail_Success() {

        // setup
        Book book = Book.builder()
            .title("Everything about Go Lang")
            .subtitle("version 3.0")
            .author("twoweekhee")
            .isbn("9781617291609")
            .publisher("Lees House")
            .published(LocalDate.of(2016, 3, 1))
            .build();

        Book savedBook = bookJpaRepository.save(book);

        // given & when
        ResponseEntity<BookResponse> response = restTemplate.getForEntity(
            "/api/books/{id}", BookResponse.class, savedBook.getId());

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(savedBook.getId(), response.getBody().getId());
        assertEquals("Everything about Go Lang", response.getBody().getTitle());
        assertEquals("version 3.0", response.getBody().getSubtitle());
        assertEquals("twoweekhee", response.getBody().getAuthor());
        assertEquals("9781617291609", response.getBody().getIsbn());
        assertEquals("Lees House", response.getBody().getPublisher());
        assertEquals(LocalDate.of(2016, 3, 1), response.getBody().getPublished());

        // cleanup
        bookJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("존재하지 않는 도서 ID로 조회 시 404를 반환한다 (통합 테스트)")
    void getBookDetail_NotFound() {
        // given
        Long nonExistentId = 999L;

        // when
        ResponseEntity<String> response = restTemplate.getForEntity(
            "/api/books/{id}", String.class, nonExistentId);

        // then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("도서 목록 조회 통합테스트 - 성공")
    void getBookList_Success() {
        // setup
        Book book1 = Book.builder()
            .title("Everything about Go Lang")
            .subtitle("version 3.0")
            .author("twoweekhee")
            .isbn("9781617291609")
            .publisher("Lees House")
            .published(LocalDate.of(2016, 3, 1))
            .build();

        Book book2 = Book.builder()
            .title("Java Spring Boot")
            .subtitle("Advanced Guide")
            .author("developer")
            .isbn("9781234567890")
            .publisher("Tech House")
            .published(LocalDate.of(2023, 5, 15))
            .build();

        bookJpaRepository.save(book1);
        bookJpaRepository.save(book2);

        // given & when
        ResponseEntity<BookListResponse> response = restTemplate.getForEntity(
            "/api/books?page=1&size=10", BookListResponse.class);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getPageInfo());
        assertNotNull(response.getBody().getBooks());
        assertEquals(1, response.getBody().getPageInfo().getCurrentPage());
        assertEquals(10, response.getBody().getPageInfo().getPageSize());
        assertTrue(response.getBody().getBooks().size() >= 2);

        // cleanup
        bookJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("페이지네이션 파라미터 기본값 적용 테스트")
    void getBookList_DefaultParameters() {
        // setup
        Book book = Book.builder()
            .title("Everything about Go Lang")
            .subtitle("version 3.0")
            .author("twoweekhee")
            .isbn("9781617291609")
            .publisher("Lees House")
            .published(LocalDate.of(2016, 3, 1))
            .build();

        bookJpaRepository.save(book);

        // given & when
        ResponseEntity<BookListResponse> response = restTemplate.getForEntity(
            "/api/books", BookListResponse.class);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getPageInfo().getCurrentPage());
        assertEquals(20, response.getBody().getPageInfo().getPageSize());

        // cleanup
        bookJpaRepository.deleteAll();
    }
}
