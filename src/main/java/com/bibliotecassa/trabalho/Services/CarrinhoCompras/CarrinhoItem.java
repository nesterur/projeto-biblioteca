
package com.bibliotecassa.trabalho.Services.CarrinhoCompras;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "carrinho_item")
public class CarrinhoItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cpf_usuario", nullable = false)
    private String usuarioId;

    @Column(nullable = false)
    private String livroId;

    @Column(name = "nome_livro", nullable = false)
    private String nomeLivro;
    
    @Column(name = "capa_url", length = 2000)
    private String capaUrl;

    @Column(name = "preco")
    private BigDecimal preco;

    @Column(name = "moeda", length = 10)
    private String moeda;

    @Column(nullable = false)
    private LocalDateTime dataAdicao;

    @Column(nullable = false)
    private String generoLivro;

    // nota entidade carrinho_item
    // armazena snapshot do livro quando adicionado ao carrinho
    // inclui preco e moeda para manter historico de preco

    public CarrinhoItem() {}

    public CarrinhoItem(String usuarioId, String livroId, String nomeLivro, String capaUrl, LocalDateTime dataAdicao, String generoLivro) {
        this.usuarioId = usuarioId;
        this.livroId = livroId;
        this.nomeLivro = nomeLivro;
        this.capaUrl = capaUrl;
        this.dataAdicao = dataAdicao;
        this.generoLivro = generoLivro;
    }

    // new constructor including price
    public CarrinhoItem(String usuarioId, String livroId, String nomeLivro, String capaUrl, LocalDateTime dataAdicao, String generoLivro, BigDecimal preco, String moeda) {
        this.usuarioId = usuarioId;
        this.livroId = livroId;
        this.nomeLivro = nomeLivro;
        this.capaUrl = capaUrl;
        this.dataAdicao = dataAdicao;
        this.generoLivro = generoLivro;
        this.preco = preco;
        this.moeda = moeda;
    }

    public Long getId() { return id; }
    public String getUsuarioId() { return usuarioId; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }
    public String getLivroId() { return livroId; }
    public void setLivroId(String livroId) { this.livroId = livroId; }
    public String getNomeLivro() { return nomeLivro; }
    public void setNomeLivro(String nomeLivro) { this.nomeLivro = nomeLivro; }
    public String getCapaUrl() { return capaUrl; }
    public void setCapaUrl(String capaUrl) { this.capaUrl = capaUrl; }
    public LocalDateTime getDataAdicao() { return dataAdicao; }
    public void setDataAdicao(LocalDateTime dataAdicao) { this.dataAdicao = dataAdicao; }
    public String getGeneroLivro() { return generoLivro; }
    public void setGeneroLivro(String generoLivro) { this.generoLivro = generoLivro; }
    public java.math.BigDecimal getPreco() { return preco; }
    public void setPreco(java.math.BigDecimal preco) { this.preco = preco; }
    public String getMoeda() { return moeda; }
    public void setMoeda(String moeda) { this.moeda = moeda; }
}
