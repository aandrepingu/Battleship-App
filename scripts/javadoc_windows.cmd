@echo off
if "%~1"=="" (
    echo "Usage: javadoc_windows.cmd <path_to_javafx_folder>"
    exit /b 1
)

set "javafxpath=%~1"


javadoc .\src\game\java\*.java .\src\gui\java\*.java .\src\p2p\java\*.java -d .\docs --module-path %javafxpath%\lib --add-modules javafx.controls,javafx.fxml
