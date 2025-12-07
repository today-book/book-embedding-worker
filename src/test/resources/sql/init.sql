CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE SCHEMA IF NOT EXISTS book;

CREATE TABLE IF NOT EXISTS book.p_books
(
    id              UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    isbn            VARCHAR(20) NOT NULL,
    title           VARCHAR(255) NOT NULL,
    categories      TEXT[],
    description     TEXT NOT NULL,
    author          VARCHAR(255) NOT NULL,
    publisher       VARCHAR(255),
    published_at    TIMESTAMP,
    thumbnail       TEXT,
    created_at      TIMESTAMP DEFAULT NOW(),
    updated_at      TIMESTAMP DEFAULT NOW()
    );

CREATE UNIQUE INDEX IF NOT EXISTS idx_p_books_isbn ON book.p_books(isbn);