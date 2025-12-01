package com.bibliotecassa.trabalho.Services.CarrinhoCompras;
// arquivo ControllerCarrinho.java
// finalidade classe ControllerCarrinho comentarios automatizados

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bibliotecassa.trabalho.Services.CarrinhoCompras.CarrinhoItem;
import com.bibliotecassa.trabalho.Services.Livros.ServiceLivro;
import com.bibliotecassa.trabalho.Services.Livros.ModelLivro;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Map;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@Controller
// definicao de class nome ControllerCarrinho
public class ControllerCarrinho {

    private static final Logger logger = LoggerFactory.getLogger(ControllerCarrinho.class);

    private final ServiceCarrinho serviceCarrinho;
    private final ServiceLivro serviceLivro;


    public ControllerCarrinho(ServiceCarrinho serviceCarrinho, ServiceLivro serviceLivro) {
        this.serviceCarrinho = serviceCarrinho;
        this.serviceLivro = serviceLivro;
    }

    
    

    @PostMapping("/carrinho/adicionar")
    @ResponseBody
    public ResponseEntity<?> adicionarItemCarrinho(
            @RequestParam("idLivro") String idLivro,
            @RequestParam("idUsuario") String idUsuario
    ) {
        logger.info("Recebendo requisicao adicionarItemCarrinho - idUsuario='{}' idLivro='{}'", idUsuario, idLivro);
        try {
            serviceCarrinho.adicionarItemCarrinho(idLivro, idUsuario);
            
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            
            logger.error("Erro ao adicionar ao carrinho: {}", e.toString(), e);
            return ResponseEntity.status(500).body("Erro ao adicionar ao carrinho: " + e.getMessage());
        }
    }

    @GetMapping("/carrinho")


    public String mostrarCarrinho(@RequestParam(name = "idUsuario", required = false) String idUsuario, Model model, HttpSession session) {
        
        if (idUsuario == null || idUsuario.isBlank()) {
            Object sessId = session.getAttribute("idUsuario");
            if (sessId != null) {
                idUsuario = sessId.toString();
            }
        }

        if (idUsuario == null || idUsuario.isBlank()) {
            
            return "redirect:/login";
        }

        List<CarrinhoItem> itens = serviceCarrinho.obterCarrinhoPorUsuario(idUsuario);
        model.addAttribute("itens", itens);
        model.addAttribute("idUsuario", idUsuario);

        
        Map<String, ModelLivro> livrosMap = new HashMap<>();
    double total = 0.0;
    Map<String, String> priceByLivroId = new HashMap<>();
        try {
            Set<String> ids = itens.stream().map(CarrinhoItem::getLivroId).collect(Collectors.toSet());
            if (!ids.isEmpty()) {
                
                java.util.List<ModelLivro> livros = serviceLivro.buscarLivrosPorIds(new ArrayList<>(ids));
                if (livros != null) {
                    for (ModelLivro ml : livros) {
                        if (ml != null && ml.getIdLivro() != null) {
                            livrosMap.put(ml.getIdLivro(), ml);
                        }
                    }
                    
                    java.text.DecimalFormat itemDf = new java.text.DecimalFormat("#,##0.00");
                    for (CarrinhoItem it : itens) {
                        
                        if (it.getPreco() != null) {
                            total += it.getPreco().doubleValue();
                            priceByLivroId.put(it.getLivroId(), "R$ " + itemDf.format(it.getPreco()));
                        } else {
                            ModelLivro ml = livrosMap.get(it.getLivroId());
                            if (ml != null && ml.getPreco() != null) {
                                total += ml.getPreco();
                                priceByLivroId.put(it.getLivroId(), ml.getPrecoFormatado());
                            } else {
                                priceByLivroId.put(it.getLivroId(), "Preço não disponível");
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("Erro ao obter preços para itens do carrinho: {}", e.toString());
        }

        DecimalFormat df = new DecimalFormat("#,##0.00");
        String totalFormatado = "R$ " + df.format(total);
        model.addAttribute("livrosMap", livrosMap);
    model.addAttribute("priceByLivroId", priceByLivroId);
        model.addAttribute("totalCarrinho", total);
        model.addAttribute("totalFormatado", totalFormatado);
        return "carrinho";
    }

    @PostMapping("/carrinho/remover")

    
    public String removerItemCarrinho(@RequestParam("idLivro") String idLivro, @RequestParam(name = "idUsuario", required = false) String idUsuario, HttpSession session) {
        if (idUsuario == null || idUsuario.isBlank()) {
            Object sessId = session.getAttribute("idUsuario");
            if (sessId != null) {
                idUsuario = sessId.toString();
            }
        }

        if (idUsuario == null || idUsuario.isBlank()) {
            return "redirect:/login";
        }

        serviceCarrinho.removerItemCarrinho(idLivro, idUsuario);
        return "redirect:/carrinho?idUsuario=" + idUsuario;
    }
}




