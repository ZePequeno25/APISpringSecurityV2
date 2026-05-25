package com.example.demo.controller;

import com.example.demo.dto.CreateUserDto;
import com.example.demo.dto.LoginUserDto;
import com.example.demo.dto.RecoveryJwtTokenDto;
import com.example.demo.dto.UserProfileDto;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    // Service responsavel pelas regras de cadastro, login e consulta de usuario.
    @Autowired
    private UserService userService;

    // Cria um novo usuario com email, senha e role enviados no corpo da requisicao.
    @PostMapping
    public ResponseEntity<Void> createUser(@RequestBody CreateUserDto dto) {
        userService.createUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // Autentica email/senha e retorna um token JWT quando as credenciais estao corretas.
    @PostMapping("/login")
    public ResponseEntity<RecoveryJwtTokenDto> login(@RequestBody LoginUserDto dto) {
        return ResponseEntity.ok(userService.authenticateUser(dto));
    }

    // Endpoint simples para validar se o token JWT foi aceito.
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Autenticado com sucesso!");
    }

    // Endpoint protegido para usuarios com role ROLE_CUSTOMER.
    @GetMapping("/test/customer")
    public ResponseEntity<String> customerTest() {
        return ResponseEntity.ok("Acesso de CUSTOMER autorizado!");
    }

    // Endpoint protegido para usuarios com role ROLE_ADMINISTRATOR.
    @GetMapping("/test/administrator")
    public ResponseEntity<String> adminTest() {
        return ResponseEntity.ok("Acesso de ADMINISTRATOR autorizado!");
    }

    // Retorna os dados do usuario logado, usando o email extraido do token JWT pelo Spring Security.
    @GetMapping("/me")
    public ResponseEntity<UserProfileDto> getCurrentUser(Authentication authentication) {
        User user = userService.findByEmail(authentication.getName());

        // Converte a lista de objetos Role para uma lista simples de nomes de roles.
        List<String> roles = user.getRoles().stream()
                .map(role -> role.getName().name())
                .toList();
        return ResponseEntity.ok(new UserProfileDto(user.getId(), user.getEmail(), roles));
    }
}
