package com.bibliotecassa.trabalho.Services.Pedidos;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private String usuarioId;

    @Column(name = "total", precision = 12, scale = 2)
    private BigDecimal total;

    @Column(name = "currency", length = 10)
    private String currency;

    @Column(name = "status", length = 32)
    private String status;

    @Column(name = "metodo_pagamento", length = 32)
    private String metodoPagamento;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // nota entidade orders
    // representa pedido de aluguel snapshot do total e metadados do pagamento

    public Order() {}

    public Long getId() { return id; }
    public String getUsuarioId() { return usuarioId; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMetodoPagamento() { return metodoPagamento; }
    public void setMetodoPagamento(String metodoPagamento) { this.metodoPagamento = metodoPagamento; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
