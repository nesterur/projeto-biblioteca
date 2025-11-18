/* ANOTAÇÕES - TrabalhoApplication.java
 * O QUE: Ponto de entrada da aplicação Spring Boot.
 * POR QUE: Inicializa contexto e configura beans padrão.
 * ENTRADAS: argumentos da linha de comando (args).
 * SAÍDAS: arranca o contexto Spring e aplicações registradas.
 * NOTAS: não altera lógica da aplicação; cuidar ao mudar perfis/porta.
 */
package com.bibliotecassa.trabalho;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TrabalhoApplication {

    public static void main(String[] args) {
        SpringApplication.run(TrabalhoApplication.class, args);
    }

}