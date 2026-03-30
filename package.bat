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

taskkill /f /im "DeliveryOrder.exe" >nul 2>&1
timeout /t 1 /nobreak >nul

if exist "%OUTPUT_DIR%" rmdir /s /q "%OUTPUT_DIR%"
mkdir "%OUTPUT_DIR%"

jpackage ^
  --type app-image ^
  --name "DeliveryOrder" ^
  --app-version "1.0.0" ^
  --input "%BUILD_DIR%" ^
  --main-jar AppServer.jar ^
  --main-class com.martin.appserver.StandaloneServer ^
  --arguments app\dist ^
  --icon "%PROJECT_DIR%\favicon.ico" ^
  --dest "%OUTPUT_DIR%" ^
  --java-options "-Djava.awt.headless=false"

:: Copiar el frontend dentro del app/ generado por jpackage
echo Copiando frontend al installer...
set INSTALLER_APP=%OUTPUT_DIR%\DeliveryOrder\app\dist
xcopy /e /i /q /y "%BUILD_DIR%\dist" "%INSTALLER_APP%\"

:: Copiar exe_apps
echo Copiando exe_apps...
xcopy /e /i /q /y "%PROJECT_DIR%\exe_apps" "%OUTPUT_DIR%\DeliveryOrder\"

if errorlevel 1 ( echo ERROR: jpackage fallo & exit /b 1 )

:: Copiar license.txt y script.iss
echo Copiando archivos Inno Setup...
copy "%PROJECT_DIR%\license.txt" "%OUTPUT_DIR%\DeliveryOrder\"
copy "%PROJECT_DIR%\script.iss" "%OUTPUT_DIR%\DeliveryOrder\"

:: Compilar instalador con Inno Setup
echo Compilando instalador...
set INNO_COMPILER="C:\Program Files (x86)\Inno Setup 6\ISCC.exe"
%INNO_COMPILER% "%OUTPUT_DIR%\DeliveryOrder\script.iss"
if errorlevel 1 ( echo ERROR: Inno Setup fallo & exit /b 1 )

:: Abrir explorador en la carpeta del instalador
explorer "%OUTPUT_DIR%\DeliveryOrder\Output"

echo.
echo INSTALADOR GENERADO OK
