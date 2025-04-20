#!/bin/sh -e
echo "Format the code"
find . -type f -name "*.c" -o -name "*.h" | xargs -r clang-format -style=llvm -i
echo "OK"
