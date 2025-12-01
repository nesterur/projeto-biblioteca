package com.bibliotecassa.trabalho.Services.Livros;
// arquivo BlockedBookRepository.java
// finalidade classe BlockedBookRepository comentarios automatizados

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
// definicao de interface nome BlockedBookRepository
public interface BlockedBookRepository extends JpaRepository<BlockedBook, Long> {
    BlockedBook findByBookId(String bookId);
    boolean existsByBookId(String bookId);
}

