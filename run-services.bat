@echo off
REM Cross-platform runner for Windows (cmd)
SET ROOT_DIR=%~dp0
SET LOG_DIR=%ROOT_DIR%logs
IF NOT EXIST "%LOG_DIR%" mkdir "%LOG_DIR%"

REM Start User Service in new window
start "User Service" cmd /k "cd /d "%ROOT_DIR%" && mvnw.cmd -DskipTests spring-boot:run > "%LOG_DIR%user.log" 2>&1"

REM Start Email Service in new window
start "Email Service" cmd /k "cd /d "%ROOT_DIR%email" && mvnw.cmd -DskipTests spring-boot:run > "%LOG_DIR%email.log" 2>&1"

echo Services started. Logs: %LOG_DIR%\user.log and %LOG_DIR%\email.log
pause
