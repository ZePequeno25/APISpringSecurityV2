package ms.email.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Nome da fila lido do application.properties.
    @Value("${broker.queue.email.name}")
    private String queue;

    // Declara a fila do RabbitMQ como duravel, ou seja, ela permanece apos reinicio do broker.
    @Bean
    public Queue queue() {
        return new Queue(queue, true);
    }

    // Converte mensagens JSON recebidas do RabbitMQ para objetos Java e vice-versa.
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}
