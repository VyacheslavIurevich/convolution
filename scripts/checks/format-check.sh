#!/bin/sh -e
echo "Check the code formatting"
find . -type f -name "*.c" -o -name "*.h" | xargs -r clang-format --dry-run --Werror
echo "OK"
