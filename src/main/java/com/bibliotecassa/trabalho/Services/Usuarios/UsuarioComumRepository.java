
package com.bibliotecassa.trabalho.Services.Usuarios;
// arquivo UsuarioComumRepository.java
// finalidade classe UsuarioComumRepository comentarios automatizados

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
// definicao de interface nome UsuarioComumRepository
public interface UsuarioComumRepository extends JpaRepository<UsuarioComum, Integer> {
    UsuarioComum findByCpf(String cpf);
}


