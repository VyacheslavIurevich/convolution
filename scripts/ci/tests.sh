#!/bin/sh -e

make clean
mkdir -p coverage
make build_coverage build_tests

LLVM_PROFILE_FILE="coverage/coverage.profraw" ./bin/tests_runner
llvm-profdata merge -sparse coverage/coverage.profraw -o coverage/coverage.profdata
echo "Coverage report:"
llvm-cov report ./bin/tests_runner \
    -instr-profile=coverage/coverage.profdata \
    --ignore-filename-regex="tests/*" \
    --show-region-summary=false \
    --show-branch-summary=false
llvm-cov show ./bin/tests_runner \
    -instr-profile=coverage/coverage.profdata \
    -format=html \
    -output-dir=coverage/html \
    --ignore-filename-regex="tests/*"
rm -rf coverage
rm -f default.profraw
make clean
echo "HTML report generated in coverage/html directory"
