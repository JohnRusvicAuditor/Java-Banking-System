@echo off
cd /d "%~dp0"

where javac >nul 2>nul
if errorlevel 1 (
  echo Java JDK not found.
  echo Install it with:  winget install EclipseAdoptium.Temurin.21.JDK
  echo Then close this window and run again.
  pause
  exit /b 1
)

if not exist out mkdir out
javac -encoding UTF-8 -d out src\bank\*.java src\bank\exception\*.java src\bank\model\*.java src\bank\service\*.java src\bank\util\*.java src\bank\test\*.java
if errorlevel 1 (
  echo.
  echo Compile failed. Fix the errors above.
  pause
  exit /b 1
)

java -cp out bank.test.BankTests
echo.
pause