$projectRoot = "C:\Users\aborr\Downloads\APISpringSecurity"

Start-Process powershell -ArgumentList @(
    '-NoExit',
    '-Command', "Set-Location '$projectRoot'; .\mvnw.cmd spring-boot:run"
)

Start-Process powershell -ArgumentList @(
    '-NoExit',
    '-Command', "Set-Location '$projectRoot\email'; .\mvnw.cmd spring-boot:run"
)
