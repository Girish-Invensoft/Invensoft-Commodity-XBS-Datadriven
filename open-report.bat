@echo off
cd /d "%~dp0"

set ALLURE_CMD=C:\Users\GirishKumarMR\scoop\shims\allure.cmd

if not exist "%ALLURE_CMD%" (
    echo ERROR: allure.cmd not found at %ALLURE_CMD%
    echo Run "where.exe allure" in PowerShell to find the correct path.
    pause
    exit /b 1
)

if not exist "target\allure-report\index.html" (
    echo ERROR: target\allure-report not found.
    echo Run "mvn clean test" first to generate the report.
    pause
    exit /b 1
)

call "%ALLURE_CMD%" open target/allure-report

pause
