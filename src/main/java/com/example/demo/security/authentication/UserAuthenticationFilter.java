package com.example.demo.security.authentication;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.config.SecurityConfiguration;
import com.example.demo.security.service.JwtTokenService;
import com.example.demo.security.service.UserDetailsImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
public class UserAuthenticationFilter extends OncePerRequestFilter {

    // Service responsavel por validar o JWT e extrair o email do usuario.
    @Autowired
    private JwtTokenService jwtTokenService;

    // Repositorio usado para carregar o usuario encontrado no token.
    @Autowired
    private UserRepository userRepository;

    // Metodo executado uma vez por requisicao para validar o token JWT.
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // Endpoints publicos passam direto, sem exigir Authorization.
        if (isPublicEndpoint(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = recoverToken(request);
        if (token != null) {
            try {
                // Valida o token e usa o subject como email do usuario.
                String email = jwtTokenService.getSubjectFromToken(token);
                User user = userRepository.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("Usuario nao encontrado."));

                // Converte o usuario para UserDetails e monta a autenticacao do Spring Security.
                UserDetailsImpl userDetails = new UserDetailsImpl(user);
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        userDetails.getUsername(),
                        null,
                        userDetails.getAuthorities()
                );

                // Guarda a autenticacao no contexto para os controllers acessarem o usuario logado.
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (RuntimeException ex) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"" + ex.getMessage() + "\"}");
                return;
            }
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Token JWT nao fornecido\"}");
            return;
        }

        // Continua a cadeia de filtros ate chegar ao controller.
        filterChain.doFilter(request, response);
    }

    // Recupera o token do header Authorization no formato Bearer <token>.
    private String recoverToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    // Verifica se a URL atual esta na lista de endpoints publicos.
    private boolean isPublicEndpoint(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return Arrays.asList(SecurityConfiguration.ENDPOINTS_WITH_AUTHENTICATION_NOT_REQUIRED)
                .contains(uri);
    }
}
