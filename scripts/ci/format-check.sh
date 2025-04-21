#!/bin/sh -e
echo "Check the code formatting"
find . -type f \( -name "*.c" -o -name "*.h" \) -print0 | xargs -r -0 clang-format --dry-run --Werror
echo "OK"
