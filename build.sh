#!/bin/sh
# Compiles the project into the "out" folder
mkdir -p out
javac -d out src/bank/*.java src/bank/exceptions/*.java src/bank/model/*.java src/bank/service/*.java src/bank/io/*.java src/bank/threads/*.java src/bank/gui/*.java || exit 1
echo "Main project compiled."
if javac -cp out -d out src/bank/applet/*.java >/dev/null 2>&1; then
    echo "Applets compiled."
else
    echo "NOTE: Applets skipped (this JDK has no Applet API). Everything else works."
fi
echo "Build finished."
