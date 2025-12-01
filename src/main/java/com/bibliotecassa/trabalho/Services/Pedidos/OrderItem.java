package com.bibliotecassa.trabalho.Services.Pedidos;
// arquivo OrderItem.java
// finalidade classe OrderItem comentarios automatizados

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_item")
// definicao de class nome OrderItem
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "livro_id")
    private String livroId;

    @Column(name = "nome_livro")
    private String nomeLivro;

    @Column(name = "preco", precision = 12, scale = 2)
    private BigDecimal preco;

    @Column(name = "rental_days")
    private Integer rentalDays;

    @Column(name = "rental_start")
    private LocalDateTime rentalStart;

    @Column(name = "rental_end")
    private LocalDateTime rentalEnd;

    @Column(name = "rental_price", precision = 12, scale = 2)
    private BigDecimal rentalPrice;

    @Column(name = "quantidade")
    private Integer quantidade = 1;

    
    


    public OrderItem() {}

    public Long getId() { return id; }

    public Order getOrder() { return order; }

    public void setOrder(Order order) { this.order = order; }

    public String getLivroId() { return livroId; }

    public void setLivroId(String livroId) { this.livroId = livroId; }

    public String getNomeLivro() { return nomeLivro; }

    public void setNomeLivro(String nomeLivro) { this.nomeLivro = nomeLivro; }

    public BigDecimal getPreco() { return preco; }

    public void setPreco(BigDecimal preco) { this.preco = preco; }

    public Integer getRentalDays() { return rentalDays; }

    public void setRentalDays(Integer rentalDays) { this.rentalDays = rentalDays; }

    public LocalDateTime getRentalStart() { return rentalStart; }

    public void setRentalStart(LocalDateTime rentalStart) { this.rentalStart = rentalStart; }

    public LocalDateTime getRentalEnd() { return rentalEnd; }

    public void setRentalEnd(LocalDateTime rentalEnd) { this.rentalEnd = rentalEnd; }

    public BigDecimal getRentalPrice() { return rentalPrice; }

    public void setRentalPrice(BigDecimal rentalPrice) { this.rentalPrice = rentalPrice; }

    public Integer getQuantidade() { return quantidade; }

    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
}




