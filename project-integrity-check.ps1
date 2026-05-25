param(
    [string]$ProjectPath = ".",
    [string]$ReportPath = ".\integrity-report.md"
)

$ProjectPath = Resolve-Path -Path $ProjectPath
Set-Location -Path $ProjectPath

function Add-Result {
    param(
        [string]$Category,
        [string]$Check,
        [string]$Status,
        [string]$Details,
        [int]$Points
    )
    return [pscustomobject]@{
        Category = $Category
        Check = $Check
        Status = $Status
        Details = $Details
        Points = $Points
    }
}

function Get-CommandAvailable {
    param([string]$Command)
    return (Get-Command $Command -ErrorAction SilentlyContinue) -ne $null
}

function Run-Command {
    param(
        [string]$Command,
        [string[]]$Arguments
    )
    try {
        $output = & $Command @Arguments 2>&1
        return @{ Succeeded = $true; Output = $output; ExitCode = 0 }
    } catch {
        return @{ Succeeded = $false; Output = $_.Exception.Message; ExitCode = 1 }
    }
}

function Write-Report {
    param(
        [psobject[]]$Results,
        [string]$ReportFile
    )

    $totalScore = ($Results | Measure-Object -Property Points -Sum).Sum
    $maxScore = 100
    if ($totalScore -gt $maxScore) { $totalScore = $maxScore }
    $grade = switch ($totalScore) {
        { $_ -ge 90 } { 'A - Excelente' }
        { $_ -ge 75 } { 'B - Bom' }
        { $_ -ge 60 } { 'C - Razoável' }
        { $_ -ge 40 } { 'D - Precisa melhorar' }
        default { 'E - Inseguro' }
    }

    $report = @()
    $report += "# Integrity Report"
    $report += ""
    $report += "- **Project path:** $ProjectPath"
    $report += "- **Generated:** $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')"
    $report += "- **Score:** $totalScore / $maxScore ($grade)"
    $report += ""
    $report += "## Categoria e Pontuação"
    $report += ""
    $report += "| Categoria | Verificação | Status | Pontos |"
    $report += "|---|---|---|---|"
    foreach ($result in $Results) {
        $details = if ($result.Details) { $result.Details } else { '-' }
        $report += "| $($result.Category) | $($result.Check) | $($result.Status) | $($result.Points) |"
    }
    $report += ""
    $report += "## Detalhes"
    $report += ""
    foreach ($group in $Results | Group-Object Category) {
        $report += "### $($group.Name)"
        $report += ""
        foreach ($item in $group.Group) {
            $report += "- **$($item.Check)**: $($item.Status)"
            if ($item.Details) {
                $report += "  - $($item.Details)"
            }
        }
        $report += ""
    }

    $failed = $Results | Where-Object { $_.Status -ne 'PASS' }
    if ($failed.Count -gt 0) {
        $report += "## Problemas encontrados"
        $report += ""
        foreach ($item in $failed) {
            $report += "- $($item.Check): $($item.Details)"
        }
        $report += ""
    } else {
        $report += "## Nenhum problema crítico encontrado."
        $report += ""
    }

    $report | Set-Content -Path $ReportFile -Encoding UTF8
    Write-Host "Relatório gerado em: $ReportFile"
}

$results = @()

# Basic checks
$gitAvailable = Get-CommandAvailable git
if ($gitAvailable) {
    $gitRoot = Run-Command git @('rev-parse', '--show-toplevel')
    if ($gitRoot.Succeeded) {
        $results += Add-Result -Category 'Repo' -Check 'Repositório Git detectado' -Status 'PASS' -Details 'Git está disponível e o diretório faz parte de um repositório Git.' -Points 10
        $status = Run-Command git @('status', '--short')
        if ($status.Succeeded -and [string]::IsNullOrWhiteSpace($status.Output -join "`n")) {
            $results += Add-Result -Category 'Repo' -Check 'Área de trabalho limpa' -Status 'PASS' -Details 'Nenhuma alteração não comitada foi encontrada.' -Points 10
        } else {
            $details = if ($status.Output) { ($status.Output -join "`n") } else { 'Não foi possível determinar o estado do repositório.' }
            $results += Add-Result -Category 'Repo' -Check 'Área de trabalho limpa' -Status 'WARN' -Details "Alterações locais detectadas ou status não disponível.`n$details" -Points 5
        }
    } else {
        $results += Add-Result -Category 'Repo' -Check 'Repositório Git detectado' -Status 'FAIL' -Details 'O diretório atual não está em um repositório Git.' -Points 0
        $results += Add-Result -Category 'Repo' -Check 'Área de trabalho limpa' -Status 'FAIL' -Details 'Não há repositório Git para verificar o status.' -Points 0
    }
} else {
    $results += Add-Result -Category 'Repo' -Check 'Repositório Git detectado' -Status 'FAIL' -Details 'O comando git não está disponível.' -Points 0
    $results += Add-Result -Category 'Repo' -Check 'Área de trabalho limpa' -Status 'FAIL' -Details 'Não foi possível verificar alterações locais sem git.' -Points 0
}

if (Test-Path '.gitignore') {
    $results += Add-Result -Category 'Repo' -Check '.gitignore presente' -Status 'PASS' -Details '.gitignore está presente no projeto.' -Points 5
    $ignoreContent = Get-Content .gitignore
    $patterns = @('.venv/', '.idea/', '.vscode/', 'target/', 'build/', 'node_modules/', '__pycache__/')
    $matched = $patterns | Where-Object { $ignoreContent -match [regex]::Escape($_) }
    $missing = $patterns | Where-Object { $ignoreContent -notmatch [regex]::Escape($_) }
    if ($matched.Count -ge 3) {
        $results += Add-Result -Category 'Repo' -Check 'Ignorar arquivos comuns' -Status 'PASS' -Details "Padrões detectados: $($matched -join ', ')" -Points 10
    } else {
        $details = if ($missing.Count -gt 0) { "Faltando padrões: $($missing -join ', ')" } else { 'Padrões de ignore insuficientes.' }
        $results += Add-Result -Category 'Repo' -Check 'Ignorar arquivos comuns' -Status 'WARN' -Details $details -Points 5
    }
} else {
    $results += Add-Result -Category 'Repo' -Check '.gitignore presente' -Status 'FAIL' -Details 'Não foi encontrado .gitignore no projeto.' -Points 0
    $results += Add-Result -Category 'Repo' -Check 'Ignorar arquivos comuns' -Status 'FAIL' -Details 'Sem .gitignore, não há regras de exclusão.' -Points 0
}

if (Test-Path 'README.md' -or Test-Path 'README.MD' -or Test-Path 'README') {
    $results += Add-Result -Category 'Doc' -Check 'README presente' -Status 'PASS' -Details 'Arquivo README encontrado.' -Points 5
} else {
    $results += Add-Result -Category 'Doc' -Check 'README presente' -Status 'WARN' -Details 'Nenhum README detectado.' -Points 2
}

# Detect project type
$projectType = 'unknown'
if (Test-Path 'pom.xml') { $projectType = 'maven' }
elseif (Test-Path 'build.gradle' -or Test-Path 'build.gradle.kts') { $projectType = 'gradle' }
elseif (Test-Path 'package.json') { $projectType = 'node' }
elseif (Test-Path 'pyproject.toml' -or Test-Path 'requirements.txt' -or Test-Path 'setup.py') { $projectType = 'python' }

$results += Add-Result -Category 'Project' -Check 'Tipo de projeto detectado' -Status 'PASS' -Details "Tipo de projeto detectado: $projectType." -Points 5

switch ($projectType) {
    'maven' {
        $mvnFile = if (Test-Path '.\mvnw.cmd') { '.\mvnw.cmd' } elseif (Test-Path '.\mvnw') { '.\mvnw' } else { 'mvn' }
        $mvnAvailable = Get-CommandAvailable $mvnFile
        if ($mvnAvailable) {
            $results += Add-Result -Category 'Build' -Check 'Maven wrapper ou Maven disponível' -Status 'PASS' -Details "Arquivo de wrapper ou comando encontrado: $mvnFile." -Points 5
            $build = Run-Command $mvnFile @('clean', 'test-compile')
            if ($build.Succeeded) {
                $results += Add-Result -Category 'Build' -Check 'Compilação Maven' -Status 'PASS' -Details 'Maven clean test-compile passou.' -Points 20
                $results += Add-Result -Category 'Build' -Check 'Testes Maven (se existentes)' -Status 'PASS' -Details 'A fase de compilação passou; testes executados quando presentes.' -Points 15
            } else {
                $results += Add-Result -Category 'Build' -Check 'Compilação Maven' -Status 'FAIL' -Details "Falha na compilação: $($build.Output -join ' `n')" -Points 0
                $results += Add-Result -Category 'Build' -Check 'Testes Maven (se existentes)' -Status 'FAIL' -Details 'A compilação falhou, portanto os testes não foram validados.' -Points 0
            }
        } else {
            $results += Add-Result -Category 'Build' -Check 'Maven wrapper ou Maven disponível' -Status 'FAIL' -Details 'Maven não está disponível no ambiente e não foi encontrado wrapper.' -Points 0
        }
    }
    'gradle' {
        $gradleFile = if (Test-Path '.\gradlew.bat') { '.\gradlew.bat' } elseif (Test-Path '.\gradlew') { './gradlew' } else { 'gradle' }
        $gradleAvailable = Get-CommandAvailable $gradleFile
        if ($gradleAvailable) {
            $results += Add-Result -Category 'Build' -Check 'Gradle wrapper ou Gradle disponível' -Status 'PASS' -Details "Arquivo de wrapper ou comando encontrado: $gradleFile." -Points 5
            $build = Run-Command $gradleFile @('build', '--console=plain')
            if ($build.Succeeded) {
                $results += Add-Result -Category 'Build' -Check 'Compilação Gradle' -Status 'PASS' -Details 'Gradle build passou.' -Points 20
                $results += Add-Result -Category 'Build' -Check 'Testes Gradle (se existentes)' -Status 'PASS' -Details 'A fase de build passou; testes executados quando presentes.' -Points 15
            } else {
                $results += Add-Result -Category 'Build' -Check 'Compilação Gradle' -Status 'FAIL' -Details "Falha na compilação: $($build.Output -join ' `n')" -Points 0
                $results += Add-Result -Category 'Build' -Check 'Testes Gradle (se existentes)' -Status 'FAIL' -Details 'A compilação falhou, portanto os testes não foram validados.' -Points 0
            }
        } else {
            $results += Add-Result -Category 'Build' -Check 'Gradle wrapper ou Gradle disponível' -Status 'FAIL' -Details 'Gradle não está disponível no ambiente e não foi encontrado wrapper.' -Points 0
        }
    }
    'node' {
        if (Get-CommandAvailable npm) {
            $results += Add-Result -Category 'Build' -Check 'npm disponível' -Status 'PASS' -Details 'npm está instalado no ambiente.' -Points 5
            if (-not (Test-Path 'node_modules')) {
                $install = Run-Command npm @('install', '--ignore-scripts')
                if ($install.Succeeded) {
                    $results += Add-Result -Category 'Build' -Check 'Instalação de dependências Node' -Status 'PASS' -Details 'Dependências npm instaladas com sucesso.' -Points 10
                } else {
                    $results += Add-Result -Category 'Build' -Check 'Instalação de dependências Node' -Status 'FAIL' -Details "Falha ao instalar dependências: $($install.Output -join ' `n')" -Points 0
                }
            } else {
                $results += Add-Result -Category 'Build' -Check 'Instalação de dependências Node' -Status 'PASS' -Details 'node_modules já está presente.' -Points 10
            }
            $packageJson = Get-Content package.json -Raw | ConvertFrom-Json
            if ($packageJson.scripts -and $packageJson.scripts.test) {
                $test = Run-Command npm @('test', '--', '--silent')
                if ($test.Succeeded) {
                    $results += Add-Result -Category 'Build' -Check 'Testes Node' -Status 'PASS' -Details 'npm test passou.' -Points 20
                } else {
                    $results += Add-Result -Category 'Build' -Check 'Testes Node' -Status 'FAIL' -Details "Falha nos testes: $($test.Output -join ' `n')" -Points 0
                }
            } elseif ($packageJson.scripts -and $packageJson.scripts.build) {
                $build = Run-Command npm @('run', 'build')
                if ($build.Succeeded) {
                    $results += Add-Result -Category 'Build' -Check 'Build Node' -Status 'PASS' -Details 'npm run build passou.' -Points 20
                } else {
                    $results += Add-Result -Category 'Build' -Check 'Build Node' -Status 'FAIL' -Details "Falha no build: $($build.Output -join ' `n')" -Points 0
                }
            } else {
                $results += Add-Result -Category 'Build' -Check 'Script de teste/build Node' -Status 'WARN' -Details 'Não foi encontrado script test ou build em package.json.' -Points 5
            }
        } else {
            $results += Add-Result -Category 'Build' -Check 'npm disponível' -Status 'FAIL' -Details 'npm não está instalado no ambiente.' -Points 0
        }
    }
    'python' {
        if (Get-CommandAvailable python) {
            $results += Add-Result -Category 'Build' -Check 'Python disponível' -Status 'PASS' -Details 'Python está instalado no ambiente.' -Points 5
            $compile = Run-Command python @('-m', 'compileall', '.', '--quiet')
            if ($compile.Succeeded) {
                $results += Add-Result -Category 'Build' -Check 'Compilação Python' -Status 'PASS' -Details 'Compilação de bytecode Python passou.' -Points 15
            } else {
                $results += Add-Result -Category 'Build' -Check 'Compilação Python' -Status 'FAIL' -Details "Falha de compilação Python: $($compile.Output -join ' `n')" -Points 0
            }
            if (Get-CommandAvailable pytest) {
                $pytest = Run-Command pytest @('--maxfail=1', '-q')
                if ($pytest.Succeeded) {
                    $results += Add-Result -Category 'Build' -Check 'Testes Python' -Status 'PASS' -Details 'pytest executou com sucesso.' -Points 20
                } else {
                    $results += Add-Result -Category 'Build' -Check 'Testes Python' -Status 'FAIL' -Details "Falha nos testes Python: $($pytest.Output -join ' `n')" -Points 0
                }
            } else {
                $results += Add-Result -Category 'Build' -Check 'pytest disponível' -Status 'WARN' -Details 'pytest não está instalado; os testes não foram executados.' -Points 0
            }
        } else {
            $results += Add-Result -Category 'Build' -Check 'Python disponível' -Status 'FAIL' -Details 'Python não está instalado no ambiente.' -Points 0
        }
    }
    default {
        $results += Add-Result -Category 'Build' -Check 'Detecção de projeto' -Status 'WARN' -Details 'Tipo de projeto não identificado para build automático.' -Points 0
    }
}

Write-Report -Results $results -ReportFile $ReportPath
