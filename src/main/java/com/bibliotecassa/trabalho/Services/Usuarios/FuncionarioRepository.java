/* ANOTAÇÕES - FuncionarioRepository.java
 * O QUE: Repositório Spring Data para entidade Funcionario.
 * POR QUE: fornece métodos CRUD e consultas por código de funcionário.
 * ENTRADAS: operações JPA executadas pelo framework.
 * SAÍDAS: objetos Funcionario retornados e persistidos.
 * NOTAS: garantir índices/unicidade no campo codigoFuncionario.
 */
package com.bibliotecassa.trabalho.Services.Usuarios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// repositorio pra acessar funcionario no banco
@Repository
public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {
    Funcionario findByIdFuncionario(String idFuncionario);
}
