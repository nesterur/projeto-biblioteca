package com.bibliotecassa.trabalho.Services.Pedidos;
// arquivo OrderRepository.java
// finalidade classe OrderRepository comentarios automatizados

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

// definicao de interface nome OrderRepository
public interface OrderRepository extends JpaRepository<Order, Long> {
	
	List<Order> findByUsuarioId(String usuarioId);
}


