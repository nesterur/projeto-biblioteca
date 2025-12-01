package com.bibliotecassa.trabalho.Services.Pedidos;
// arquivo BookRentalLockRepository.java
// finalidade classe BookRentalLockRepository comentarios automatizados

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

// definicao de interface nome BookRentalLockRepository
public interface BookRentalLockRepository extends JpaRepository<BookRentalLock, Long> {
    List<BookRentalLock> findByLivroIdAndRentedUntilAfter(String livroId, LocalDateTime now);
}

