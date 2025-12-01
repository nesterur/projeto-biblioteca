package com.bibliotecassa.trabalho.Services.Livros;
// arquivo ServiceLivro.java
// finalidade classe ServiceLivro comentarios automatizados

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Set;
import java.util.HashSet;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
// definicao de class nome ServiceLivro
public class ServiceLivro {
    private static final Logger logger = LoggerFactory.getLogger(ServiceLivro.class);
    @Autowired
    private BlockedBookRepository blockedBookRepository;
    
    
    
    
    

    public List<ModelLivro> buscarLivrosPorIds(List<String> ids) {
        List<ModelLivro> livros = new ArrayList<>();
        RestTemplate restTemplate = new RestTemplate();
        for (String id : ids) {
            String url = "https://www.googleapis.com/books/v1/volumes/" + id + "?key=" + googleBooksApiKey;
            try {
                ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
                if (response.getBody() != null && response.getBody().containsKey("volumeInfo")) {
                    Map<String, Object> volumeInfo = (Map<String, Object>) response.getBody().get("volumeInfo");
                    String titulo = (String) volumeInfo.getOrDefault("title", "Sem título");
                    List<String> autores = (List<String>) volumeInfo.get("authors");
                    String autor = (autores != null && !autores.isEmpty()) ? autores.get(0) : "Autor desconhecido";
                    List<String> categorias = (List<String>) volumeInfo.get("categories");
                    String categoria = (categorias != null && !categorias.isEmpty()) ? categorias.get(0) : "Sem categoria";
                    String descricao = (String) volumeInfo.getOrDefault("description", "Sem descrição");
                    Map<String, Object> imageLinks = (Map<String, Object>) volumeInfo.get("imageLinks");
                    String urlCapa = (imageLinks != null) ? (String) imageLinks.getOrDefault("thumbnail", "") : "";
                    logger.debug("buscarLivrosPorIds - id='{}' imageLinks={} urlCapa={}", id, imageLinks, urlCapa);
                    ModelLivro ml = new ModelLivro(id, titulo, autor, categoria, descricao, urlCapa);
                    
                    Map<String, Object> saleInfo = (Map<String, Object>) response.getBody().get("saleInfo");
                    applyPriceFromSaleInfo(saleInfo, ml, id);
                    livros.add(ml);
                }
            } catch (Exception e) {
                
                logger.warn("buscarLivrosPorIds - erro ao buscar id={}: {}", id, e.toString());
            }
        }
        // filtra livros bloqueados pelo admin (quando busca por ids)
        try {
            Set<String> blockedIds = new HashSet<>();
            if (blockedBookRepository != null) {
                for (BlockedBook b : blockedBookRepository.findAll()) blockedIds.add(b.getBookId());
            }
            if (!blockedIds.isEmpty()) {
                livros.removeIf(l -> l.getIdLivro() != null && blockedIds.contains(l.getIdLivro()));
            }
        } catch (Exception e) {
            logger.debug("Não foi possível aplicar filtro de livros bloqueados (ids): {}", e.toString());
        }
        return livros;
    }

    
    @SuppressWarnings("unchecked")

    private void applyPriceFromSaleInfo(Map<String, Object> saleInfo, ModelLivro ml, String id) {
        try {
            if (saleInfo != null) {
                
                Map<String, Object> priceMap = (Map<String, Object>) saleInfo.get("retailPrice");
                if (priceMap == null) priceMap = (Map<String, Object>) saleInfo.get("listPrice");
                String saleability = (String) saleInfo.get("saleability");
                if (priceMap != null && priceMap.get("amount") != null) {
                    try {
                        Double amount = Double.valueOf(priceMap.get("amount").toString());
                        String currency = (String) priceMap.getOrDefault("currencyCode", "BRL");
                        ml.setPreco(amount);
                        ml.setMoeda(currency);
                        return;
                    } catch (Exception e) {
                        logger.debug("Erro ao parsear priceMap amount: {}", e.toString());
                    }
                }
                if (saleability != null && saleability.equalsIgnoreCase("FREE")) {
                    ml.setPreco(0.0);
                    ml.setMoeda("BRL");
                    return;
                }
            }
        } catch (Exception ex) {
            logger.debug("applyPriceFromSaleInfo falhou para id={}: {}", id, ex.toString());
        }

        
        int minCents = 4099;
        int maxCents = 14999;
        int range = maxCents - minCents + 1;
        int hash = (id != null) ? Math.abs(id.hashCode()) : (int) (System.currentTimeMillis() & 0x7fffffff);
        int cents = minCents + (hash % range);
        double price = cents / 100.0;
        ml.setPreco(price);
        ml.setMoeda("BRL");
    }
    @Value("${google.books.api.url}")
    private String googleBooksApiUrl;

    @Value("${google.books.api.key}")
    private String googleBooksApiKey;
    

    

    public List<ModelLivro> listarLivros(String query) {
        
    String url = googleBooksApiUrl + "?q=" + query + "&key=" + googleBooksApiKey + "&maxResults=40&orderBy=relevance";

        RestTemplate restTemplate = new RestTemplate();

        
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

        
        List<ModelLivro> livros = new ArrayList<>();

        
        if (response.getBody() != null && response.getBody().containsKey("items")) {
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
                logger.debug("listarLivros - titulo='{}' imageLinks={} urlCapa={}", titulo, imageLinks, urlCapa);
                
                Double averageRating = null;
                if (volumeInfo.get("averageRating") != null) {
                    try {
                        averageRating = Double.valueOf(volumeInfo.get("averageRating").toString());
                    } catch (Exception e) {  }
                }
                String idLivro = (String) item.get("id");
                logger.debug("listarLivros(paginado) - titulo='{}' id='{}' imageLinks={} urlCapa={}", titulo, idLivro, imageLinks, urlCapa);
                ModelLivro ml = new ModelLivro(idLivro, titulo, autor, categoria, descricao, urlCapa);
                
                Map<String, Object> saleInfoItem = (Map<String, Object>) item.get("saleInfo");
                applyPriceFromSaleInfo(saleInfoItem, ml, idLivro);
                livros.add(ml);
            }
        }
        
            livros.removeIf(livro -> 
                livro.getUrlCapa() == null || livro.getUrlCapa().isEmpty() ||
                livro.getTitulo() == null || livro.getAutor() == null
            );

        // filtra livros bloqueados pelo admin
        try {
            Set<String> blockedIds = new HashSet<>();
            if (blockedBookRepository != null) {
                for (BlockedBook b : blockedBookRepository.findAll()) blockedIds.add(b.getBookId());
            }
            if (!blockedIds.isEmpty()) {
                livros.removeIf(l -> l.getIdLivro() != null && blockedIds.contains(l.getIdLivro()));
            }
        } catch (Exception e) {
            logger.debug("Não foi possível aplicar filtro de livros bloqueados: {}", e.toString());
        }
        return livros;
    }

    // definicao de class nome ResultadoPaginacao
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
        
        tamanhoPagina = 12;
        int startIndex = (pagina - 1) * tamanhoPagina;
        String url = googleBooksApiUrl + "?q=" + query + "&key=" + googleBooksApiKey +
            "&maxResults=" + tamanhoPagina + "&startIndex=" + startIndex + "&orderBy=relevance";

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        List<ModelLivro> livros = new ArrayList<>();
        int totalItems = 0;

        if (response.getBody() != null) {
            
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
                    String idLivro = (String) item.get("id");
                    ModelLivro ml = new ModelLivro(idLivro, titulo, autor, categoria, descricao, urlCapa);
                    Map<String, Object> saleInfoItem = (Map<String, Object>) item.get("saleInfo");
                    applyPriceFromSaleInfo(saleInfoItem, ml, idLivro);
                    livros.add(ml);
                }
            }
            
            if (response.getBody().containsKey("totalItems")) {
                totalItems = ((Number) response.getBody().get("totalItems")).intValue();
            }
        }

        int totalPaginas = (int) Math.ceil((double) totalItems / tamanhoPagina);
        
        
        livros.removeIf(livro -> 
            livro.getUrlCapa() == null || livro.getUrlCapa().isEmpty() ||
            livro.getTitulo() == null || livro.getAutor() == null
        );
        logger.debug("listarLivros(paginado) - total apos filtro={}", livros.size());
        // filtra livros bloqueados pelo admin (paginado)
        try {
            Set<String> blockedIds = new HashSet<>();
            if (blockedBookRepository != null) {
                for (BlockedBook b : blockedBookRepository.findAll()) blockedIds.add(b.getBookId());
            }
            if (!blockedIds.isEmpty()) {
                livros.removeIf(l -> l.getIdLivro() != null && blockedIds.contains(l.getIdLivro()));
            }
        } catch (Exception e) {
            logger.debug("Não foi possível aplicar filtro de livros bloqueados (paginado): {}", e.toString());
        }
        return new ResultadoPaginacao(livros, pagina, totalPaginas);
    }

    
}





