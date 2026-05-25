package com.example.demo.dto;

// DTO recebido no login.
public record LoginUserDto(
        // Email informado pelo usuario.
        String email,

        // Senha informada pelo usuario.
        String password
) {
}
