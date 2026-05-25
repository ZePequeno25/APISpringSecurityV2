Executar ambos os serviços (User + Email)

Linux / macOS

1. Dê permissão de execução no script:

```bash
chmod +x run-services.sh
```

2. Execute:

```bash
./run-services.sh
```

- Os logs ficarão em `./logs/user.log` e `./logs/email.log`

Windows (PowerShell)

1. No PowerShell, execute:

```powershell
.\run-services.ps1
```

ou (cmd)

```cmd
run-services.bat
```

Observações

- Antes de rodar, certifique-se de criar os bancos com `mysql-setup.sql`.
- Se preferir rodar manualmente, use:

```bash
# User service
cd <repo-root>
./mvnw spring-boot:run

# Email service
cd <repo-root>/email
./mvnw spring-boot:run
```
