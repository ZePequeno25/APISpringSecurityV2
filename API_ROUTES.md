# API Documentation

## Overview
Esta documentação cobre os dois microsserviços do projeto:

- **User Service** — API REST com JWT, MySQL e RabbitMQ
- **Email Service** — consumidor RabbitMQ para receber eventos de e-mail

> O projeto está preparado para ser executado com MySQL. Use o arquivo `mysql-setup.sql` para criar os bancos de dados e as tabelas necessárias.

---

## User Service

- Base URL: `http://localhost:8081`
- API root: `/users`
- MySQL database: `ms_user`
- RabbitMQ queue: `default.email`

### 1. Criar usuário
- Method: `POST`
- Path: `/users`
- Auth: `None`
- Request body:
  ```json
  {
    "email": "usuario@example.com",
    "password": "senha123",
    "role": "ROLE_CUSTOMER"
  }
  ```
- Response:
  - `201 Created`
  - Body: vazio

### 2. Login
- Method: `POST`
- Path: `/users/login`
- Auth: `None`
- Request body:
  ```json
  {
    "email": "usuario@example.com",
    "password": "senha123"
  }
  ```
- Response:
  - `200 OK`
  - Body:
    ```json
    {
      "token": "jwt-token-string"
    }
    ```

### 3. Testar autenticação
- Method: `GET`
- Path: `/users/test`
- Auth: `Bearer <token>`
- Response:
  - `200 OK`
  - Body: `Autenticado com sucesso!`

### 4. Testar role CUSTOMER
- Method: `GET`
- Path: `/users/test/customer`
- Auth: `Bearer <token>`
- Required role: `ROLE_CUSTOMER`
- Response:
  - `200 OK`
  - Body: `Acesso de CUSTOMER autorizado!`

### 5. Testar role ADMINISTRATOR
- Method: `GET`
- Path: `/users/test/administrator`
- Auth: `Bearer <token>`
- Required role: `ROLE_ADMINISTRATOR`
- Response:
  - `200 OK`
  - Body: `Acesso de ADMINISTRATOR autorizado!`

### 6. Consultar usuário autenticado
- Method: `GET`
- Path: `/users/me`
- Auth: `Bearer <token>`
- Response:
  - `200 OK`
  - Body:
    ```json
    {
      "id": 1,
      "email": "usuario@example.com",
      "roles": ["ROLE_CUSTOMER"]
    }
    ```

---

## Email Service

- Base URL: `http://localhost:8082`
- MySQL database: `ms_email`
- RabbitMQ queue: `default.email`

### Comportamento atual
- O serviço consome mensagens da fila `default.email`
- O consumidor imprime o destinatário recebido no console
- O serviço está preparado para um fluxo assíncrono de envio de e-mail

### Observação
- Ainda não há endpoints HTTP expostos no Email Service
- O serviço está pronto para receber eventos do User Service via RabbitMQ

---

## Configurações importantes

### User Service
- `server.port=8081`
- `spring.datasource.url=jdbc:mysql://localhost:3306/ms_user?useSSL=false&serverTimezone=UTC`
- `spring.datasource.username=root`
- `spring.datasource.password=SUA_SENHA`
- `spring.jpa.hibernate.ddl-auto=update`
- `broker.queue.email.name=default.email`
- `spring.rabbitmq.addresses=amqps://...`

### Email Service
- `server.port=8082`
- `spring.datasource.url=jdbc:mysql://localhost:3306/ms_email?useSSL=false&serverTimezone=UTC`
- `spring.datasource.username=root`
- `spring.datasource.password=SUA_SENHA`
- `spring.jpa.hibernate.ddl-auto=update`
- `broker.queue.email.name=default.email`
- `spring.rabbitmq.addresses=amqps://...`

---

## Arquivos auxiliares

- `mysql-setup.sql` — cria os bancos `ms_user` e `ms_email`, além das tabelas `users`, `roles`, `users_roles` e `email_records`
- `thunder-client-collection.json` — coleção pronta para importação no Thunder Client

---

## Como testar no Thunder Client

1. Importe `thunder-client-collection.json` no Thunder Client.
2. Execute `Create user`.
3. Use o token retornado em `Authorization: Bearer <token>` nos requests protegidos.
4. Teste os endpoints `/users/test`, `/users/test/customer`, `/users/test/administrator` e `/users/me`.
