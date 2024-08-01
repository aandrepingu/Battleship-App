@echo off
if "%~1"=="" (
    echo "Usage: run_junit_windows.cmd <path to junit standalone jar>"
    exit /b 1
)

set "junitpath=%~1"

java -jar %junitpath% execute -f=".\src\test\java\BattleshipPlayerTest.java"
