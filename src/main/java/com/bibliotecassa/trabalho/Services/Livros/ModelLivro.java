package com.bibliotecassa.trabalho.Services.Livros;
// arquivo ModelLivro.java
// finalidade: modelo de domínio para livros

public class ModelLivro {
    private String idLivro;
    private String titulo;
    private String autor;
    private String categoria;
    private String descricao;
    private String urlCapa;

    private Double preco;
    private String moeda;

    public ModelLivro() {}

    public ModelLivro(String idLivro, String titulo, String autor, String categoria, String descricao, String urlCapa) {
        this.idLivro = idLivro;
        this.titulo = titulo;
        this.autor = autor;
        this.categoria = categoria;
        this.descricao = descricao;
        this.urlCapa = urlCapa;
    }

    public Double getPreco() { return preco; }
    public void setPreco(Double preco) { this.preco = preco; }

    public String getMoeda() { return moeda; }
    public void setMoeda(String moeda) { this.moeda = moeda; }

    public String getPrecoFormatado() {
        if (preco == null) return "Preço não disponível";
        java.text.DecimalFormat df = new java.text.DecimalFormat("#,##0.00");
        String formatted = df.format(preco);
        if (moeda != null) {
            if (moeda.equalsIgnoreCase("BRL")) {
                return "R$ " + formatted;
            } else {
                return formatted + " " + moeda;
            }
        }
        return "R$ " + formatted;
    }

    public String getIdLivro() { return idLivro; }
    public void setIdLivro(String idLivro) { this.idLivro = idLivro; }

    public String getTitulo() { return titulo; }
    public String getAutor() { return autor; }
    public String getCategoria() { return categoria; }
    public String getDescricao() { return descricao; }
    public String getUrlCapa() { return urlCapa; }

    public String setTitulo(String titulo) { this.titulo = titulo; return this.titulo; }
    public String setAutor(String autor) { this.autor = autor; return this.autor; }
    public String setCategoria(String categoria) { this.categoria = categoria; return this.categoria; }
    public String setDescricao(String descricao) { this.descricao = descricao; return this.descricao; }
    public String setUrlCapa(String urlCapa) { this.urlCapa = urlCapa; return this.urlCapa; }
}




