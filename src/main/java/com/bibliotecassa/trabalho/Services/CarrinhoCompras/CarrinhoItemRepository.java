package com.bibliotecassa.trabalho.Services.CarrinhoCompras;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CarrinhoItemRepository extends JpaRepository<CarrinhoItem, Long> {
    List<CarrinhoItem> findByUsuarioId(String usuarioId);
    List<CarrinhoItem> findByLivroId(String livroId);
    Optional<CarrinhoItem> findByUsuarioIdAndLivroId(String usuarioId, String livroId);
}
