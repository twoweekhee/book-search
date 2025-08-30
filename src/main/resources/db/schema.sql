-- Book 테이블 생성
CREATE TABLE IF NOT EXISTS book (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(500) NOT NULL,
    subtitle VARCHAR(500),
    author VARCHAR(200) NOT NULL,
    isbn VARCHAR(20) NOT NULL,
    publisher VARCHAR(200),
    published DATE
    );

-- ISBN 컬럼에 UNIQUE 제약조건 추가
ALTER TABLE book ADD CONSTRAINT uk_book_isbn UNIQUE (isbn);

