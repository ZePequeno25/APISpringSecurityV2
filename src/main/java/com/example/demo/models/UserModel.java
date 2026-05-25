package com.example.demo.models;

import java.util.UUID;

// Modelo simples usado para montar a mensagem de email de boas-vindas.
public record UserModel(
        // Identificador do usuario.
        UUID userId,

        // Email do usuario que receberia a mensagem.
        String email,

        // Nome exibido no corpo do email.
        String name
) {
}
