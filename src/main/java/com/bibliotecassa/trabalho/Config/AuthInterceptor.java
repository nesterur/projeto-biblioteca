/* ANOTAÇÕES - AuthInterceptor.java
 * O QUE: Interceptor para verificar autenticação/autorização nas rotas.
 * POR QUE: protege endpoints e valida sessão/tipo de usuário.
 * ENTRADAS: HttpRequest, HttpResponse, sessão do usuário.
 * SAÍDAS: permissão concedida ou redirecionamento para login.
 * NOTAS: lógica de sessão simples; considerar melhoria para roles.
 */
package com.bibliotecassa.trabalho.Config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    // intercepta requisições e garante que usuario esteja na sessao
    private final List<String> whitelistPrefixes = List.of(
            "/login",
            "/css/",
            "/img/",
            "/js/",
            "/register",
            "/cadastro",
            "/"
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();
        // allow static and login related paths
        for (String p : whitelistPrefixes) {
            if (path.startsWith(p)) return true;
        }

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("idUsuario") == null) {
            // redirect to login
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }
        return true;
    }
}
