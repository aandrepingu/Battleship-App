@echo off
if "%~1"=="" (
    echo "Usage: run_windows.cmd <path_to_javafx_folder>"
    exit /b 1
)

set "javafxpath=%~1"

javac .\src\game\java\*.java ./src/gui/java/*.java ./src/p2p/java/*.java -d bin --module-path %javafxpath%\lib --add-modules javafx.controls,javafx.fxml
java --module-path %javafxpath%\lib --add-modules javafx.controls,javafx.fxml -cp bin gui.java.BattleshipGUI
