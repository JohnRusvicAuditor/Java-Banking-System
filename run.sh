#!/usr/bin/env bash
# Compiles and launches the GUI. Works locally and in GitHub Codespaces.
set -e
cd "$(dirname "$0")"
mkdir -p out
javac -encoding UTF-8 -d out $(find src -name "*.java")
if [ -z "$DISPLAY" ]; then
  echo "No display found. In Codespaces open the 'Desktop (noVNC)' port 6080 (password: vscode) and set DISPLAY=:1"
  export DISPLAY=:1
fi
java -cp out bank.Main
