package com.bibliotecassa.trabalho.Services.Livros;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlockedBookRepository extends JpaRepository<BlockedBook, Long> {
    BlockedBook findByBookId(String bookId);
    boolean existsByBookId(String bookId);
}
