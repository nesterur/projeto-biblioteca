package com.bibliotecassa.trabalho.Services.CarrinhoCompras;
import com.bibliotecassa.trabalho.Services.Livros.ModelLivro;
import java.util.List;

public class ModelCarrinho {
    private String idCarrinho;
    private String idUsuario;
    private List<ModelLivro> itens;

    // construtor carrinho
    public ModelCarrinho(String idCarrinho, String idUsuario, List<ModelLivro> itens) {
        this.idCarrinho = idCarrinho;
        this.idUsuario = idUsuario;
        this.itens = itens;
    }

    // getters e setter
    public String getIdcarrinho() { return idCarrinho; }
    public void setIdcarrinho(String idCarrinho) { this.idCarrinho = idCarrinho; }

    public String getIdusuario() { return idUsuario; }
    public void setIdusuario(String idUsuario) { this.idUsuario = idUsuario; }

    public List<ModelLivro> getItens() { return itens; }
    public void setItens(List<ModelLivro> itens) { this.itens = itens; }
}