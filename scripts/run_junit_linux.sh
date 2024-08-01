#! /usr/bin/bash
if [ $# -eq 0 ]; then
    echo "Usage: $0 <junit standalone jar path>"
fi
junitpath=$1
if [ -f "$junitpath" ]; then
    echo " jar is : $junitpath"
    java -jar "$junitpath" execute -f=./src/test/java/BattleshipGameTest.java
fi
