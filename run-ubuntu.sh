#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
if [[ -x ./mvnw ]]; then
  ./mvnw clean javafx:run
elif command -v mvn >/dev/null 2>&1; then
  mvn clean javafx:run
else
  echo 'Falta Maven: sudo apt install maven openjdk-21-jdk' >&2
  exit 1
fi
