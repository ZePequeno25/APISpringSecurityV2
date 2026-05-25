package com.example.demo.dto;

// DTO retornado apos login bem-sucedido.
public record RecoveryJwtTokenDto(
        // Token JWT que deve ser enviado no header Authorization: Bearer <token>.
        String token
) {
}
