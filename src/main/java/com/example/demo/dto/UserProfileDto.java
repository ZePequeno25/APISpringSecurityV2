package com.example.demo.dto;

import java.util.List;

// DTO retornado pelo endpoint /users/me com dados do usuario autenticado.
public record UserProfileDto(
        // Identificador do usuario no banco.
        Long id,

        // Email do usuario autenticado.
        String email,

        // Lista de roles/permissoes do usuario.
        List<String> roles
) {
}
