package com.example.demo.dto;

import com.example.demo.enums.RoleName;

// DTO recebido no cadastro de usuario.
public record CreateUserDto(
        // Email que sera usado para login.
        String email,

        // Senha em texto puro recebida na requisicao; sera criptografada no service.
        String password,

        // Role escolhida para o usuario, como ROLE_CUSTOMER ou ROLE_ADMINISTRATOR.
        RoleName role
) {
}
