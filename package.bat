@echo off
setlocal enabledelayedexpansion

echo Generando .exe con jpackage...

set PROJECT_DIR=%~dp0
set PROJECT_DIR=%PROJECT_DIR:~0,-1%
set BUILD_DIR=%PROJECT_DIR%\build\standalone
set OUTPUT_DIR=%PROJECT_DIR%\build\installer

if not exist "%BUILD_DIR%\AppServer.jar" (
    echo ERROR: No se encontro AppServer.jar. Ejecuta build-standalone.bat primero.
    exit /b 1
)

taskkill /f /im "POS Server.exe" >nul 2>&1
timeout /t 1 /nobreak >nul

if exist "%OUTPUT_DIR%" rmdir /s /q "%OUTPUT_DIR%"
mkdir "%OUTPUT_DIR%"

jpackage ^
  --type app-image ^
  --name "POS Server" ^
  --app-version "1.0.0" ^
  --input "%BUILD_DIR%" ^
  --main-jar AppServer.jar ^
  --main-class com.martin.appserver.StandaloneServer ^
  --arguments app\dist ^
  --dest "%OUTPUT_DIR%" ^
  --java-options "-Djava.awt.headless=false"

:: Copiar el frontend dentro del app/ generado por jpackage
echo Copiando frontend al installer...
set INSTALLER_APP=%OUTPUT_DIR%\POS Server\app\dist
xcopy /e /i /q /y "%BUILD_DIR%\dist" "%INSTALLER_APP%\"

if errorlevel 1 ( echo ERROR: jpackage fallo & exit /b 1 )

echo.
echo .exe generado en: %OUTPUT_DIR%
