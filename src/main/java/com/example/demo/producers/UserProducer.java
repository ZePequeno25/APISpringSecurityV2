package com.example.demo.producers;

import com.example.demo.dto.EmailDto;
import com.example.demo.models.UserModel;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UserProducer {

    // RabbitTemplate publica mensagens no RabbitMQ.
    private final RabbitTemplate rabbitTemplate;

    // Nome da fila configurado no application.properties.
    private final String queueName;

    // Injeta o RabbitTemplate e le a fila configurada em broker.queue.email.name.
    public UserProducer(RabbitTemplate rabbitTemplate,
                        @Value("${broker.queue.email.name}") String queueName) {
        this.rabbitTemplate = rabbitTemplate;
        this.queueName = queueName;
    }

    // Monta a mensagem de email de boas-vindas e publica na fila do RabbitMQ.
    public void publishMessageEmail(UserModel userModel) {
        var emailDto = new EmailDto(
                userModel.userId(),
                userModel.email(),
                "Cadastro realizado com sucesso!",
                userModel.name() + ", seja bem-vindo(a)!\nAgradecemos o seu cadastro, aproveite agora todos os recursos da nossa plataforma!"
        );

        // Exchange vazio usa a direct exchange padrao do RabbitMQ; queueName funciona como routing key.
        rabbitTemplate.convertAndSend("", queueName, emailDto);
    }
}
