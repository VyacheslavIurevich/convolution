#!/bin/sh -e
echo "Lint the code"
make clean
bear -- make build
find . -type f \( -name "*.c" -o -name "*.h" \) -print0 | xargs -r -0 clang-tidy -p
make clean
rm -f compile_commands.json
echo "OK"
