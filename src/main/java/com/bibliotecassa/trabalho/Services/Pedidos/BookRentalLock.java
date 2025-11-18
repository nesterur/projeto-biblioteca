package com.bibliotecassa.trabalho.Services.Pedidos;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "book_rental_lock")
public class BookRentalLock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "livro_id", nullable = false)
    private String livroId;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "usuario_id")
    private String usuarioId;

    @Column(name = "rented_until")
    private LocalDateTime rentedUntil;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public BookRentalLock() {}
    public Long getId() { return id; }
    public String getLivroId() { return livroId; }
    public void setLivroId(String livroId) { this.livroId = livroId; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public String getUsuarioId() { return usuarioId; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }
    public LocalDateTime getRentedUntil() { return rentedUntil; }
    public void setRentedUntil(LocalDateTime rentedUntil) { this.rentedUntil = rentedUntil; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
