package com.bibliotecassa.trabalho.Services.Livros;

public class ModelLivro {
    private String titulo;
    private String autor;
    private String categoria;
    private String descricao;
    private String urlCapa;

    public ModelLivro(String titulo, String autor, String categoria, String descricao, String urlCapa) {
        this.titulo = titulo;
        this.autor = autor;
        this.categoria = categoria;
        this.descricao = descricao;
        this.urlCapa = urlCapa;
    }

    // getters
    public String getTitulo() { return titulo; }
    public String getAutor() { return autor; }
    public String getCategoria() { return categoria; }
    public String getDescricao() { return descricao; }
    public String getUrlCapa() { return urlCapa; }

    public String setTitulo(String titulo) {
        this.titulo = titulo;
        return this.titulo;
    }
    public String setAutor(String autor) {
        this.autor = autor;
        return this.autor;
    }
    public String setCategoria(String categoria) {
        this.categoria = categoria;
        return this.categoria;
    }
    public String setDescricao(String descricao) {
        this.descricao = descricao;
        return this.descricao;
    }
    public String setUrlCapa(String urlCapa) {
        this.urlCapa = urlCapa;
        return this.urlCapa;
    }
}