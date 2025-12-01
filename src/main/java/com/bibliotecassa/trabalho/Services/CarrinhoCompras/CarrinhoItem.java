
package com.bibliotecassa.trabalho.Services.CarrinhoCompras;
// arquivo CarrinhoItem.java
// finalidade classe CarrinhoItem comentarios automatizados

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "carrinho_item")
// definicao de class nome CarrinhoItem
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

    
    
    

/**
 * Executa a operaÃ§Ã£o relacionada Ã  classe.
 */
    public CarrinhoItem() {}

/**
 * Executa a operaÃ§Ã£o relacionada Ã  classe.
 */
    public CarrinhoItem(String usuarioId, String livroId, String nomeLivro, String capaUrl, LocalDateTime dataAdicao, String generoLivro) {
        this.usuarioId = usuarioId;
        this.livroId = livroId;
        this.nomeLivro = nomeLivro;
        this.capaUrl = capaUrl;
        this.dataAdicao = dataAdicao;
        this.generoLivro = generoLivro;
    }

    
/**
 * Executa a operaÃ§Ã£o relacionada Ã  classe.
 */
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
    public String getLivroId() { return livroId; }
/**
 * Executa a operaÃ§Ã£o relacionada Ã  classe.
 */
    public void setLivroId(String livroId) { this.livroId = livroId; }
/**
 * Executa a operaÃ§Ã£o relacionada Ã  classe.
 */
    public String getNomeLivro() { return nomeLivro; }
/**
 * Executa a operaÃ§Ã£o relacionada Ã  classe.
 */
    public void setNomeLivro(String nomeLivro) { this.nomeLivro = nomeLivro; }
/**
 * Executa a operaÃ§Ã£o relacionada Ã  classe.
 */
    public String getCapaUrl() { return capaUrl; }
/**
 * Executa a operaÃ§Ã£o relacionada Ã  classe.
 */
    public void setCapaUrl(String capaUrl) { this.capaUrl = capaUrl; }
/**
 * Executa a operaÃ§Ã£o relacionada Ã  classe.
 */
    public LocalDateTime getDataAdicao() { return dataAdicao; }
/**
 * Executa a operaÃ§Ã£o relacionada Ã  classe.
 */
    public void setDataAdicao(LocalDateTime dataAdicao) { this.dataAdicao = dataAdicao; }
/**
 * Executa a operaÃ§Ã£o relacionada Ã  classe.
 */
    public String getGeneroLivro() { return generoLivro; }
/**
 * Executa a operaÃ§Ã£o relacionada Ã  classe.
 */
    public void setGeneroLivro(String generoLivro) { this.generoLivro = generoLivro; }
/**
 * Executa a operaÃ§Ã£o relacionada Ã  classe.
 */
    public java.math.BigDecimal getPreco() { return preco; }
/**
 * Executa a operaÃ§Ã£o relacionada Ã  classe.
 */
    public void setPreco(java.math.BigDecimal preco) { this.preco = preco; }
/**
 * Executa a operaÃ§Ã£o relacionada Ã  classe.
 */
    public String getMoeda() { return moeda; }
/**
 * Executa a operaÃ§Ã£o relacionada Ã  classe.
 */
    public void setMoeda(String moeda) { this.moeda = moeda; }
}




