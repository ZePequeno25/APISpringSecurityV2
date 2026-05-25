package com.example.demo.security.service;

import com.example.demo.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
public class UserDetailsImpl implements UserDetails {

    // Usuario do dominio que sera adaptado para o formato esperado pelo Spring Security.
    private final User user;

    // Recebe o usuario carregado do banco.
    public UserDetailsImpl(User user) {
        this.user = user;
    }

    // Converte as roles do usuario em authorities usadas pelo Spring Security.
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .toList();
    }

    // Retorna a senha criptografada salva no banco.
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    // Retorna o email como username de autenticacao.
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    // Indica que a conta nao expira neste projeto.
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // Indica que a conta nao possui bloqueio neste projeto.
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // Indica que as credenciais nao expiram neste projeto.
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // Indica que o usuario esta habilitado.
    @Override
    public boolean isEnabled() {
        return true;
    }
}
