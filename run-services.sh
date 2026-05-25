#!/usr/bin/env bash
# Cross-platform runner (Linux/macOS)
# Starts User Service and Email Service in background and writes logs to ./logs

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
LOG_DIR="$ROOT_DIR/logs"
mkdir -p "$LOG_DIR"

# Start User Service
echo "Starting User Service (logs: $LOG_DIR/user.log)"
cd "$ROOT_DIR"
if [ -x "$ROOT_DIR/mvnw" ]; then
  ./mvnw -DskipTests spring-boot:run > "$LOG_DIR/user.log" 2>&1 &
else
  mvn -DskipTests -f "$ROOT_DIR/pom.xml" spring-boot:run > "$LOG_DIR/user.log" 2>&1 &
fi
USER_PID=$!

echo "User Service PID: $USER_PID"

# Start Email Service
echo "Starting Email Service (logs: $LOG_DIR/email.log)"
cd "$ROOT_DIR/email"
if [ -x "$ROOT_DIR/mvnw" ]; then
  ../mvnw -DskipTests spring-boot:run > "$LOG_DIR/email.log" 2>&1 &
else
  mvn -DskipTests -f "email/pom.xml" spring-boot:run > "$LOG_DIR/email.log" 2>&1 &
fi
EMAIL_PID=$!

echo "Email Service PID: $EMAIL_PID"

echo "Both services started. Tail logs with: tail -f $LOG_DIR/user.log $LOG_DIR/email.log"

exit 0
