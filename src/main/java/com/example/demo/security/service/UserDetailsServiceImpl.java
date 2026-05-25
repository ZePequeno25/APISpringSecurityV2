package com.example.demo.security.service;

import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    // Repositorio usado para encontrar o usuario pelo email digitado no login.
    @Autowired
    private UserRepository userRepository;

    // Metodo chamado pelo Spring Security durante a autenticacao.
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario nao encontrado: " + email));

        // Adapta a entidade User para o contrato UserDetails.
        return new UserDetailsImpl(user);
    }
}
