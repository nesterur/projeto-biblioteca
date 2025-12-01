
package com.bibliotecassa.trabalho.Services.Usuarios;
// arquivo FuncionarioRepository.java
// finalidade classe FuncionarioRepository comentarios automatizados

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
// definicao de interface nome FuncionarioRepository
public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {
    Funcionario findByIdFuncionario(String idFuncionario);
}


