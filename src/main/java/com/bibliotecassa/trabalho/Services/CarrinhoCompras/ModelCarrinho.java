package com.bibliotecassa.trabalho.Services.CarrinhoCompras;
// arquivo ModelCarrinho.java
// finalidade classe ModelCarrinho comentarios automatizados
import com.bibliotecassa.trabalho.Services.Livros.ModelLivro;
import java.util.List;

// definicao de class nome ModelCarrinho
public class ModelCarrinho {
    private String idCarrinho;
    private String idUsuario;
    private List<ModelLivro> itens;

    

    public ModelCarrinho(String idCarrinho, String idUsuario, List<ModelLivro> itens) {
        this.idCarrinho = idCarrinho;
        this.idUsuario = idUsuario;
        this.itens = itens;
    }

    

    public String getIdcarrinho() { return idCarrinho; }


    public void setIdcarrinho(String idCarrinho) { this.idCarrinho = idCarrinho; }


    public String getIdusuario() { return idUsuario; }

    public void setIdusuario(String idUsuario) { this.idUsuario = idUsuario; }


    public List<ModelLivro> getItens() { return itens; }

    public void setItens(List<ModelLivro> itens) { this.itens = itens; }
}



