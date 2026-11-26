@echo off
REM Compiles the project into the "out" folder (works even if the path has spaces)
cd /d "%~dp0"
if not exist out mkdir out

REM Step 1: main project (console + AWT/Swing GUI)
javac -d out src\bank\*.java src\bank\exceptions\*.java src\bank\model\*.java src\bank\service\*.java src\bank\io\*.java src\bank\threads\*.java src\bank\gui\*.java
if errorlevel 1 (
    echo Build failed.
    pause
    exit /b 1
)
echo Main project compiled.

REM Step 2: applets (optional - newer JDKs removed the Applet API)
javac -cp out -d out src\bank\applet\*.java >nul 2>&1
if errorlevel 1 (
    echo NOTE: Applets were skipped because this JDK does not have the Applet API.
    echo       Everything else works normally. Use JDK 8 if you want to run the applets.
) else (
    echo Applets compiled.
)
echo Build finished.
