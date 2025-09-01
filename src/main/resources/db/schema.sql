-- Book 테이블 생성
CREATE TABLE IF NOT EXISTS book (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(500) NOT NULL,
    subtitle VARCHAR(500),
    author VARCHAR(200) NOT NULL,
    isbn VARCHAR(20) NOT NULL,
    publisher VARCHAR(200),
    published DATE,

    -- Full Text Search 컬럼
    document tsvector
    );

-- ISBN 컬럼에 UNIQUE 제약조건 추가
ALTER TABLE book ADD CONSTRAINT uk_book_isbn UNIQUE (isbn);

-- 기존 데이터 업데이트 (FTS용 document 컬럼 채우기)
UPDATE book
SET document =
        setweight(to_tsvector('simple', coalesce(title, '')), 'A') ||
        setweight(to_tsvector('simple', coalesce(subtitle, '')), 'B') ||
        setweight(to_tsvector('simple', coalesce(author, '')), 'C') ||
        setweight(to_tsvector('simple', coalesce(publisher, '')), 'D');

-- GIN 인덱스 생성 (FTS 전용)
CREATE INDEX IF NOT EXISTS book_document_idx ON book USING GIN (document);

-- 자동 업데이트 트리거 (옵션: insert/update 시 document 갱신)
CREATE FUNCTION book_tsvector_trigger() RETURNS trigger AS $$
begin
  new.document :=
    setweight(to_tsvector('simple', coalesce(new.title, '')), 'A') ||
    setweight(to_tsvector('simple', coalesce(new.subtitle, '')), 'B') ||
    setweight(to_tsvector('simple', coalesce(new.author, '')), 'C') ||
    setweight(to_tsvector('simple', coalesce(new.publisher, '')), 'D');
return new;
end
$$ LANGUAGE plpgsql;

CREATE TRIGGER book_tsvector_update
    BEFORE INSERT OR UPDATE ON book
                         FOR EACH ROW EXECUTE FUNCTION book_tsvector_trigger();
