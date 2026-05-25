# Project Integrity Check

Este documento descreve o script `project-integrity-check.ps1`, que verifica a integridade de um projeto genérico e gera um relatório em Markdown.

## Objetivo

O script detecta automaticamente o tipo de projeto e executa verificações de ambiente, build e testes para avaliar a saúde geral do repositório.

## Arquivo relacionado

- `project-integrity-check.ps1`

## Requisitos

- Windows PowerShell
- `git` disponível no PATH para verificações de repositório
- Dependências específicas do projeto (por exemplo, Maven, Gradle, npm, Python) podem ser necessárias para executar o build/testes

## Como usar

No terminal, execute o script a partir da raiz do projeto:

```powershell
.
project-integrity-check.ps1
```

ou informando um caminho de projeto e um caminho diferente para o relatório:

```powershell
.
project-integrity-check.ps1 -ProjectPath 'C:\meu-projeto' -ReportPath 'C:\meu-projeto\integrity-report.md'
```

## O que o script verifica

### Repositório

- Se o diretório está em um repositório Git
- Se a área de trabalho está limpa (sem mudanças não commitadas)
- Se existe um arquivo `.gitignore`
- Se o `.gitignore` contém padrões comuns como `.venv/`, `.idea/`, `.vscode/`, `target/`, `build/`, `node_modules/` e `__pycache__/`

### Documentação

- Se existe um arquivo `README` no projeto

### Detecção de tipo de projeto

O script identifica automaticamente projetos:

- Maven (`pom.xml`)
- Gradle (`build.gradle` ou `build.gradle.kts`)
- Node (`package.json`)
- Python (`pyproject.toml`, `requirements.txt` ou `setup.py`)

### Build e testes

Dependendo do tipo de projeto, o script executa:

- Maven: `clean test-compile`
- Gradle: `build`
- Node: `npm install` + `npm test` ou `npm run build`
- Python: `python -m compileall` e, se disponível, `pytest`

## Saída

O script gera um relatório em Markdown no arquivo padrão `integrity-report.md`.

O relatório contém:

- Pontuação total e nível de classificação
- Lista de verificações com status `PASS`, `WARN` ou `FAIL`
- Detalhes agrupados por categoria (`Repo`, `Doc`, `Build`)
- Problemas encontrados

## Níveis de avaliação

A pontuação final é avaliada como:

- `A - Excelente` (>= 90)
- `B - Bom` (>= 75)
- `C - Razoável` (>= 60)
- `D - Precisa melhorar` (>= 40)
- `E - Inseguro` (< 40)

## Observações

- O script é genérico e funciona em projetos diferentes desde que o ambiente tenha as ferramentas necessárias instaladas.
- Ele não modifica o projeto.
- Para um projeto Java/Maven, use o wrapper `mvnw.cmd` quando disponível.
