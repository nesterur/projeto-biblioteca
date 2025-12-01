package com.bibliotecassa.trabalho.Services.Pedidos;
// arquivo Order.java
// finalidade classe Order comentarios automatizados

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
// definicao de class nome Order
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

    
    

/**
 * Executa a operaÃ§Ã£o relacionada Ã  classe.
 */
    public Order() {}

/**
 * Executa a operaÃ§Ã£o relacionada Ã  classe.
 */
    public Long getId() { return id; }
/**
 * Executa a operaÃ§Ã£o relacionada Ã  classe.
 */
    public String getUsuarioId() { return usuarioId; }
/**
 * Executa a operaÃ§Ã£o relacionada Ã  classe.
 */
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }
/**
 * Executa a operaÃ§Ã£o relacionada Ã  classe.
 */
    public BigDecimal getTotal() { return total; }
/**
 * Executa a operaÃ§Ã£o relacionada Ã  classe.
 */
    public void setTotal(BigDecimal total) { this.total = total; }
/**
 * Executa a operaÃ§Ã£o relacionada Ã  classe.
 */
    public String getCurrency() { return currency; }
/**
 * Executa a operaÃ§Ã£o relacionada Ã  classe.
 */
    public void setCurrency(String currency) { this.currency = currency; }
/**
 * Executa a operaÃ§Ã£o relacionada Ã  classe.
 */
    public String getStatus() { return status; }
/**
 * Executa a operaÃ§Ã£o relacionada Ã  classe.
 */
    public void setStatus(String status) { this.status = status; }
/**
 * Executa a operaÃ§Ã£o relacionada Ã  classe.
 */
    public String getMetodoPagamento() { return metodoPagamento; }
/**
 * Executa a operaÃ§Ã£o relacionada Ã  classe.
 */
    public void setMetodoPagamento(String metodoPagamento) { this.metodoPagamento = metodoPagamento; }
/**
 * Executa a operaÃ§Ã£o relacionada Ã  classe.
 */
    public LocalDateTime getCreatedAt() { return createdAt; }
/**
 * Executa a operaÃ§Ã£o relacionada Ã  classe.
 */
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
/**
 * Executa a operaÃ§Ã£o relacionada Ã  classe.
 */
    public LocalDateTime getUpdatedAt() { return updatedAt; }
/**
 * Executa a operaÃ§Ã£o relacionada Ã  classe.
 */
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}




