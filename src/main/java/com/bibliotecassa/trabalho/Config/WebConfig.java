/* ANOTAÇÕES - WebConfig.java
 * O QUE: Configurações web da aplicação (interceptors, resource handlers).
 * POR QUE: centraliza ajustes do MVC e pontos de extensão do Spring.
 * ENTRADAS: beans de configuração e registro de interceptors.
 * SAÍDAS: comportamentos globais aplicados ao pipeline HTTP.
 * NOTAS: alterar com cautela — pode afetar segurança e roteamento.
 */
package com.bibliotecassa.trabalho.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**");
    }
}
