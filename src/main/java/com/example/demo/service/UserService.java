package com.example.demo.service;

import com.example.demo.dto.CreateUserDto;
import com.example.demo.dto.LoginUserDto;
import com.example.demo.dto.RecoveryJwtTokenDto;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.service.JwtTokenService;
import com.example.demo.security.service.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    // Componente do Spring Security que valida email e senha no login.
    @Autowired
    private AuthenticationManager authenticationManager;

    // Service que gera e valida tokens JWT.
    @Autowired
    private JwtTokenService jwtTokenService;

    // Repositorio usado para salvar e buscar usuarios.
    @Autowired
    private UserRepository userRepository;

    // Repositorio usado para buscar/criar roles.
    @Autowired
    private RoleRepository roleRepository;

    // Criptografa senhas antes de salvar no banco.
    @Autowired
    private PasswordEncoder passwordEncoder;

    // Autentica o usuario e devolve um JWT para ser usado nas proximas requisicoes.
    public RecoveryJwtTokenDto authenticateUser(LoginUserDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.email(), loginDto.password())
        );

        // O principal autenticado guarda os dados do usuario no formato esperado pelo Spring Security.
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String token = jwtTokenService.generateToken(userDetails);
        return new RecoveryJwtTokenDto(token);
    }

    // Cria um usuario novo, criptografando a senha e vinculando a role informada.
    public void createUser(CreateUserDto createDto) {
        // Reaproveita uma role existente; se nao existir, cria uma nova.
        Role role = roleRepository.findByName(createDto.role())
                .orElseGet(() -> roleRepository.save(Role.builder().name(createDto.role()).build()));

        User newUser = User.builder()
                .email(createDto.email())
                .password(passwordEncoder.encode(createDto.password()))
                .roles(List.of(role))
                .build();
        userRepository.save(newUser);
    }

    // Busca um usuario pelo email. Usado pelo endpoint /users/me e pelo fluxo de autenticacao.
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario nao encontrado."));
    }
}
