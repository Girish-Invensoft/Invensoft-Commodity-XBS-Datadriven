@echo off
cd /d "%~dp0"

set ALLURE_CMD=C:\Users\GirishKumarMR\Desktop\AutomationInvensoft\ERP-Automation\scoop\shims\allure.cmd

if not exist "%ALLURE_CMD%" (
    echo ERROR: allure.cmd not found at %ALLURE_CMD%
    echo Run "where.exe allure" in PowerShell to find the correct path.
    pause
    exit /b 1
)

if not exist "target\allure-results" (
    echo ERROR: target\allure-results not found.
    echo Run "mvn clean test" first to generate test results.
    pause
    exit /b 1
)

echo Generating latest Allure report...
call "%ALLURE_CMD%" generate target\allure-results -o target\allure-report --clean

if not exist "target\allure-report\index.html" (
    echo ERROR: Report generation failed - index.html not found.
    pause
    exit /b 1
)

echo Opening report...
call "%ALLURE_CMD%" open target\allure-report

pause