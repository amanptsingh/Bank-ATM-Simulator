@echo off
REM Compiles the project (if needed) and opens the ATM GUI directly
cd /d "%~dp0"
if not exist out\bank\Main.class (
    echo Compiling project...
    call build.bat
)
if not exist out\bank\Main.class (
    echo Compilation failed. Check that the JDK is installed and in PATH.
    pause
    exit /b 1
)
java -cp out bank.Main gui
