package com.example.demo.dto;

import java.util.UUID;

// DTO publicado no RabbitMQ para solicitar envio de email.
public record EmailDto(
        // Identificador do usuario relacionado ao email.
        UUID userId,

        // Destinatario do email.
        String emailTo,

        // Assunto da mensagem.
        String subject,

        // Corpo do email.
        String text
) {
}
