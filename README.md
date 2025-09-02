# 📚 BookSearch

> Spring Boot 기반의 도서 검색 API 서비스

## 🎯 프로젝트 개요

BookSearch는 Spring Boot를 활용하여 구현된 RESTful API 서비스입니다. <br>
도서 정보를 효율적으로 조회하고 페이징을 통해 대용량 데이터를 처리할 수 있는 기능을 제공합니다.

## 🛠️ 기술 스택

### Backend
- **Java 21 (LTS)** - 최신 LTS 버전으로 안정성과 성능 최적화
- **Spring Boot 3.x** - 강력한 자동 설정과 의존성 관리
- **JPA (Hibernate)** - 객체 관계 매핑을 통한 데이터베이스 추상화

### Database
- **PostgreSQL** - 고성능 오픈소스 관계형 데이터베이스

### Build & DevOps
- **Gradle** - 유연하고 강력한 빌드 도구
- **Docker & Docker Compose** - 컨테이너화된 개발 환경

### 기술 선택 이유

| 기술 | 선택 이유 |
|------|-----------|
| **Java 21** | • LTS 버전의 안정성<br/>• 가상 스레드(Project Loom) 지원<br/>• 최신 언어 기능 활용 |
| **Spring Boot 3.x** | • 풍부한 생태계와 커뮤니티<br/>• 자동 설정을 통한 개발 생산성 향상<br/>• 검증된 안정성 |
| **Gradle** | • 유연한 DSL 지원<br/>• 빠른 빌드 성능<br/>• 강력한 의존성 관리 |
| **JPA** | • 객체 지향적 데이터베이스 접근<br/>• 반복적인 SQL 코드 감소<br/>• 생산성 향상 |
| **PostgreSQL** | • 뛰어난 데이터 무결성<br/>• 고급 기능 지원<br/>• 확장성과 안정성 |
| **Docker** | • 일관된 개발 환경 보장<br/>• 배포 환경 예측 가능성 향상<br/>• 팀 협업 효율성 |

## 📡 API 명세

> url | http://localhost:8080/docs/index.html

### 도서 목록 조회
```http
GET /api/books
```

**Query Parameters:**
- `page` (optional): 페이지 번호 (기본값: 1)
- `size` (optional): 페이지당 항목 수 (기본값: 20)

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "title": "도서 제목",
      "subtitle": "부제목",
      "author": "저자명",
      "isbn": "978-89-xxx-xxxx-x",
      "publisher": "출판사",
      "published": "2024-01-15"
    }
  ],
  "pageable": {
    "page": 1,
    "size": 20
  },
  "totalElements": 100
}
```

### 도서 상세 조회
```http
GET /api/books/{id}
```

**Path Parameters:**
- `id`: 도서 식별자 (Long)

**Response:**
```json
{
  "id": 1,
  "title": "도서 제목",
  "subtitle": "부제목",
  "author": "저자명",
  "isbn": "978-89-xxx-xxxx-x",
  "publisher": "출판사",
  "published": "2024-01-15"
}
```

## 🗄️ 데이터 모델

### Book Entity

| 필드 | 타입 | 제약조건 | 설명 |
|------|------|----------|------|
| `id` | `Long` | Primary Key, Auto Increment | 도서 식별자 |
| `title` | `String` | Not Null, Max 200 | 도서 제목 |
| `subtitle` | `String` | Max 200 | 도서 부제목 |
| `author` | `String` | Not Null, Max 100 | 저자명 |
| `isbn` | `String` | Unique, Max 20 | 국제표준도서번호 |
| `publisher` | `String` | Max 100 | 출판사명 |
| `published` | `LocalDate` | | 출판일 |

## 🚀 빠른 시작

### 사전 요구사항
- ☕ Java 21 이상
- 🐳 Docker & Docker Compose
- 📦 Git

### 설치 및 실행

1. **저장소 클론**
   ```bash
   git clone <repository-url>
   cd booksearch
   ```

2. **데이터베이스와 어플리케이션 실행**
   ```bash
   # PostgreSQL 컨테이너 실행
   docker-compose up -d
   
   # 실행 확인
   docker-compose ps
   ```

## 📁 프로젝트 구조

```
src/
├── main/
│   ├── java/
│   │   └── com/example/booksearch/
│   │       ├── BookSearchApplication.java
│   │       ├── controller/
│   │       │   └── BookController.java
│   │       ├── service/
│   │       │   └── BookService.java
│   │       ├── repository/
│   │       │   └── BookRepository.java
│   │       ├── entity/
│   │       │   └── Book.java
│   │       └── dto/
│   │           ├── BookResponse.java
│   │           └── BookListResponse.java
│   └── resources/
│       ├── application.yml
│       └── db/migration/
│           └── V1__Create_book_table.sql
└── test/
    └── java/
        └── com/example/booksearch/
            ├── controller/
            │   └── BookControllerTest.java
            └── service/
                └── BookServiceTest.java
```

## 🏗️ 아키텍처 결정 사항

### 설계 철학
완벽한 DDD(Domain Driven Design)를 구현하기에는 오버 아키텍처라고 판단하여, 현재 구조로 구현했습니다.

**핵심 원칙:**
- **의존성 역전**: 외부 인프라가 내부 서비스를 알지 못해도 구현 가능하도록 설계
- **단순함과 실용성**: 과도한 복잡성을 피하고 유지보수하기 쉬운 구조 선택
- **확장 가능성**: 향후 요구사항 변화에 대응할 수 있는 유연한 설계

### 문제 해결 중 고민 과정

**고민했던 선택지:**
- 🔀 복합 쿼리와 키워드 검색을 별도 API로 분리
- 🎯 단일 통합 검색 API 제공 (최종 선택)

**통합 결정 이유:**

| 관점 | 분석 결과 |
|------|-----------|
| **기능적 동일성** | 복합쿼리와 키워드 검색의 본질적 로직이 동일 |
| **코드 중복 방지** | 분리 시 중복되는 코드가 과도하게 발생 |
| **프론트엔드 UX** | 동일한 request/response로 일관된 사용성 제공 |
| **유지보수성** | 검색 로직 수정 시 한 곳만 변경하면 되어 효율적 |
| **API 일관성** | '검색'이라는 단일 도메인 내에서 통합된 인터페이스 |


---

**📧 Contact**: [myjjoo4758@gmail.com](mailto:myjjoo4758@gmail.com)  
