@echo off
setlocal enabledelayedexpansion

echo Iniciando build standalone...

set PROJECT_DIR=%~dp0
set PROJECT_DIR=%PROJECT_DIR:~0,-1%
set APP_DIR=%PROJECT_DIR%\app
set SRC_DIR=%PROJECT_DIR%\src
set LIB_DIR=%PROJECT_DIR%\lib
set STUBS_DIR=%PROJECT_DIR%\stubs
set BUILD_DIR=%PROJECT_DIR%\build\standalone
set DIST_DIR=%BUILD_DIR%\dist
set FINAL_LIB_DIR=%BUILD_DIR%\lib

:: Limpiar build anterior
echo Limpiando build anterior...
if exist "%BUILD_DIR%" rmdir /s /q "%BUILD_DIR%"
mkdir "%BUILD_DIR%"
mkdir "%FINAL_LIB_DIR%"

:: Build frontend
echo Construyendo frontend...
cd /d "%APP_DIR%"
call npm install
if errorlevel 1 ( echo ERROR: npm install fallo & exit /b 1 )
call npm run build
if errorlevel 1 ( echo ERROR: npm run build fallo & exit /b 1 )

echo Copiando frontend...
xcopy /e /i /q "%APP_DIR%\dist" "%DIST_DIR%"

:: Copiar DB si existe
if exist "%PROJECT_DIR%\appserver.db" copy "%PROJECT_DIR%\appserver.db" "%BUILD_DIR%\"

:: Compilar Java
echo Compilando Java...
cd /d "%PROJECT_DIR%"

:: Generar lista de fuentes (solo clases standalone, sin AppServer.java ni DB.java)
set SOURCES_FILE=%BUILD_DIR%\sources.txt
if exist "%SOURCES_FILE%" del "%SOURCES_FILE%"

(for /r "%SRC_DIR%\com\martin\appserver\server" %%f in (*.java) do @echo %%f) >> "%SOURCES_FILE%"
(for /r "%SRC_DIR%\com\martin\appserver\routing" %%f in (*.java) do @echo %%f) >> "%SOURCES_FILE%"
(for /r "%SRC_DIR%\com\martin\appserver\validation" %%f in (*.java) do @echo %%f) >> "%SOURCES_FILE%"
(for /r "%SRC_DIR%\com\martin\appserver\dto" %%f in (*.java) do @echo %%f) >> "%SOURCES_FILE%"
(for /r "%SRC_DIR%\com\martin\appserver\controllers" %%f in (*.java) do @echo %%f) >> "%SOURCES_FILE%"
(for /r "%SRC_DIR%\com\martin\appserver\constants" %%f in (*.java) do @echo %%f) >> "%SOURCES_FILE%"
(for /r "%SRC_DIR%\com\martin\appserver\utils" %%f in (*.java) do @echo %%f) >> "%SOURCES_FILE%"
(for /r "%SRC_DIR%\com\martin\appserver\services" %%f in (*.java) do @echo %%f) >> "%SOURCES_FILE%"
(for /r "%SRC_DIR%\com\martin\appserver\web" %%f in (*.java) do @echo %%f) >> "%SOURCES_FILE%"
(
  echo %SRC_DIR%\com\martin\appserver\StandaloneServer.java
  echo %SRC_DIR%\com\martin\appserver\database\DBAdapter.java
  echo %SRC_DIR%\com\martin\appserver\database\StandaloneDB.java
  echo %SRC_DIR%\com\martin\appserver\database\DBSchema.java
) >> "%SOURCES_FILE%"

javac -cp "%LIB_DIR%\sqlite-jdbc.jar;%LIB_DIR%\gson-2.8.9.jar;%LIB_DIR%\slf4j-api.jar;%LIB_DIR%\json.jar" ^
  -d "%BUILD_DIR%" ^
  @"%SOURCES_FILE%"

if errorlevel 1 ( echo ERROR: Compilacion Java fallo & exit /b 1 )
echo Java compilado OK

:: Copiar librerías
echo Copiando librerias...
copy "%LIB_DIR%\sqlite-jdbc.jar" "%FINAL_LIB_DIR%\"
copy "%LIB_DIR%\gson-2.8.9.jar" "%FINAL_LIB_DIR%\"
copy "%LIB_DIR%\slf4j-api.jar" "%FINAL_LIB_DIR%\"
copy "%LIB_DIR%\json.jar" "%FINAL_LIB_DIR%\"

:: Crear JAR
echo Generando JAR...
cd /d "%BUILD_DIR%"
jar cfe AppServer.jar com.martin.appserver.StandaloneServer .
if errorlevel 1 ( echo ERROR: JAR fallo & exit /b 1 )
echo JAR generado OK

:: Prueba local
echo Probando ejecucion...
java -cp "AppServer.jar;lib\*" com.martin.appserver.StandaloneServer dist

echo.
echo BUILD LISTO
echo Ubicacion: %BUILD_DIR%
echo.
echo Siguiente paso: ejecutar package.bat para generar el .exe
