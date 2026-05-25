# Resumo das Ações do Desafio Prático

Este README documenta a implementação da última etapa do desafio prático descrito em `Aula_APISpringSecurity.txt`.

## Objetivo do Desafio

Adicionar um endpoint `GET /users/me` que retorna os dados do usuário autenticado pelo token JWT.

## O que foi implementado

1. Criado o DTO de resposta `UserProfileDto` em `src/main/java/com/example/demo/dto/UserProfileDto.java`.
2. Adicionado o endpoint `GET /users/me` em `src/main/java/com/example/demo/controller/UserController.java`.
3. O endpoint usa `Authentication` injetado pelo Spring Security para obter o email do usuário logado.
4. Implementado o método `UserService.findByEmail(String email)` em `src/main/java/com/example/demo/service/UserService.java` para buscar o usuário pelo email autenticado.
5. Configurado o `SecurityConfiguration` em `src/main/java/com/example/demo/security/config/SecurityConfiguration.java` para permitir acesso autenticado ao endpoint `/users/me`.
6. Validado que o endpoint é acessível apenas com token JWT válido e retorna `id`, `email` e lista de `roles`.

## Locais de implementação

- `src/main/java/com/example/demo/dto/UserProfileDto.java`
- `src/main/java/com/example/demo/controller/UserController.java`
- `src/main/java/com/example/demo/service/UserService.java`
- `src/main/java/com/example/demo/security/config/SecurityConfiguration.java`

## Arquivos e referências adicionais

- `Aula_APISpringSecurity.txt` — material do desafio prático.
- `HELP.md` — arquivo de apoio existente no repositório.
- `.gitignore` — atualizado para incluir `.github/java-upgrade/` e evitar commitar artefatos gerados automaticamente.

## Observações

- O endpoint atende ao requisito de retornar o usuário autenticado com qualquer papel.
- A autenticação é feita via JWT, usando o mesmo fluxo de login já existente no projeto.
- O projeto permanece em Java 17 conforme a configuração original.

## Pipeline de CI atualizado para 2026

Este repositório inclui um pipeline GitHub Actions em `.github/workflows/ci.yml`.

O workflow executa:

- checkout do código
- cache de dependências Maven
- build e compilação com `./mvnw -B clean test-compile`
- execução de testes com `./mvnw -B test`
- execução do script de verificação de integridade `project-integrity-check.ps1`
- upload do relatório `integrity-report.md` como artefato do workflow

## Como executar o projeto

1. Abra o terminal no diretório do projeto.
2. Para compilar a aplicação:
   - `./mvnw.cmd clean package`
3. Para executar a aplicação:
   - `./mvnw.cmd spring-boot:run`
4. A aplicação estará disponível em `http://localhost:8081`.
5. Use o arquivo `mysql-setup.sql` para criar os bancos `ms_user` e `ms_email` antes de iniciar os serviços.
6. Importe `thunder-client-collection.json` no Thunder Client para testar os endpoints rapidamente.
7. Para testar o Email Service, execute o módulo `email` separadamente e verifique `http://localhost:8082`.
8. Para usar o endpoint protegido `/users/me`:
   - Crie um usuário via `POST /users`.
   - Faça login via `POST /users/login`.
   - Envie o token JWT no header `Authorization: Bearer <token>` ao chamar `GET /users/me`.
