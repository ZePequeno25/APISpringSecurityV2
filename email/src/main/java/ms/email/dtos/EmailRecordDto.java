package ms.email.dtos;

import java.util.UUID;

// DTO recebido pelo Email Service a partir da fila RabbitMQ.
public record EmailRecordDto(
        // Identificador do usuario que originou o evento.
        UUID userId,

        // Destinatario do email.
        String emailTo,

        // Assunto da mensagem.
        String subject,

        // Corpo da mensagem.
        String text
) { }
