package com.bibliotecassa.trabalho.Services.Pedidos;
// arquivo OrderItemRepository.java
// finalidade classe OrderItemRepository comentarios automatizados

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

// definicao de interface nome OrderItemRepository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
	
	List<OrderItem> findByOrderUsuarioId(String usuarioId);

	
	@org.springframework.data.jpa.repository.Query("SELECT oi.livroId, oi.nomeLivro, SUM(oi.quantidade) " +
		"FROM OrderItem oi GROUP BY oi.livroId, oi.nomeLivro ORDER BY SUM(oi.quantidade) DESC")
	List<Object[]> findVolumeAlugueisPorLivro();
}


