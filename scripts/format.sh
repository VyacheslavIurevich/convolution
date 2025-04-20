#!/bin/sh -e
echo "Format the code"
find . -type f \( -name "*.c" -o -name "*.h" \) -print0 | xargs -r -0 clang-format -i
echo "OK"
