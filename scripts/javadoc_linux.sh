#! /usr/bin/bash
if [ $# -eq 0 ]; then
    echo "Usage: $0 <javafx-sdk-path>"
fi
javafxpath=$1
if [ -d "$javafxpath" ]; then
    javadoc -d docs src/game/java/*.java src/gui/java/*.java src/p2p/java/*.java --module-path "$javafxpath"/lib --add-modules javafx.controls,javafx.fxml
fi


