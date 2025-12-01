package com.bibliotecassa.trabalho.Services.Livros;
// arquivo ControllerLivro.java
// finalidade classe ControllerLivro comentarios automatizados

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;

@Controller
// definicao de class nome ControllerLivro
public class ControllerLivro {

    
    private final ServiceLivro serviceLivro;


    public ControllerLivro(ServiceLivro serviceLivro) {
        this.serviceLivro = serviceLivro;
    }

    
    @GetMapping("/livros")
    public String listarLivros(
        @RequestParam(name = "busca", required = false) String busca,
        @RequestParam(name = "genero", required = false) String genero,
        @RequestParam(name = "pagina", required = false, defaultValue = "1") int pagina,
        @RequestParam(name = "tamanho", required = false, defaultValue = "12") int tamanho,
        Model model,
        HttpSession session 
    ) {
        
        String idUsuario = (String) session.getAttribute("idUsuario");
        model.addAttribute("idUsuario", idUsuario);
        if ((busca == null || busca.trim().isEmpty()) && (genero == null || genero.trim().isEmpty())) {
            
            model.addAttribute("livros", java.util.Collections.emptyList());
            model.addAttribute("pagina", 1);
            model.addAttribute("totalPaginas", 1);
            model.addAttribute("paginasExibir", 1);
            model.addAttribute("busca", "");
            model.addAttribute("tamanho", tamanho);
            
            ServiceLivro.ResultadoPaginacao broad = serviceLivro.listarLivros("a", 1, 40);
            java.util.Set<String> genresSet = new java.util.LinkedHashSet<>();
            if (broad != null && broad.getLivros() != null) {
                for (ModelLivro ml : broad.getLivros()) {
                    if (ml.getCategoria() != null && !ml.getCategoria().isBlank()) genresSet.add(ml.getCategoria());
                }
            }
            model.addAttribute("genres", new java.util.ArrayList<>(genresSet));
            model.addAttribute("selectedGenre", null);
            return "dashboard";
        } else {
            
            String apiQuery = (busca != null) ? busca.trim() : "";
            if (genero != null && !genero.isBlank()) {
                
                String genrePart = String.format("subject:\"%s\"", genero.trim());
                
                if (apiQuery.isEmpty()) {
                    apiQuery = genrePart;
                } else {
                    apiQuery = apiQuery + " " + genrePart;
                }
            }

            ServiceLivro.ResultadoPaginacao resultado = serviceLivro.listarLivros(apiQuery, pagina, tamanho);

            
            java.util.Set<String> genresSet = new java.util.LinkedHashSet<>();
            if (resultado != null && resultado.getLivros() != null) {
                for (ModelLivro ml : resultado.getLivros()) {
                    if (ml.getCategoria() != null && !ml.getCategoria().isBlank()) genresSet.add(ml.getCategoria());
                }
            }
            java.util.List<String> genres = new java.util.ArrayList<>(genresSet);

            int paginaAtual = (resultado != null) ? resultado.getPaginaAtual() : 1;
            int totalPaginas = (resultado != null) ? resultado.getTotalPaginas() : 1;
            int paginasExibir = totalPaginas > 10 ? 10 : totalPaginas;
            model.addAttribute("livros", (resultado != null) ? resultado.getLivros() : java.util.Collections.emptyList());
            model.addAttribute("genres", genres);
            model.addAttribute("selectedGenre", genero);
            model.addAttribute("pagina", paginaAtual);
            model.addAttribute("totalPaginas", totalPaginas);
            
            model.addAttribute("totalPaginasDisplay", paginasExibir);
            model.addAttribute("paginasExibir", paginasExibir);
            model.addAttribute("busca", busca);
            model.addAttribute("tamanho", tamanho);
            return "dashboard";
        }
    }
}



