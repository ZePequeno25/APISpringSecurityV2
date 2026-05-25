package ms.email;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EmailApplication {

    // Metodo principal do Email Service. Inicia o Spring Boot na porta configurada em application.properties.
    public static void main(String[] args) {
        SpringApplication.run(EmailApplication.class, args);
    }
}
