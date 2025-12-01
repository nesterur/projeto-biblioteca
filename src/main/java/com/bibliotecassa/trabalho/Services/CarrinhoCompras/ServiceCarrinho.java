package com.bibliotecassa.trabalho.Services.CarrinhoCompras;
// arquivo ServiceCarrinho.java
// finalidade classe ServiceCarrinho comentarios automatizados

import com.bibliotecassa.trabalho.Services.Livros.ModelLivro;
import com.bibliotecassa.trabalho.Services.Livros.ServiceLivro;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.*;
import java.time.LocalDateTime;

@Service
// definicao de class nome ServiceCarrinho
public class ServiceCarrinho {
    private static final Logger logger = LoggerFactory.getLogger(ServiceCarrinho.class);
    private final CarrinhoItemRepository carrinhoItemRepository;
    private final ServiceLivro serviceLivro;

    
    
    

    @Autowired


    public ServiceCarrinho(CarrinhoItemRepository carrinhoItemRepository, ServiceLivro serviceLivro) {
        this.carrinhoItemRepository = carrinhoItemRepository;
        this.serviceLivro = serviceLivro;
    }


    public void adicionarItemCarrinho(String idLivro, String idUsuario) {
        
        if (idUsuario == null || idUsuario.isBlank()) {
            throw new IllegalArgumentException("idUsuario ausente");
        }
        if (idLivro == null || idLivro.isBlank()) {
            throw new IllegalArgumentException("idLivro ausente");
        }

        
        List<String> ids = new ArrayList<>();
        ids.add(idLivro);
        List<ModelLivro> livros = Collections.emptyList();
        try {
            livros = serviceLivro.buscarLivrosPorIds(ids);
        } catch (Exception e) {
            logger.warn("buscarLivrosPorIds falhou para id='{}': {}", idLivro, e.toString());
        }

        String nomeLivro = "Desconhecido";
        String genero = "";
        String capaUrl = "";
        if (livros != null && !livros.isEmpty()) {
            ModelLivro l = livros.get(0);
            if (l.getTitulo() != null && !l.getTitulo().isBlank()) nomeLivro = l.getTitulo();
            if (l.getCategoria() != null) genero = l.getCategoria();
            if (l.getUrlCapa() != null) capaUrl = l.getUrlCapa();
            
            if (capaUrl != null && capaUrl.length() > 1999) {
                logger.warn("capaUrl muito longa ({} chars), truncando para 2000", capaUrl.length());
                capaUrl = capaUrl.substring(0, 1999);
            }
        }

        
        java.math.BigDecimal precoBD = null;
        String moeda = null;
        if (livros != null && !livros.isEmpty()) {
            ModelLivro ml = livros.get(0);
            if (ml != null && ml.getPreco() != null) {
                precoBD = java.math.BigDecimal.valueOf(ml.getPreco());
                moeda = ml.getMoeda();
            }
        }

        
        try {
            java.util.Optional<CarrinhoItem> existente = carrinhoItemRepository.findByUsuarioIdAndLivroId(idUsuario, idLivro);
            if (existente.isPresent()) {
                
                CarrinhoItem e = existente.get();
                e.setNomeLivro(nomeLivro);
                e.setCapaUrl(capaUrl);
                e.setGeneroLivro(genero);
                e.setDataAdicao(LocalDateTime.now());
                
                if (e.getPreco() == null && precoBD != null) {
                    e.setPreco(precoBD);
                    e.setMoeda(moeda);
                }
                logger.info("Atualizando item existente no carrinho para usuario={}, livroId={}", idUsuario, idLivro);
                carrinhoItemRepository.save(e);
                return;
            }
        } catch (Exception ex) {
            logger.warn("Erro ao checar item existente no carrinho: {}", ex.toString());
        }

        CarrinhoItem item = new CarrinhoItem(idUsuario, idLivro, nomeLivro, capaUrl, LocalDateTime.now(), genero, precoBD, moeda);
        
        logger.info("Adicionando item ao carrinho - usuario={}, livroId={}, nome='{}', capaUrl='{}', preco={}", idUsuario, idLivro, nomeLivro, capaUrl, precoBD);
        carrinhoItemRepository.save(item);
    }


    public List<CarrinhoItem> obterCarrinhoPorUsuario(String usuarioId) {
        return carrinhoItemRepository.findByUsuarioId(usuarioId);
    }


    public void removerItemCarrinho(String idLivro, String usuarioId) {
        List<CarrinhoItem> itens = carrinhoItemRepository.findByUsuarioId(usuarioId);
        for (CarrinhoItem item : itens) {
            if (item.getLivroId().equals(idLivro)) {
                carrinhoItemRepository.delete(item);
                break;
            }
        }
    }


    public void limparCarrinhoDoUsuario(String usuarioId) {
        if (usuarioId == null || usuarioId.isBlank()) return;
        List<CarrinhoItem> itens = carrinhoItemRepository.findByUsuarioId(usuarioId);
        if (itens != null && !itens.isEmpty()) {
            carrinhoItemRepository.deleteAll(itens);
            logger.info("Limpei {} itens do carrinho do usuario={}", itens.size(), usuarioId);
        }
    }
}




