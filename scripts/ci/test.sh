#!/bin/sh -e

echo "Run tests and count coverage"
make clean
make build build_test
mkdir -p coverage
lcov -q --directory . --zerocounters > /dev/null 2>&1
./bin/test_runner
lcov -q --directory . --capture --output-file coverage/coverage.info --branch-coverage > /dev/null 2>&1
lcov -q --extract coverage/coverage.info "src/*" --output-file coverage/coverage_filtered.info > /dev/null 2>&1
genhtml -o coverage/report coverage/coverage_filtered.info --branch-coverage > /dev/null 2>&1
echo "Coverage report generated at coverage/report/index.html"
lcov --summary coverage/coverage_filtered.info 2>&1 | \
    grep -A3 "Summary coverage rate:" | \
    tail -n 4
make clean
rm -rf coverage
echo "OK"
