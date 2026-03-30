@echo off
setlocal enabledelayedexpansion

set PROJECT_DIR=%~dp0
set PROJECT_DIR=%PROJECT_DIR:~0,-1%
set JAR=%PROJECT_DIR%\build\standalone\AppServer.jar

echo [1/2] Ejecutando build-standalone...
start "BUILD_STANDALONE" cmd /k "cd /d "%PROJECT_DIR%" && call build-standalone.bat"

:: Esperar a que el JAR sea generado (indica que el build termino)
echo Esperando que el build termine...
:wait_loop
timeout /t 3 /nobreak >nul
if not exist "%JAR%" goto wait_loop

:: Dar 2 segundos extra para que el servidor de prueba arranque y luego matarlo
timeout /t 2 /nobreak >nul
echo Deteniendo servidor de prueba...
taskkill /fi "WINDOWTITLE eq BUILD_STANDALONE" /f >nul 2>&1
taskkill /f /fi "IMAGENAME eq java.exe" >nul 2>&1

echo [2/2] Ejecutando package...
call "%PROJECT_DIR%\package.bat"
if errorlevel 1 ( echo ERROR en package & pause & exit /b 1 )

echo.
echo PROCESO COMPLETO
pause
