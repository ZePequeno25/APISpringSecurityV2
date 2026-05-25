package ms.email.consumers;

import ms.email.dtos.EmailRecordDto;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class EmailConsumer {

    // Escuta a fila configurada em broker.queue.email.name e recebe mensagens de email.
    @RabbitListener(queues = "${broker.queue.email.name}")
    public void listenEmailQueue(@Payload EmailRecordDto emailRecordDto) {
        // Nesta etapa, apenas mostra no console o destinatario recebido pela mensageria.
        System.out.println("Email recebido: " + emailRecordDto.emailTo());
    }
}
