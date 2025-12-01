package com.bibliotecassa.trabalho.Services.Livros;
// arquivo BlockedBook.java
// finalidade classe BlockedBook comentarios automatizados

import jakarta.persistence.*;

@Entity
@Table(name = "blocked_book", uniqueConstraints = {@UniqueConstraint(columnNames = {"book_id"})})
// definicao de class nome BlockedBook
public class BlockedBook {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "book_id", nullable = false, unique = true)
    private String bookId;


    public BlockedBook() {}

    public BlockedBook(String bookId) { this.bookId = bookId; }


    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getBookId() { return bookId; }

    public void setBookId(String bookId) { this.bookId = bookId; }
}



