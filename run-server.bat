@echo off
setlocal

set PROJECT_DIR=%~dp0
set PROJECT_DIR=%PROJECT_DIR:~0,-1%
set APP_DIR=%PROJECT_DIR%\app
set SRC_DIR=%PROJECT_DIR%\src
set LIB_DIR=%PROJECT_DIR%\lib
set STUBS_DIR=%PROJECT_DIR%\stubs
set BUILD_DIR=%PROJECT_DIR%\build\standalone

echo Ejecutando npm run build en /app...
cd /d "%APP_DIR%"
call npm run build
if errorlevel 1 ( echo ERROR: npm run build fallo & exit /b 1 )

echo Compilando AppServer...
mkdir "%BUILD_DIR%" 2>nul
cd /d "%PROJECT_DIR%"

set SOURCES_FILE=%BUILD_DIR%\sources.txt
if exist "%SOURCES_FILE%" del "%SOURCES_FILE%"

for /r "%SRC_DIR%\com\martin\appserver" %%f in (*.java) do echo %%f >> "%SOURCES_FILE%"
for /r "%STUBS_DIR%" %%f in (*.java) do echo %%f >> "%SOURCES_FILE%"

javac -source 8 -target 8 ^
  -cp "%LIB_DIR%\sqlite-jdbc.jar;%LIB_DIR%\gson-2.8.9.jar;%LIB_DIR%\slf4j-api.jar" ^
  -d "%BUILD_DIR%" ^
  @"%SOURCES_FILE%"

if errorlevel 1 (
    echo ERROR: Error en la compilacion
    exit /b 1
)

echo Compilacion exitosa
echo.

cd /d "%BUILD_DIR%"
java -cp ".;%LIB_DIR%\sqlite-jdbc.jar;%LIB_DIR%\slf4j-api.jar;%LIB_DIR%\slf4j-simple.jar;%LIB_DIR%\gson-2.8.9.jar" ^
  com.martin.appserver.StandaloneServer "%APP_DIR%\dist"
