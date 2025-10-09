package com.bibliotecassa.trabalho.Services.Livros;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

@Service
public class ServiceLivro {
    @Value("${google.books.api.url}")
    private String googleBooksApiUrl;

    @Value("${google.books.api.key}")
    private String googleBooksApiKey;

    public List<ModelLivro> listarLivros(String query) {
        // Adiciona maxResults=40 para trazer até 40 livros
    String url = googleBooksApiUrl + "?q=" + query + "&key=" + googleBooksApiKey + "&maxResults=40&orderBy=relevance";

        RestTemplate restTemplate = new RestTemplate();

        // GE e resposta como um map
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

        // Lista para armazenar os livros convertidos
        List<ModelLivro> livros = new ArrayList<>();

        // Verifica se a resposta contém itens
        if (response.getBody() != null && response.getBody().containsKey("items")) {
            List<Map<String, Object>> items = (List<Map<String, Object>>) response.getBody().get("items");
            for (Map<String, Object> item : items) {
                Map<String, Object> volumeInfo = (Map<String, Object>) item.get("volumeInfo");
                String titulo = (String) volumeInfo.getOrDefault("title", "Sem título");
                // Pega o primeiro autor, se existir
                List<String> autores = (List<String>) volumeInfo.get("authors");
                String autor = (autores != null && !autores.isEmpty()) ? autores.get(0) : "Autor desconhecido";
                // Pega a primeira categoria, se existir
                List<String> categorias = (List<String>) volumeInfo.get("categories");
                String categoria = (categorias != null && !categorias.isEmpty()) ? categorias.get(0) : "Sem categoria";
                String descricao = (String) volumeInfo.getOrDefault("description", "Sem descrição");
                //URL da capa, se existir
                Map<String, Object> imageLinks = (Map<String, Object>) volumeInfo.get("imageLinks");
                String urlCapa = (imageLinks != null) ? (String) imageLinks.getOrDefault("thumbnail", "") : "";
                // Cria o DTO ModelLivro e adiciona à lista
                livros.add(new ModelLivro(titulo, autor, categoria, descricao, urlCapa));
            }
        }
        return livros;
    }

    public class ResultadoPaginacao{
        private List<ModelLivro> livros;
        private int paginaAtual;
        private int totalPaginas;

        public ResultadoPaginacao(List<ModelLivro> livros, int paginaAtual, int totalPaginas) {
            this.livros = livros;
            this.paginaAtual = paginaAtual;
            this.totalPaginas = totalPaginas;
        }

        public List<ModelLivro> getLivros() { return livros; }
        public int getPaginaAtual() { return paginaAtual; }
        public int getTotalPaginas() { return totalPaginas; }
    }

    public ResultadoPaginacao listarLivros(String query, int pagina, int tamanhoPagina) {
        int startIndex = (pagina - 1) * tamanhoPagina;
    String url = googleBooksApiUrl + "?q=" + query + "&key=" + googleBooksApiKey +
        "&maxResults=" + tamanhoPagina + "&startIndex=" + startIndex + "&orderBy=relevance";

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        List<ModelLivro> livros = new ArrayList<>();
        int totalItems = 0;

        if (response.getBody() != null) {
            // Pega a lista de livros
            if (response.getBody().containsKey("items")) {
                List<Map<String, Object>> items = (List<Map<String, Object>>) response.getBody().get("items");
                for (Map<String, Object> item : items) {
                    Map<String, Object> volumeInfo = (Map<String, Object>) item.get("volumeInfo");
                    String titulo = (String) volumeInfo.getOrDefault("title", "Sem título");
                    List<String> autores = (List<String>) volumeInfo.get("authors");
                    String autor = (autores != null && !autores.isEmpty()) ? autores.get(0) : "Autor desconhecido";
                    List<String> categorias = (List<String>) volumeInfo.get("categories");
                    String categoria = (categorias != null && !categorias.isEmpty()) ? categorias.get(0) : "Sem categoria";
                    String descricao = (String) volumeInfo.getOrDefault("description", "Sem descrição");
                    Map<String, Object> imageLinks = (Map<String, Object>) volumeInfo.get("imageLinks");
                    String urlCapa = (imageLinks != null) ? (String) imageLinks.getOrDefault("thumbnail", "") : "";
                    livros.add(new ModelLivro(titulo, autor, categoria, descricao, urlCapa));
                }
            }
            // Pega o total de livros encontrados
            if (response.getBody().containsKey("totalItems")) {
                totalItems = ((Number) response.getBody().get("totalItems")).intValue();
            }
        }

        int totalPaginas = (int) Math.ceil((double) totalItems / tamanhoPagina);
        if (totalPaginas > 10) {
            totalPaginas = 10;
        }
        return new ResultadoPaginacao(livros, pagina, totalPaginas);
    }
}
