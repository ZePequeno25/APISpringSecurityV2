package com.example.demo.security.config;

import com.example.demo.security.authentication.UserAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    // Endpoints publicos: podem ser acessados sem token JWT.
    public static final String[] ENDPOINTS_WITH_AUTHENTICATION_NOT_REQUIRED = {
            "/users/login",
            "/users"
    };

    // Endpoints liberados apenas para usuarios com ROLE_ADMINISTRATOR.
    public static final String[] ENDPOINTS_ADMIN = {
            "/users/test/administrator"
    };

    // Endpoints liberados apenas para usuarios com ROLE_CUSTOMER.
    public static final String[] ENDPOINTS_CUSTOMER = {
            "/users/test/customer"
    };

    // Filtro que le e valida o token JWT antes do filtro padrao de login/senha do Spring.
    @Autowired
    private UserAuthenticationFilter userAuthenticationFilter;

    // Configura as regras de seguranca HTTP da aplicacao.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // Desabilita CSRF porque a API usa JWT stateless, nao sessao de navegador.
                .csrf(csrf -> csrf.disable())

                // Define que a aplicacao nao guarda sessao no servidor.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Define quais endpoints sao publicos, autenticados ou restritos por role.
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(ENDPOINTS_WITH_AUTHENTICATION_NOT_REQUIRED).permitAll()
                        .requestMatchers("/users/me").authenticated()
                        .requestMatchers(ENDPOINTS_ADMIN).hasRole("ADMINISTRATOR")
                        .requestMatchers(ENDPOINTS_CUSTOMER).hasRole("CUSTOMER")
                        .anyRequest().authenticated()
                )

                // Executa o filtro JWT antes do filtro UsernamePasswordAuthenticationFilter.
                .addFilterBefore(userAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    // Expoe o AuthenticationManager para o UserService autenticar email/senha.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // Cria o codificador BCrypt usado para criptografar e validar senhas.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
