#!/bin/sh

set -euo pipefail

LOG_DIR="./valgrind_logs"
mkdir -p "$LOG_DIR"

check_program() {
    local prog="$1"
    local prog_name=$(basename "$prog")
    local logfile="${LOG_DIR}/valgrind_${prog_name}.log"

    echo "Checking $prog for memory leaks..."

    valgrind \
        --leak-check=full \
        --show-leak-kinds=all \
        --track-origins=yes \
        --errors-for-leak-kinds=all \
        --error-exitcode=1 \
        --log-file="$logfile" \
        ./"$prog"

    if ! grep -q "ERROR SUMMARY: 0 errors" "$logfile"; then
        echo "Memory leaks detected in $prog!"
        cat "$logfile"
        return 1
    fi

    if grep -q "still reachable" "$logfile"; then
        echo " Warning: still reachable memory in $prog"
        return 1
    fi
    echo "No memory leaks found in $prog."
    return 0
}

make build build_test

find ./bin -type f -executable | while read -r prog; do
    check_program "$prog" || failed=1
done

make clean
rm -rf "$LOG_DIR"

if [ "${failed-0}" -eq 1 ]; then
    echo "Some programs have memory leaks!"
    exit 1
else
    echo "All checks passed! No memory leaks detected."
    exit 0
fi
