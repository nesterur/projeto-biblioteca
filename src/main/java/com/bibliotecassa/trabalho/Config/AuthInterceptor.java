package com.bibliotecassa.trabalho.Config;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;

@Component
// definicao de class nome AuthInterceptor
public class AuthInterceptor implements HandlerInterceptor {

    
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
        
        for (String p : whitelistPrefixes) {
            if (path.startsWith(p)) return true;
        }

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("idUsuario") == null) {
            
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }
        return true;
    }
}


